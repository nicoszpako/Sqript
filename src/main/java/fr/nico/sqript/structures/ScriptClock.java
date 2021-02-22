package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptException;

public class ScriptClock {

    ScriptContext context;

    public ScriptClock(ScriptContext context) {
        this.context = context;
    }

    public ScriptClock(ScriptContext context, long delay) {
        this.context = context;
        this.delay = delay;
    }

    public long delay;

    public IScript current;

    public boolean stopped = false;

    //Starts a new ScriptClock with the given root IScript.
    public void start(IScript next) throws ScriptException {
        do{
            //System.out.println(">> Executing : "+next.getClass().getSimpleName()+" with "+l(context.hashCode())+(next.line!=null?" at n°"+next.line.number:""));
            next = next.run(context);
            //System.out.println("> Next to run is : "+(next==null?"null":next.getClass().getSimpleName()));
            current = next;

        }while(next != null && !stopped);
    }

    //Debug purpose
    public char l(int hash){
        return (char)(97+(hash%26));
    }

    public void stop(){
        stopped = true;
    }

    public void resume() throws ScriptException {
        stopped = false;
        start(current);
    }
}
