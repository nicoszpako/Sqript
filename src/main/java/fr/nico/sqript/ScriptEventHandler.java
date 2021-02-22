package fr.nico.sqript;

import fr.nico.sqript.events.EvtOnDrawNameplate;
import fr.nico.sqript.events.EvtOnPlayerHit;
import fr.nico.sqript.events.PlayerEvents;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ScriptEventHandler {

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) throws ScriptException {
        if(event.phase== TickEvent.Phase.START)
            ScriptTimer.tick();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (event.isCancelable()) {
                ScriptContext context = ScriptManager.callEventAndGetContext(new EvtOnDrawNameplate((EntityPlayer)event.getEntity()));
                if((Boolean)context.returnValue.element.getObject())event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerHit(LivingAttackEvent event){
        if(event.getEntity() instanceof EntityPlayer){
            if(event.getSource().getImmediateSource() instanceof EntityPlayer){
                if(ScriptManager.callEvent(new EvtOnPlayerHit((EntityPlayer)event.getEntity(),(EntityPlayer)event.getSource().getImmediateSource()))){
                    event.setCanceled(true);
                }
            }
        }

    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        EntityPlayer player = event.player;
        if(event.phase == TickEvent.Phase.START){
        }else{
            double px = player.prevPosX;
            double py = player.prevPosY;
            double pz = player.prevPosZ;
            if((px-player.posX)!=0 || (py-player.posY)!=0 || (pz-player.posZ)!=0){ //Player moved
                if(ScriptManager.callEvent(new PlayerEvents.EvtOnPlayerMove(player))){ //We post the event OnPlayerEvent and move back the player if the event was cancelled
                    player.setPositionAndUpdate(px,py,pz);
                }
            }
        }
    }
}
