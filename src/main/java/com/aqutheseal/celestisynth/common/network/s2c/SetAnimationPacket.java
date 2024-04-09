package com.aqutheseal.celestisynth.common.network.s2c;

import com.aqutheseal.celestisynth.common.network.c2s.UpdateAnimationToAllPacket;
import com.aqutheseal.celestisynth.manager.CSNetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetAnimationPacket {
    private final boolean isOtherLayer;
    private final int animId;

    public SetAnimationPacket(boolean isOtherLayer, int animId) {
        this.isOtherLayer = isOtherLayer;
        this.animId = animId;
    }

    public SetAnimationPacket(FriendlyByteBuf buf) {
        this.isOtherLayer = buf.readBoolean();
        this.animId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isOtherLayer);
        buf.writeInt(animId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> CSNetworkManager.sendToPlayersNearbyAndSelf(new UpdateAnimationToAllPacket(isOtherLayer, context.getSender().getId(), this.animId), context.getSender()));
        return true;
    }
}