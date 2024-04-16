package com.aqutheseal.celestisynth.common.network.c2s;

import com.aqutheseal.celestisynth.api.animation.player.AnimationManager;
import com.aqutheseal.celestisynth.api.animation.player.CSAnimator;
import com.aqutheseal.celestisynth.api.animation.player.LayerManager;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateAnimationToAllPacket {
    private final int layerIndex;
    private final int playerId;
    private final int animId;

    public UpdateAnimationToAllPacket(int layerIndex, int playerId, int animId) {
        this.layerIndex = layerIndex;
        this.playerId = playerId;
        this.animId = animId;
    }

    public UpdateAnimationToAllPacket(FriendlyByteBuf buf) {
        this.layerIndex = buf.readInt();
        this.playerId = buf.readInt();
        this.animId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(layerIndex);
        buf.writeInt(playerId);
        buf.writeInt(animId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft instance = Minecraft.getInstance();
            var player = instance.level.getEntity(playerId);
            animatePlayer(layerIndex, animId, (AbstractClientPlayer) player);
        });
        return true;
    }

    public static void animatePlayer(int layerIndex, int animId, AbstractClientPlayer player) {
        ModifierLayer<IAnimation> animation = switch (layerIndex) {
            case LayerManager.MAIN_LAYER -> CSAnimator.animationData.get(player);
            case LayerManager.LOW_PRIORITY_LAYER -> CSAnimator.otherAnimationData.get(player);
            case LayerManager.MIRRORED_LAYER -> CSAnimator.mirroredAnimationData.get(player);
            default -> throw new IllegalStateException("Invalid layer index!");
        };
        if (animation != null) AnimationManager.playAnimation(AnimationManager.getAnimFromId(animId).getAnimation(), animation);
    }
}
