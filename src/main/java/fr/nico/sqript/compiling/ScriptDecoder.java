package fr.nico.sqript.compiling;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ActDefinition;
import fr.nico.sqript.actions.ActSimpleExpression;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptBlockFunction;
import fr.nico.sqript.blocks.ScriptFunctionalBlock;
import fr.nico.sqript.function.ScriptNativeFunction;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeFunction;
import fr.nico.sqript.types.primitive.PrimitiveType;
import fr.nico.sqript.expressions.*;
import fr.nico.sqript.meta.*;
import fr.nico.sqript.types.primitive.TypeString;
import scala.actors.migration.pattern;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptDecoder {


    public static final String CAPTURE_BETWEEN_QUOTES = "(?:\"((?:[^\"\\\\\\\\]|\\\\.)*)\")";
    public static final String CAPTURE_FULL_NUMBER = "(?:\\s*[0-9]+)";
    public static final String CAPTURE_BOOLEAN = "(?:([Tt]rue|[Ff]alse))";
    public static final String CAPTURE_FLOAT = "(?:\\s*[0-9]*\\.[0-9]*+)";
    public static final String CAPTURE_EXPRESSION_LAZY = "(?:.*?)";
    public static final String CAPTURE_EXPRESSION_GREEDY = "(?:.*)";

    public static List<String> operators_list = new LinkedList<>();

    public static IScript getIScript(ScriptLine line, ScriptCompileGroup compileGroup) throws Exception {

        IScript sc = getLoop(line, compileGroup); //On rentre dans un bloc, on créé un nouveau CompileGroup
        if (sc == null)
            sc = getAction(line, compileGroup);//Si c'est pas une boucle c'est une action, et on ne change pas de bloc

        return sc;
    }


    public static ScriptNativeFunction getNativeFunction(ScriptLine name) {
        for (NativeDefinition i : ScriptManager.nativeFunctions.values()) {
            int r;
            if ((r = i.getMatchedPatternIndex(name.text)) != -1) {
                Class<? extends ScriptNativeFunction> funcClass = i.getNativeClass();
                try {
                    Constructor<? extends ScriptNativeFunction> t = funcClass.getDeclaredConstructor(int.class);
                    return t.newInstance(r);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static ExprNativeFunction getExprNativeFunction(ScriptLine parameter) {
        Class<ExprNativeFunction> function = ExprNativeFunction.class;
        ScriptNativeFunction f = getNativeFunction(parameter);
        if (f != null) {
            Constructor<ExprNativeFunction> c = null;
            try {
                c = function.getConstructor(ScriptNativeFunction.class);
                return c.newInstance(f);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
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

    public static boolean matchesList(String test, List<String> list) {
        for (String s : list) {
            Pattern p = Pattern.compile(s);
            Matcher m = p.matcher(test);
            if (m.find()) return true;
        }
        return false;
    }

    public static boolean checkAllMatches(String test, List<String> matches) {
        for (String s : matches) {
            //System.out.println("Checking "+test+" with "+ s );
            Pattern p = Pattern.compile(s);
            Matcher m = p.matcher(test);
            if (m.find()) {
                //System.out.println("Found for "+test+" with "+s);
                return true;
            }
        }
        return false;
    }

    static Pattern pattern_function = Pattern.compile("^\\s*(\\w*)\\((.*)\\)\\s*$");
    static Pattern pattern_removed_string = Pattern.compile("\\{S(\\d*)}");
    static Pattern pattern_capture_quotes = Pattern.compile("("+ CAPTURE_BETWEEN_QUOTES + ")");

    public static String[] extractStrings(String parameter){
        List<String> result = new ArrayList<>();
        //System.out.println("Extracting strings for : "+parameter);

        Matcher m = pattern_capture_quotes.matcher(parameter);
        int i = 0;
        while (m.find()) {
            String f = m.group(2);
            result.add(f);
            i++;
        }
        return result.toArray(new String[0]);
    }

    public static String replaceStrings(String parameter,String[] strings)
    {
        int i = 0;
        for(String s : strings){
            parameter = parameter.replaceFirst("\\{S"+i+"}", s);
            i++;
        }
        return parameter;
    }
    public static String removeStrings(String parameter, String[] strings){
        int i = 0;
        for(String s : strings){
            parameter = parameter.replaceFirst(Pattern.quote("\""+s+"\""), "{S" + i+"}");
            i++;
        }
        return parameter;
    }

    static Pattern pattern_L = Pattern.compile("(?<!\\\\)(L\\d+)");

    public static String buildOperators(String t,List<ScriptOperator> operators){
        List<String> saved = new ArrayList<>();
        //System.out.println("Building operators for : "+t);

        Matcher m = pattern_capture_quotes.matcher(t);
        int i = 0;
        while (m.find()) {
            String f = m.group(1);
            saved.add(f);
            t = t.replaceFirst(Pattern.quote(f), "L" + i);
            i++;
        }

        //Placing operators
        while (checkAllMatches(t, operators_list)) {
            int id = -1;
            for (ScriptOperator s : ScriptManager.operators) {
                id++;
                //String b = "", c = "(?![^(]*\\))";
                String b = "", c = "";
                if (s.word) {
                    b = "("+(s.unary?"":"\\)")+"\\s+|^|})";
                    //c = "\\s\\(+" + c;
                    c = "\\s+\\(";
                }
                //System.out.println("Checking with : "+"(" + b + Pattern.quote(s.symbol) + c + ")");
                Pattern p = Pattern.compile("(" + b + Pattern.quote(s.symbol) + c + ")");
                m = p.matcher(t);
                //System.out.println("Trying to match  : "+t+" with regex : "+"(" + b + Pattern.quote(s.symbol) + c + ")");
                if (m.find()) {
                    if (s.unary && !s.postfixed) {//Check if this one is unary
                        boolean isUnary = false;
                        int position = m.start();
                        if (position == 0)
                            isUnary = true;
                        else {
                            while (t.charAt(position) == ' ' && position > 0) {
                                position--;
                            }
                            if (t.charAt(position) == '(' || t.charAt(position) == '}')
                                isUnary = true;
                        }
                        if (!isUnary) continue;
                    }


                    t = t.replaceFirst("\\s*" + Pattern.quote(s.symbol) + "\\s*", "#{" + id + "}");
                    //System.out.println("t: "+t);
                    operators.add(s);
                }
            }
        }
        //Replacing text between []'s

        m = pattern_L.matcher(t);
        i = 0;
        while (m.find()) {
            t = t.replaceFirst("L" + i, saved.get(i));
            i++;
        }
        //System.out.println("Built : "+t);
        return t;
    }

    public static ScriptExpression getExpression(ScriptLine parameter, ScriptCompileGroup compileGroup) throws Exception {
        //Removing strings from the line in order to avoid interpretation issues
        String[] strings = extractStrings(parameter.text);
        String line_without_strings = removeStrings(parameter.text, strings);
        return getExpression(parameter.with(line_without_strings), compileGroup, strings);
    }


        public static ScriptExpression getExpression(ScriptLine parameter, ScriptCompileGroup compileGroup, String[] replacedStrings) throws Exception {
        //System.out.println("\nGetting expression for : "+parameter);

        //Creating a new reference to the line because we are going to modify it a little
        parameter = (ScriptLine) parameter.clone();

        if (parameter.text.isEmpty()) return null;

        parameter.text = parameter.text.trim();




        List<ScriptExpression> operands = new ArrayList<>();
        List<ScriptOperator> operators = new ArrayList<>();

        //Splitting at each operator
        ScriptLine finalParameter = parameter;
        parameter.text = new Object() {

            ScriptLine main = finalParameter;

            String getExpressions(String text) throws Exception {
                text = text.trim();
                text = removeOutsideParenthesis(text);
                //Check if it is a simple litteral expression to prevent useless operators splitting
                Object[] result = getLitteralExpression(main.with(text), compileGroup, replacedStrings);

                //if(result!=null)
                    //System.out.println("r:"+result[1]);

                boolean greedyExpression = false;
                if (result != null)
                    greedyExpression = isLast((String[][]) result[1], text);

                //System.out.println("g:"+greedyExpression);
                //If a direct expression (expression that doesn't need parameters or sub-expressions) hasn't been
                // found, we extract operators and create a compiled evaluation expression
                if (result == null || greedyExpression) {
                    //Escaping the meta characters we use further
                    text = text.replaceAll("#","\\#")
                            .replaceAll("L","\\L");
                    //Preventing text between [] or "" to be matched by expressions and saving it
                    //System.out.println("-------");
                    if(result != null) {
                        for (String[] groups : (String[][]) result[1]) {
                            //System.out.println("g:"+Arrays.toString(groups)+" @ "+finalParameter);
                            for (String splits : groups) {
                                //System.out.println("split:"+splits);
                                if (splits != null){
                                    text = text.replaceFirst(Pattern.quote(splits), Matcher.quoteReplacement(buildOperators(splits, operators)));
                                }
                            }
                            //System.out.println(text);
                        }
                    }
                    else{
                        text = buildOperators(text, operators);
                    }
                }
                String[] groups = splitAtOperators(text);
                //System.out.println("groups:"+text+"  ::  "+Arrays.toString(groups));


                if(!greedyExpression){
                    if (result != null)
                        //What we found first is going to be parsed
                        groups = new String[]{text};
                    else if (groups.length == 1 && groups[0].equals(text)) {
                        //No expression found because the text wasn't transformed
                        return null;
                    }
                }
                //System.out.println("m");
                //System.out.println("Going to go through groups");
                for (String part : groups) {
                    //We remove the outside parenthesis, if there is an error due to this manipulation it means there is a bad
                    //parenthesage of the expression
                    part = removeOutsideParenthesis(part);
                    //System.out.println("p:"+part);
                    //We replace the meta characters naturally in the expression
                    //All saved parts with "L\d+" were already replaced sooner
                    part = part.replaceAll("\\\\#","#");

                    StringBuilder finalString;
                    result = getLitteralExpression(main.with(part), compileGroup,replacedStrings);

                    if (result != null) {

                        ScriptExpression e = (ScriptExpression) result[0];
                        if (e == null) throw new ScriptException.ScriptUnknownExpressionException(main.with(part));
                        String[][] subGroups = (String[][]) result[1];
                        //System.out.println("i"+ Arrays.toString(result)+" subGroups.length:"+subGroups.length);

                        //Arity is the number of arguments that an application (expression or function) takes
                        int arity = subGroups.length;

                        if (e instanceof ExprNativeFunction) {
                            ExprNativeFunction f = (ExprNativeFunction) e;
                            arity = f.function.getNbParameters();
                        } else if (e instanceof ExprFunction) {
                            ExprFunction f = (ExprFunction) e;
                            arity = f.function.parameters.length;
                        } else if (e instanceof ExprArrays && e.getMatchedIndex() == 0) {
                            arity = subGroups[0].length;
                        }

                        finalString = new StringBuilder("@{" + operands.size() + "/" + arity + "}" + (subGroups.length > 0 ? "(" : ""));
                        operands.add(e);

                        for (int j = 0; j < subGroups.length; j++) {
                            //System.out.println("l:"+Arrays.toString(subGroups[j]));
                            String[] group = subGroups[j];
                            if (group.length > 1) {

                                //Avoiding duplication of call of pattern n°0 of ExprArray
                                boolean alreadyExprArray = false;
                                //Creating an array if it is one
                                if (!(operands.get(operands.size() - 1).getMatchedIndex() == 0 && operands.get(operands.size() - 1) instanceof ExprArrays)) {
                                    ExprArrays arrayInstancier = new ExprArrays();
                                    arrayInstancier.setMatchedIndex(0);
                                    arrayInstancier.setLine(finalParameter);
                                    operands.add(arrayInstancier);
                                    //@(id of this operand / arity of this operand)
                                    finalString.append("@{").append(operands.size()).append("/").append(group.length).append("}");
                                    finalString.append("(");
                                    alreadyExprArray = true;
                                }

                                for (String parameter : group) {
                                    if (!parameter.isEmpty() && !parameter.matches("^\\s*$"))
                                        finalString.append(getExpressions(parameter));
                                    else {
                                        finalString.append("@{").append(operands.size()).append("/").append(0).append("}");
                                        operands.add(null);

                                    }
                                    finalString.append(',');

                                }
                                finalString = new StringBuilder(finalString.substring(0, finalString.length() - 1));

                                if (alreadyExprArray) {
                                    finalString.append(')');
                                }

                            } else if (group.length == 1) {
                                //System.out.println("k");
                                if (group[0]!=null && !group[0].isEmpty() && !group[0].matches("^\\s*$"))
                                    finalString.append(getExpressions(group[0]));
                                else {
                                    finalString.append("@{").append(operands.size()).append("/").append(0).append("}");
                                    operands.add(null);

                                }
                                finalString.append(',');
                            }
                            //if (j < subGroups.length - 1) finalString.append(",");
                        }
                        if (subGroups.length > 0){
                            finalString.deleteCharAt(finalString.length()-1);
                            finalString.append(")");
                        }
                        text = text.replaceFirst(Pattern.quote(part) + "(?![^{]*\\})", finalString.toString());

                    } else {
                        //System.out.println("j:"+part);
                        String res = getExpressions(part);
                        if (res == null) {
                            //System.out.println("Returning null 1");
                            return null;
                        }
                        text = text.replaceFirst(Pattern.quote(part) + "(?![^{]*\\})", res);
                    }
                }
                return text;
            }

            public String removeOutsideParenthesis(String s){
                int c = 0;
                boolean flat = false;
                int p = 0;
                if(s.charAt(0)!='(')
                    return s;
                while(c<s.length()){
                    if(s.charAt(c)=='(')
                        p++;
                    else if(s.charAt(c)==')')
                        p--;
                    if(p==0 && c!=0 && c!=s.length()-1){
                        flat = true;
                    }
                    c++;
                }
                if(!flat)
                    return removeOutsideParenthesis(s.substring(1,s.length()-1));
                else return s;
            }

        }.getExpressions(((ScriptLine) parameter.clone()).text);

        //If we only have one expression we return the expression directly, as we don't have any parameters/arguments to give
        if (operands.size() == 1 && operators.isEmpty()) {
            return operands.get(0);
        }
        //If we have multiple expressions to combine, and no errors, we returned a compiled evaluation of the expression
        if (operands.size() > 0 && parameter.text != null) {
            //System.out.println("Returning compiled expression : "+parameter+" "+ Arrays.toString(operands.toArray()));
            return new ExprCompiledEvaluation(parameter, operands.toArray(new ScriptExpression[0]), operators.toArray(new ScriptOperator[0]));
        }
        //System.out.println("Returning null");
        return null;
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

    public static boolean isLast(String[][] parameters, String line) {
        if(line!=null)
            for (String[] s : parameters) {
                if(s.length>0 && s[s.length-1]!=null)
                    if (line.lastIndexOf(s[s.length-1]) + s[s.length - 1].length() == line.length()) {
                        return true;
                    }
            }
        return false;
    }



    //[0]: ScriptExpression : expression instance
    //[1]: String[] : ordered sub-groups (ex : first element of random numbers in range of (50#{5}5) -> [random numbers in range of (50#{5}5)]
    private static Object[] getLitteralExpression(ScriptLine parameter, ScriptCompileGroup compileGroup, String[] strings) throws Exception {
        //Null parameters
        if(parameter.text==null)
            return null;

        //System.out.println("Getting litteral expression: "+parameter.text);

        //Check if it is a native function
        ExprNativeFunction f;
        Matcher m = pattern_function.matcher(parameter.text);
        if (m.find()) {
            //System.out.println("Found for a function : "+m.group(1));
            //Priority to not native functions
            ScriptFunctionalBlock sf;
            if ((sf = parameter.scriptInstance.getFunction(m.group(1))) != null) {
                return new Object[]{new ExprFunction(sf), new String[][]{m.group(2).split(",")}};
            }
            if ((f = getExprNativeFunction(parameter.with(m.group(1)))) != null) {
                return new Object[]{f, new String[][]{m.group(2).split(",")}};
            }
        }

        //Check for replaced string
        m = pattern_removed_string.matcher(parameter.text);
        if(m.matches()){
            int n = Integer.parseInt(m.group(1));
            return new Object[]{new ExprPrimitive(new TypeString(strings[n])), new String[0][0]};
        }

        //Check if it is a primitive
        PrimitiveType primitive = getPrimitive(parameter);
        //System.out.println("Primitive is null : "+(primitive==null));
        if (primitive != null) {
            //System.out.println("Found a primitive ! It's a : "+primitive.getClass());
            return new Object[]{new ExprPrimitive(primitive), new String[0][0]};
        }

        //Check if it is a reference to a variable of the context
        Integer h;
        if ((h = compileGroup.getHashFor(parameter.text)) != null || (!parameter.text.isEmpty() && parameter.text.charAt(0) == '$' && parameter.text.matches("\\$\\w+"))) {
            //System.out.println("Got "+parameter.text+" in compileGroup with indentId="+compileGroup.indentId);
            Class<? extends ScriptElement<?>> returnType = getType("element");
            if (returnType == null) throw new ScriptException.ScriptUndefinedReferenceException(parameter);
            else {
                ExprReference s = new ExprReference(returnType);
                s.setLine((ScriptLine) parameter.clone());
                if (h == null)
                    h = parameter.text.hashCode();
                s.setVarHash(h);
                return new Object[]{s, new String[0][0]};
            }
        }

        //Check if it is an expression
        ExpressionDefinition def = null;
        int[] index = null;
        for (ExpressionDefinition expressionDefinition : ScriptManager.expressions) {
            //Sorting expressions to find the latest one, using the "score" got in "getMatchedPatternIndexAndScore"
            int[] t = expressionDefinition.getMatchedPatternIndexAndPosition(parameter.text);

            //If we found an expression that is nearer to the end of the sentence or an expression that don't take any parameters
             if ((t != null && (index == null || t[1] > index[1] || !expressionDefinition.getPatterns()[t[0]].contains("{")))) {
                index = t;
                def = expressionDefinition;
            }

        }
        if (def != null && index.length > 0)
            try {
                TransformedPattern tp = def.transformedPatterns[index[0]];
                Class<? extends ScriptExpression> expression = def.getExpressionClass();
                ScriptExpression exp = expression.getConstructor().newInstance();
                exp.setMatchedIndex(index[0]);
                exp.setMarks(index[2]);
                exp.setLine((ScriptLine) parameter.clone());

                List<String[]> parameters = new ArrayList<>();
                Pattern p = tp.getPattern();
                m = p.matcher(parameter.text);
                //System.out.println(m.pattern());
                List<String> group = new ArrayList<>();
                if (m.find()) {
                    //System.out.println("Found expression definition : "+def.getExpressionClass()+" "+def.getName());
                    int i = 0;
                    for (String matchGroup : tp.getAllArguments(parameter.text)) {
                        //System.out.println("iter: "+i+" "+ matchGroup +" "+tp.getTypes().length+" "+tp.getTypes()[i].isN_args());
                        if (i < tp.getTypes().length && tp.getTypes()[i].isN_args()) {
                            if (matchGroup == null || matchGroup.isEmpty()) {
                                parameters.add(new String[0]);
                                continue;
                            }
                            String[] parts = splitAtComa(matchGroup);
                            group.addAll(Arrays.asList(parts));
                        } else
                            group.add(m.group(i + 1));
                        parameters.add(group.toArray(new String[0]));
                        //System.out.println("Added " + Arrays.toString(parameters.get(parameters.size()-1)));
                        group.clear();
                        i++;
                    }
                }
                //System.out.println("Returning : "+exp);
                return new Object[]{exp, parameters.toArray(new String[0][0])};
            } catch (Exception e) { e.printStackTrace(); }


        //Check if it is a not-native function reference
        //System.out.println("Checking if it's a function");
        for (ScriptBlock fc : parameter.scriptInstance.getBlocksOfClass(ScriptBlockFunction.class)) {
            //System.out.println("Checking for : "+fc.getClass().getSimpleName());
            ScriptBlockFunction function = (ScriptBlockFunction) fc;
            if (function.name.equals(parameter.text))
                return new Object[]{new ExprResult(new TypeFunction(function)), new String[0][0]};
        }

        if(parameter.text.startsWith("@")){
            if(parameter.scriptInstance.getOptions().containsKey(parameter.text.substring(1))){
                return new Object[]{parameter.scriptInstance.getOptions().get(parameter.text.substring(1)), new String[0][0]};
            }
        }
        return null;
    }

    private static String[] splitAtComa(String text) {
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
                if (!r.isEmpty()){
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
        if (!r.isEmpty()){
            splits.add(r); //We add the current built word
        }
        return splits.toArray(new String[0]);
    }

    private static String[] splitAtOperators(String text) {
        List<String> splits = new ArrayList<>();
        int c = 0;
        int ps = 0;
        StringBuilder current = new StringBuilder();
        while (c < text.length()) {
            if (text.charAt(c) == ')') {
                ps--;
            }
            if (text.charAt(c) == '(') {
                ps++;
            }
            if (ps > 0) {
                current.append(text.charAt(c));
                c++;
                continue;
            }
            if (text.charAt(c) == '#' && c + 1 < text.length() && text.charAt(c + 1) == '{' && (c==0 || text.charAt(c-1)!='\\')) {
                String r = current.toString();

                if (!r.isEmpty()) {
                    splits.add(r); //We add the current built word
                }
                current = new StringBuilder(); //We start a new word
                c++; //First {
                c++; //First number
                while (text.charAt(c) >= '0' && text.charAt(c) <= '9') {
                    c++;
                }
                c++; //Last }
            } else {
                current.append(text.charAt(c));
                c++;
            }

        }
        String r = current.toString();
        if (!r.isEmpty()) splits.add(r); //We add the current built word
        return splits.toArray(new String[0]);
    }

    public static int getOperatorIndex(String symbol, boolean binary) {
        for (int j = 0; j < ScriptManager.operators.size(); j++) {
            ScriptOperator o = ScriptManager.operators.get(j);
            if (o.symbol.equals(symbol) && (o.unary == !binary)) {
                return j;
            }
        }
        return -1;
    }

    public static Class<? extends ScriptElement<?>> getType(String type) {
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

    public static ScriptAction getAction(ScriptLine line, ScriptCompileGroup compileGroup) throws Exception {
        line = line.with(line.text.replaceFirst("\\s*", ""));


        //System.out.println("Getting action for : "+line.text);

        //Removing strings from the line in order to avoid interpretation issues
        String[] strings = extractStrings(line.text);
        String line_without_strings = removeStrings(line.text, strings);

        for (ActionDefinition actionDefinition : ScriptManager.actions) {
            //System.out.println("Checking for action : "+actionDefinition.getActionClass().getSimpleName());
            int[] indexAndMarks = actionDefinition.getMatchedPatternIndexAndMarks(line_without_strings);
            if (indexAndMarks.length>0) {
                int index = indexAndMarks[0];
                int marks = indexAndMarks[1];
                //System.out.println(index+" "+Integer.toBinaryString(marks));
                //System.out.println("AA:"+infos.transformedPatterns[index].regex+" -- "+line.text);
                List<String> parameters = new ArrayList<>(Arrays.asList(actionDefinition.transformedPatterns[index].getAllArguments(line_without_strings)));
                //System.out.println("Parameters size : "+parameters.size());

                //If accessing a global variable,
                //we parse the argument as a string to make the action
                //able to register the new variable in the context
                if (actionDefinition.getActionClass().equals(ActDefinition.class) && index == 2 && (compileGroup.getHashFor(parameters.get(0)) == null)) {
                    String varName = parameters.get(0);
                    if (!varName.matches("[^\\s]*")) {
                        ScriptManager.log.error("Variables cannot have whitespaces in their names.");
                        return null;
                    }
                    compileGroup.add(varName); //We tell the compile group that a variable named parameters.get(0) can now be used in the script
                    //System.out.println("Set parameter : "+parameters.get(0));
                }

                List<ScriptExpression> expressions = new ArrayList<>(parameters.size());

                for (String parameter : parameters) {
                    if(parameter==null) {
                        expressions.add(null);
                        continue;
                    }
                    ScriptExpression e = getExpression(line.with(parameter), compileGroup,strings);
                    if (e != null)
                        expressions.add(e);
                    else {
                        throw new ScriptException.ScriptUnknownExpressionException(line.with(parameter).withString(strings));
                    }
                }

                ScriptAction action = actionDefinition.getActionClass().getConstructor().newInstance();
                action.setParameters(expressions);
                action.setMatchedIndex(index);
                action.setLine(line);
                action.setMarks(marks);
                return action;
            }
        }
        ScriptExpression s = getExpression(line.with(line_without_strings), compileGroup, strings);
        if (s != null)
            return new ActSimpleExpression(s);
        return null;
    }

    //cette fonction traduit une liste de lignes en un ensemble de blocs de scripts, regroupés en ScriptContainers et sous-ScriptContainers et sous-sous-ScriptContainer etc.
    public static IScript group(@Nullable IScript parent, List<ScriptLine> lines, ScriptCompileGroup compileGroup) throws Exception {
        int c = 0;//premiere ligne du container
        IScript previousAddedScript = null;
        IScript first = null;
        while (c < lines.size()) {
            ScriptLine line = lines.get(c);
            line.text = getUncommentedPart(line.text);
            if (line.text.isEmpty() || line.text.matches("^\\s*$")) {//It's a comment
                c++;
                continue;
            }
            int tabLevel = ScriptDecoder.getTabLevel(line.text);
            IScript script = ScriptDecoder.getIScript(line, compileGroup);
            if (script == null) {
                throw new ScriptException.ScriptUnknownTokenException(line);
            }
            if (first == null)
                first = script;
            script.setLine(line);
            script.setParent(parent);
            if (script instanceof ScriptLoop) {
                //ScriptCompileGroup newBlockGroup = new ScriptCompileGroup(compileGroup); //We are in a new block, we wrap the current group to a new one
                if (script instanceof ScriptLoop.ScriptLoopIF) {
                    if (c + 1 < lines.size() && ScriptDecoder.getTabLevel(lines.get(c + 1).text) == tabLevel)
                        throw new ScriptException.ScriptIndentationErrorException(lines.get(c + 1));
                    List<ScriptLine> ifContainer = new ArrayList<>();
                    do {
                        c++;
                        ifContainer.add(lines.get(c));
                    }
                    while (c + 1 < lines.size() && ScriptDecoder.getTabLevel((lines.get(c + 1).text)) > tabLevel);

                    ((ScriptLoop.ScriptLoopIF) (script)).wrap(group(script, ifContainer, compileGroup));

                    if (previousAddedScript != null && (script instanceof ScriptLoop.ScriptLoopELSE || script instanceof ScriptLoop.ScriptLoopELSEIF)) {
                        if (previousAddedScript instanceof ScriptLoop.ScriptLoopIF) {
                            ((ScriptLoop.ScriptLoopIF) previousAddedScript).setElseContainer((ScriptLoop.ScriptLoopIF) script);
                            script.parent = previousAddedScript;
                        } else {
                            throw new ScriptException.ScriptSyntaxException(line, "else statement not following an if statement");
                        }
                    }
                } else {//While-loop and for-loop
                    //System.out.println(c+" "+lines.get(c+1).text+" "+tabLevel+" | "+lines.size());
                    if (c + 1 < lines.size() && ScriptDecoder.getTabLevel(lines.get(c + 1).text) == tabLevel)
                        throw new ScriptException.ScriptIndentationErrorException(lines.get(c + 1));
                    List<ScriptLine> forContainer = new ArrayList<>();
                    do {
                        c++;
                        forContainer.add(lines.get(c));
                    }
                    while (c + 1 < lines.size() && ScriptDecoder.getTabLevel((lines.get(c + 1).text)) > tabLevel);
                    IScript grouped = group(script, forContainer, compileGroup);
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

    public static TransformedPattern transformPattern(String pattern) throws Exception {
        //System.out.println("Transforming : "+pattern);

        //Saved reference to the base pattern
        String basePattern = pattern;
        pattern = pattern.replaceAll(" \\[","[ ");
        int i = 0;

        int markCount = 0;
        int argCount = 0;

        while(i<pattern.length()) {
            char c = pattern.charAt(i);
            boolean comment = i>1 && pattern.charAt(i-1)=='~';
            if (c == ')' && !comment) {
                int j = i;
                int mark = -1;
                while (j > 0) {
                    if (pattern.charAt(j) == ';' && !(j>1 && pattern.charAt(j-1)=='~')) { //Marks
                        j--;
                        //System.out.println("found a dot-comma : "+j+" "+pattern.charAt(j));
                        StringBuilder number = new StringBuilder();
                        while(j>0 && pattern.charAt(j)>='0' && pattern.charAt(j)<='9'){
                            //System.out.println("charAt(j) is a number : "+j+" "+pattern.charAt(j));
                            number.insert(0, pattern.charAt(j));
                            j--;
                        }
                        mark = Integer.parseInt(number.toString());
                        if(mark<0 || mark>15)
                            throw new ScriptException.ScriptPatternError("Marks id's must be between 0 and 15 : "+basePattern+". Check the syntax.");
                        markCount++;
                    }

                    if (pattern.charAt(j) == '(' && !(j>1 && pattern.charAt(j-1)=='~'))
                        break;
                    j--;
                }
                //j is now the index of the opening associated bracket
                //Bracket error
                if (j == -1)
                    throw new ScriptException.ScriptPatternError("Unbalanced parenthesis in pattern : "+basePattern+". Check the syntax.");

                String firstPart = pattern.substring(0, j);
                //System.out.println("mark:"+mark);
                String middlePart = pattern.substring(j + (mark!=-1 ? String.valueOf(mark).length() + 2 : 1), i);
                String lastPart = pattern.substring(i + 1);
                //System.out.println("\n"+firstPart+"\n"+middlePart+"\n"+lastPart);
                pattern = firstPart + "(?"+(mark!=-1?"<m"+mark+">":":") + middlePart + ")" + lastPart;
                //System.out.println(pattern+"   "+i);
                i += mark!=-1 ? String.valueOf(mark).length()+3 : 2;
            }

            i++;
        }
        i=0;
        markCount = 0;
        while(i<pattern.length()) {
            char c = pattern.charAt(i);
            boolean comment = i>1 && pattern.charAt(i-1)=='~';
            if (c == ']' && !comment) {
                boolean nextWp = (i + 1 < pattern.length() && pattern.charAt(i + 1) == ' ');
                int j = i;
                while (j > 0) {
                    if (pattern.charAt(j) == '[' && !(j>1 && pattern.charAt(j-1)=='~')) {
                        break;
                    }
                    j--;
                }
                //j is now the index of the opening associated bracket
                //Bracket error
                if (j == -1)
                    throw new ScriptException.ScriptPatternError("Unbalanced brackets in pattern : "+basePattern+". Check the syntax.");

                String firstPart;
                String middlePart = pattern.substring(j + 1, i);
                String lastPart = pattern.substring(i + (nextWp ? 2 : 1));

                if (j == 0 || pattern.charAt(j - 1) == ' ') {
                    firstPart = j==0?"":pattern.substring(0, j - 1);
                    pattern = firstPart + "(?:"+(j==0?"":" ") + middlePart + (nextWp ? " " : "") + ")?" + lastPart;
                } else {
                    firstPart = pattern.substring(0, j);
                    pattern = firstPart + "(?:" + middlePart + ")?" + (nextWp ? " " : "") + lastPart;
                }
                i += nextWp ? 3 : 2;
            }
            i++;
        }

        pattern=pattern.replaceAll("~","\\\\");

        //System.out.println("Basic translation : "+pattern);

        //End of basic translation to regex

        //Now we care about {type} catches

        List<ScriptParameterDefinition> paramTypes = new ArrayList<>();

        Pattern p = Pattern.compile("\\{(.*?)}");
        Matcher m = p.matcher(pattern);
        m:
        while (m.find()) {
            //System.out.println("Found group 1 as : " + m.group(1));
            String g = m.group(1);
            String t = g;
            String exp_capture = CAPTURE_EXPRESSION_GREEDY;
            if (g.charAt(0) == '+'|| shouldBeLazy(pattern,m.end(1))) {
                exp_capture = CAPTURE_EXPRESSION_LAZY;
                if(g.charAt(0) == '+')
                    t = g.substring(1);
            }else{
                //t = g.substring(1);
            }

            boolean n_args = false;
            if (t.endsWith("*")) {
                n_args = true;
                t = t.substring(0, t.length() - 1);
            }

            for (TypeDefinition typeDefinition : ScriptManager.types.values()) {
                if (typeDefinition.getName().equals(t)) {
                    pattern = pattern.replaceFirst("\\{" + Pattern.quote(g) + "}", "(?<a"+argCount+">" + exp_capture + ")");
                    paramTypes.add(new ScriptParameterDefinition(typeDefinition.getTypeClass(), n_args));
                    argCount++;
                    continue m;
                }
            }
            for (TypeDefinition primitiveDefinition : ScriptManager.primitives.values()) {
                if (primitiveDefinition.getName().equals(t)) {
                    pattern = pattern.replaceFirst("\\{" + Pattern.quote(g) + "}", "(?<a"+argCount+">"+ exp_capture + ")");
                    paramTypes.add(new ScriptParameterDefinition(primitiveDefinition.getTypeClass(), n_args));
                    argCount++;
                    continue m;
                }
            }
            throw new Exception(t + " type is either not registered or not recognized");
        }
        pattern ="^\\s*"+pattern+ "$";//End of parsing;
        //System.out.println("Transformed : "+pattern+" \n");
        return new TransformedPattern(pattern,markCount,argCount,paramTypes.toArray(new ScriptParameterDefinition[0]));
    }

    public static boolean shouldBeLazy(String p, int s){
        //System.out.println("shouldBeLazy "+s+" :"+p);
        while(s<p.length()){
            if(p.charAt(s) == '[' || p.charAt(s) == '(')
                return true;
            s++;
        }
        return false;
    }

    public static String getNameForType(Class type) {
        if (type.isAnnotationPresent(Type.class)) {
            return ((Type) (type.getAnnotation(Type.class))).name();
        } else if (type.isAnnotationPresent(Primitive.class)) {
            return ((Primitive) (type.getAnnotation(Primitive.class))).name();
        }
        return null;
    }

    public static PrimitiveType getPrimitive(ScriptLine parameter) throws Exception {
        //System.out.println("Getting primitive for "+parameter.text);
        for (TypeDefinition primitiveDefinition : ScriptManager.primitives.values()) {
            //System.out.println("Checking primitive, checking if "+infos.getName()+" with regex "+infos.transformedPatterns[0].regex+" is matched by "+parameter);
            if ((primitiveDefinition.matchedPattern(parameter.text))) {
                Pattern p = primitiveDefinition.transformedPattern.getPattern();
                Matcher m = p.matcher(parameter.text);
                if (m.find() && m.groupCount() > 0) {
                    String g = m.group(1);
                    //System.out.println("First group is "+g);
                    Class<?> c = primitiveDefinition.getTypeClass();
                    try {
                        return (PrimitiveType<?>) c.getDeclaredConstructor(String.class).newInstance(g);
                    } catch (IllegalAccessException | InvocationTargetException e) {
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

    public static ScriptWrapper getLoop(ScriptLine line, ScriptCompileGroup compileGroup) throws Exception {
        line = line.with(line.text.replaceFirst("\\s*", ""));


        if (line.text.startsWith("if")) {
            ScriptLine transformed = new ScriptLine(line.text.replaceFirst("\\s*if\\s*", "").replaceAll(":", ""), line.number, line.scriptInstance);
            ScriptExpression scriptExpression = getExpression(transformed, compileGroup);
            //System.out.println("NB Declared variables : "+compileGroup.declaredVariables.size());
            compileGroup.debugVariables();
            if (scriptExpression != null)
                return new ScriptLoop.ScriptLoopIF(scriptExpression,line);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line);
            }
        }
        if (line.text.startsWith("else if")) {
            ScriptLine transformed = new ScriptLine(line.text.replaceFirst("\\s*else if\\s*", "").replaceAll(":", ""), line.number, line.scriptInstance);
            ScriptExpression scriptExpression = getExpression(transformed, compileGroup);
            if (scriptExpression != null)
                return new ScriptLoop.ScriptLoopELSEIF(scriptExpression,line);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line);
            }
        }
        if (line.text.startsWith("else")) {
            return new ScriptLoop.ScriptLoopELSE();
        }
        if (line.text.startsWith("for")) {
            Pattern p = Pattern.compile("\\s*for\\s+(\\w*)\\s+in\\s+(.*):\\s*$");
            Matcher m = p.matcher(line.text);
            if(m.find()){
                String varName = m.group(1);
                compileGroup.add(varName);
                String array = m.group(2);
                //System.out.println(array+" " +line.toString());
                ScriptExpression scriptExpression = getExpression(line.with(array), compileGroup);
                Class<? extends ScriptElement> type;
                if(scriptExpression == null)
                    throw new ScriptException.ScriptUnknownExpressionException(line);
                if (scriptExpression.getClass().getAnnotation(Expression.class) != null)
                    type = getType(scriptExpression.getClass().getAnnotation(Expression.class).patterns()[scriptExpression.getMatchedIndex()].split(":")[1]);
                else
                    type = scriptExpression.getReturnType();
                assert type != null;
                if (!type.isAssignableFrom(TypeArray.class)) {
                    throw new ScriptException.ScriptTypeException(line, TypeArray.class, type);
                }
                return new ScriptLoop.ScriptLoopFOR(varName, scriptExpression, line);
            }
            throw new ScriptException.ScriptSyntaxException(line,"Incorrect for-loop definition");
        }
        if (line.text.startsWith("while")) {
            ScriptLine transformed = line.with(line.text.replaceFirst("\\s*while\\s*", "").replaceAll(":", ""));
            ScriptExpression scriptExpression = getExpression(transformed, compileGroup);
            if (scriptExpression != null)
                return new ScriptLoop.ScriptLoopWHILE(scriptExpression,line);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(transformed);
            }
        }
        return null;
    }




    public static BlockDefinition findBlockDefinition(ScriptLine head) {
        for(BlockDefinition d : ScriptManager.blocks){
            Matcher m = d.getRegex().matcher(head.text);
            if(m.matches())
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
