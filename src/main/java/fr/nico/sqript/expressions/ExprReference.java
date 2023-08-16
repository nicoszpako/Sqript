package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;

public class ExprReference extends ScriptExpression {

    public ScriptExpression scriptExpression;

    public boolean global = false;

    @Override
    public void setLine(ScriptToken line) {
        super.setLine(line);
        global = line.getText().startsWith("$");
    }

    Integer varHash = null;

    public void setVarHash(Integer varHash) {
        this.varHash = varHash;
    }

    public ExprReference(ScriptExpression scriptExpression) {
        this.scriptExpression = scriptExpression;
    }

    public ExprReference(int varHash) {
        this.varHash = varHash;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) throws ScriptException {
        int varHash;
        ScriptContext varContext = context;
        if (global)
            varContext = ScriptManager.GLOBAL_CONTEXT;
        if (scriptExpression != null) {
            //System.out.println("Expression is : "+scriptExpression);
            //System.out.println("Getting for global : "+global);
            String var = scriptExpression.get(context).getObject().toString();
            //System.out.println("Getting var hash for : "+var);
            //System.out.println("Context vars are : "+varContext.printVariables());
            varHash = var.hashCode();
            if (varHash == 0)
                varHash = varContext.getHash(line.getText(), false);
            if(varContext.getAccessor(varHash) != null)
                return varContext.getVariable(varHash);
            else return new TypeNull();
        } else {
            //System.out.println("Getting reference for : "+line.getText()+", its null ? : "+(context.getVariable(this.varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.getVariable(this.varHash)==null));
            return varContext.getVariable(this.varHash);
        }
    }


    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException {
        ScriptContext varContext = context;
        if (global) {
            varContext = ScriptManager.GLOBAL_CONTEXT;
        }
        if (scriptExpression != null) {
            //System.out.println("scriptExpression is : "+scriptExpression);
            String var = scriptExpression.get(context).getObject().toString();

            varHash = var.hashCode();
            if (varHash == 0)
                varHash = varContext.getHash(line.getText(), false);
            ScriptTypeAccessor typeAccessor = varContext.getAccessor(varHash);
            if(typeAccessor != null)
                typeAccessor.setElement(to);
            else{
                typeAccessor = new ScriptTypeAccessor(to,varHash);
                typeAccessor.setKey(var);
                varContext.put(typeAccessor);
            }
            //System.out.println("Setting var hash for : "+var+" : "+var.hashCode()+" ("+(typeAccessor == null)+")");
            //System.out.println("Context vars are : "+context.printPatterns());
        } else {
            //System.out.println("Setting reference for : "+line.getText()+", its null ? : "+(context.getAccessor(varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.getAccessor(this.varHash)==null));
            ScriptTypeAccessor typeAccessor = varContext.getAccessor(varHash);
            if(typeAccessor != null)
                typeAccessor.setElement(to);
            else{
                typeAccessor = new ScriptTypeAccessor(to,varHash);
                typeAccessor.setKey(line.getText());
                varContext.put(typeAccessor);
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "ExprReference:" +line.getText();
    }
}
