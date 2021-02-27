package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;

public class ExprReference extends ScriptExpression{

    //Special one (not registered commonly)

    public int getVarHash() {
        return varHash;
    }

    public void setVarHash(int varHash) {
        this.varHash = varHash;
    }

    public int varHash;

    public Class<? extends ScriptElement> type;

    public ExprReference(Class<? extends ScriptElement> type) {
        this.type=type;
    }

    public Class<? extends ScriptElement> getReturnType() {
        return type;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        //System.out.println("Getting for : "+line.text+", its null ? : "+(context.get(varHash)==null));
        ScriptType r;
        if((r=context.get(varHash))!=null)
            return r;
        else if (line.text.charAt(0)=='$'){
            return new TypeNull();
        }
        //System.out.println("Returning null in ExprRegerence");
        return null;
    }

    @Override
    public boolean set(ScriptContext context,ScriptType to, ScriptType[] parameters) throws ScriptException {
        if(line.text.charAt(0)=='$'){
            ScriptAccessor a = ScriptManager.GLOBAL_CONTEXT.getAccessor(varHash);
            if(a!=null){
                a.element=to;
            }else{
                ScriptManager.GLOBAL_CONTEXT.put(new ScriptAccessor(to,line.text,varHash));
            }
        }else{
            //System.out.println("Setting reference for : "+line.text+" with type : "+to);
            ScriptAccessor a = context.getAccessor(varHash);
            if(a!=null){
                a.element=to;
            }else{
                context.put(new ScriptAccessor(to,line.text,varHash));
            }
        }
        return true;
    }
}
