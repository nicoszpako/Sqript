package fr.nico.sqript.events;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;

public abstract class ScriptEvent {

    /**
    Permet de créer des events que l'ont peut écrire dans un script.
    L'objectif est, via une classe propre à chaque event, de construire un ScriptContext à partir d'objets, qui sera transmis au script fils exécuté.
    Un ScriptEvent n'est pas un IScript, il n'est jamais exécuté. C'est juste une classe intermédiaire qui traduit des objets dans un ScriptContext.
    **Utiliser l'annonation Cancelable de forge pour indiquer qu'il est cancelable**
     */

    private ScriptTypeAccessor[] accessors;

    public ScriptEvent(){
    }

    public ScriptEvent(ScriptTypeAccessor... accessors){
        this.accessors=accessors;
    }

    /**
     * Executed during run time, return true if the given parameters concur with this event configuration in the script file. For an example, the event "on right click on stick" will check if the clicked item in the forge event instance is a stick, and return false otherwise, so that the code inside the event block won't be executed.
     * @param parameters the parameters of this event
     * @return true if the event block should be executed.
     */
    public boolean check(ScriptType[] parameters, int marks){
        return true;
    } ;

    /**
     * Executed during interpretation time, the parsing check of this event will be ignored if this method returns false. Use it as a way to restrain the possible interpretation of your pattern.
     * @param parameters The given parameters of this event instance.
     * @param marks The marks used in this instance.
     * @return True if the given configuration should come to a correct instance of this event (for an example, check if resources give the good type of object).
     */
    public boolean validate(ScriptType[] parameters, int marks){
        return true;
    } ;

    public ScriptTypeAccessor[] getAccessors() {
        return accessors;
    }

    public void setAccessors(ScriptTypeAccessor[] accessors) {
        this.accessors = accessors;
    }

    public boolean checkMark(int markIndex, int marks){
        return ((marks >> markIndex) & 1)==1;
    }

}
