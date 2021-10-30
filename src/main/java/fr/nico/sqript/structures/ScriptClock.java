package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
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

    public void start() throws ScriptException {
        start(current.next);
    }

    //Starts a new ScriptClock with the given root IScript.
    public void start(IScript next) throws ScriptException {
        do{
            try{
                //System.out.println(">> Executing : "+next.getClass().getSimpleName()+" with "+l(context.hashCode())+(next.line!=null?" at n°"+next.line.number:""));
                if(next != null){
                    next = next.run(context);
                    //System.out.println("> Next to run is : "+(next==null?"null":next.getClass().getSimpleName()));
                    current = next;
                }else {
                    stop();
                }

            }catch(Exception e){
                if(e instanceof ScriptException)
                    throw e;
                else{
                    ScriptManager.log.error("Error at : "+current.getLine());
                    e.printStackTrace();
                    throw new ScriptException.ScriptWrappedException(next.getLine(),e);
                }
            }

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
