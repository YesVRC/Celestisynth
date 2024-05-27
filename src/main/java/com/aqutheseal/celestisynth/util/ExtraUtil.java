package com.aqutheseal.celestisynth.util;

import net.minecraft.util.Mth;
import software.bernie.geckolib.core.object.Color;

public class ExtraUtil {
    public static Color getCelestialColor(int tickCount) {
        float rr = Mth.sin(tickCount * 0.3F) * 0.25F;
        float gg = Mth.sin(tickCount * 0.15F) * 0.25F;
        float bb = Mth.cos(tickCount * 0.25F) * 0.25F;
        return Color.ofRGBA(0.75F + rr, 1, 0.75F + bb, 1);
    }
}
