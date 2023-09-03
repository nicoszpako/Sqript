package fr.nico.sqript.compiling;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.actions.ActSimpleExpression;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptBlockFunction;
import fr.nico.sqript.blocks.ScriptFunctionalBlock;
import fr.nico.sqript.compiling.parsers.ScriptExpressionParser;
import fr.nico.sqript.function.ScriptNativeFunction;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeFunction;
import fr.nico.sqript.types.primitive.PrimitiveType;
import fr.nico.sqript.expressions.*;
import fr.nico.sqript.meta.*;
import fr.nico.sqript.types.primitive.TypeString;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptDecoder {

    public static final String CAPTURE_BETWEEN_QUOTES = "(?:\"((?:[^\"\\\\\\\\]|\\\\.)*)\")";
    public static final String CAPTURE_INTEGER = "(?:\\s*[0-9]+)";
    public static final String CAPTURE_BOOLEAN = "(?:([Tt]rue|[Ff]alse))";
    public static final String CAPTURE_NUMBER = "(?:\\s*[0-9ABCDEFabcdef]*(?:\\.[0-9]*+)?)";
    public static final String CAPTURE_EXPRESSION_LAZY = "(?:.+?)";
    public static final String CAPTURE_EXPRESSION_GREEDY = "(?:.+)";
    public static final String CAPTURE_EXPRESSION_OR_NONE = "(?:.*)";

    public static List<String> operators_list = new LinkedList<>();
    public static List<Pattern> operators_pattern = new LinkedList<>();
    public static Pattern pattern_function = Pattern.compile("^\\s*(\\w*)\\((.*)\\)\\s*$");
    public static Pattern pattern_removed_string = Pattern.compile("\\{S(\\d*)}");
    public static Pattern pattern_capture_quotes = Pattern.compile("(" + CAPTURE_BETWEEN_QUOTES + ")");
    public static Pattern pattern_variable = Pattern.compile("^(?:\\$)?\\{(.*)}$");
    public static Pattern pattern_compiled_string = Pattern.compile("@\\{S(\\d*)}");
    public static Pattern pattern_percent = Pattern.compile("(?<!~)%");
    public static Pattern pattern_mark = Pattern.compile("\\?<m(\\d*)>");
    public static Pattern pattern_L = Pattern.compile("(?<!\\\\)(L\\{(\\d+)})");


    public static void init() {

        ScriptExpressionParser.init();

    }

    public static IScript parseLine(ScriptToken line, ScriptCompilationContext compileGroup) throws Exception {

        IScript script = parseLoop(line, compileGroup); //On rentre dans un bloc
        if (script == null)
            script = parseAction(line, compileGroup);//Si c'est pas une boucle c'est une action

        //Custom parsers
        if (script == null)
            for (IScriptParser parser : ScriptManager.parsers) {
                if ((script = parser.parse(line, compileGroup)) != null)
                    return script;
            }
        return script;
    }

    public static ScriptNativeFunction getNativeFunction(ScriptToken name) {
        for (NativeDefinition nativeDefinition : ScriptManager.nativeFunctions.values()) {
            int matchedPatternIndex;
            if ((matchedPatternIndex = nativeDefinition.getMatchedPatternIndex(name.getText())) != -1) {
                Class<? extends ScriptNativeFunction> funcClass = nativeDefinition.getNativeClass();
                try {
                    Constructor<? extends ScriptNativeFunction> t = funcClass.getDeclaredConstructor(int.class);
                    return t.newInstance(matchedPatternIndex);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static ExprNativeFunction parseNativeFunction(ScriptToken parameter) {
        Class<ExprNativeFunction> function = ExprNativeFunction.class;
        ScriptNativeFunction f = getNativeFunction(parameter);
        if (f != null) {
            Constructor<ExprNativeFunction> nativeFunctionConstructor = null;
            try {
                nativeFunctionConstructor = function.getConstructor(ScriptNativeFunction.class);
                return nativeFunctionConstructor.newInstance(f);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isParenthesageGood(String line) {
        if (line == null)
            return true;
        int p = 0;
        int b = 0;
        int c = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '(')
                p++;
            if (line.charAt(i) == '[')
                b++;
            if (line.charAt(i) == '{')
                c++;
            if (line.charAt(i) == ')')
                p--;
            if (line.charAt(i) == ']')
                b--;
            if (line.charAt(i) == '}')
                c--;
        }
        return p == 0 && b == 0 && c == 0;
    }

    public static boolean isOperator(String p) {
        for (ScriptOperator op : ScriptManager.operators) {
            if (p.equals(op.symbol)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkCharList(char test, String charlist) {
        for (byte j = 0; j < charlist.length(); j++) {
            if (test == charlist.charAt(j)) return true;
        }
        return false;
    }

    public static boolean isAlgebraic(String expression){
        return expression.contains("(") || expression.contains(")") || containsOperator(expression);
    }

    public static boolean containsOperator(String expression) {
        for (Pattern p : operators_pattern) {
            Matcher m = p.matcher(expression);
            //System.out.println("Checking if "+expression+" matches "+p.pattern());
            if (m.find()) {
                //System.out.println("Found contained operator for "+expression+" with "+p.pattern());
                return true;
            }
        }
        return false;
    }

    public static String[] extractStrings(String parameter) {
        List<String> result = new ArrayList<>();
        //System.out.println("Extracting strings for : "+parameter);

        Matcher m = pattern_capture_quotes.matcher(parameter);
        while (m.find()) {
            String f = m.group(2);
            result.add(f);
        }
        //System.out.println("Returning : "+ Arrays.toString(result.toArray(new String[0])));
        return result.toArray(new String[0]);
    }

    public static String replaceStrings(String parameter, String[] strings) {
        int i = 0;
        for (String s : strings) {
            parameter = parameter.replaceFirst("@\\{S" + i + "}", "\"" + Matcher.quoteReplacement(s) + "\"");
            i++;
        }
        return parameter;
    }

    public static String removeStrings(String parameter, String[] strings) {
        int i = 0;
        for (String s : strings) {
            parameter = parameter.replaceFirst(Pattern.quote("\"" + s + "\""), "@{S" + i + "}");
            i++;
        }
        return parameter;
    }

    public static String save(String expression, String start_c, String end_c, List<String> saved) {
        //System.out.println("Saving : "+expression);
        //Removing variables
        int l = 0;
        int[] depth = new int[start_c.length()];
        int start = 0;
        int start_char_index = -1;
        while (l < expression.length()) {
            //System.out.println("Char at : "+l+" is a "+expression.charAt(l));
            if ((start_char_index = start_c.indexOf(expression.charAt(l))) != -1) {
                //System.out.println("Char at : "+l+" is a "+expression.charAt(l)+" as a : '"+start_c.charAt(start_char_index)+"'");
                if (depth[start_char_index] == 0)
                    start = l;
                depth[start_char_index]++;
            }
            if ((start_char_index = end_c.indexOf(expression.charAt(l))) != -1) {
                depth[start_char_index]--;
                if (depth[start_char_index] == 0) {
                    String a = expression.substring(0, start);
                    String b = expression.substring(l + 1);
                    String m = expression.substring(start, l + 1);
                    String n = "L{" + saved.size() + "}";
                    expression = a + n + b;
                    //System.out.println("j:"+expression);
                    l += n.length() - m.length();
                    saved.add(m);
                }
            }
            l++;
        }
        //System.out.println("Result saved : "+expression);
        return expression;
    }

    public static String buildOperators(ScriptToken line, String expression) throws ScriptException.ScriptUnknownExpressionException {
        List<String> saved = new ArrayList<>();
        List<ScriptOperator> operators = new ArrayList<>();
        //System.out.println("Building operators for : " + expression);
        //Saving variables and arrays

        //System.out.println("a : " + expression);
        //Removing strings
        Matcher matcher = pattern_capture_quotes.matcher(expression);
        while (matcher.find()) {
            String f = matcher.group(1);
            expression = expression.replaceFirst(Pattern.quote(f), "L{" + saved.size() + "}");
            //System.out.println("n:"+expression);
            saved.add(f);
        }
        expression = save(expression, "{[<", "}]>", saved);

        //System.out.println("b : " + expression+" ; saved = "+saved);
        String testedExpression = emptyDelimiters('(', ')', expression);
        testedExpression = emptyDelimiters('[', ']', testedExpression);
        testedExpression = emptyDelimiters('{', '}', testedExpression);
        testedExpression = emptyDelimiters('<', '>', testedExpression);
        //Placing operators
        //System.out.println("Tested is : "+testedExpression);
        while (containsOperator(testedExpression)) {
            String newTestedExpression = testedExpression;
            for (ScriptOperator operator : ScriptManager.operators) {
                String patternPrefix = "", patternSuffix = "(?![^(]*\\))";
                if (operator.word) {
                    patternPrefix = "(?:" + (operator.unary ? "" : "\\)") + "\\s+|^|}|\\()";
                    //patternSuffix = "\\operator\\(+" + patternSuffix;
                    patternSuffix = "\\s+\\(";
                }
                //System.out.println("Checking with : "+patternPrefix +"(" + Pattern.quote(operator.symbol)+")"+ patternSuffix);

                Pattern p = Pattern.compile(patternPrefix +"(" + Pattern.quote(operator.symbol)+")"+ patternSuffix);
                matcher = p.matcher(expression);
                //System.out.println("Trying to match  : "+expression+" with regex : "+"(" + patternPrefix + Pattern.quote(operator.symbol) + patternSuffix + ")");
                if (matcher.find()) {
                    if (operator.unary && !operator.postfixed) {//Check if this one is unary
                        boolean isUnary = false;
                        int position = matcher.start();
                        if (position == 0)
                            isUnary = true;
                        else {
                            while (expression.charAt(position) == ' ' && position > 0) {
                                position--;
                            }
                            if (expression.charAt(position) == '(' || expression.charAt(position) == '}')
                                isUnary = true;
                        }
                        if (!isUnary) continue;
                    }
                    int match_start = matcher.start(1);
                    int match_end = matcher.end(1);
                    //System.out.println("Expression A ; " + expression);
                    expression = expression.substring(0, match_start) + "#{" + ScriptManager.operators.indexOf(operator) + "}" + expression.substring(match_end);
                    //System.out.println("Expression B ; " + expression);
                    newTestedExpression = newTestedExpression.replaceFirst("\\s*" + Pattern.quote(operator.symbol) + "\\s*", "#{" + ScriptManager.operators.indexOf(operator) + "}");

                    //System.out.println(Pattern.quote(operator.symbol) + " expression: " + expression);
                    //System.out.println(operators.size());
                    operators.add(operator);
                    //System.out.println("Now: "+expression);
                }
            }
            if (newTestedExpression.equals(testedExpression)){
                throw new ScriptException.ScriptUnknownExpressionException(line);
            }
            testedExpression = newTestedExpression;
        }
        //System.out.println("c : " + expression);

        //Replacing text between []'s
        //System.out.println("Not replaced : "+expression);
        //System.out.println("Pattern : "+pattern_L.pattern());
        //System.out.println("Saved are : "+saved);
        while ((matcher = pattern_L.matcher(expression)).find()) {
            //System.out.println("Found a group : "+matcher.group());
            expression = expression.replaceFirst(Pattern.quote(matcher.group()), saved.get(Integer.parseInt(matcher.group(2))));
            //System.out.println("Transformed is : "+expression);
        }
        //System.out.println("Built : " + expression);
        return expression;
    }

    public static ScriptExpression parse(ScriptToken expressionToken, ScriptCompilationContext compilationContext) throws ScriptException {
        return parse(expressionToken, compilationContext, new Class[]{ScriptElement.class});
    }

    public static ScriptExpression parse(ScriptToken expressionToken, ScriptCompilationContext compilationContext, Class[] requiredType) throws ScriptException {
        Node node = parseExpressionTree(expressionToken, compilationContext, requiredType);
        //System.out.println("Result : "+node);
        if (node != null) {
            //System.out.println("Parsed " + expressionToken + " as node : " + node);
            if (node.getChildren() == null || node.getChildren().length == 0) {
                return ((NodeExpression) node).getExpression();
            }
            ExprCompiledExpression compiledExpression = new ExprCompiledExpression(node);
            return compiledExpression;
        } else {
            throw new ScriptException.ScriptUnknownExpressionException(expressionToken);
        }
    }


    public static Node parseExpressionTree(ScriptToken scriptToken, ScriptCompilationContext compilationContext, Class[] requiredType) throws ScriptException {
        ScriptExpressionParser parser = new ScriptExpressionParser();
        scriptToken.trim();
        return parser.parse(scriptToken, compilationContext, requiredType);
    }


    /**
     * Splits a string at each coma, according to the parentheses.
     * Example : "cos(5)+(4-max(3,4)), 18" -> ["cos(5)+(4-max(3,4))","18"]
     *
     * @param expressionString The string to split.
     * @return an array of the given expressionString split at each coma.
     */
    public static String[] splitAtComa(String expressionString) {
        //System.out.println("Splitting at coma for : "+expressionString);
        expressionString = trim(expressionString);
        List<String> splits = new ArrayList<>();
        int c = 0;
        int ps = 0;
        StringBuilder current = new StringBuilder();
        while (c < expressionString.length()) {
            if (expressionString.charAt(c) == ')' || expressionString.charAt(c) == ']') {
                ps--;
            }
            if (expressionString.charAt(c) == '(' || expressionString.charAt(c) == '[') {
                ps++;
            }
            if (ps > 0) {
                current.append(expressionString.charAt(c));
                c++;
                continue;
            }
            if (expressionString.charAt(c) == ',') {
                String r = current.toString();
                if (!r.isEmpty()) {
                    splits.add(r); //We add the current built word
                }
                current = new StringBuilder(); //We start a new word
                c++; //We go forward and skip the comma
                continue;
            }
            current.append(expressionString.charAt(c));
            c++;
        }
        String r = current.toString();
        if (!r.isEmpty()) {
            splits.add(r); //We add the current built word
        }
        return splits.toArray(new String[0]);
    }

    /**
     * Removes useless whitespaces and parenthesis from a string.
     *
     * @param string The string to trim.
     * @return A trimmed string from the given string parameter.
     */
    public static String trim(String string) {
        //System.out.println("Removing outside parenthesis : "+string);
        if (string == null)
            return null;
        string = string.trim();
        int c = 0;
        boolean flat = false;
        int p = 0;
        //System.out.println("String char 0 : "+string.charAt(0));
        if (string == null || string.isEmpty() || string.charAt(0) != '(')
            return string;
        while (c < string.length()) {
            if (string.charAt(c) == '(')
                p++;
            else if (string.charAt(c) == ')')
                p--;
            if (p == 0 && c != 0 && c != string.length() - 1) {
                flat = true;
            }
            c++;
        }
        if (!flat) {
            //System.out.println("Returning for r.o.p of "+string.substring(1, string.length() - 1));
            return trim(string.substring(1, string.length() - 1));
        } else {
            //System.out.println("Returning for "+string);
            return string;
        }
    }

    public static String[] splitAtDoubleDot(String p) {
        List<String> parts = new ArrayList<>();
        int c = p.length() - 1;
        StringBuilder r = new StringBuilder();
        while (c >= 0 && p.charAt(c) != ':') {
            r.insert(0, p.charAt(c));
            c--;
        }
        if (c > 0) parts.add(p.substring(0, c));
        parts.add(r.toString());
        return parts.toArray(new String[0]);
    }

    /**
     * Checks if a token represents a variable.
     * Example : ${variable} -> true
     * Example : cos(15) -> false
     *
     * @param token The token to check.
     * @return true if the given token represents a variable.
     */
    public static boolean checkIfVariable(ScriptToken token) {
        int currentCharIndex = 0;
        int parenthesesDeepness = 0;
        while (currentCharIndex < token.getText().length()) {
            if (token.getText().charAt(currentCharIndex) == '$') {
                currentCharIndex++;
                continue;
            }
            if (token.getText().charAt(currentCharIndex) == '{')
                parenthesesDeepness++;
            else if (token.getText().charAt(currentCharIndex) == '}')
                parenthesesDeepness--;
            else if (parenthesesDeepness <= 0)
                return false;
            currentCharIndex++;
        }
        return true;
    }

    /**
     * Compiles a raw string into an expression.
     * Example : "print "%5+5%" -> print "10"
     *
     * @param line         The line to compile
     * @param compileGroup A ScriptCompileGroup holding information of the compilation context.
     * @return A ParseResult.
     * @throws ScriptException if the line misses parentheses or if a parsing error occurred.
     */
    public static Node compileString(ScriptToken line, ScriptCompilationContext compileGroup) throws ScriptException {
        String string = line.getText();
        int c = 0;
        StringBuilder finalString = new StringBuilder();
        List<Node> nodes = new ArrayList<>();
        while (c < string.length()) {
            if (string.charAt(c) == '%' && (c == 0 || string.charAt(c - 1) != '\\')) {
                int start = c;
                c++;
                while (c < string.length() && string.charAt(c) != '%' && (c == 0 || string.charAt(c - 1) != '\\')) {
                    c++;
                }
                if (c >= string.length())
                    return new NodeExpression(new ExprPrimitive(new TypeString(line.getText())));
                //System.out.println("Start : "+start+" End : "+c +" '"+string.substring(start+1,c)+"'");
                if (finalString.length() > 0) {
                    nodes.add(new NodeOperation(ScriptOperator.ADD));
                }
                //System.out.println("Finalstring : "+finalString.toString());
                nodes.add(new NodeExpression(new ExprPrimitive(new TypeString(string.substring(0, start)))));
                nodes.add(new NodeOperation(ScriptOperator.ADD));
                nodes.add(parseExpressionTree(line.with(string.substring(start + 1, c)), compileGroup, new Class[]{ScriptElement.class}));
                if (c + 1 > string.length())
                    throw new ScriptException.ScriptMissingClosingTokenException(line);
                string = string.substring(c + 1);
                //System.out.println("New string : "+string);
            }
            c++;
        }

        ExprPrimitive reference = new ExprPrimitive(new TypeString(string.replaceAll("\\\\%", "%")));
        reference.setLine(line);
        Node result;
        if (!nodes.isEmpty()) {
            nodes.add(new NodeOperation(ScriptOperator.ADD));
            nodes.add(new NodeExpression(reference));
            //System.out.println("Nodes : "+nodes);
            //System.out.println("RPN : "+ExprCompiledExpression.infixToRPN(new ArrayList<>(nodes)));
            result = ExprCompiledExpression.rpnToAST(ExprCompiledExpression.infixToRPN(nodes));
        } else {
            result = new NodeExpression(reference);
        }
        return result;


    }

    /**
     * Compiles a variable name into an expression.
     * Example : "{<player's name>.test}" -> ExprPlayers + ExprPrimitive(".test")
     *
     * @param line         The line to compile
     * @param compileGroup A ScriptCompileGroup holding information of the compilation context.
     * @return A ParseResult.
     * @throws ScriptException if the line misses parentheses or if a parsing error occurred.
     */
    public static ScriptExpression compileVariableName(ScriptToken line, ScriptCompilationContext compileGroup) throws ScriptException {
        String string = line.getText();
        int c = 0;
        StringBuilder finalString = new StringBuilder();
        List<Node> nodes = new ArrayList<>();
        //System.out.println("Compiling : "+string);
        while (c < string.length()) {
            if (string.charAt(c) == '<') {
                int start = c;
                c++;
                while (c < string.length() && string.charAt(c) != '>') {
                    c++;
                }
                //System.out.println("Start : "+start+" End : "+c +" '"+string.substring(start+1,c)+"'");
                if (finalString.length() > 0) {
                    nodes.add(new NodeOperation(ScriptOperator.ADD));
                }
                //System.out.println("Finalstring : "+finalString.toString());
                nodes.add(new NodeExpression(new ExprPrimitive(new TypeString(string.substring(0, start)))));

                nodes.add(new NodeOperation(ScriptOperator.ADD));
                //System.out.println("Parsing for compiling : "+line.with(string.substring(start + 1, c)));
                try {
                    nodes.addAll(ExprCompiledExpression.astToInfix(parseExpressionTree(line.with(string.substring(start + 1, c)), compileGroup, new Class[]{ScriptElement.class})));
                } catch (Exception e) {
                    throw new ScriptException.ScriptUnknownTokenException(line);
                }
                if (c + 1 > string.length())
                    throw new ScriptException.ScriptMissingClosingTokenException(line);
                string = string.substring(c + 1);
                //System.out.println("New string : "+string);
            }
            c++;
        }
        if (nodes.size() > 1 && !string.isEmpty()) {
            nodes.add(new NodeOperation(ScriptOperator.ADD));
        }
        ExprPrimitive reference = new ExprPrimitive(new TypeString(string));
        reference.setLine(line);
        nodes.add(new NodeExpression(reference));
        //System.out.println("Nodes : "+nodes);
        //System.out.println("Infox : "+((ExprCompiledExpression.infixToRPN(new ArrayList<>(nodes)))));
        Node result = ExprCompiledExpression.rpnToAST(ExprCompiledExpression.infixToRPN(nodes));
        //System.out.println("Result "+result);
        return new ExprCompiledExpression(result);
    }

    /**
     * Splits a transformed string at each operator.
     * Example :
     * "5 #{0} 3.14" -> OperatorSplitResult(["5","3.14"],[0])
     *
     * @param transformedString The transformed string to split.
     * @return An OperatorSplitResult holding an array of operands, and an array of operators indices.
     */
    public static ExpressionToken[] splitAtOperators(String transformedString) {
        //System.out.println("Splitting : " + transformedString);
        List<ExpressionToken> tokens = new ArrayList<>();
        int currentCharIndex = 0;
        StringBuilder current = new StringBuilder();
        int depth = 0;
        while (currentCharIndex < transformedString.length()) {
            if (transformedString.charAt(currentCharIndex) == '"') {
                while (currentCharIndex < transformedString.length() && transformedString.charAt(currentCharIndex) != '"') {
                    currentCharIndex++;
                }
            }
            if (transformedString.charAt(currentCharIndex) == ')') {
                if(depth == 1){
                    String currentString = current.toString();
                    //System.out.println("1 Current for "+transformedString+" : "+currentCharIndex+" '"+transformedString.charAt(currentCharIndex)+"'");
                    if (!currentString.trim().isEmpty()) {
                        //System.out.println("Adding token expression : " + currentString);
                        tokens.add(new ExpressionToken(EnumTokenType.EXPRESSION, currentString)); //We add the current built word
                    }
                    current = new StringBuilder(); //We start a new word
                    //System.out.println("Adding token right parenthesis");
                    tokens.add(new ExpressionToken(EnumTokenType.RIGHT_PARENTHESIS, ""));
                }else {
                    current.append(transformedString.charAt(currentCharIndex));
                    depth--;
                }
                currentCharIndex++;
            } else if (transformedString.charAt(currentCharIndex) == '(') {
                if(depth == 0){
                    //System.out.println("2 Current for "+transformedString+" : "+currentCharIndex+" '"+transformedString.charAt(currentCharIndex)+"'");
                    String currentString = current.toString();
                    if (!currentString.trim().isEmpty()) {
                        //System.out.println("Adding token expression : " + currentString);
                        tokens.add(new ExpressionToken(EnumTokenType.EXPRESSION, currentString)); //We add the current built word
                    }
                    current = new StringBuilder(); //We start a new word
                    //System.out.println("Adding token left parenthesis");
                    tokens.add(new ExpressionToken(EnumTokenType.LEFT_PARENTHESIS, ""));
                }else{
                    current.append(transformedString.charAt(currentCharIndex));
                }
                depth++;
                currentCharIndex++;
            } else if (transformedString.charAt(currentCharIndex) == '#' && currentCharIndex + 1 < transformedString.length() && transformedString.charAt(currentCharIndex + 1) == '{' && (currentCharIndex == 0 || transformedString.charAt(currentCharIndex - 1) != '\\')) {
                String r = current.toString();
                if (!r.trim().isEmpty()) {
                    //System.out.println("Adding token expression : " + r);
                    tokens.add(new ExpressionToken(EnumTokenType.EXPRESSION, r)); //We add the current built word
                }
                current = new StringBuilder(); //We start a new word
                currentCharIndex++; //First {
                currentCharIndex++; //First number
                StringBuilder number = new StringBuilder();
                //System.out.println("3 Current for "+transformedString+" : "+currentCharIndex+" '"+transformedString.charAt(currentCharIndex)+"'");

                while (transformedString.charAt(currentCharIndex) >= '0' && transformedString.charAt(currentCharIndex) <= '9') {
                    //System.out.println("Added : "+transformedString.charAt(currentCharIndex));
                    number.append(transformedString.charAt(currentCharIndex));
                    currentCharIndex++;
                }

                tokens.add(new ExpressionToken(ScriptManager.operators.get(Integer.parseInt(number.toString())))); //We add the current built word
                currentCharIndex++; //Last }
            } else {
                current.append(transformedString.charAt(currentCharIndex));
                //System.out.println("currentCharIndex : "+currentCharIndex+" : "+transformedString.charAt(currentCharIndex));
                currentCharIndex++;
            }

        }
        String r = current.toString();
        if (!r.trim().isEmpty()) {
            //System.out.println("Adding token expression : " + r);
            tokens.add(new ExpressionToken(EnumTokenType.EXPRESSION, r)); //We add the current built word}

        }
        //System.out.println("Operator split : " + tokens);
        return tokens.toArray(new ExpressionToken[0]);
    }


    /**
     * Returns the class of the type associated to the given String.
     *
     * @param type The type's name registered in its @Type annotation.
     * @return The class of the type.
     */
    public static Class<? extends ScriptElement<?>> parseType(String type) {
        //System.out.println("Getting type for : "+type);
        for (TypeDefinition typeDefinition : ScriptManager.types.values()) {
            if (typeDefinition.getName().equals(type)) return typeDefinition.getTypeClass();
        }
        //Si c'est pas un Type, c'est peut être un primitive
        for (TypeDefinition primitiveDefinition : ScriptManager.primitives.values()) {
            if (primitiveDefinition.getName().equals(type)) return primitiveDefinition.getTypeClass();
        }
        return null;
    }

    /**
     * Returns the action associated to a ScriptLine according to the given ScriptCompileGroup.
     *
     * @param line         The line of the action.
     * @param compileGroup A ScriptCompileGroup holding information of the compilation context.
     * @return the parsed ScriptAction
     * @throws Exception if no action was parsed.
     */
    public static ScriptAction parseAction(ScriptToken line, ScriptCompilationContext compileGroup) throws Exception {
        line = line.with(line.getText().replaceFirst("\\s*", ""));
        //System.out.println();
        //System.out.println("Parsing action for : "+line);
        //Removing strings from the line in order to avoid interpretation issues
        String[] strings = extractStrings(line.getText());
        String lineWithoutStrings = removeStrings(line.getText(), strings);
        int currentPriority = 0;
        ScriptAction action = null;
        for (ActionDefinition actionDefinition : ScriptManager.actions) {
            //System.out.println("Checking for action : "+actionDefinition.getActionClass().getSimpleName());
            int[] indexAndMarks = actionDefinition.getMatchedPatternIndexAndMarks(lineWithoutStrings);
            if (indexAndMarks.length > 0) {
                int index = indexAndMarks[0];
                int marks = indexAndMarks[1];
                //System.out.println(index+" "+Integer.toBinaryString(marks));
                String lineWithStrings = ScriptDecoder.replaceStrings(lineWithoutStrings, strings);
                List<String> parameters = new ArrayList<>(Arrays.asList(actionDefinition.transformedPatterns[index].getAllArguments(lineWithStrings)));
                //System.out.println("Matched action : "+actionDefinition.transformedPatterns[index].getPattern()+" "+actionDefinition.getPriority());
                //System.out.println("Parameters are : "+parameters);
                if (action == null || actionDefinition.getPriority()>=currentPriority){
                    action = actionDefinition.getActionClass().getConstructor().newInstance();
                    action.build(line.with(lineWithStrings), compileGroup, parameters, index, marks);
                    currentPriority = actionDefinition.getPriority();
                }
            }
        }
        if (action != null)
            return action;

        ScriptExpression s = parse(line.with(ScriptDecoder.replaceStrings(lineWithoutStrings, strings)), compileGroup);
        if (s != null)
            return new ActSimpleExpression(s);
        return null;
    }

    /**
     * Parses a group of lines into an interpretable script.
     *
     * @param parent       The parent IScript of this group of lines. Might be null.
     * @param lines        The set of lines to parse.
     * @param compileGroup A ScriptCompileGroup holding information of the compilation context.
     * @return The final IScript.
     * @throws Exception if an error was thrown during parse.
     */
    public static IScript group(@Nullable IScript parent, List<ScriptToken> lines, ScriptCompilationContext compileGroup) throws Exception {
        int c = 0;//premiere ligne du container
        IScript previousAddedScript = null;
        IScript first = null;
        while (c < lines.size()) {
            ScriptToken line = lines.get(c);
            line.setText(getUncommentedPart(line.getText()));
            if (line.getText().isEmpty() || line.getText().matches("^\\s*$")) {//It's a comment
                c++;
                continue;
            }
            int tabLevel = ScriptDecoder.getTabLevel(line.getText());
            IScript script = ScriptDecoder.parseLine(line, compileGroup);
            if (script == null) {
                throw new ScriptException.ScriptUnknownTokenException(line);
            }
            if (first == null)
                first = script;
            script.setLine(line);
            script.setParent(parent);
            if (script instanceof ScriptLoop) {
                if (script instanceof ScriptLoop.ScriptLoopIF) {
                    if (c + 1 < lines.size() && ScriptDecoder.getTabLevel(lines.get(c + 1).getText()) == tabLevel)
                        throw new ScriptException.ScriptIndentationErrorException(lines.get(c + 1));
                    List<ScriptToken> ifContainer = new ArrayList<>();
                    do {
                        c++;
                        ifContainer.add(lines.get(c));
                    }
                    while (c + 1 < lines.size() && ScriptDecoder.getTabLevel((lines.get(c + 1).getText())) > tabLevel);

                    ((ScriptLoop.ScriptLoopIF) (script)).wrap(group(script, ifContainer, compileGroup));

                    if (previousAddedScript != null && (script instanceof ScriptLoop.ScriptLoopELSE || script instanceof ScriptLoop.ScriptLoopELSEIF)) {
                        if (previousAddedScript instanceof ScriptLoop.ScriptLoopIF) {
                            ((ScriptLoop.ScriptLoopIF) previousAddedScript).setElseContainer((ScriptLoop.ScriptLoopIF) script);
                            script.parent = previousAddedScript;
                        } else {
                            throw new ScriptException.ScriptSyntaxException(line, "else statement not following an if statement");
                        }
                    }
                } else {//Other control structures
                    //System.out.println(c+" "+lines.get(c+1).text+" "+tabLevel+" | "+lines.size());
                    if (c + 1 < lines.size() && ScriptDecoder.getTabLevel(lines.get(c + 1).getText()) == tabLevel)
                        throw new ScriptException.ScriptIndentationErrorException(lines.get(c + 1));

                    List<ScriptToken> forContainer = new ArrayList<>();
                    try {
                        do {
                            c++;
                            forContainer.add(lines.get(c));
                        }
                        while (c + 1 < lines.size() && ScriptDecoder.getTabLevel((lines.get(c + 1).getText())) > tabLevel);
                    } catch (IndexOutOfBoundsException e) {
                        throw new ScriptException.ScriptEmptyLoopException(line);
                    }
                    IScript grouped = group(script, forContainer, compileGroup);
                    grouped.setParent(script);
                    grouped.setLine(forContainer.get(0));
                    ((ScriptLoop) (script)).wrap(grouped);
                }

            }
            c++;
            if (script instanceof ScriptLoop.ScriptLoopELSE || script instanceof ScriptLoop.ScriptLoopELSEIF)
                continue;
            if (previousAddedScript != null) previousAddedScript.next = script;
            previousAddedScript = script;

        }
        return first;
    }

    public static String getUncommentedPart(String text) {
        int c = 0;
        boolean care = true;
        boolean escaping = false;
        StringBuilder result = new StringBuilder();
        int escape_reminder = -1;
        while (c < text.length()) {
            if (escape_reminder == c) escaping = false;
            char t = text.charAt(c);
            if (t == '#' && care) {
                return result.toString();
            } else if (t == '\\') {
                escaping = true;
                escape_reminder = c + 2;
            } else if (t == '\"') {
                if (!escaping) {
                    care = !care;
                }
            }
            result.append(t);
            c++;
        }
        return result.toString();
    }


    private static void printStringWithCharIndicator(String string, int charIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charIndex; i++) {
            sb.append("-");
        }
        sb.append("^");
        //System.out.println("\n"+string+"\n"+sb.toString());
    }

    public static TransformedPattern transformPattern(String pattern) throws Exception {
        //System.out.println("Transforming : "+pattern);
        //Saved reference to the base pattern
        String basePattern = pattern;
        int markCount = 0;
        //Checking if greedy
        boolean greedy = isPatternGreedy(pattern);

        pattern = SimpleRegex.simplePatternToRegex(pattern,false);
        Matcher m = pattern_mark.matcher(pattern);
        while (m.find()) {
            markCount = Math.max(markCount, Integer.parseInt(m.group(1)));
        }
        //System.out.println("Basic translation : "+pattern);

        //End of basic translation to regex

        //Now we care about {type} catches

        List<ScriptParameterDefinition[]> paramTypes = new ArrayList<>();
        int argCount = 0;
        Pattern p = Pattern.compile("\\{(.*?)}");
        m = p.matcher(pattern);
        m:
        while (m.find()) {
            //System.out.println("Found group 1 as : " + m.group(1));
            String group = m.group(1);
            String temp = group;
            String exp_capture = CAPTURE_EXPRESSION_GREEDY;
            if (((group.charAt(0) == '+') && group.charAt(0) != '!') || shouldBeLazy(pattern, m.end(1))) {
                exp_capture = CAPTURE_EXPRESSION_LAZY;
            }
            if (group.charAt(0) == '+' || group.charAt(0) == '!')
                temp = group.substring(1);

            boolean n_args = false;
            if (temp.endsWith("*")) {
                n_args = true;
                temp = temp.substring(0, temp.length() - 1);
                exp_capture = CAPTURE_EXPRESSION_OR_NONE;

            }
            List<ScriptParameterDefinition> parameterDefinitions = new ArrayList<>();
            //System.out.println("Splitting : "+temp);
            String[] subTypes = temp.split("\\|");
            l:
            for (int j = 0; j < subTypes.length; j++) {
                //System.out.println("Now : "+pattern);
                for (TypeDefinition typeDefinition : ScriptManager.types.values()) {
                    if (typeDefinition.getName().equals(subTypes[j])) {
                        if (j == 0)
                            pattern = pattern.replaceFirst("\\{" + Pattern.quote(group) + "}", "(?<a" + argCount + ">" + exp_capture + ")");
                        parameterDefinitions.add(new ScriptParameterDefinition(typeDefinition.getTypeClass(), n_args));
                        continue l;
                    }
                }
                for (TypeDefinition primitiveDefinition : ScriptManager.primitives.values()) {
                    if (primitiveDefinition.getName().equals(subTypes[j])) {
                        if (j == 0)
                            pattern = pattern.replaceFirst("\\{" + Pattern.quote(group) + "}", "(?<a" + argCount + ">" + exp_capture + ")");
                        parameterDefinitions.add(new ScriptParameterDefinition(primitiveDefinition.getTypeClass(), n_args));
                        continue l;
                    }
                }
                throw new Exception(temp + " type is either not registered or not recognized in " + basePattern);
            }
            argCount++;
            //System.out.println("argCount = "+argCount);
            paramTypes.add(parameterDefinitions.toArray(new ScriptParameterDefinition[0]));
        }
        pattern = "^\\s*" + pattern + "$";//End of parsing;
        //System.out.println("Transformed : "+pattern+" with :"+markCount+" marks\n");
        TransformedPattern result = new TransformedPattern(pattern, markCount, argCount, paramTypes.toArray(new ScriptParameterDefinition[0][0]));
        result.setGreedy(greedy);
        return result;
    }

    private static boolean isPatternGreedy(String pattern) {
        pattern = removeDelimiters('[', ']', pattern);
        pattern = pattern.trim();
        if (pattern.isEmpty())
            return false;
        else {
            pattern = removeDelimiters('{', '}', pattern);
            pattern = pattern.trim();
            return pattern.isEmpty();
        }
    }

    private static String removeDelimiters(char start, char end, String pattern) {
        //System.out.println("Removing delimiters : "+start+" -> "+end+" in : "+pattern);
        int i = 0;
        while (i < pattern.length()) {
            char c = pattern.charAt(i);
            if (c == start) {
                int j = i + 1;
                while (j < pattern.length()) {
                    char e = pattern.charAt(j);
                    if (e == end) {
                        String s_start = pattern.substring(0, i);
                        String s_end = pattern.substring(j + 1);
                        //System.out.println("Stop at : "+i+" ->"+ j +" Start : "+s_start + " End : "+s_end);
                        pattern = s_start + s_end;
                        break;
                    }
                    j++;
                }
            }
            i++;
        }
        //System.out.println("Result : "+pattern);
        return pattern;
    }

    private static String emptyDelimiters(char startToken, char endToken, String pattern) {
        //System.out.println("Removing delimiters : "+startToken+" -> "+endToken+" in : "+pattern);
        int charIndex = 0;
        int depth = 0;
        while (charIndex < pattern.length()) {
            char currentChar = pattern.charAt(charIndex);
            if (currentChar == startToken) {
                int farCharIndex = charIndex + 1;
                while (farCharIndex < pattern.length()) {
                    char farChar = pattern.charAt(farCharIndex);
                    if (farChar == startToken){
                        depth++;
                    }
                    if (farChar == endToken) {
                        if(depth == 0){
                            String s_start = pattern.substring(0, charIndex + 1);
                            String s_end = pattern.substring(farCharIndex);
                            //System.out.println("Stop at : "+charIndex+" ->"+ farCharIndex +" Start : "+s_start + " End : "+s_end);
                            pattern = s_start + s_end;
                            break;
                        }
                        if(depth > 0)
                            depth--;

                    }
                    farCharIndex++;
                }
            }
            charIndex++;
        }
        //System.out.println("Result : "+pattern);
        return pattern;
    }

    public static boolean shouldBeLazy(String p, int s) {
        //System.out.println("Should be lazy : "+p+" at "+s);
        while (s < p.length()) {
            if ("[(])".contains("" + p.charAt(s)) && (s == 0 || p.charAt(s - 1) != '\\'))
                return true;
            s++;
        }
        //System.out.println("False");
        return false;
    }

    public static String getNameOfType(Class type) {
        if (type.isAnnotationPresent(Type.class)) {
            return ((Type) (type.getAnnotation(Type.class))).name();
        } else if (type.isAnnotationPresent(Primitive.class)) {
            return ((Primitive) (type.getAnnotation(Primitive.class))).name();
        }
        return null;
    }

    public static PrimitiveType parsePrimitive(ScriptToken parameter) throws ScriptException {
        //System.out.println("Getting primitive for "+parameter.getText());
        if (parameter.getText().isEmpty())
            return null;
        for (TypeDefinition primitiveDefinition : ScriptManager.primitives.values()) {
            //System.out.println("Checking primitive, checking if "+primitiveDefinition.getName()+" with regex "+primitiveDefinition.transformedPattern.getPattern()+" is matched by "+parameter);
            if ((primitiveDefinition.matchedPattern(parameter.getText()))) {
                Pattern p = primitiveDefinition.transformedPattern.getPattern();
                Matcher m = p.matcher(parameter.getText());
                if (m.find() && m.groupCount() > 0) {
                    String g = m.group(1);
                    //System.out.println("First group is "+g);
                    Class<?> c = primitiveDefinition.getTypeClass();
                    try {
                        return (PrimitiveType<?>) c.getDeclaredConstructor(String.class).newInstance(g);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    public static ScriptWrapper parseLoop(ScriptToken line, ScriptCompilationContext compileGroup) throws Exception {
        for (LoopDefinition loopDefinition : ScriptManager.loops) {
            if (loopDefinition.matches(line.getText().trim())) {
                ScriptLoop loop = SqriptUtils.rawInstantiation(ScriptLoop.class, loopDefinition.getLoopClass());
                loop.build(line, compileGroup);
                return loop;
            }
        }
        return null;
    }


    public static BlockDefinition findBlockDefinition(ScriptToken head) {
        for (BlockDefinition d : ScriptManager.blocks) {
            Matcher m = d.getRegex().matcher(head.getText());
            if (m.matches())
                return d;
        }
        return null;
    }

    public static int getTabLevel(String line) {
        int i = 0;//tab number
        int r = 0;//char index
        while (r < line.length() && (line.charAt(r) == '\t' || line.charAt(r) == ' ')) {
            if (line.charAt(r) == '\t') i += 4;
            if (line.charAt(r) == ' ') i++;
            r++;
        }
        return i / 4;
    }

}
