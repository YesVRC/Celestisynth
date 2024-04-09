package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.api.item.TieredItemStats;
import com.aqutheseal.celestisynth.api.misc.CodecJsonDataManager;

public class CSDataLoaders {
    public static final CodecJsonDataManager<TieredItemStats> TIERED_ITEM_STATS = new CodecJsonDataManager<>("tiered_item_stats", TieredItemStats.CODEC);
}
