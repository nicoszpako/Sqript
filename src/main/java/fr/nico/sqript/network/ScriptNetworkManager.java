package fr.nico.sqript.network;

import fr.nico.sqript.SqriptForge;
import fr.nico.sqript.blocks.ScriptBlockMessage;
import fr.nico.sqript.types.ScriptType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScriptNetworkManager {

    public static final HashMap<String, ScriptType> syncValue = new HashMap<>();
    private static final List<ScriptBlockMessage> messageList = new ArrayList<>();

    public static ScriptBlockMessage getMessage(String id) {
        return messageList.stream().filter(a->a.name.equals(id)).findAny().orElse(null);
    }

    public static void registerMessage(ScriptBlockMessage message) {
        messageList.add(message);
    }

    public static void init(){
    }

    @SideOnly(Side.CLIENT)
    public static void put(String as,ScriptType element){
        syncValue.remove(as);
        syncValue.put(as,element);
    }

    @SideOnly(Side.CLIENT)
    public static ScriptType get(String as){
        return syncValue.get(as);
    }

    @SideOnly(Side.SERVER)
    public static void send(String as, ScriptType element, EntityPlayer to){
        SqriptForge.channel.sendTo(new ScriptSyncDataMessage(as,element), (EntityPlayerMP) to);
    }

    @SideOnly(Side.SERVER)
    public static void sendToAll(String as, ScriptType element){
        SqriptForge.channel.sendToAll(new ScriptSyncDataMessage(as,element));
    }


}
