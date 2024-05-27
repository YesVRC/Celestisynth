package com.aqutheseal.celestisynth.mixin;

import com.aqutheseal.celestisynth.common.registry.CSRarityTypes;
import com.aqutheseal.celestisynth.util.ExtraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(Rarity.class)
public class RarityMixin {
    @Inject(method = "getStyleModifier", at = @At("HEAD"), cancellable = true, remap = false)
    public void getStyleModifier(CallbackInfoReturnable<UnaryOperator<Style>> cir) {
        if (Minecraft.getInstance().player != null) {
            int tickCount = Minecraft.getInstance().player.tickCount;
            if ((Object) this == CSRarityTypes.CELESTIAL) {
                cir.setReturnValue(style -> style.withColor(ExtraUtil.getCelestialColor(tickCount).argbInt()));
            }
        }
    }
}
