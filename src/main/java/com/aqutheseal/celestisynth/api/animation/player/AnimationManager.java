package com.aqutheseal.celestisynth.api.animation.player;

import com.aqutheseal.celestisynth.common.network.s2c.SetAnimationPacket;
import com.aqutheseal.celestisynth.manager.CSConfigManager;
import com.aqutheseal.celestisynth.manager.CSNetworkManager;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public class AnimationManager {

    public static void playAnimation(Level level, PlayerAnimationContainer animation) {
        playAnimation(level, animation, LayerManager.MAIN_LAYER);
    }

    public static void playAnimation(Level level, PlayerAnimationContainer animation, int layerIndex) {
        if (level.isClientSide()) {
            playAnimation(animation, layerIndex);
        }
    }

    public static void playAnimation(PlayerAnimationContainer animation, int layerIndex) {
        CSNetworkManager.sendToServer(new SetAnimationPacket(layerIndex, animation.animationId()));
    }

    public static void playAnimation(@Nullable KeyframeAnimation animation, ModifierLayer<IAnimation> layer) {
        boolean isFirstPersonModelLoaded = ModList.get().isLoaded("firstpersonmod");

        if (animation == null) {
            layer.setAnimation(null);
        } else {
            if (CSAnimator.animationData.containsValue(layer) || CSAnimator.otherAnimationData.containsValue(layer) || CSAnimator.mirroredAnimationData.containsValue(layer)) {
                //AbstractFadeModifier fade = AbstractFadeModifier.standardFadeIn(0, Ease.CONSTANT);
                layer.setAnimation(new KeyframeAnimationPlayer(animation)
                        .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                        .setFirstPersonConfiguration(new FirstPersonConfiguration()
                                .setShowRightArm(!isFirstPersonModelLoaded && CSConfigManager.CLIENT.showRightArmOnAnimate.get()).setShowRightItem(!isFirstPersonModelLoaded)
                                .setShowLeftArm(!isFirstPersonModelLoaded && CSConfigManager.CLIENT.showLeftArmOnAnimate.get()).setShowLeftItem(!isFirstPersonModelLoaded)
                        )
                );
            }
        }
    }
}
