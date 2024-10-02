package com.aqutheseal.celestisynth.manager;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.config.client.CSClientConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public final class CSConfigManager {
    //public static final ForgeConfigSpec COMMON_SPEC;
    //public static final CSCommonConfig COMMON;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final CSClientConfig CLIENT;

    static {
        //final Pair<CSCommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(CSCommonConfig::new);
        final Pair<CSClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(CSClientConfig::new);

        //COMMON_SPEC = commonSpecPair.getRight();
        //COMMON = commonSpecPair.getLeft();

        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }

    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC, Celestisynth.MODID + "/celestisynth-client.toml");
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, Celestisynth.MODID + "/celestisynth-common.toml");
    }
}
