package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeBlock;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtBlock {
    @Cancelable
    @Event(name = "Block placed",
            description = "Called when a player placed a block",
            examples = "on place of stone:",
            patterns = "[block] place [of {block}]",
            accessors = {"player:player","(placed block|event-block):block"
            }
    )
    public static class EvtOnBlockPlace extends ScriptEvent {

        public TypeBlock placedBlock;

        public EvtOnBlockPlace(EntityPlayer player, TypeBlock broken) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),new ScriptTypeAccessor(broken,"(placed block|event-block)"));
            this.placedBlock = broken;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            return (((TypeResource)parameters[0]).getObject().equals(placedBlock.getObject().getBlock().getRegistryName()));
        }
    }
    @Cancelable
    @Event(name = "Block broken",
            description = "Called when a player breaks a block",
            examples = "on break of stone:",
            patterns = "[block] break [of {block}]",
            accessors = {"player:player","(broken block|break-block|event-block):block"
            }
    )
    public static class EvtOnBlockBreak extends ScriptEvent {

        public TypeBlock brokenBlock;

        public EvtOnBlockBreak(EntityPlayer player, TypeBlock broken) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),new ScriptTypeAccessor(broken,"(broken block|break-block|event-block)"));
            this.brokenBlock = broken;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            return (((TypeResource)parameters[0]).getObject().equals(brokenBlock.getObject().getBlock().getRegistryName()));
        }
    }
    @Cancelable
    @Event(name = "Block right clicked",
            description = "Called when a player clicks on a block",
            examples = "on click on block of stone:",
            patterns = "on ((1;left)|(2;right)) click on (block [of {block}]|{block}) [with ((3;left)|(4;right)) hand]",
            accessors = {"player:player","(clicked block|click-block):block"
            }
    )
    public static class EvtOnBlockClick extends ScriptEvent {

        public TypeBlock clickedBlock;
        public EnumHand hand;
        public int clickType = 1; //1 = right, 0 = left

        public EvtOnBlockClick(EntityPlayer player, TypeBlock clicked, EnumHand hand, int clickType, BlockPos pos) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),new ScriptTypeAccessor(clicked,"(clicked block|click-block)"));
            this.clickedBlock = clicked;
            this.hand = hand;
            this.clickType = clickType;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            Object registryName = parameters[0]==null ? parameters[1].getObject() == null ? null : parameters[1].getObject() : parameters[0].getObject();

            boolean hand = true;
            if (this.hand == EnumHand.MAIN_HAND)
                hand = ((marks >> 4) & 1)==1;

            if (this.hand == EnumHand.OFF_HAND)
                hand = ((marks >> 3) & 1)==1;

            hand = hand | marks == 0; //Case in which no hand has been configured

            if(registryName == null)
                return hand;


            return registryName.equals(clickedBlock.getObject().getBlock().getRegistryName()) && hand;
        }
    }



}
