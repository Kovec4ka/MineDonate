package ru.alastar.minedonate.network.manage.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.alastar.minedonate.MineDonate;
import ru.alastar.minedonate.network.INetworkTask;
import ru.alastar.minedonate.network.manage.packets.FreezeObjectPacket;
import ru.alastar.minedonate.network.manage.packets.ManageResponsePacket;
import ru.alastar.minedonate.rtnl.ModManager;
import ru.alastar.minedonate.rtnl.ModNetworkTaskProcessor;
import ru.alastar.minedonate.rtnl.common.Account;
import ru.alastar.minedonate.rtnl.common.Shop;

import java.util.UUID;

public class FreezeObjectServerPacketHandler implements IMessageHandler<FreezeObjectPacket, IMessage>, INetworkTask<FreezeObjectPacket, IMessage> {

    public FreezeObjectServerPacketHandler() {

    }

    @Override
    public IMessage onMessage(FreezeObjectPacket message, MessageContext ctx) {

        ModNetworkTaskProcessor.processTask((INetworkTask) this, message, ctx);

        return null;

    }

    @Override
    public IMessage onMessageProcess(FreezeObjectPacket message, MessageContext ctx) {

        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

        Account acc = MineDonate.getAccount(serverPlayer);

        if (message.type == FreezeObjectPacket.Type.SHOP) {

            if (!MineDonate.checkShopAndLoad(message.shopId)) {

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, message.bool ? ManageResponsePacket.ResponseCode.FREEZ : ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.ERROR_SHOP_NOTFOUND);

            }

            Shop s = MineDonate.shops.get(message.shopId);

            if ((message.bool && acc.canFreezeShop(s.owner)) || (!message.bool && acc.canUnFreezeShop(s.owner))) {

                if (message.bool && s.isFreezed) {

                    return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.FREEZ, ManageResponsePacket.ResponseStatus.ERRROR_ACCOUNT_ALREADY_FREEZED);

                } else if (!message.bool && !s.isFreezed) {

                    return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.ERRROR_ACCOUNT_NO_FREEZED);

                }

                if (message.bool && (message.reason == null || message.reason.isEmpty() || message.reason.length() > 140)) {

                    return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, ManageResponsePacket.ResponseCode.FREEZ, ManageResponsePacket.ResponseStatus.ERROR_UNKNOWN);

                }

                if (message.bool) {

                    ModManager.freezeShop(s, serverPlayer.getName(), message.reason);

                } else {

                    ModManager.unFreezeShop(s, serverPlayer.getName());

                }

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, message.bool ? ManageResponsePacket.ResponseCode.FREEZ : ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.OK);

            } else {

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.SHOP, message.bool ? ManageResponsePacket.ResponseCode.FREEZ : ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.ERROR_ACCESS_DENIED);

            }

        } else if (message.type == FreezeObjectPacket.Type.ACCOUNT) {

            if ((message.bool && acc.canFreezeOtherAccount()) || (!message.bool && acc.canUnFreezeOtherAccount())) {

                Account accFreez = MineDonate.getAccountWithoutRegister(UUID.fromString(message.account));

                if (accFreez == null) {

                    return new ManageResponsePacket(ManageResponsePacket.ResponseType.ACCOUNT, message.bool ? ManageResponsePacket.ResponseCode.FREEZ : ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.ERROR_ACCOUNT_NOTFOUND);

                }

                if (message.bool && accFreez.freezedShopCreate()) {

                    return new ManageResponsePacket(ManageResponsePacket.ResponseType.ACCOUNT, ManageResponsePacket.ResponseCode.FREEZ, ManageResponsePacket.ResponseStatus.ERRROR_ACCOUNT_ALREADY_FREEZED);

                } else if (!message.bool && !accFreez.freezedShopCreate()) {

                    return new ManageResponsePacket(ManageResponsePacket.ResponseType.ACCOUNT, ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.ERRROR_ACCOUNT_NO_FREEZED);

                }

                if (message.bool && (message.reason == null || message.reason.isEmpty() || message.reason.length() > 140)) {

                    return new ManageResponsePacket(ManageResponsePacket.ResponseType.ACCOUNT, ManageResponsePacket.ResponseCode.FREEZ, ManageResponsePacket.ResponseStatus.ERROR_UNKNOWN);

                }

                if (message.bool) {

                    ModManager.freezePlayer(accFreez, serverPlayer.getName(), message.reason);

                } else {

                    ModManager.unFreezePlayer(accFreez, serverPlayer.getName());

                }

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.ACCOUNT, message.bool ? ManageResponsePacket.ResponseCode.FREEZ : ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.OK);

            } else {

                return new ManageResponsePacket(ManageResponsePacket.ResponseType.ACCOUNT, message.bool ? ManageResponsePacket.ResponseCode.FREEZ : ManageResponsePacket.ResponseCode.UNFREEZ, ManageResponsePacket.ResponseStatus.ERROR_ACCESS_DENIED);

            }

        }

        return null;

    }

}