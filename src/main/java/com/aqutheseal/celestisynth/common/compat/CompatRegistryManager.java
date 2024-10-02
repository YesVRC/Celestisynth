package com.aqutheseal.celestisynth.common.compat;

import com.aqutheseal.celestisynth.common.compat.spellbooks.ISSCompatItems;
import com.aqutheseal.celestisynth.common.compat.spellbooks.ISSItemUtil;
import com.aqutheseal.celestisynth.manager.CSIntegrationManager;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class CompatRegistryManager {

    public static void registerIntegratedRegistries(IEventBus modBus) {
        if (CSIntegrationManager.checkIronsSpellbooks()) {
            ISSCompatItems.SPELLBOOKS_ITEMS.register(modBus);
        }
    }

    public static void manageCompatAttributes(ItemAttributeModifierEvent event) {
        if (CSIntegrationManager.checkIronsSpellbooks()) {
            ISSItemUtil.manageCompatAttributes(event);
        }
    }
}
