package ru.alastar.minedonate.network.manage.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.alastar.minedonate.MineDonate;
import ru.alastar.minedonate.network.INetworkTask;
import ru.alastar.minedonate.network.manage.packets.ManageResponsePacket;
import ru.alastar.minedonate.network.manage.packets.RenameShopPacket;
import ru.alastar.minedonate.rtnl.ModManager;
import ru.alastar.minedonate.rtnl.ModNetworkTaskProcessor;
import ru.alastar.minedonate.rtnl.common.Shop;

public class RenameShopServerPacketHandler implements IMessageHandler<RenameShopPacket, IMessage>, INetworkTask<RenameShopPacket, IMessage> {

    public RenameShopServerPacketHandler() {

    }

    @Override
    public IMessage onMessage(RenameShopPacket message, MessageContext ctx) {

        ModNetworkTaskProcessor.processTask((INetworkTask) this, message, ctx);

        return null;

    }

    @Override
    public IMessage onMessageProcess(RenameShopPacket message, MessageContext ctx) {

        if (!MineDonate.checkShopAndLoad(message.shopId)) {

            return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.RENAME, ManageResponsePacket.ResponseStatus.ERROR_SHOP_NOTFOUND);

        }

        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

        Shop s = MineDonate.shops.get(message.shopId);

        if (MineDonate.getAccount(serverPlayer).canRenameShop(s.owner)) {

            if (s.isFreezed) {

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.RENAME, ManageResponsePacket.ResponseStatus.ERROR_SHOP_FREEZED);

            }

            if (message.name == null || message.name.isEmpty() || message.name.length() > 140) {

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.RENAME, ManageResponsePacket.ResponseStatus.ERROR_UNKNOWN);

            }

            ModManager.renameShop(s, message.name);

            return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.RENAME, ManageResponsePacket.ResponseStatus.OK);

        } else {

            return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.RENAME, ManageResponsePacket.ResponseStatus.ERROR_ACCESS_DENIED);

        }

        //return new ManageResponsePacket ( ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.RENAME, ManageResponsePacket.ResponseStatus.OK ) ;

    }

}