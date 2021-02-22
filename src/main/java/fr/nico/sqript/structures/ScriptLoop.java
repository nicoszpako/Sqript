package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;

public class ScriptLoop extends ScriptWrapper {

    //Concerne tous les blocs pouvant exécuter du code selon une certaine condition.

    public ScriptExpression condition;

    public ScriptLoop(){}

    public boolean broken = false;

    public void doBreak(){
        broken = true;
    }

    public ScriptLoop(IScript wrapped, ScriptExpression condition){
        super(wrapped);
        this.setLine(wrapped.getLine());
        this.condition=condition;
    }

    public ScriptLoop(ScriptExpression condition, ScriptLine line){
        this.condition=condition;
    }

    @Override
    public void setNext(IScript next) {
        super.setNext(next);
        getWrapped().setNext(next);
    }


    public static class ScriptLoopIF extends ScriptLoop
    {
        public ScriptLoopIF elseContainer;

        public ScriptLoopIF(IScript wrapped, ScriptExpression condition) {
            super(wrapped,condition);
        }

        public ScriptLoopIF(ScriptExpression condition, ScriptLine line){super(condition, line);}

        public ScriptLoopIF() {}

        public void setElseContainer(ScriptLoopIF loop){
            if(elseContainer==null){
                elseContainer = loop;
            }
            else elseContainer.setElseContainer(loop);
        }

        @Override
        public void setNext(IScript next) {
            super.setNext(next);
            if(elseContainer!=null)elseContainer.setNext(next);
        }


        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            //System.out.println("At line "+line.number+", condition is : "+(condition==null?"null":condition.getClass()+" "+condition.getMatchedIndex()));
            if((boolean)(condition.get(context).getObject())) {
                return getWrapped();
            }
            else if(elseContainer!=null){
                return elseContainer;
            }
            return getNext(context);
        }
    }

    public static class ScriptLoopELSE extends ScriptLoopIF {

        public ScriptLoopELSE(IScript wrapped, ScriptExpression condition) {
            super(wrapped, condition);
        }

        public ScriptLoopELSE(ScriptExpression condition, ScriptLine line) {
            super(condition,line);
        }

        public ScriptLoopELSE() {
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            return getWrapped();
        }

        @Override
        public void setParent(IScript parent) {
            if(getParent()!=null && !(getParent() instanceof ScriptLoopIF))
                getParent().setParent(parent);
            super.setParent(parent);
        }

    }

    public static class ScriptLoopELSEIF extends ScriptLoopIF
    {

        public IScript elseContainer;

        public ScriptLoopELSEIF(IScript wrapped, ScriptExpression condition) {
            super(wrapped,condition);
        }

        public ScriptLoopELSEIF(ScriptExpression condition, ScriptLine line){super(condition, line);}

        @Override
        public void setParent(IScript parent) {
            if(getParent()!=null && !(getParent() instanceof ScriptLoopIF))
                getParent().setParent(parent);
            super.setParent(parent);
        }

        @Override
        public void setNext(IScript next) {
            super.setNext(next);
            if(elseContainer!=null)elseContainer.setNext(next);
        }
    }

    public static class ScriptLoopFOR extends ScriptLoop
    {

        int varHash;
        ScriptExpression array;
        TypeArray typeArray = null;

        public ScriptLoopFOR(String varName, ScriptExpression array, ScriptLine line){
            this.varHash=varName.hashCode();
            this.array=array;
            //Used to identify the for-loop in the ScriptContexts
            this.hash = line.hashCode();
        }

        @Override
        public IScript getNext(ScriptContext context) {
            if(context.get(hash)==null){
                context.put(new ScriptAccessor(new TypeNumber(-1),hash));
            }
            TypeNumber index = (TypeNumber) context.get(hash);
            index.setObject(index.getObject()+1);
            if(index.getObject()==0) {
                try {
                    typeArray = (TypeArray) array.get(context);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
            if(index.getObject()<typeArray.getObject().size() && !broken) {
                //System.out.println("Added variable "+sa+", now it contains : "+context.printVariables());
                //System.out.println("Executing : "+((ScriptContainer)(wrapped)).subScripts.get(0).getClass().getSimpleName());
                ScriptAccessor sa = new ScriptAccessor((ScriptType<?>) typeArray.getObject().get(index.getObject().intValue()),varHash);
                context.put(sa);
                return getWrapped();
            }else{
                context.remove(varHash);
                context.remove(hash);
            }
            broken = false;
            return super.getNext(context);
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {

            return getNext(context);
        }
    }

    public static class ScriptLoopWHILE extends ScriptLoop
    {

        public ScriptLoopWHILE(IScript wrapped, ScriptExpression condition) {
            super(wrapped,condition);
        }

        public ScriptLoopWHILE(ScriptExpression condition, ScriptLine line){super(condition,line);}

        @Override
        public IScript getNext(ScriptContext context) {
            try {
                if((boolean)condition.get(context).getObject() && !broken)
                    return getWrapped();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            broken = false;
            return super.getNext(context);
        }

        @Override
        public IScript run(ScriptContext context) throws ScriptException {
            if((boolean)condition.get(context).getObject()){
                return getWrapped();
            }else
                return super.getNext(context);
        }
    }

}
