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

    public boolean global = false;

    @Override
    public void setLine(ScriptLine line) {
        super.setLine(line);
        global = line.text.startsWith("$");
    }

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
        if(global)
            context = ScriptManager.GLOBAL_CONTEXT;
        if(this.varHash == null){
            String var = stringExpression.get(context).getObject().toString();
            //System.out.println("Getting var hash for : "+var);
            //System.out.println("Context vars are : "+context.printVariables());
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
        int varHash = 0;
        if(global)
            context = ScriptManager.GLOBAL_CONTEXT;
        if(this.varHash == null){
            String var = stringExpression.get(context).getObject().toString();
            //System.out.println("Setting  var hash for : "+var+" : "+var.hashCode());
            //System.out.println("Context vars are : "+context.printVariables());
            varHash = var.hashCode();
            if(varHash == 0)
                varHash = context.get(line.text);
            ScriptAccessor accessor = new ScriptAccessor(to,varHash);
            accessor.key = var;
            context.put(accessor);
        }else {
            //System.out.println("Setting reference for : "+line.text+", its null ? : "+(context.get(varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.get(this.varHash)==null));
            context.put(new ScriptAccessor(to,this.varHash));
        }

        return true;
    }
}
