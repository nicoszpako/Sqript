package fr.nico.sqript.compiling;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptBlockEvent;
import fr.nico.sqript.meta.BlockDefinition;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptInstance;
import fr.nico.sqript.structures.ScriptLoop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Chargement du script. Converti un ensemble de lignes en un ensemble de blocs (et sous-blocs) pouvant être exécutés selon un ScriptContext donné (lors du runtime).
 */
public class ScriptLoader {
    public File file;
    public String name;

    public ScriptLoader(File file) {
        this.file = file;
        this.name = file.getName().replaceFirst("[.][^.]+$", "");
    }


    public static void dispScriptTree(IScript s, int i) {
        String tab = "";
        for (int j = 0; j < i; j++) tab += "|    ";
        String actionString = (s instanceof ScriptAction ? (s.toString().isEmpty() ? " <" + ((ScriptAction) s).getMatchedName() + "> " + "(" + ((ScriptAction) s).getParameters() + ")" : s.toString()) : "");
        String eventString = (s instanceof ScriptBlockEvent ? ((ScriptBlockEvent)s).eventType.getSimpleName() : "");
        ScriptManager.log.info(tab + (s.parent != null ? s.parent.getClass().getSimpleName() + " >> " : "") + s.getClass().getSimpleName() + eventString + actionString + " -> " + ((s.next != null ? s.next.getClass().getSimpleName() : "[null]")));
        if (s instanceof ScriptLoop) {
            ScriptLoop sl = (ScriptLoop) s;
            if (sl instanceof ScriptLoop.ScriptLoopIF) {
                ScriptLoop.ScriptLoopIF si = (ScriptLoop.ScriptLoopIF) sl;
                ScriptManager.log.info(tab + si.condition);
            }
            if (sl.getWrapped() != null)
                dispScriptTree(sl.getWrapped(), i + 1);
            else
                //System.out.println(tab + " No wrapped IScript's !");
                if (sl instanceof ScriptLoop.ScriptLoopIF) {
                    ScriptLoop.ScriptLoopIF si = (ScriptLoop.ScriptLoopIF) sl;
                    ScriptManager.log.info(tab+si.condition);
                    if (si.elseContainer != null) dispScriptTree(si.elseContainer, i);
                }
        } else if (s instanceof ScriptBlock) {
            ((ScriptBlock) (s)).displayTree(i + 1);
        }
        if (s.next != null)
            dispScriptTree(s.next, i);
    }

    public static List<ScriptToken> stringToLines(File file, ScriptInstance instance) throws IOException {
        List<ScriptToken> scriptlines = new ArrayList<>();
        int i = 0;
        for (String s : Files.readAllLines(file.toPath())) {
            scriptlines.add(new ScriptToken(s, i, instance));
            i++;
        }
        return scriptlines;
    }


    public ScriptInstance loadScript() throws ScriptException.ScriptExceptionList {
        ScriptException.ScriptExceptionList exceptionList = new ScriptException.ScriptExceptionList();
        ScriptManager.log.info("Loading : " + file.getName());
        ScriptInstance instance = new ScriptInstance(name, file);
        long c = System.currentTimeMillis();
        List<ScriptToken> lines = null;
        try {
            lines = stringToLines(file, instance);
        } catch (IOException e) {
            exceptionList.exceptionList.add(e);
        }
        List<ScriptToken> block = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            ScriptToken line = lines.get(i);
            if (line.getText().matches("\\s*") || line.getText().matches("\\s*#.*"))
                continue;

            //Else, we add the line to the actual block
            if (ScriptDecoder.getTabLevel(line.getText()) > 0 || block.isEmpty()) {
                //System.out.println("Added:"+line);
                block.add(line);
                continue;
            }

            if ((ScriptDecoder.getTabLevel(line.getText()) == 0) || line == lines.get(lines.size() - 1)) {//si nouveau trigger ou derniere ligne du fichier
                //System.out.println("Processing block : "+line+" with size : "+block.size());
                //The block is ended, we process it
                try {
                    loadBlock(block, instance);
                } catch (Exception e) {
                    exceptionList.exceptionList.add(e);
                }
                block.clear();
                block.add(line);//Adding the current header
            }

        }
        try {
            loadBlock(block, instance);
        } catch (Exception e) {
            exceptionList.exceptionList.add(e);
        }
        if (!exceptionList.exceptionList.isEmpty())
            throw exceptionList;

        ScriptManager.log.info("Finished loading " + file.getName() + ", it took : " + (System.currentTimeMillis() - c) + " ms");
        return instance;
    }

    public void loadBlock(List<ScriptToken> block, ScriptInstance instance) throws Exception {
        if (block.isEmpty())
            return;
        ScriptToken head = block.remove(0);
        //System.out.println("Loading block : "+head+" "+block);
        if (!block.isEmpty()) {
            BlockDefinition blockDefinition = ScriptDecoder.findBlockDefinition(head);
            if (blockDefinition == null)
                throw new ScriptException.ScriptUnknownTokenException(head);
            if (blockDefinition.getFeature().side().isValid() && (!ScriptManager.RELOADING || blockDefinition.isReloadable())) {
                Class scriptBlockClass = blockDefinition.getBlockClass();
                ScriptBlock scriptBlock = (ScriptBlock) scriptBlockClass.getConstructor(ScriptToken.class).newInstance(head);
                scriptBlock.setLine(head);
                scriptBlock.setScriptInstance(instance);
                scriptBlock.init(new ScriptBlock.ScriptLineBlock("main", block));
            }
        }

    }


}
