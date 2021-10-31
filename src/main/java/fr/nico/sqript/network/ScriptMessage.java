package fr.nico.sqript.network;

import fr.nico.sqript.ScriptDataManager;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlockPacket;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptClock;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.interfaces.ISerialisable;
import fr.nico.sqript.types.ScriptType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;


public class ScriptMessage implements IMessage {

    public ScriptMessage() {
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public ScriptType[] getParameters() {
        return parameters;
    }

    public void setParameters(ScriptType[] parameters) {
        this.parameters = parameters;
    }

    String message_id;
    ScriptType[] parameters;

    public ScriptMessage(String message_id, ScriptType[] parameters) {
        this.message_id = message_id;
        this.parameters = parameters;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        message_id = ByteBufUtils.readUTF8String(buf);
        int length = buf.readInt();
        parameters = new ScriptType[length];
        for (int i = 0; i < length; i++) {
            String type = ByteBufUtils.readUTF8String(buf);
            NBTTagCompound tag = ByteBufUtils.readTag(buf);
            try {
                parameters[i] = ScriptDataManager.instanciateWithData(type, tag);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, message_id);
        buf.writeInt(parameters.length);
        for(ScriptType parameter : parameters){
            if (parameter instanceof ISerialisable) {
                ISerialisable s = (ISerialisable) parameter;
                ByteBufUtils.writeUTF8String(buf, Objects.requireNonNull(ScriptDecoder.getNameOfType(parameter.getClass())));
                ByteBufUtils.writeTag(buf, s.write(new NBTTagCompound()));
            }else{
                ScriptManager.log.error("Could not serialize : "+parameter+" for message : "+message_id);
            }

        }

    }

    public static class ScriptMessageHandler implements IMessageHandler<ScriptMessage,ScriptMessage> {

        @Override
        public ScriptMessage onMessage(ScriptMessage message, MessageContext ctx) {
            ScriptBlockPacket m = ScriptNetworkManager.getMessage(message.message_id);
            if(m==null){
                ScriptManager.log.error("No message registered on "+ctx.side+" side with id : "+message.message_id);
                return null;
            }
            ScriptContext context = new ScriptContext(ScriptManager.GLOBAL_CONTEXT);
            try {
                m.wrapParametersInContext(context, message.parameters);
            } catch (ScriptException.ScriptNotEnoughArgumentException e) {
                e.printStackTrace();
            }
            try {
                if(ctx.side == Side.CLIENT){
                    ScriptClock clock = new ScriptClock(context);
                    clock.start(m.getClient());
                }else {
                    context.put(new ScriptTypeAccessor(new TypePlayer(ctx.getServerHandler().player),"(player|sender)"));
                    ScriptClock clock = new ScriptClock(context);
                    clock.start(m.getServer());
                }
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}