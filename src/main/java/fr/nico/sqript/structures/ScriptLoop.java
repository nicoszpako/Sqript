package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
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


    public void build(ScriptLine line, ScriptCompileGroup compileGroup) throws ScriptException {
    }


    public static class ScriptLoopRepeated extends ScriptLoop {
        public boolean broken = false;

        public void doBreak() {
            broken = true;
        }

    }

    @Loop(name = "if", pattern = "if .*")
    public class ScriptLoopIF extends ScriptLoop {
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
        public void build(ScriptLine line, ScriptCompileGroup compileGroup) throws ScriptException {
            String condition = line.text.replaceFirst("\\s*if\\s*", "").trim();
            condition = condition.substring(0,condition.length()-1);
            line = line.with(condition);
            ScriptLine transformed = new ScriptLine(condition, line.number, line.scriptInstance);
            ScriptExpression scriptExpression = ScriptDecoder.getExpression(transformed, compileGroup);
            setLine(line);
            if (scriptExpression != null)
                setCondition(scriptExpression);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line);
            }
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            //System.out.println("At line "+getLine().number+", condition is : "+(condition==null?"null":condition.getClass()+" "+condition.getMatchedIndex()));
            if ((boolean) (condition.get(context).getObject())) {
                return getWrapped();
            } else if (elseContainer != null) {
                return elseContainer;
            }
            return getNext(context);
        }
    }

    @Loop(name = "else", pattern = "else\\s*:")
    public class ScriptLoopELSE extends ScriptLoopIF {


        public ScriptLoopELSE() {
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            return getWrapped();
        }

        @Override
        public void setParent(IScript parent) {
            if (getParent() != null && !(getParent() instanceof ScriptLoopIF))
                getParent().setParent(parent);
            super.setParent(parent);
        }

        @Override
        public void build(ScriptLine line, ScriptCompileGroup compileGroup) {

        }
    }

    @Loop(name = "else if", pattern = "else if .*", priority = 1)
    public class ScriptLoopELSEIF extends ScriptLoopIF {

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
        public void build(ScriptLine line, ScriptCompileGroup compileGroup) throws ScriptException {
            line = line.with(line.text.replaceFirst("\\s*", ""));

            ScriptLine transformed = new ScriptLine(line.text.replaceFirst("\\s*else if\\s*", "").replaceAll(":", ""), line.number, line.scriptInstance);
            ScriptExpression scriptExpression = ScriptDecoder.getExpression(transformed, new ScriptCompileGroup());
            if (scriptExpression != null)
                setCondition(scriptExpression);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line);
            }
        }
    }

    @Loop(name = "for", pattern = "for .*")
    public class ScriptLoopFOR extends ScriptLoopRepeated {

        int varHash;
        ScriptExpression array;
        TypeArray typeArray = null;

        @Override
        public void build(ScriptLine line, ScriptCompileGroup compileGroup) throws ScriptException {
            line = line.with(line.text.replaceFirst("\\s*", ""));
            Pattern p = Pattern.compile("\\s*for (\\{.*}) in\\s+(.*):\\s*$");
            Matcher m = p.matcher(line.text);
            if (m.matches()) {
                String varName = m.group(1);
                String array = m.group(2);
                //System.out.println("ARRAY IS : " + array + " " + line.toString());
                ScriptExpression scriptExpression = ScriptDecoder.getExpression(line.with(array), compileGroup);
                Class<? extends ScriptElement> type;
                if (scriptExpression == null)
                    throw new ScriptException.ScriptUnknownExpressionException(line);
                if (scriptExpression.getClass().getAnnotation(Expression.class) != null)
                    type = ScriptDecoder.getType(scriptExpression.getClass().getAnnotation(Expression.class).patterns()[scriptExpression.getMatchedIndex()].split(":")[1]);
                else
                    type = scriptExpression.getReturnType();
                assert type != null;
                if (!type.isAssignableFrom(TypeArray.class)) {
                    throw new ScriptException.ScriptTypeException(line, TypeArray.class, type);
                }
                this.varHash = varName.hashCode();
                //System.out.println("This.varHash : " + varHash);
                this.array = scriptExpression;
                this.setLine(line);
                return;
            }
            throw new ScriptException.ScriptSyntaxException(line, "Incorrect for-loop definition");

        }

        @Override
        public IScript getNext(ScriptContext context) throws ScriptException {
            if (context.get(varHash) == null) {
                context.put(new ScriptAccessor(new TypeNumber(-1), varHash));
            }
            TypeNumber index = (TypeNumber) context.get(varHash);
            //System.out.println(index);
            index.setObject(index.getObject() + 1);
            if (index.getObject() == 0) {
                typeArray = (TypeArray) array.get(context);
            }
            //System.out.println("This.varHash : "+varHash);
            //System.out.println("typeArray is null : " + (typeArray == null) + " " + array.getClass() + " " + array.getMatchedIndex());
            if (index.getObject() < typeArray.getObject().size() && !broken) {
                //System.out.println("Added variable "+sa+", now it contains : "+context.printVariables());
                //System.out.println("Executing : "+((ScriptContainer)(wrapped)).subScripts.get(0).getClass().getSimpleName());
                ScriptAccessor sa = new ScriptAccessor((ScriptType<?>) typeArray.getObject().get(index.getObject().intValue()), varHash);
                context.put(sa);
                return getWrapped();
            } else {
                context.remove(varHash);
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
    public class ScriptLoopWHILE extends ScriptLoopRepeated {
        public ScriptExpression condition;


        @Override
        public void build(ScriptLine line, ScriptCompileGroup compileGroup) throws ScriptException {
            line = line.with(line.text.replaceFirst("\\s*", ""));
            ScriptLine transformed = line.with(line.text.replaceFirst("\\s*while\\s*", "").replaceAll(":", ""));
            ScriptExpression scriptExpression = ScriptDecoder.getExpression(transformed, compileGroup);
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
