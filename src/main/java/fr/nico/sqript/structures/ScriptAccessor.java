package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.types.ScriptType;

import java.util.Objects;
import java.util.regex.Pattern;

public class ScriptAccessor {

    //Permet d'accéder à une variable d'un event

    public ScriptType element;
    private Pattern pattern;
    public String key;
    Class<? extends ScriptEvent> eventType;
    int lineCounter;

    /***
     * Very important, allows Sqript to recognize a variable efficiently with this hash code.
      */
    public int hash;

    public ScriptAccessor(ScriptType element, String match) {
        this.element = element;
        try {
            this.pattern = ScriptDecoder.patternToRegex(match).pattern;
            this.key = match;
            if(pattern == null)
                throw new ScriptException.ScriptPatternError("");
        } catch (ScriptException.ScriptPatternError scriptPatternError) {
            ScriptManager.log.error("Error trying to generate an accessor : "+pattern+" in "+eventType.getSimpleName());
            scriptPatternError.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Setting with match : "+match+" giving : "+match.hashCode());
        this.hash=match.hashCode();
    }

    public ScriptAccessor(ScriptType element, int hash) {
        this.element = element;
        this.hash=hash;
    }

    public ScriptAccessor(ScriptType element, String match, int varHash) {
        this.element = element;
        try {
            this.pattern = ScriptDecoder.patternToRegex(match).pattern;
            this.key = match;
        } catch (ScriptException.ScriptPatternError scriptPatternError) {
            ScriptManager.log.error("Error trying to generate an accessor : "+pattern+" in "+eventType.getSimpleName());
            scriptPatternError.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.hash = varHash;
    }

    @Override
    public String toString() {
        return element.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, eventType, lineCounter);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
