package com.aqutheseal.celestisynth.common.compat.apotheosis;

import com.aqutheseal.celestisynth.manager.CSIntegrationManager;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.entity.LivingEntity;

public class CSCompatAP {

    public static float apothRainfallSerenityDrawSpeed(LivingEntity entity) {
        if (!CSIntegrationManager.checkApothicAttributes()) return 0;
        return (float) entity.getAttribute(ALObjects.Attributes.DRAW_SPEED.get()).getValue();
    }

    public static float apothRainfallSerenityDamage(LivingEntity entity) {
        if (!CSIntegrationManager.checkApothicAttributes()) return 0;
        return (float) entity.getAttribute(ALObjects.Attributes.ARROW_DAMAGE.get()).getValue();
    }

    public static float apothRainfallSerenityCritChance(LivingEntity entity) {
        if (!CSIntegrationManager.checkApothicAttributes()) return 0;
        return (float) entity.getAttribute(ALObjects.Attributes.CRIT_CHANCE.get()).getValue();
    }

    public static float apothRainfallSerenityCritDamage(LivingEntity entity) {
        if (!CSIntegrationManager.checkApothicAttributes()) return 0;
        return (float) entity.getAttribute(ALObjects.Attributes.CRIT_DAMAGE.get()).getValue();
    }
}
