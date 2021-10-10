package fr.nico.sqript.events;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeBlock;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

public class EvtBlock {
    @Cancelable
    @Event(
            feature = @Feature(name = "Block placed", description = "Called when a player places a block", examples = "on place of minecraft:stone:\n" + "    cancel event #Prevents stone placing", pattern = "[block] place [of {block}]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that placed the block.", pattern = "player", type = "player"),
                    @Feature(name = "Placed block", description = "The placed block.", pattern = "(placed block|event-block)", type = "block"),
            }
    )
    public static class EvtOnBlockPlace extends ScriptEvent {

        public TypeBlock placedBlock;

        public EvtOnBlockPlace(EntityPlayer player, TypeBlock broken) {
            super(new ScriptTypeAccessor(new TypePlayer(player), "player"), new ScriptTypeAccessor(broken, "(placed block|event-block)"));
            this.placedBlock = broken;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            return (((TypeResource) parameters[0]).getObject().equals(placedBlock.getObject().getBlock().getRegistryName()));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Block broken", description = "Called when a player breaks a block", examples = "on break:\n" + "    cancel event #Prevents players from breaking blocks", pattern = "[block] break [of {block}]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that broke the block.", pattern = "player", type = "player"),
                    @Feature(name = "Broken block", description = "The broken block.", pattern = "(broken block|break-block|event-block)", type = "block"),
            }
    )
    public static class EvtOnBlockBreak extends ScriptEvent {

        public TypeBlock brokenBlock;

        public EvtOnBlockBreak(EntityPlayer player, TypeBlock broken) {
            super(new ScriptTypeAccessor(new TypePlayer(player), "player"), new ScriptTypeAccessor(broken, "(broken block|break-block|event-block)"));
            this.brokenBlock = broken;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            return (((TypeResource) parameters[0]).getObject().equals(brokenBlock.getObject().getBlock().getRegistryName()));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Block right clicked",
                    description = "Called when a player clicks on a block",
                    examples = "on right click on minecraft:diamond_block:\n",
                    pattern = "((1;left)|(2;right)) click on (block|[block of] {block}) [with ((3;left)|(4;right)) hand]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that clicked the block.", pattern = "player", type = "player"),
                    @Feature(name = "Clicked block", description = "The clicked block.", pattern = "(clicked block|click-block)", type = "block"),
                    @Feature(name = "Position block", description = "The position of block.", pattern = "(clicked pos[ition]|click-pos[ition])", type = "array"),
            }

    )
    public static class EvtOnBlockClick extends ScriptEvent {

        public TypeBlock clickedBlock;
        public EnumHand hand;
        public int clickType = 1; //1 = right, 0 = left

        public EvtOnBlockClick(EntityPlayer player, TypeBlock clicked, EnumHand hand, int clickType, BlockPos pos) {
            super(new ScriptTypeAccessor(new TypePlayer(player), "player"), new ScriptTypeAccessor(clicked, "(clicked block|click-block)"), new ScriptTypeAccessor(new TypeArray(SqriptUtils.locactionToArray(pos.getX(), pos.getY(), pos.getZ())), "(clicked pos[ition]|click-pos[ition])"));
            this.clickedBlock = clicked;
            this.hand = hand;
            this.clickType = clickType;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            Object registryName = null;
            if(parameters[0] != null)
                registryName = parameters[0].getObject();

            boolean hand = this.hand == EnumHand.MAIN_HAND;

            if(((marks >> 3) & 1) == 1){
                hand = this.hand == EnumHand.OFF_HAND;
            }

            if (registryName == null)
                return hand;

            return registryName.equals(clickedBlock.getObject().getBlock().getRegistryName()) && hand;
        }
    }


}
