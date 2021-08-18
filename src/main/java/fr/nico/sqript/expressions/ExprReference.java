package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;

public class ExprReference extends ScriptExpression {

    public Class<? extends ScriptElement> type;

    public ScriptExpression stringExpression;

    public boolean global = false;

    @Override
    public void setLine(ScriptToken line) {
        super.setLine(line);
        global = line.getText().startsWith("$");
    }

    public ExprReference(Class<? extends ScriptElement> type, ScriptExpression stringExpression) {
        this.type = type;
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
        int varHash;
        if (global)
            context = ScriptManager.GLOBAL_CONTEXT;
        if (this.varHash == null) {
            String var = stringExpression.get(context).getObject().toString();
            //System.out.println("Getting var hash for : "+var);
            //System.out.println("Context vars are : "+context.printVariables());
            varHash = var.hashCode();
            if (varHash == 0)
                varHash = context.getHash(line.getText());
            return context.getVariable(varHash);
        } else {
            //System.out.println("Getting reference for : "+line.text+", its null ? : "+(context.get(varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.get(this.varHash)==null));
            return context.getVariable(this.varHash);
        }
    }


    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException {
        if (global)
            context = ScriptManager.GLOBAL_CONTEXT;
        if (this.varHash == null) {
            String var = stringExpression.get(context).getObject().toString();
            //System.out.println("Setting  var hash for : "+var+" : "+var.hashCode());
            //System.out.println("Context vars are : "+context.printVariables());
            varHash = context.getHash(line.getText());
            ScriptTypeAccessor typeAccessor = context.getAccessor(var);
            typeAccessor.setElement(to);
        } else {
            //System.out.println("Setting reference for : "+line.text+", its null ? : "+(context.get(varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.get(this.varHash)==null));
            ScriptTypeAccessor typeAccessor = context.getAccessor(varHash);
            typeAccessor.setElement(to);
        }

        return true;
    }
}
