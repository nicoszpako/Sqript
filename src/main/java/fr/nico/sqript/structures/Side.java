package fr.nico.sqript.structures;

import net.minecraftforge.fml.common.FMLCommonHandler;

public enum Side {

        SERVER,

        CLIENT,

        BOTH;


        public static Side from(String value){
                if(value.equalsIgnoreCase("server"))
                        return SERVER;
                if(value.equalsIgnoreCase("client"))
                        return CLIENT;
                if(value.equalsIgnoreCase("both"))
                        return BOTH;
                return null;
        }

        public boolean isValid(){
                switch(this){
                        case SERVER:
                                return FMLCommonHandler.instance().getEffectiveSide().isServer();
                        case CLIENT:
                                return FMLCommonHandler.instance().getEffectiveSide().isClient();
                        case BOTH: default:
                                return true;
                }
        }

        public boolean isEffectivelyValid(){
                switch(this){
                        case SERVER:
                                return FMLCommonHandler.instance().getEffectiveSide() == net.minecraftforge.fml.relauncher.Side.SERVER;
                        case CLIENT:
                                return FMLCommonHandler.instance().getEffectiveSide() == net.minecraftforge.fml.relauncher.Side.CLIENT;
                        case BOTH: default:
                                return true;
                }
        }

}