package fr.nico.sqript.events;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
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
            return parameters[0] instanceof TypeNull || brokenBlock == null || (((TypeResource) parameters[0]).getObject().equals(brokenBlock.getObject().getBlock().getRegistryName()));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Block right clicked",
                    description = "Called when a player clicks on a block",
                    examples = "on right click on minecraft:diamond_block:\n",
                    pattern = "[((1;left)|(2;right))] click on [[a] block of] {block} [with ((3;left)|(4;right)) hand]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that clicked the block.", pattern = "player", type = "player"),
                    @Feature(name = "Clicked block", description = "The clicked block.", pattern = "(clicked block|click-block|block)", type = "block"),
            }

    )
    public static class EvtOnBlockClick extends ScriptEvent {

        public TypeBlock clickedBlock;
        public EnumHand hand;
        public int clickType = 1; //1 = right, 0 = left

        public EvtOnBlockClick(EntityPlayer player, TypeBlock clicked, EnumHand hand, int clickType, BlockPos pos) {

            super(new ScriptTypeAccessor(new TypePlayer(player), "player"), new ScriptTypeAccessor(clicked, "(clicked block|click-block|block)"));
            //System.out.println("Clicked block : "+clicked);
            this.clickedBlock = clicked;
            this.hand = hand;
            this.clickType = clickType;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {

            if (checkMark(1,marks)){
                if (clickType != 0)
                    return false;
            }

            if (checkMark(2,marks)){
                if (clickType != 1)
                    return false;
            }

            //System.out.println("Checking with :"+Arrays.toString(parameters)+" "+parameters[0].getClass()+" "+clickedBlock.getObject()+" "+marks);
            ResourceLocation registryName = null;
            if (parameters[0] != null) {
                TypeBlock block;
                if(parameters[0] instanceof TypeBlock)
                    block = (TypeBlock)parameters[0];
                else
                    block = ScriptManager.parse(parameters[0],TypeBlock.class);
                registryName = block.getObject().getBlock().getRegistryName();
            }

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
