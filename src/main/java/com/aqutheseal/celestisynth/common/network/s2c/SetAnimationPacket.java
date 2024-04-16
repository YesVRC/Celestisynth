package com.aqutheseal.celestisynth.common.network.s2c;

import com.aqutheseal.celestisynth.common.network.c2s.UpdateAnimationToAllPacket;
import com.aqutheseal.celestisynth.manager.CSNetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetAnimationPacket {
    private final int layerIndex;
    private final int animId;

    public SetAnimationPacket(int layerIndex, int animId) {
        this.layerIndex = layerIndex;
        this.animId = animId;
    }

    public SetAnimationPacket(FriendlyByteBuf buf) {
        this.layerIndex = buf.readInt();
        this.animId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(layerIndex);
        buf.writeInt(animId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> CSNetworkManager.sendToPlayersNearbyAndSelf(new UpdateAnimationToAllPacket(layerIndex, context.getSender().getId(), this.animId), context.getSender()));
        return true;
    }
}