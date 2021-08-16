package fr.nico.sqript.network;

import fr.nico.sqript.ScriptDataManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.types.interfaces.ISerialisable;
import fr.nico.sqript.types.ScriptType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ScriptSyncDataMessage implements IMessage {

    public ScriptSyncDataMessage() {
    }

    String key;
    ScriptType element;

    public ScriptSyncDataMessage(String key, ScriptType element) {
        this.key = key;
        this.element = element;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        key = ByteBufUtils.readUTF8String(buf);
        //If the element was not serialized, return void.
        if(!buf.readBoolean()){
            return;
        }
        String type = ByteBufUtils.readUTF8String(buf);
        try {
            element = ScriptDataManager.instanciateWithData(type, ByteBufUtils.readTag(buf));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScriptNetworkManager.put(key, element);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, key);
        if (element instanceof ISerialisable) {
            buf.writeBoolean(true);
            ByteBufUtils.writeUTF8String(buf, ScriptDecoder.getNameOfType(element.getClass()));
            ISerialisable s = (ISerialisable) element;
            ByteBufUtils.writeTag(buf, s.write(new NBTTagCompound()));
        }else{
            buf.writeBoolean(false);
        }
    }

    public static class ScriptSyncDataMessageHandler implements IMessageHandler<ScriptSyncDataMessage,ScriptSyncDataMessage> {

        @Override
        public ScriptSyncDataMessage onMessage(ScriptSyncDataMessage message, MessageContext ctx) {
            if(ctx.side == Side.CLIENT){ //Obviously always client-side, but we never know
                ScriptNetworkManager.put(message.key,message.element);
                //System.out.println("Keys are : " + ScriptNetworkManager.syncValue.keySet());
            }
            return null;
        }
    }
}