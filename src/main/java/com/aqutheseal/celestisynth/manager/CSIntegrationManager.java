package com.aqutheseal.celestisynth.manager;

import net.minecraftforge.fml.ModList;

public class CSIntegrationManager {

    public static boolean checkBetterCombat() {
        return ModList.get().isLoaded("bettercombat");
    }

    public static boolean checkIronsSpellbooks() {
        return ModList.get().isLoaded("irons_spellbooks");
    }

    public static boolean checkApothicAttributes() {
        return ModList.get().isLoaded("attributeslib");
    }
}
