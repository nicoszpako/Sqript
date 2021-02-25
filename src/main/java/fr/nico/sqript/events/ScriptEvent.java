package fr.nico.sqript.events;

import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.types.ScriptType;

public abstract class ScriptEvent {

    /*
    Permet de créer des events que l'ont peut écrire dans un script.
    L'objectif est, via une classe propre à chaque event, de construire un ScriptContext à partir d'objets, qui sera transmis au script fils exécuté.
    Un ScriptEvent n'est pas un IScript, il n'est jamais exécuté. C'est juste une classe intermédiaire qui traduit des objets dans un ScriptContext.
    **Utiliser l'annonation Cancelable de forge pour indiquer qu'il est cancelable**
     */

    public ScriptAccessor[] accessors;
    public ScriptEvent(ScriptAccessor... accessors){
        this.accessors=accessors;
    }

    /**
     * @param parameters the parameters of this event
     * @return true if the event block should be execute
     */
    public boolean check(ScriptType[] parameters, int marks){
        return true;
    } ;

}
