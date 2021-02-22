package fr.nico.sqript.structures;

import net.minecraftforge.fml.common.FMLCommonHandler;

public enum Side {

        SERVER,

        CLIENT,

        RELATIVE,

        BOTH;


        public static Side from(String value){
                if(value.equalsIgnoreCase("server"))
                        return SERVER;
                if(value.equalsIgnoreCase("client"))
                        return CLIENT;
                if(value.equalsIgnoreCase("relative"))
                        return RELATIVE;
                if(value.equalsIgnoreCase("both"))
                        return BOTH;
                return BOTH;
        }

        public boolean isValid(){
                switch(this){
                        case SERVER:
                                return FMLCommonHandler.instance().getSide() == net.minecraftforge.fml.relauncher.Side.SERVER;
                        case CLIENT:
                                return FMLCommonHandler.instance().getSide() == net.minecraftforge.fml.relauncher.Side.CLIENT;
                        case RELATIVE: case BOTH: default:return true;
                }
        }

}