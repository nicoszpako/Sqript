package fr.nico.sqript.compiling;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.actions.ActSimpleExpression;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptBlockFunction;
import fr.nico.sqript.blocks.ScriptFunctionalBlock;
import fr.nico.sqript.function.ScriptNativeFunction;
import fr.nico.sqript.structures.*;
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
    public static final String CAPTURE_EXPRESSION_LAZY = "(?:.*?)";
    public static final String CAPTURE_EXPRESSION_GREEDY = "(?:.*)";

    public static List<String> operators_list = new LinkedList<>();
    public static List<Pattern> operators_pattern = new LinkedList<>();
    public static List<IParser> parsers = new ArrayList<>();
    static Pattern pattern_function = Pattern.compile("^\\s*(\\w*)\\((.*)\\)\\s*$");
    static Pattern pattern_removed_string = Pattern.compile("\\{S(\\d*)}");
    static Pattern pattern_capture_quotes = Pattern.compile("(" + CAPTURE_BETWEEN_QUOTES + ")");
    static Pattern pattern_variable = Pattern.compile("^(?:\\$)?\\{(.*)}$");
    static Pattern pattern_compiled_string = Pattern.compile("@\\{S(\\d*)}");
    static Pattern pattern_L = Pattern.compile("(?<!\\\\)(L\\{\\d+})");

    public static void init() {

        parsers.add((token, group) -> {
            //Check if it is a variable
            if (checkIfVariable(token)) {
                ScriptExpression expression;
                if (token.getText().contains("%"))
                    expression = compileString(token, group).getExpression();
                else expression = new ExprPrimitive(new TypeString(token.getText()));
                ExprReference r = new ExprReference(expression);
                r.setLine(token);
                return new ParseResult(r, new String[0]);
            }
            return null;
        });


        parsers.add((token, group) -> {
            //Check if it is a function
            ExprNativeFunction f;
            Matcher m = pattern_function.matcher(token.getText());
            if (m.find()) {
                //System.out.println("Found for a function : "+m.group(1)+" with "+m.group(2)+" for "+parameter);
                //Priority to not native functions
                ScriptFunctionalBlock sf;
                if ((sf = token.getScriptInstance().getFunction(m.group(1))) != null) {
                    return new ParseResult(new ExprFunction(sf), splitAtComa(m.group(2)));
                }
                if ((f = parseNativeFunction(token.with(m.group(1)))) != null) {
                    return new ParseResult(f, splitAtComa(m.group(2)));
                }
            }
            return null;
        });

        parsers.add((line, group) -> {
            //Check for replaced string
            Matcher m = pattern_capture_quotes.matcher(line.getText());
            if (m.matches()) {
                //System.out.println("It matches");
                if (!m.group(1).contains("<") && !m.group(1).contains("%"))
                    return new ParseResult(new ExprPrimitive(new TypeString(m.group(2))), new String[0]);
                else {
                    return compileString(line.with(m.group(2)), group);
                }
            }
            return null;
        });


        parsers.add((line, group) -> {
            //Check if it is a primitive
            PrimitiveType primitive = parsePrimitive(line);
            //System.out.println("Primitive is null : "+(primitive==null));
            if (primitive != null) {
                //System.out.println("Found a primitive ! It's a : "+primitive.getClass());
                return new ParseResult(new ExprPrimitive(primitive), new String[0]);
            }
            return null;
        });

        parsers.add((line, group) -> {
            //Check if it is a reference to an accessor or an other element that can be parsed
            Integer h = group.getHashFor(line.getText());
            if (h != null) {
                Class<? extends ScriptElement<?>> returnType = parseType("element");
                if (returnType == null)
                    throw new ScriptException.ScriptUndefinedReferenceException(line);
                else {
                    //System.out.println("Returning reference for : "+parameter);
                    ExprReference s = new ExprReference(new ExprPrimitive(new TypeString(line.getText())));
                    s.setLine((ScriptToken) line.clone());
                    s.setVarHash(h);
                    return new ParseResult(s, new String[0]);
                }
            }
            return null;
        });


        parsers.add((line, group) -> {
            //Check if it is an expression
            ExpressionDefinition def = null;
            int[] index = null;
            for (ExpressionDefinition expressionDefinition : ScriptManager.expressions) {
                //Sorting expressions to find the latest one, using the "score" got in "getMatchedPatternIndexAndScore"
                int[] t = expressionDefinition.getMatchedPatternIndexAndPosition(line.getText());
                //If we found an expression that is nearer to the end of the sentence or an expression that don't take any parameters
                if ((t != null && (index == null || t[1] > index[1] || !expressionDefinition.getFeatures()[t[0]].pattern().contains("{")))) {
                    index = t;
                    def = expressionDefinition;
                }
            }
            if (def != null && index.length > 0)
                try {
                    //System.out.println("Returning : " + def.getExpressionClass() + ":" + Arrays.toString(index) + " " + def.transformedPatterns[index[0]].getPattern());
                    Class<? extends ScriptExpression> expression = def.getExpressionClass();
                    ScriptExpression exp = expression.getConstructor().newInstance();
                    exp.setMatchedIndex(index[0]);
                    exp.setMarks(index[2]);
                    exp.setLine((ScriptToken) line.clone());
                    List<String> parameters = new ArrayList<>();
                    TransformedPattern transformedPattern = def.transformedPatterns[index[0]];
                    Collections.addAll(parameters, transformedPattern.getAllArguments(line.getText()));
                    return new ParseResult(exp, parameters.toArray(new String[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
        });


        parsers.add((line, group) -> {
            //Check if it is a not-native function reference
            //System.out.println("Checking if it's a function");
            if (line.getScriptInstance() != null)
                for (ScriptBlock fc : line.getScriptInstance().getBlocksOfClass(ScriptBlockFunction.class)) {
                    //System.out.println("Checking for : "+fc.getClass().getSimpleName());
                    ScriptBlockFunction function = (ScriptBlockFunction) fc;
                    if (function.name.equals(line.getText())) {
                        //System.out.println("Returning function : "+function);
                        return new ParseResult(new ExprResult(new TypeFunction(function)), new String[0]);
                    }
                }
            return null;
        });

        parsers.add((line, group) -> {

            //Check if it is an option
            if (line.getText().startsWith("@")) {
                if (line.getScriptInstance().getOptions().containsKey(line.getText().substring(1))) {
                    return new ParseResult(line.getScriptInstance().getOptions().get(line.getText().substring(1)), new String[0]);
                }
            }
            return null;
        });

    }


    public static IScript parseLine(ScriptToken line, ScriptCompileGroup compileGroup) throws Exception {

        IScript script = parseLoop(line, compileGroup); //On rentre dans un bloc
        if (script == null)
            script = parseAction(line, compileGroup);//Si c'est pas une boucle c'est une action

        //Custom parsers
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

    public static boolean checkAllMatches(String test, List<Pattern> patterns) {
        for (Pattern p : patterns) {
            //System.out.println("Checking "+test+" with "+ s );
            Matcher m = p.matcher(test);
            if (m.find()) {
                //System.out.println("Found for "+test+" with "+s);
                return true;
            }
        }
        return false;
    }

    public static String[] extractStrings(String parameter) {
        List<String> result = new ArrayList<>();
        //System.out.println("Extracting strings for : "+parameter);

        Matcher m = pattern_capture_quotes.matcher(parameter);
        int i = 0;
        while (m.find()) {
            String f = m.group(2);
            result.add(f);
            i++;
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

        //Removing variables
        int l = 0;
        int depth = 0;
        int start = 0;
        int start_char_index = -1;
        while (l < expression.length()) {
            //System.out.println("Char at : "+l+" is a "+expression.charAt(l));
            if (start_char_index == -1 && (start_char_index = start_c.indexOf(expression.charAt(l))) != -1) {
                //System.out.println("Char at : "+l+" is a "+expression.charAt(l)+" as a : '"+start_c.charAt(start_char_index)+"'");
                if (depth == 0)
                    start = l;
                depth++;
            }
            if (start_char_index >= 0 && expression.charAt(l) == end_c.charAt(start_char_index)) {
                depth--;
                if (depth == 0) {
                    String a = expression.substring(0, start);
                    String b = expression.substring(l + 1);
                    String m = expression.substring(start, l + 1);
                    String n = "L{" + saved.size() + "}";
                    expression = a + n + b;
                    l += n.length() - m.length();
                    start_char_index = -1;
                    saved.add(m);
                }
            }
            l++;
        }
        return expression;
    }

    public static ExtractedOperatorsResult buildOperators(String expression) {
        List<String> saved = new ArrayList<>();
        List<ScriptOperator> operators = new ArrayList<>();
        //System.out.println("Building operators for : " + expression);
        //Saving variables and arrays
        expression = save(expression, "{[", "}]", saved);

        //System.out.println("a : " + expression);
        //Removing strings
        Matcher m = pattern_capture_quotes.matcher(expression);
        while (m.find()) {
            String f = m.group(1);
            expression = expression.replaceFirst(Pattern.quote(f), "L{" + saved.size() + "}");
            saved.add(f);
        }
        //System.out.println("b : " + expression);

        //Placing operators
        while (checkAllMatches(expression, operators_pattern)) {
            for (ScriptOperator s : ScriptManager.operators) {
                //String b = "", c = "(?![^(]*\\))";
                String b = "", c = "";
                if (s.word) {
                    b = "(" + (s.unary ? "" : "\\)") + "\\s+|^|})";
                    //c = "\\s\\(+" + c;
                    c = "\\s+\\(";
                }
                //System.out.println("Checking with : "+"(" + b + Pattern.quote(s.symbol) + c + ")");
                Pattern p = Pattern.compile("(" + b + Pattern.quote(s.symbol) + c + ")");
                m = p.matcher(expression);
                //System.out.println("Trying to match  : "+expression+" with regex : "+"(" + b + Pattern.quote(s.symbol) + c + ")");
                if (m.find()) {
                    if (s.unary && !s.postfixed) {//Check if this one is unary
                        boolean isUnary = false;
                        int position = m.start();
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


                    expression = expression.replaceFirst("\\s*" + Pattern.quote(s.symbol) + "\\s*", "#{" + operators.size() + "}");
                    //System.out.println(Pattern.quote(s.symbol) + " expression: " + expression);
                    //System.out.println(operators.size());
                    operators.add(s);
                    //System.out.println(expression);

                }
            }
        }
        //System.out.println("c : " + expression);

        //Replacing text between []'s
        //System.out.println("Not replaced : "+expression);
        //System.out.println("Pattern : "+pattern_L.pattern());
        m = pattern_L.matcher(expression);
        int i = 0;
        while (m.find()) {
            //System.out.println("Found a group : "+m.group());
            expression = expression.replaceFirst(Pattern.quote(m.group()), saved.get(i));
            i++;
        }
        //System.out.println("Built : " + expression);
        return new ExtractedOperatorsResult(expression, operators.toArray(new ScriptOperator[0]));
    }

    public static ScriptExpression parseExpression(ScriptToken parameter, ScriptCompileGroup compileGroup) throws ScriptException {
        //Removing strings from the line in order to avoid interpretation issues
        String[] strings = extractStrings(parameter.getText());
        String line_without_strings = removeStrings(parameter.getText(), strings);
        //System.out.println(parameter.text+" without strings is : "+line_without_strings);
        return parseExpression(parameter.with(line_without_strings), compileGroup, strings);
    }

    public static ScriptExpression parseExpression(ScriptToken line, ScriptCompileGroup compileGroup, String[] replacedStrings) throws ScriptException {
        //System.out.println("Getting global expression for : " + line + " with strings : " + Arrays.toString(replacedStrings));

        //Creating a new reference to the line because we are going to modify it a bit
        ScriptToken parameter = (ScriptToken) line.clone();

        if (parameter.getText().isEmpty()) {
            throw new ScriptException.ScriptEmptyExpressionException(line);
        }

        parameter.trim();

        List<ScriptExpression> operands_array = new ArrayList<>();
        List<ScriptOperator> operators_array = new ArrayList<>();

        final int[] depth = {0};
        StringBuilder output = new StringBuilder();
        parameter.setText(new Object() {

            final ScriptToken main = parameter;

            String getExpressions(String expression) throws ScriptException {
                depth[0]++;
                StringBuilder tabbuilder = new StringBuilder();
                for (int i = 0; i < depth[0]; i++) {
                    tabbuilder.append('\t');
                }
                String tab = tabbuilder.toString();
                output.append(tab).append("Getting local expression for : ").append(expression).append('\n');
                expression = expression.trim();
                expression = removeOutsideParenthesis(expression);
                expression = expression.replaceAll("#", "\\#")
                        .replaceAll("L", "\\L");

                ParseResult parseResult = parseLitteralExpression(main.with(expression), replacedStrings, compileGroup);

                StringBuilder finalResult = new StringBuilder();
                StringBuilder content = new StringBuilder();
                int arity = 0;

                //System.out.println("Arguments of expression are : "+ Arrays.toString(parseResult.getArguments()));
                output.append(tab).append("Arguments of expression are : ").append(Arrays.toString(parseResult.getArguments())).append('\n');

                for (String argument : parseResult.getArguments()) {

                    if (argument == null) {
                        //System.out.println("Adding null operand : "+(new StringBuilder().append("@{").append(operands_array.size()).append("/").append(0).append("}").toString()));

                        if (arity > 0)
                            content.append(',');


                        content.append("@{").append(operands_array.size()).append("/").append(0).append("}");
                        operands_array.add(null);
                        arity++;
                        continue;
                    }
                    String[] subArguments = splitAtComa(argument);
                    //System.out.println("Sub-arguments are : "+Arrays.toString(subArguments));
                    for (String subArgument : subArguments) { //Split all those arguments at commas

                        ExtractedOperatorsResult extractedOperatorsResult = buildOperators(subArgument);
                        ScriptOperator[] operators = extractedOperatorsResult.operators;
                        String builtOperators = extractedOperatorsResult.transformedString;
                        builtOperators = removeOutsideParenthesis(builtOperators);
                        Collections.addAll(operators_array, operators);
                        OperatorSplitResult operatorSplitResult = splitAtOperators(builtOperators);
                        OperatorSplitResult.Token[] operands = operatorSplitResult.getOperands();
                        //System.out.println("Operands are : "+Arrays.toString(operands));
                        //System.out.println("Operators are : "+Arrays.toString(operators));
                        output.append(tab).append("Operators are : " + Arrays.toString(operators)).append(" with indices ").append(Arrays.toString(operatorSplitResult.getOperatorIndices())).append('\n');
                        output.append(tab).append("Operands are : " + Arrays.toString(operands)).append('\n');

                        int added_operators = 0;
                        for (OperatorSplitResult.Token token : operands) { //Split all those sub-arguments at operators
                            output.append(tab).append("Next token is : " + token).append('\n');
                            if (token.type == OperatorSplitResult.EnumTokenType.LEFT_PARENTHESIS) {
                                content.append('(');
                                continue;
                            } else if (token.type == OperatorSplitResult.EnumTokenType.RIGHT_PARENTHESIS) {
                                content.append(')');
                                continue;
                            } else if (token.type == OperatorSplitResult.EnumTokenType.EXPRESSION) {
                                if (token.expression.equals(expression) && operands.length == 1 && subArguments.length == 1 && parseResult.getExpression() == null)
                                    //If no transformation has been made, it may loop infinitely because
                                    //no expression can be found.
                                    throw new ScriptException.ScriptUnknownExpressionException(parameter.with(expression));

                                String transformed = getExpressions(token.expression);
                                //System.out.println("Transformed : '"+token+"' to '"+transformed+"'");
                                output.append(tab).append("Transformed : '").append(token.expression).append("' to '").append(transformed).append("'").append('\n');

                                //System.out.println("Operators length is : "+operators.length+" with : "+Arrays.toString(operators));
                                //System.out.println("Added operators is : "+added_operators);
                                content.append(transformed);

                            } else if (token.type == OperatorSplitResult.EnumTokenType.OPERATOR) {
                                if (operators.length > 0 && added_operators < operators.length && added_operators < operatorSplitResult.getOperatorIndices().length) {
                                    //System.out.println("c");
                                    content.append("#{").append(operatorSplitResult.getOperatorIndices()[added_operators]).append('}');
                                    added_operators++;
                                }
                            }
                            //System.out.println("Content is now : "+content.toString());
                            output.append(tab).append("Content is now : " + content).append('\n');
                            output.append(tab).append("Arity is now : " + arity).append('\n');
                        }
                        content.append(',');
                        arity++;

                    }

                }
                if (content.length() > 0 && content.charAt(content.length() - 1) == ',') {
                    output.append(tab).append("Deleting last comma ! \n");

                    content.deleteCharAt(content.length() - 1); //Deletes last comma}
                }

                if (parseResult.getExpression() != null) {
                    finalResult.append("@{").append(operands_array.size()).append("/").append(arity).append("}");
                    operands_array.add(parseResult.getExpression());
                }
                if (content.length() > 0) {
                    finalResult.append('(').append(content).append(')');
                }
                //System.out.println("Returning result : "+finalResult.toString()+" for "+expression);
                output.append(tab).append("Returning result : " + finalResult.toString() + " for " + expression).append('\n');
                output.append(tab).append("Operators are : ").append(operators_array).append('\n');


                return finalResult.toString();
            }

        }.getExpressions(((ScriptToken) parameter.clone()).getText()));

        //String toPrint = output.toString();
        //System.out.println("Log for : " + parameter.text + "\n" + toPrint);
        //System.out.println("Operands size : "+operands_array.size());
        //System.out.println("Operators size : "+operators_array.size());
        //If we only have one expression we return the expression directly, as we don't have any parameters/arguments to give
        if (operands_array.size() == 1 && operators_array.isEmpty()) {
            //System.out.println("Operands is 1 returned");
            return operands_array.get(0);
        }
        //If we have multiple expressions to combine, and no errors, we returned a compiled evaluation of the expression
        else if (operators_array.size() > 0 || operands_array.size() > 1) {
            //System.out.println("Returning compiled expression : " + parameter + " " + Arrays.toString(operands_array.toArray()));
            ExprCompiledEvaluation compiledEvaluation = new ExprCompiledEvaluation(parameter, operands_array.toArray(new ScriptExpression[0]), operators_array.toArray(new ScriptOperator[0]));
            compiledEvaluation.setLine(line);
            return compiledEvaluation;
        }
        //System.out.println("Returning null");
        throw new ScriptException.ScriptUnknownExpressionException(line);
    }


    public static String removeOutsideParenthesis(String s) {
        //System.out.println("Removing outside parenthesis : "+s);
        int c = 0;
        boolean flat = false;
        int p = 0;
        if (s == null || s.isEmpty() || s.charAt(0) != '(')
            return s;
        while (c < s.length()) {
            if (s.charAt(c) == '(')
                p++;
            else if (s.charAt(c) == ')')
                p--;
            if (p == 0 && c != 0 && c != s.length() - 1) {
                flat = true;
            }
            c++;
        }
        if (!flat) {
            //System.out.println("Returning for r.o.p of "+s.substring(1, s.length() - 1));
            return removeOutsideParenthesis(s.substring(1, s.length() - 1));
        } else {
            //System.out.println("Returning for "+s);
            return s;
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

    public static boolean isLastParameterAtTheEndOfLine(String[] parameters, String line) {
        if (line != null)
            if (parameters.length > 0 && parameters[parameters.length - 1] != null)
                return line.lastIndexOf(parameters[parameters.length - 1]) + parameters[parameters.length - 1].length() == line.length();
        return false;
    }


    public static ParseResult parseLitteralExpression(ScriptToken token, String[] cachedStrings, ScriptCompileGroup compileGroup) throws ScriptException {
        //System.out.println("Getting literal expression for : " + token);
        token.trim();
        //Null parameters
        if (token.getText() == null) {
            //System.out.println("Null token");
            return null;
        }

        Matcher compiledStringMatcher = pattern_compiled_string.matcher(token.getText());
        if (compiledStringMatcher.matches()) {
            return new ParseResult(new ExprPrimitive(new TypeString(cachedStrings[Integer.parseInt(compiledStringMatcher.group(1))])), new String[0]);
        }

        ParseResult result;
        for (IParser parser : parsers) {
            if ((result = parser.parse(token, compileGroup)) != null)
                return result;
        }

        //System.out.println("Returning null litteral for : "+token);
        return new ParseResult(null, new String[]{token.getText()});
    }

    /**
     * Checks if a token represents a variable.
     * Example : ${variable} -> true
     * Example : cos(15) -> false
     * @param token The token to check.
     * @return true if the given token represents a variable.
     */
    private static boolean checkIfVariable(ScriptToken token) {
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
     * @param line The line to compile
     * @param compileGroup A ScriptCompileGroup holding information of the compilation context.
     * @return A ParseResult.
     * @throws ScriptException if the line misses parentheses or if a parsing error occurred.
     */
    private static ParseResult compileString(ScriptToken line, ScriptCompileGroup compileGroup) throws ScriptException {
        String string = line.getText();
        int c = 0;
        //System.out.println("Compiling string : "+line);
        StringBuilder finalString = new StringBuilder();
        List<ScriptExpression> operands = new ArrayList<>();
        List<ScriptOperator> operators = new ArrayList<>();
        while (c < string.length()) {
            if (string.charAt(c) == '%') {
                int start = c;
                c++;
                StringBuilder toParse = new StringBuilder();
                while (c < string.length() && string.charAt(c) != '%') {
                    toParse.append(string.charAt(c));
                    c++;
                }
                printStringWithCharIndicator(string, c);
                //System.out.println("Start : "+start+" End : "+c +" '"+string.substring(start+1,c)+"'");
                if (finalString.length() > 0) {
                    finalString.append("#{").append(operators.size()).append("}");
                    operators.add(ScriptOperator.ADD);
                }
                finalString.append("@{").append(operands.size()).append("/0}#{").append(operators.size()).append("}@{").append(operands.size() + 1).append("/0}");
                //System.out.println("Finalstring : "+finalString.toString());
                operands.add(new ExprPrimitive(new TypeString(string.substring(0, start))));
                operators.add(ScriptOperator.ADD);
                operands.add(parseExpression(line.with(string.substring(start + 1, c)), compileGroup));
                if (c + 1 > string.length())
                    throw new ScriptException.ScriptMissingClosingTokenException(line);
                string = string.substring(c + 1);
                c = 0;
                //System.out.println("New string : "+string);
            }
            c++;
        }
        if (operands.size() > 1 && !string.isEmpty()) {
            finalString.append("#{").append(operators.size()).append("}@{").append(operands.size()).append("/0}");
            operators.add(ScriptOperator.ADD);
        } else if (operands.size() == 0) {
            finalString.append("@{").append(operands.size()).append("/0}");
        }
        operands.add(new ExprPrimitive(new TypeString(string)));
        return new ParseResult(new ExprCompiledEvaluation(line.with(finalString.toString()), operands.toArray(new ScriptExpression[0]), operators.toArray(new ScriptOperator[0])), new String[0]);
    }

    /**
     * Splits a string at each coma, according to the parentheses.
     * Example : "cos(5)+(4-max(3,4)), 18" -> ["cos(5)+(4-max(3,4))","18"]
     * @param text The string to split.
     * @return an array of the given text split at each coma.
     */
    public static String[] splitAtComa(String text) {
        //System.out.println("Splitting at coma for : "+text);
        List<String> splits = new ArrayList<>();
        int c = 0;
        int ps = 0;
        StringBuilder current = new StringBuilder();
        while (c < text.length()) {
            if (text.charAt(c) == ')' || text.charAt(c) == ']') {
                ps--;
            }
            if (text.charAt(c) == '(' || text.charAt(c) == '[') {
                ps++;
            }
            if (ps > 0) {
                current.append(text.charAt(c));
                c++;
                continue;
            }
            if (text.charAt(c) == ',') {
                String r = current.toString();
                if (!r.isEmpty()) {
                    splits.add(r); //We add the current built word
                }
                current = new StringBuilder(); //We start a new word
                c++; //We go forward and skip the comma
                continue;
            }
            current.append(text.charAt(c));
            c++;
        }
        String r = current.toString();
        if (!r.isEmpty()) {
            splits.add(r); //We add the current built word
        }
        return splits.toArray(new String[0]);
    }

    /**
     * Splits a transformed string at each operator.
     * Example :
     *  "5 #{0} 3.14" -> OperatorSplitResult(["5","3.14"],[0])
     * @param transformedString The transformed string to split.
     * @return An OperatorSplitResult holding an array of operands, and an array of operators indices.
     */
    private static OperatorSplitResult splitAtOperators(String transformedString) {
        //System.out.println("Splitting : "+transformedString);
        List<OperatorSplitResult.Token> tokens = new ArrayList<>();
        List<Integer> operatorsIndices = new ArrayList<>();
        int c = 0;
        StringBuilder current = new StringBuilder();
        while (c < transformedString.length()) {
            if (transformedString.charAt(c) == ')') {
                String r = current.toString();
                //System.out.println("1 Current for "+transformedString+" : "+c+" '"+transformedString.charAt(c)+"'");
                if (!r.isEmpty()) {
                    tokens.add(new OperatorSplitResult.Token(OperatorSplitResult.EnumTokenType.EXPRESSION, r)); //We add the current built word
                }
                current = new StringBuilder(); //We start a new word
                tokens.add(new OperatorSplitResult.Token(OperatorSplitResult.EnumTokenType.RIGHT_PARENTHESIS, ""));
                c++;
            } else if (transformedString.charAt(c) == '(') {
                tokens.add(new OperatorSplitResult.Token(OperatorSplitResult.EnumTokenType.LEFT_PARENTHESIS, ""));
                c++;
            } else if (transformedString.charAt(c) == '#' && c + 1 < transformedString.length() && transformedString.charAt(c + 1) == '{' && (c == 0 || transformedString.charAt(c - 1) != '\\')) {
                String r = current.toString();
                //System.out.println("1 Current for "+transformedString+" : "+c+" '"+transformedString.charAt(c)+"'");
                if (!r.isEmpty()) {
                    tokens.add(new OperatorSplitResult.Token(OperatorSplitResult.EnumTokenType.EXPRESSION, r)); //We add the current built word
                }
                current = new StringBuilder(); //We start a new word
                c++; //First {
                c++; //First number
                String number = "";
                //System.out.println("2 Current for "+transformedString+" : "+c+" '"+transformedString.charAt(c)+"'");

                while (transformedString.charAt(c) >= '0' && transformedString.charAt(c) <= '9') {
                    //System.out.println("Added : "+transformedString.charAt(c));
                    number += transformedString.charAt(c);
                    c++;
                }
                tokens.add(new OperatorSplitResult.Token(OperatorSplitResult.EnumTokenType.OPERATOR, number)); //We add the current built word
                operatorsIndices.add(Integer.parseInt(number));
                c++; //Last }
            } else {
                current.append(transformedString.charAt(c));
                c++;
            }

        }
        String r = current.toString();
        if (!r.isEmpty())
            tokens.add(new OperatorSplitResult.Token(OperatorSplitResult.EnumTokenType.EXPRESSION, r)); //We add the current built word
        //System.out.println("Operator split : "+tokens);
        return new OperatorSplitResult(tokens.toArray(new OperatorSplitResult.Token[0]), operatorsIndices.toArray(new Integer[0]));
    }


    /**
     * Returns the class of the type associated to the given String.
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
    public static ScriptAction parseAction(ScriptToken line, ScriptCompileGroup compileGroup) throws Exception {
        line = line.with(line.getText().replaceFirst("\\s*", ""));
        //System.out.println("Getting action for : "+line.text);
        //Removing strings from the line in order to avoid interpretation issues
        String[] strings = extractStrings(line.getText());
        String lineWithoutStrings = removeStrings(line.getText(), strings);
        for (ActionDefinition actionDefinition : ScriptManager.actions) {
            //System.out.println("Checking for action : "+actionDefinition.getActionClass().getSimpleName());
            int[] indexAndMarks = actionDefinition.getMatchedPatternIndexAndMarks(lineWithoutStrings);
            if (indexAndMarks.length > 0) {
                int index = indexAndMarks[0];
                int marks = indexAndMarks[1];
                //System.out.println(index+" "+Integer.toBinaryString(marks));
                //System.out.println("AA:"+infos.transformedPatterns[index].regex+" -- "+line.text);
                String lineWithStrings = ScriptDecoder.replaceStrings(lineWithoutStrings, strings);
                List<String> parameters = new ArrayList<>(Arrays.asList(actionDefinition.transformedPatterns[index].getAllArguments(lineWithStrings)));
                //System.out.println("Parameters size : "+parameters.size());

                ScriptAction action = actionDefinition.getActionClass().getConstructor().newInstance();
                action.build(line.with(lineWithStrings), compileGroup, parameters, index, marks);
                return action;
            }
        }
        ScriptExpression s = parseExpression(line.with(lineWithoutStrings), compileGroup, strings);
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
    public static IScript group(@Nullable IScript parent, List<ScriptToken> lines, ScriptCompileGroup compileGroup) throws Exception {
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
        //pattern = pattern.replaceAll(" \\[","[ ");
        int i = 0;

        int markCount = 0;
        int argCount = 0;
        //System.out.println("Processing : "+pattern);
        while (i < pattern.length()) {
            char c = pattern.charAt(i);
            boolean comment = i > 0 && pattern.charAt(i - 1) == '~';
            if (c == ')' && !comment) {
                int j = i;
                int mark = -1;
                while (j > 0) {
                    if (pattern.charAt(j) == ';' && !(pattern.charAt(j - 1) == '~')) { //Marks
                        j--;
                        //System.out.println("found a dot-comma : "+openingToken+" "+pattern.charAt(openingToken));
                        StringBuilder number = new StringBuilder();
                        while (j > 0 && pattern.charAt(j) >= '0' && pattern.charAt(j) <= '9') {
                            //System.out.println("charAt(openingToken) is a number : "+openingToken+" "+pattern.charAt(openingToken));
                            number.insert(0, pattern.charAt(j));
                            j--;
                        }
                        mark = Integer.parseInt(number.toString());
                        markCount++;
                    }

                    if (pattern.charAt(j) == '(' && !(j > 1 && pattern.charAt(j - 1) == '~' || pattern.charAt(j + 1) == '?'))
                        break;
                    j--;
                }

                String firstPart = pattern.substring(0, j);
                //System.out.println("mark:"+mark);
                String middlePart = pattern.substring(j + (mark != -1 ? String.valueOf(mark).length() + 2 : 1), i);
                String lastPart = pattern.substring(i + 1);
                //System.out.println("\n"+firstPart+"\n"+middlePart+"\n"+lastPart);
                pattern = firstPart + "(?" + (mark != -1 ? "<m" + mark + ">" : ":") + middlePart + ")" + lastPart;
                //System.out.println(pattern+"   "+i);
                i += mark != -1 ? String.valueOf(mark).length() + 3 : 2;
            }

            i++;
        }
        //System.out.println("Pre final processing : "+pattern);

        int closingToken = 0;
        int openingToken = 0;
        while (openingToken < pattern.length()) {
            boolean comment = openingToken > 0 && pattern.charAt(openingToken - 1) == '~';
            if (pattern.charAt(openingToken) == '[' && !comment) {
                //System.out.println(pattern);
                //pct(openingToken);
                closingToken = openingToken;
                //System.out.println("Pattern is now : "+pattern);
                boolean eatLeftSpace = true;
                boolean eatRightSpace = true;
                boolean needsRightSpace = false;
                boolean needsLeftSpace = false;
                int brDepth = 0;
                while (closingToken < pattern.length()) {
                    //System.out.println(pattern);
                    //pct(i);
                    if (pattern.charAt(closingToken) == '[')
                        brDepth++;
                    if (pattern.charAt(closingToken) == ']')
                        brDepth--;
                    if (brDepth == 0 && pattern.charAt(closingToken) == ']' && !(closingToken > 1 && pattern.charAt(closingToken - 1) == '~')) { //Found closing token
                        if (openingToken - 1 > 0 && pattern.charAt(openingToken - 1) == ' ') {
                            needsLeftSpace = true;
                            for (int k = openingToken - 1; k >= 0; k--) {
                                //System.out.println("Processing char : "+pattern.charAt(k));
                                if (k > 0 && pattern.charAt(k - 1) == '~')
                                    continue;
                                if ("?)(:".contains("" + pattern.charAt(k))) {
                                    //System.out.println("3 Setting eatLeftSpace to false : " + k + " : " + pattern.charAt(k));
                                    eatLeftSpace = false;
                                    break;
                                }
                            }

                        }
                        int depth = 0;
                        if (closingToken + 1 < pattern.length()
                                && pattern.charAt(closingToken + 1) == ' '
                                && (openingToken == 0
                                || " ?)(:|".contains("" + pattern.charAt(openingToken - 1)))) {
                            //pct(closingToken);
                            needsRightSpace = true;
                            if (openingToken == 0)
                                eatRightSpace = false;
                            for (int k = 0; k < openingToken; k++) {
                                if (k > 0 && pattern.charAt(k - 1) == '~')
                                    continue;
                                if (pattern.charAt(k) == '[' || pattern.charAt(k) == '(')
                                    depth++;
                                else if (pattern.charAt(k) == ']' || pattern.charAt(k) == ')')
                                    depth--;
                                else if (depth == 0 && !" ?)(:|".contains("" + pattern.charAt(k)) || k == openingToken - 1) {
                                    eatRightSpace = false;
                                    break;
                                }
                            }
                            if (!eatRightSpace) {
                                depth = 0;
                                for (int k = closingToken + 1; k < pattern.length(); k++) {
                                    if (pattern.charAt(k - 1) == '~')
                                        continue;
                                    if (pattern.charAt(k) == '[')
                                        depth++;
                                    else if (pattern.charAt(k) == ']')
                                        depth--;
                                    else if (depth == 0 && !" ?)(:|".contains("" + pattern.charAt(k))) {
                                        eatRightSpace = !(eatLeftSpace && needsLeftSpace);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    closingToken++;
                }

                String firstPart;
                String middlePart = pattern.substring(openingToken + 1, closingToken);
                String lastPart = pattern.substring(closingToken + (needsRightSpace ? 2 : 1));
                //System.out.println("Processed "+pattern+" "+needsLeftSpace+" "+eatLeftSpace+" "+needsRightSpace+" "+eatRightSpace);

                firstPart = pattern.substring(0, openingToken - (needsLeftSpace ? 1 : 0));
                pattern = firstPart + (needsLeftSpace ? "(?: " : "(?:") + middlePart + (needsRightSpace ? (eatRightSpace ? " )?" : ")? ") : ")?") + lastPart;
            }
            openingToken++;
        }

        pattern=pattern.replaceAll("~","\\\\");
        //System.out.println("Basic translation : "+pattern);

        //End of basic translation to regex

        //Now we care about {type} catches

        List<ScriptParameterDefinition[]> paramTypes = new ArrayList<>();

        Pattern p = Pattern.compile("\\{(.*?)}");
        Matcher m = p.matcher(pattern);
        m:
        while (m.find()) {
            //System.out.println("Found group 1 as : " + m.group(1));
            String g = m.group(1);
            String t = g;
            String exp_capture = CAPTURE_EXPRESSION_GREEDY;
            if ((g.charAt(0) == '+' || shouldBeLazy(pattern, m.end(1))) && g.charAt(0) != '!') {
                exp_capture = CAPTURE_EXPRESSION_LAZY;
            }
            if (g.charAt(0) == '+' || g.charAt(0) == '!')
                t = g.substring(1);

            boolean n_args = false;
            if (t.endsWith("*")) {
                n_args = true;
                t = t.substring(0, t.length() - 1);
            }
            List<ScriptParameterDefinition> parameterDefinitions = new ArrayList<>();
            for(String type : t.split("\\|")){
                for (TypeDefinition typeDefinition : ScriptManager.types.values()) {
                    if (typeDefinition.getName().equals(type)) {
                        pattern = pattern.replaceFirst("\\{" + Pattern.quote(g) + "}", "(?<a" + argCount + ">" + exp_capture + ")");
                        parameterDefinitions.add(new ScriptParameterDefinition(typeDefinition.getTypeClass(), n_args));
                        argCount++;
                        continue m;
                    }
                }
                for (TypeDefinition primitiveDefinition : ScriptManager.primitives.values()) {
                    if (primitiveDefinition.getName().equals(type)) {
                        pattern = pattern.replaceFirst("\\{" + Pattern.quote(g) + "}", "(?<a" + argCount + ">" + primitiveDefinition.transformedPattern.getPattern().pattern() + ")");
                        parameterDefinitions.add(new ScriptParameterDefinition(primitiveDefinition.getTypeClass(), n_args));
                        argCount++;
                        continue m;
                    }
                }
            }
            paramTypes.add(parameterDefinitions.toArray(new ScriptParameterDefinition[0]));
            throw new Exception(t + " type is either not registered or not recognized");
        }
        pattern = "^\\s*" + pattern + "$";//End of parsing;
        //System.out.println("Transformed : "+pattern+" with :"+markCount+" marks\n");
        return new TransformedPattern(pattern, markCount, argCount, paramTypes.toArray(new ScriptParameterDefinition[0][0]));
    }

    public static TransformedPattern patternToRegex(String pattern) throws Exception {

        //Saved reference to the base pattern
        String basePattern = pattern;
        //pattern = pattern.replaceAll(" \\[","[ ");
        int i = 0;

        int markCount = 0;
        int argCount = 0;

        while (i < pattern.length()) {
            char c = pattern.charAt(i);
            boolean comment = i > 0 && pattern.charAt(i - 1) == '~';
            if (c == ')' && !comment) {
                int j = i;
                int mark = -1;
                while (j > 0) {
                    if (pattern.charAt(j) == ';' && !(pattern.charAt(j - 1) == '~')) { //Marks
                        j--;
                        //System.out.println("found a dot-comma : "+j+" "+pattern.charAt(j));
                        StringBuilder number = new StringBuilder();
                        while (j > 0 && pattern.charAt(j) >= '0' && pattern.charAt(j) <= '9') {
                            //System.out.println("charAt(j) is a number : "+j+" "+pattern.charAt(j));
                            number.insert(0, pattern.charAt(j));
                            j--;
                        }
                        mark = Integer.parseInt(number.toString());
                        markCount++;
                    }

                    if (pattern.charAt(j) == '(' && !(j > 1 && pattern.charAt(j - 1) == '~' || pattern.charAt(j + 1) == '?'))
                        break;
                    j--;
                }

                String firstPart = pattern.substring(0, j);
                //System.out.println("mark:"+mark);
                String middlePart = pattern.substring(j + (mark != -1 ? String.valueOf(mark).length() + 2 : 1), i);
                String lastPart = pattern.substring(i + 1);
                //System.out.println("\n"+firstPart+"\n"+middlePart+"\n"+lastPart);
                pattern = firstPart + "(?" + (mark != -1 ? "<m" + mark + ">" : ":") + middlePart + ")" + lastPart;
                //System.out.println(pattern+"   "+i);
                i += mark != -1 ? String.valueOf(mark).length() + 3 : 2;
            }

            i++;
        }

        i = 0;
        int j = 0;
        markCount = 0;
        while (j < pattern.length()) {
            boolean comment = j > 0 && pattern.charAt(j - 1) == '~';
            if (pattern.charAt(j) == '[' && !comment) {
                i = j;
                //System.out.println(pattern);
                boolean eatLeftSpace = true;
                boolean eatRightSpace = true;
                boolean needsRightSpace = false;
                boolean needsLeftSpace = false;
                int brDepth = 0;
                while (i < pattern.length()) {
                    if (pattern.charAt(i) == '[')
                        brDepth++;
                    if (pattern.charAt(i) == ']')
                        brDepth--;
                    if (brDepth == 0 && pattern.charAt(i) == ']' && !(i > 1 && pattern.charAt(i - 1) == '~')) {
                        if (j - 1 > 0 && pattern.charAt(j - 1) == ' ') {
                            needsLeftSpace = true;
                            for (int k = j - 1; k >= 0; k--) {
                                //System.out.println(pattern.charAt(k));
                                if (k > 0 && pattern.charAt(k - 1) == '~')
                                    continue;
                                if ("?)(:".contains("" + pattern.charAt(k))) {
                                    //System.out.println("3 Setting eatLeftSpace to false : " + k + " : " + pattern.charAt(k));
                                    eatLeftSpace = false;
                                    break;
                                }
                            }

                        }
                        int depth = 0;
                        if (i + 1 < pattern.length() && pattern.charAt(i + 1) == ' ' && (j == 0 || " ?)(:".contains("" + pattern.charAt(j - 1)))) {
                            needsRightSpace = true;
                            if (j == 0)
                                eatRightSpace = false;
                            for (int k = 0; k < j; k++) {
                                if (k > 0 && pattern.charAt(k - 1) == '~')
                                    continue;
                                if (pattern.charAt(k) == '[' || pattern.charAt(k) == '(')
                                    depth++;
                                else if (pattern.charAt(k) == ']' || pattern.charAt(k) == ')')
                                    depth--;
                                else if (depth == 0 && !" ?)(:".contains("" + pattern.charAt(k)) || k == j - 1) {
                                    eatRightSpace = false;
                                    break;
                                }
                            }
                            if (!eatRightSpace) {
                                depth = 0;
                                for (int k = i + 1; k < pattern.length(); k++) {
                                    if (pattern.charAt(k - 1) == '~')
                                        continue;
                                    if (pattern.charAt(k) == '[' || (k < pattern.length() - 1 && pattern.charAt(k) == '(' && pattern.charAt(k + 1) == '?'))
                                        depth++;
                                    else if (pattern.charAt(k) == ']' || (k < pattern.length() - 1 && pattern.charAt(k) == ')' && pattern.charAt(k + 1) == '?'))
                                        depth--;
                                    else if (depth == 0 && !" ?)(:".contains("" + pattern.charAt(k))) {
                                        eatRightSpace = !eatLeftSpace;
                                        break;
                                    }
                                }
                            }

                        }

                        break;
                    }
                    i++;
                }


                String firstPart;
                String middlePart = pattern.substring(j + 1, i);
                String lastPart = pattern.substring(i + (needsRightSpace ? 2 : 1));
                //System.out.println("- "+pattern+" "+needsLeftSpace+" "+eatLeftSpace+" "+needsRightSpace+" "+eatRightSpace);

                firstPart = pattern.substring(0, j - (needsLeftSpace ? 1 : 0));
                pattern = firstPart + (needsLeftSpace ? "(?: " : "(?:") + middlePart + (needsRightSpace ? (eatRightSpace ? " )?" : ")? ") : ")?") + lastPart;
                //System.out.println(pattern);
            }
            j++;
        }

        pattern = pattern.replaceAll("~", "\\\\");
        pattern = pattern.replaceAll("\\{", "\\\\{");

        //System.out.println("Basic translation : "+pattern);
        //System.out.println("Transformed : "+pattern+" with :"+markCount+" marks\n");
        return new TransformedPattern(pattern, markCount, argCount, new ScriptParameterDefinition[0][0]);
    }

    public static boolean shouldBeLazy(String p, int s) {
        //System.out.println("shouldBeLazy "+s+" :"+p);
        while (s < p.length()) {
            if (p.charAt(s) == '[' || p.charAt(s) == '(')
                return true;
            s++;
        }
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
        //System.out.println("Getting primitive for "+parameter.text);
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

    public static ScriptWrapper parseLoop(ScriptToken line, ScriptCompileGroup compileGroup) throws Exception {
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
