package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptException;

public abstract class ScriptWrapper extends IScript {

    private IScript wrapped;

    public IScript getWrapped() {
        return wrapped;
    }

    public void wrap(IScript wrap) {
        this.wrapped = wrap;
    }

    public ScriptWrapper(IScript wrapped){
        this.wrapped=wrapped;
    }

    public ScriptWrapper(){}

    @Override
    public IScript run(ScriptContext context) throws ScriptException {
        return wrapped;
    }

    @Override

    public void execute(ScriptContext context) throws ScriptException {
    }
}
