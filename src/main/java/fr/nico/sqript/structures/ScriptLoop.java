package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptLoop extends ScriptWrapper {

    //Concerne tous les blocs pouvant exécuter du code selon une certaine condition.


    public ScriptLoop() {
    }

    @Override
    public void setNext(IScript next) {
        super.setNext(next);
        getWrapped().setNext(next);
    }


    public void build(ScriptToken line, ScriptCompilationContext compileGroup) throws ScriptException {
    }


    public static class ScriptLoopRepeated extends ScriptLoop {
        public boolean broken = false;

        public void doBreak() {
            broken = true;
        }

    }

    @Loop(name = "if", pattern = "if .*")
    public static class ScriptLoopIF extends ScriptLoop {

        public ScriptLoopIF elseContainer;
        public ScriptExpression condition;

        public void setCondition(ScriptExpression condition) {
            this.condition = condition;
        }

        public ScriptLoopIF() {
        }

        public void setElseContainer(ScriptLoopIF loop) {
            if (elseContainer == null) {
                elseContainer = loop;
            } else elseContainer.setElseContainer(loop);
        }

        @Override
        public void setNext(IScript next) {
            super.setNext(next);
            if (elseContainer != null) elseContainer.setNext(next);
        }


        @Override
        public void build(ScriptToken line, ScriptCompilationContext compileGroup) throws ScriptException {
            String condition = line.getText().replaceFirst("\\s*if\\s*", "").trim();
            condition = condition.substring(0, condition.length() - 1);
            line = line.with(condition);
            ScriptToken transformed = new ScriptToken(condition, line.getLineNumber(), line.getScriptInstance());
            ScriptExpression scriptExpression = ScriptDecoder.parse(transformed, compileGroup);
            setLine(line);
            if (scriptExpression != null)
                setCondition(scriptExpression);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line);
            }
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            //System.out.println("At line "+getLine().getLineNumber()+", condition is : "+(condition==null?"null":condition.getClass()+" "+condition.getMatchedIndex()+" "+getWrapped()+ ":"+getWrapped().getLine()));
            if ((boolean) (condition.get(context).getObject())) {
                //System.out.println("Returning wrapped");
                return getWrapped();
            } else if (elseContainer != null) {
                //System.out.println("Returning else container");
                return elseContainer;
            }
            //System.out.println("Returning getNext");
            return getNext(context);
        }
    }

    @Loop(name = "else", pattern = "else\\s*:")
    public static class ScriptLoopELSE extends ScriptLoopIF {


        public ScriptLoopELSE() {
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            //System.out.println("Returning wrapped");
            return getWrapped();
        }

        @Override
        public void setParent(IScript parent) {
            if (getParent() != null && !(getParent() instanceof ScriptLoopIF))
                getParent().setParent(parent);
            super.setParent(parent);
        }

        @Override
        public void build(ScriptToken line, ScriptCompilationContext compileGroup) {

        }
    }

    @Loop(name = "else if", pattern = "else if .*", priority = 1)
    public static class ScriptLoopELSEIF extends ScriptLoopIF {

        public IScript elseContainer;


        @Override
        public void setParent(IScript parent) {
            if (getParent() != null && !(getParent() instanceof ScriptLoopIF))
                getParent().setParent(parent);
            super.setParent(parent);
        }

        @Override
        public void setNext(IScript next) {
            super.setNext(next);
            if (elseContainer != null) elseContainer.setNext(next);
        }

        @Override
        public void build(ScriptToken line, ScriptCompilationContext compileGroup) throws ScriptException {
            line = line.with(line.getText().replaceFirst("\\s*", ""));
            ScriptToken transformed = new ScriptToken(line.getText().replaceFirst("\\s*else if\\s*", "").replaceAll(":", ""), line.getLineNumber(), line.getScriptInstance());
            ScriptExpression scriptExpression = ScriptDecoder.parse(transformed, compileGroup);
            if (scriptExpression != null)
                setCondition(scriptExpression);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line);
            }
        }
    }

    @Loop(name = "for", pattern = "for .*")
    public static class ScriptLoopFOR extends ScriptLoopRepeated {

        int varHash;
        String varName;
        ScriptExpression array;
        TypeArray typeArray = null;

        @Override
        public void build(ScriptToken line, ScriptCompilationContext compileGroup) throws ScriptException {
            line = line.with(line.getText().replaceFirst("\\s*", ""));
            Pattern p = Pattern.compile("\\s*for (\\{.*}) in\\s+(.*):\\s*$");
            Matcher m = p.matcher(line.getText());
            if (m.matches()) {
                //System.out.println("Building for loop");
                varName = m.group(1);
                String array = m.group(2);
                ScriptExpression scriptExpression = ScriptDecoder.parse(line.with(array), compileGroup);
                //System.out.println("Parsed : "+scriptExpression);
                Class<? extends ScriptElement> type;

                if (scriptExpression == null)
                    throw new ScriptException.ScriptUnknownExpressionException(line);

                if (scriptExpression.getClass().getAnnotation(Expression.class) != null)
                    type = ScriptDecoder.parseType(scriptExpression.getClass().getAnnotation(Expression.class).features()[scriptExpression.getMatchedIndex()].type());
                else
                    type = scriptExpression.getReturnType();

                if (type != null && !type.isAssignableFrom(TypeArray.class)) {
                    throw new ScriptException.ScriptTypeException(line, new Class[]{TypeArray.class}, type);
                }
                this.varHash = varName.hashCode();
                //System.out.println("This.varHash : " + varHash);

                this.array = scriptExpression;
                this.setLine(line);
                ScriptTypeAccessor indexAccessor = new ScriptTypeAccessor();
                indexAccessor.setPattern(Pattern.compile(Pattern.quote(varName)+"'s index"));
                indexAccessor.setKey(varName+"'s index");
                indexAccessor.setHash((varName+"'s index").hashCode());
                indexAccessor.setReturnType(TypeNumber.class);
                compileGroup.declaredVariables.add(indexAccessor);
                return;
            }
            throw new ScriptException.ScriptSyntaxException(line, "Incorrect for-loop definition");

        }

        @Override
        public IScript getNext(ScriptContext context) throws ScriptException {
            if (context.getVariable(varName+"'s index", true) == null) {
                //System.out.println("Registering new variable");
                ScriptTypeAccessor indexAccessor = new ScriptTypeAccessor();
                indexAccessor.setElement(new TypeNumber(-1));
                indexAccessor.setPattern(Pattern.compile(Pattern.quote(varName)+"'s index"));
                indexAccessor.setKey(varName+"'s index");
                indexAccessor.setHash((varName+"'s index").hashCode());
                context.put(indexAccessor);
            }
            //System.out.println(context.printVariables());
            //System.out.println(context.printPatterns());
            //System.out.println("Getting : "+varName+" "+context.getVariable(varName+"'s index"));
            TypeNumber index = (TypeNumber) context.getVariable(varName+"'s index", true);

            index.setObject(index.getObject() + 1);
            //System.out.println("o for "+varName+" :"+index.getObject());
            typeArray = (TypeArray) array.get(context);

            //System.out.println(typeArray+" "+index.getObject().intValue()+" "+varHash);
            if (context.getVariable(varHash, true) == null) {
                context.put(new ScriptTypeAccessor(typeArray.get(index.getObject().intValue()), varHash));
            }
            //System.out.println("This.varHash : "+varHash);
            //System.out.println("typeArray is null : " + (typeArray == null) + " " + array.getClass() + " " + array.getMatchedIndex());
            if (index.getObject() < typeArray.getObject().size() && !broken) {
                //System.out.println("Added variable "+varName+", now it contains : "+context.printVariables());
                ScriptTypeAccessor sa = new ScriptTypeAccessor((ScriptType<?>) typeArray.getObject().get(index.getObject().intValue()), varHash);
                context.put(sa);
                return getWrapped();
            } else {
                //We exit the loop, we clear the data.
                index.setObject(-1d);
            }
            broken = false;
            return super.getNext(context);
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            return getNext(context);
        }
    }

    @Loop(name = "while", pattern = "while .*")
    public static class ScriptLoopWHILE extends ScriptLoopRepeated {
        public ScriptExpression condition;


        @Override
        public void build(ScriptToken line, ScriptCompilationContext compileGroup) throws ScriptException {
            line = line.with(line.getText().replaceFirst("\\s*", ""));
            ScriptToken transformed = line.with(line.getText().replaceFirst("\\s*while\\s*", "").replaceAll(":", ""));
            ScriptExpression scriptExpression = ScriptDecoder.parse(transformed, compileGroup);
            this.setLine(line);
            if (scriptExpression != null)
                this.condition = scriptExpression;
            else {
                throw new ScriptException.ScriptUnknownExpressionException(transformed);
            }
        }

        @Override
        public IScript getNext(ScriptContext context) throws ScriptException {
            try {
                if ((boolean) condition.get(context).getObject() && !broken)
                    return getWrapped();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            broken = false;
            return super.getNext(context);
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            if ((boolean) condition.get(context).getObject()) {
                return getWrapped();
            } else
                return super.getNext(context);
        }


    }

}
