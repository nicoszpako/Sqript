package fr.nico.sqript.forge.common;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.ScriptTimer;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.events.*;
import fr.nico.sqript.forge.capabilities.Capability;
import fr.nico.sqript.forge.capabilities.CapabilityProvider;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeBlock;
import fr.nico.sqript.types.TypeItemStack;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

public class ScriptEventHandler {

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) throws ScriptException {
        if (event.phase == TickEvent.Phase.START)
            ScriptTimer.tick();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) throws ScriptException {
        if (event.phase == TickEvent.Phase.START)
            ScriptTimer.tick();
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) throws ScriptException {
        if (event.phase == TickEvent.Phase.START)
            ScriptManager.callEvent(new EvtOnWorldTick(event.world));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) throws ScriptException {
        if (event.phase == TickEvent.Phase.START)
            ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerTick(event.player));
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) throws ScriptException {
        event.setCanceled(ScriptManager.callEvent(new EvtPlayer.EvtOnItemPickup(event.getEntityPlayer(), event.getItem().getItem())));
    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent.Start event) throws ScriptException {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtPlayer.EvtOnItemUse((EntityPlayer) event.getEntity(), event.getItem()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onItemUsed(LivingEntityUseItemEvent.Finish event) throws ScriptException {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtPlayer.EvtOnItemUsed((EntityPlayer) event.getEntity(), event.getItem()))) {
                event.setCanceled(true);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        try {
            if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderHealthBar(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
                Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
            } else if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderChat(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            } else if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderFoodBar(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            } else if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
                //long start = System.currentTimeMillis();
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderOverlay(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            /*
            long end = System.currentTimeMillis();
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            Minecraft.getMinecraft().fontRenderer.drawString((end-start)+"ms",resolution.getScaledWidth()-Minecraft.getMinecraft().fontRenderer.getStringWidth((end-start)+"ms"),resolution.getScaledHeight()-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT,0xFFFFFFFF);
            */
            } else if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderXPBar(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            } else if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderCrosshair(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            }
        } catch (Exception e) {
            Minecraft.getMinecraft().fontRenderer.drawString(e.toString(), 0, 0, 0xFFFF0000);
        }
    }


    @SubscribeEvent
    public void onPlayerSendCommand(CommandEvent event) {
        if (event.getSender() != null && event.getPhase() == EventPriority.NORMAL && event.getSender() instanceof EntityPlayer) {
            ScriptContext context = null;
            try {
                context = ScriptManager.callEventAndGetContext(new EvtPlayer.EvtOnPlayerSendCommand((EntityPlayer) event.getSender(),  event.getCommand().getName(), event.getParameters()));

                if (context.getAccessor("arguments") != null && context.getAccessor("arguments").element != null)
                    event.setParameters(Arrays.stream(((TypeArray) context.getAccessor("arguments").element).getObject().toArray(new ScriptType<?>[0])).map(ScriptElement::getObject).toArray(String[]::new));
                //System.out.println("Cancelled : "+(boolean) context.getReturnValue().element.getObject());
                if ((boolean) context.getReturnValue().element.getObject()) {
                    event.setCanceled(true);
                }
            } catch (ScriptException e) {
                e.printStackTrace();
            }

        }
    }

    @SubscribeEvent
    public void onPlayerSendMessage(ServerChatEvent event) {
        if (event.getPlayer() != null && event.getPhase() == EventPriority.NORMAL) {
            ScriptContext context = null;
            //System.out.println("Calling message sent with player : "+event.getPlayer());
            try {
                context = ScriptManager.callEventAndGetContext(new EvtPlayer.EvtOnPlayerSendMessage(event.getPlayer(), event.getMessage()));
                if (context.getAccessor("message") != null && context.getAccessor("message").element != null && !context.getAccessor("message").element.getObject().equals(event.getMessage()))
                    event.setComponent(new TextComponentString((String) context.getAccessor("message").element.getObject()));
                if ((boolean) context.getReturnValue().element.getObject()) {
                    event.setCanceled(true);
                }
            } catch (ScriptException e) {
                e.printStackTrace();
            }

        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLiving(RenderWorldLastEvent event) {
        ScriptManager.callEvent(new EvtRender.EvtOnRenderWorld(event.getPartialTicks()));
    }




    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtRender.EvtOnDrawNameplate((EntityPlayer) event.getEntity()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onGuiOpened(GuiOpenEvent event) {
        if (ScriptManager.callEvent(new EvtGUI.EvtGUIOpen(event.getGui()))) {
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        ScriptManager.callEvent(new EvtPlayer.EvtOnKeyInputEvent());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        ScriptManager.callEvent(new EvtPlayer.EvtOnMouseInputEvent());
    }

    @SubscribeEvent
    public void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        //System.out.println("On block right click");
        if (event.getEntity() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtBlock.EvtOnBlockClick((EntityPlayer) event.getEntity(),  new TypeBlock(event.getEntityPlayer().getEntityWorld().getBlockState(new BlockPos(event.getPos())), event.getPos(), event.getWorld()), event.getHand(), 1, event.getPos()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtBlock.EvtOnBlockClick((EntityPlayer) event.getEntity(), new TypeBlock(Block.getBlockFromItem(event.getItemStack().getItem()).getDefaultState(), event.getPos(), event.getWorld()), event.getHand(), 0, event.getPos()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtBlock.EvtOnBlockPlace((EntityPlayer) event.getEntity(), new TypeBlock(event.getPlacedBlock(), event.getPos(), event.getWorld())))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (ScriptManager.callEvent(new EvtBlock.EvtOnBlockBreak(event.getPlayer(), new TypeBlock(event.getState(), event.getPos(), event.getWorld())))) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof EntityPlayer) {
            //System.out.println("clicking : "+event.getHand()+" "+event.getSide()+" "+event.getItemStack());
            if (ScriptManager.callEvent(new EvtPlayer.EvtOnItemRightClick((EntityPlayer) event.getEntity(), event.getItemStack(), event.getHand(), event.getSide()))) {
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public void onItemLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity() instanceof EntityPlayer) {
            //System.out.println("clicking : "+event.getHand()+" "+event.getSide()+" "+event.getItemStack());
            if (ScriptManager.callEvent(new EvtPlayer.EvtOnItemLeftClick((EntityPlayer) event.getEntity(), event.getItemStack(), event.getHand(), event.getSide()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerJump((EntityPlayer) event.getEntity()))) {
                event.getEntityLiving().setVelocity(0, 0, 0);
                event.getEntityLiving().velocityChanged = true;
            }
        }
    }


    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerLogin(event.player))) {
            ((EntityPlayerMP) event.player).connection.disconnect(new TextComponentString("Disconnected."));
        }
    }



    @SubscribeEvent
    public void onPlayerHit(LivingAttackEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (event.getSource().getImmediateSource() instanceof EntityPlayer) {
                if (ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerHit((EntityPlayer) event.getEntity(), (EntityPlayer) event.getSource().getImmediateSource()))) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof EntityPlayer && event.getTarget() instanceof EntityPlayer) {
            if (ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerHit((EntityPlayer) event.getTarget(), event.getEntityPlayer()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            Capability capability = player.getCapability(CapabilityProvider.MAIN, null);
            Double px = capability.getPrevX();
            Double py = capability.getPrevY();
            Double pz = capability.getPrevZ();
            if (px != null && py != null && pz != null) {
                if (Math.abs(px - player.posX) > 0.02 || Math.abs(py - player.posY) > 0.02 || Math.abs(pz - player.posZ) > 0.02) { //Player moved
                    if (ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerMove(player))) { //We post the event OnPlayerEvent and move back the player if the event was cancelled
                        player.setPositionAndUpdate(px, py, pz);
                    }
                }
            }
            capability.setPrevZ(player.posZ);
            capability.setPrevY(player.posY);
            capability.setPrevX(player.posX);
        }

    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() == EnumHand.MAIN_HAND && ScriptManager.callEvent(new EvtPlayer.EvtOnEntityInteract(event.getTarget(), event.getHand(), event.getEntityPlayer()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        if (ScriptManager.callEvent(new EvtLiving.EvtOnLivingDamage(event.getEntity(), event.getSource(), event.getAmount()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (ScriptManager.callEvent(new EvtLiving.EvtOnLivingDeath(event.getEntity(), event.getSource()))) {
            event.getEntityLiving().setHealth(1f);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (ScriptManager.callEvent(new EvtLiving.EvtOnLivingFall(event.getEntity(), event.getDistance(), event.getDamageMultiplier()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        ArrayList<ScriptType<?>> list = new ArrayList<>();
        event.getDrops().forEach(entityItem -> {
            list.add(new TypeItemStack(entityItem.getItem()));
        });
        if (ScriptManager.callEvent(new EvtLiving.EvtOnLivingDrops(event.getEntity(), event.getSource(), new TypeArray(list)))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (ScriptManager.callEvent(new EvtLiving.EvtOnEntityJoinWorld(event.getEntity(), event.getWorld()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerDrops(PlayerDropsEvent event) {
        ArrayList<ScriptType<?>> list = new ArrayList<>();
        event.getDrops().forEach(entityItem -> {
            list.add(new TypeItemStack(entityItem.getItem()));
        });
        if (ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerDrops(event.getEntityPlayer(), event.getSource(), new TypeArray(list)))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        if (ScriptManager.callEvent(new EvtPlayer.EvtOnItemToss(event.getPlayer(), event.getEntityItem()))) {
            event.setCanceled(true);
            event.getPlayer().inventory.addItemStackToInventory(event.getEntityItem().getItem());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerRespawn(event.player, event.isEndConquered()));
    }

}