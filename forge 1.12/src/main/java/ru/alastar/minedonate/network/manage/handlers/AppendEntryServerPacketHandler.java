package ru.alastar.minedonate.network.manage.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.alastar.minedonate.MineDonate;
import ru.alastar.minedonate.network.INetworkTask;
import ru.alastar.minedonate.network.manage.packets.AppendEntryPacket;
import ru.alastar.minedonate.network.manage.packets.ManageResponsePacket;
import ru.alastar.minedonate.rtnl.ModManager;
import ru.alastar.minedonate.rtnl.ModNetworkTaskProcessor;
import ru.alastar.minedonate.rtnl.common.Account;
import ru.alastar.minedonate.rtnl.common.Shop;

public class AppendEntryServerPacketHandler implements IMessageHandler<AppendEntryPacket, IMessage>, INetworkTask<AppendEntryPacket, IMessage> {

    public AppendEntryServerPacketHandler() {

    }

    @Override
    public IMessage onMessage(AppendEntryPacket message, MessageContext ctx) {

        ModNetworkTaskProcessor.processTask((INetworkTask) this, message, ctx);

        return null;

    }

    @Override
    public IMessage onMessageProcess(AppendEntryPacket message, MessageContext ctx) {

        if (!MineDonate.checkShopExists(message.shopId)) {

            return new ManageResponsePacket(ManageResponsePacket.ResponseType.OBJ, ManageResponsePacket.ResponseCode.UPPEND, ManageResponsePacket.ResponseStatus.ERROR_SHOP_NOTFOUND);

        }

        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

        Shop s = MineDonate.shops.get(message.shopId);

        Account acc = MineDonate.getAccount(serverPlayer);

        if (acc.canEditShop(s.owner)) {

            if (s.isFreezed) {

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.OBJ, ManageResponsePacket.ResponseCode.UPPEND, ManageResponsePacket.ResponseStatus.ERROR_SHOP_FREEZED);

            }

            if (!MineDonate.checkCatExists(s.sid, message.catId)) {

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.OBJ, ManageResponsePacket.ResponseCode.UPPEND, ManageResponsePacket.ResponseStatus.ERROR_CAT_NOTFOUND);

            }

            switch (s.cats[message.catId].getCatType()) {

                case ITEMS:

                    if (acc.ms.currentItemStack == null) {

                        return new ManageResponsePacket(ManageResponsePacket.ResponseType.OBJ, ManageResponsePacket.ResponseCode.UPPEND, ManageResponsePacket.ResponseStatus.ERROR_UNKNOWN);

                    }

                    int merchId = ModManager.canUppendAnotherItemInShop(acc, s, message.catId);

                    if (merchId == -1) {

                        return new ManageResponsePacket(ManageResponsePacket.ResponseType.OBJ, ManageResponsePacket.ResponseCode.UPPEND, ManageResponsePacket.ResponseStatus.ERROR_ENTRY_NOTFOUND);

                    }

                    ModManager.uppendItemInShop(acc, s, message.catId, merchId);

                    break;

                default:
                    break;

            }

            return new ManageResponsePacket(ManageResponsePacket.ResponseType.OBJ, ManageResponsePacket.ResponseCode.UPPEND, ManageResponsePacket.ResponseStatus.OK);

        } else {

            return new ManageResponsePacket(ManageResponsePacket.ResponseType.OBJ, ManageResponsePacket.ResponseCode.UPPEND, ManageResponsePacket.ResponseStatus.ERROR_ACCESS_DENIED);

        }

    }

}