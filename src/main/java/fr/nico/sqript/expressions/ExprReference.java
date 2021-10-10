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

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) throws ScriptException {
        int varHash;
        if (global)
            context = ScriptManager.GLOBAL_CONTEXT;
        if (scriptExpression != null) {
            //System.out.println("Expression is :"+scriptExpression);
            String var = scriptExpression.get(context).getObject().toString();
            //System.out.println("Getting var hash for : "+var);
            //System.out.println("Context vars are : "+context.printVariables());
            varHash = var.hashCode();
            if (varHash == 0)
                varHash = context.getHash(line.getText());
            if(context.getAccessor(varHash) != null)
                return context.getVariable(varHash);
            else return new TypeNull();
        } else {
            //System.out.println("Getting reference for : "+line.getText()+", its null ? : "+(context.getVariable(this.varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.getVariable(this.varHash)==null));
            return context.getVariable(this.varHash);
        }
    }


    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException {
        if (global)
            context = ScriptManager.GLOBAL_CONTEXT;
        if (scriptExpression != null) {
            String var = scriptExpression.get(context).getObject().toString();

            varHash = var.hashCode();
            if (varHash == 0)
                varHash = context.getHash(line.getText());
            ScriptTypeAccessor typeAccessor = context.getAccessor(varHash);
            if(typeAccessor != null)
                typeAccessor.setElement(to);
            else{
                typeAccessor = new ScriptTypeAccessor(to,varHash);
                typeAccessor.setKey(var);
                context.put(typeAccessor);
            }
            //System.out.println("Setting var hash for : "+var+" : "+var.hashCode()+" ("+(typeAccessor == null)+")");
            //System.out.println("Context vars are : "+context.printVariables());
        } else {
            //System.out.println("Setting reference for : "+line.text+", its null ? : "+(context.get(varHash)==null));
            //System.out.println("varHash : "+this.varHash);
            //System.out.println("Context vars are : "+context.printVariables());
            //System.out.println("Result is null : "+(context.get(this.varHash)==null));
            ScriptTypeAccessor typeAccessor = context.getAccessor(varHash);
            if(typeAccessor != null)
                typeAccessor.setElement(to);
            else{
                typeAccessor = new ScriptTypeAccessor(to,varHash);
                typeAccessor.setKey(line.getText());
                context.put(typeAccessor);
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "ExprReference:" +line.getText();
    }
}
