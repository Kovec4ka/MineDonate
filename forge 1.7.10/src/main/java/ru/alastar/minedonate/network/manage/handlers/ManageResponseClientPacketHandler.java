package ru.alastar.minedonate.network.manage.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import ru.alastar.minedonate.gui.ShopGUI;
import ru.alastar.minedonate.network.manage.packets.ManageResponsePacket;

public class ManageResponseClientPacketHandler implements IMessageHandler < ManageResponsePacket, IMessage > {
	
    public ManageResponseClientPacketHandler ( ) {

    }

    @Override
    public IMessage onMessage ( ManageResponsePacket message, MessageContext ctx ) {
    	
    	ShopGUI . instance . setLoading ( false ) ;
    	ShopGUI . instance . initGui ( ) ;
    	
    	System.err.println(message.type + "> " + message.code + "> " + message.status );
        return null;
        
    }
    
}