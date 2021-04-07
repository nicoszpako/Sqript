package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;

public class ExprReference extends ScriptExpression{

    //Special one (not registered commonly)


    public Class<? extends ScriptElement> type;

    public ScriptExpression stringExpression;

    public ExprReference(Class<? extends ScriptElement> type, ScriptExpression stringExpression) {
        this.type=type;
        this.stringExpression = stringExpression;
    }

    Integer varHash = null;

    public void setVarHash(Integer varHash) {
        this.varHash = varHash;
    }

    public ExprReference(ScriptExpression stringExpression) {
        this.stringExpression = stringExpression;
    }

    public Class<? extends ScriptElement> getReturnType() {
        return type;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) throws ScriptException {
        int varHash = 0;
        if(this.varHash == null){
            String var = stringExpression.get(context).getObject().toString();
            varHash = var.hashCode();
            if(varHash == 0)
                varHash = context.get(line.text);
            return context.get(varHash);
        }else {
            //System.out.println("Getting reference for : "+line.text+", its null ? : "+(context.get(varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.get(this.varHash)==null));
            return context.get(this.varHash);
        }
    }


    @Override
    public boolean set(ScriptContext context,ScriptType to, ScriptType[] parameters) throws ScriptException {
        String var = stringExpression.get(context).getObject().toString();

        if(var.charAt(0)=='$'){
            ScriptAccessor a = ScriptManager.GLOBAL_CONTEXT.getAccessor(this.varHash == null ? var.hashCode() : this.varHash);
            if(a!=null){
                a.element=to;
            }else{
                ScriptManager.GLOBAL_CONTEXT.put(new ScriptAccessor(to,var,this.varHash == null ? var.hashCode() : this.varHash));
            }
        }else{
            //System.out.println("Setting reference for : "+var+" with type : "+to);
            ScriptAccessor a = context.getAccessor(this.varHash == null ? var.hashCode() : this.varHash);
            if(a!=null){
                a.element=to;
            }else{
                context.put(new ScriptAccessor(to,var,this.varHash == null ? var.hashCode() : this.varHash));
            }
        }
        return true;
    }
}
