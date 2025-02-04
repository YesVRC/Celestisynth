package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.api.item.CSArmorItem;
import com.aqutheseal.celestisynth.api.item.CSArmorMaterials;
import com.aqutheseal.celestisynth.common.entity.helper.MonolithRunes;
import com.aqutheseal.celestisynth.common.item.misc.CelestialCoreItem;
import com.aqutheseal.celestisynth.common.item.misc.CelestialDebuggerItem;
import com.aqutheseal.celestisynth.common.item.misc.StarMonolithItem;
import com.aqutheseal.celestisynth.common.item.weapons.*;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Celestisynth.MODID);

    public static final RegistryObject<Item> CELESTIAL_CORE = ITEMS.register("celestial_core", () -> new CelestialCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> CELESTIAL_CORE_HEATED = ITEMS.register("celestial_core_heated", () -> new CelestialCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SUPERNAL_NETHERITE_INGOT = ITEMS.register("supernal_netherite_ingot", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> CELESTIAL_NETHERITE_INGOT = ITEMS.register("celestial_netherite_ingot", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> LUNAR_SCRAP = ITEMS.register("lunar_scrap", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EYEBOMINATION = ITEMS.register("eyebomination", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STARSTRUCK_SCRAP = ITEMS.register("starstruck_scrap", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STARSTRUCK_FEATHER = ITEMS.register("starstruck_feather", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WINTEREIS_SHARD = ITEMS.register("wintereis_shard", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRISMSON_PIECE = ITEMS.register("crimson_piece", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SOLAR_CRYSTAL_HELMET = ITEMS.register("solar_crystal_helmet", () -> new CSArmorItem(CSArmorMaterials.SOLAR_CRYSTAL, ArmorItem.Type.HELMET, new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SOLAR_CRYSTAL_CHESTPLATE = ITEMS.register("solar_crystal_chestplate", () -> new CSArmorItem(CSArmorMaterials.SOLAR_CRYSTAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SOLAR_CRYSTAL_LEGGINGS = ITEMS.register("solar_crystal_leggings", () -> new CSArmorItem(CSArmorMaterials.SOLAR_CRYSTAL, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SOLAR_CRYSTAL_BOOTS = ITEMS.register("solar_crystal_boots", () -> new CSArmorItem(CSArmorMaterials.SOLAR_CRYSTAL, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> LUNAR_STONE_HELMET = ITEMS.register("lunar_stone_helmet", () -> new CSArmorItem(CSArmorMaterials.LUNAR_STONE, ArmorItem.Type.HELMET, new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> LUNAR_STONE_CHESTPLATE = ITEMS.register("lunar_stone_chestplate", () -> new CSArmorItem(CSArmorMaterials.LUNAR_STONE, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> LUNAR_STONE_LEGGINGS = ITEMS.register("lunar_stone_leggings", () -> new CSArmorItem(CSArmorMaterials.LUNAR_STONE, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> LUNAR_STONE_BOOTS = ITEMS.register("lunar_stone_boots", () -> new CSArmorItem(CSArmorMaterials.LUNAR_STONE, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> STAR_MONOLITH = ITEMS.register("star_monolith", () -> new StarMonolithItem(new Item.Properties(), MonolithRunes.NO_RUNE));
    public static final RegistryObject<Item> STAR_MONOLITH_BLOOD = ITEMS.register("star_monolith_blood", () -> new StarMonolithItem(new Item.Properties(), MonolithRunes.BLOOD_RUNE));
    public static final RegistryObject<Item> TRAVERSER_SPAWN_EGG = ITEMS.register("traverser_spawn_egg", () -> new ForgeSpawnEggItem(CSEntityTypes.TRAVERSER, 917528, 16711680, new Item.Properties()));
    public static final RegistryObject<Item> TEMPEST_SPAWN_EGG = ITEMS.register("tempest_spawn_egg", () -> new ForgeSpawnEggItem(CSEntityTypes.TEMPEST, 0, 0, new Item.Properties()));

    public static final RegistryObject<Item> SOLARIS = ITEMS.register("solaris", () -> new SolarisItem(CSItemTiers.CELESTIAL,  7 - 4, -2.5F, (new Item.Properties()).fireResistant().rarity(CSRarityTypes.CELESTIAL)));
    public static final RegistryObject<Item> CRESCENTIA = ITEMS.register("crescentia", () -> new CrescentiaItem(CSItemTiers.CELESTIAL, 8 - 4, -2.7F, (new Item.Properties()).fireResistant().rarity(CSRarityTypes.CELESTIAL)));
    public static final RegistryObject<Item> BREEZEBREAKER = ITEMS.register("breezebreaker", () -> new BreezebreakerItem(CSItemTiers.CELESTIAL, 5 - 4, -2.0F, (new Item.Properties()).fireResistant().rarity(CSRarityTypes.CELESTIAL)));
    public static final RegistryObject<Item> POLTERGEIST = ITEMS.register("poltergeist", () -> new PoltergeistItem(CSItemTiers.CELESTIAL, 10 - 4, -3.1F, (new Item.Properties()).fireResistant().rarity(CSRarityTypes.CELESTIAL)));
    public static final RegistryObject<Item> AQUAFLORA = ITEMS.register("aquaflora", () -> new AquafloraItem(CSItemTiers.CELESTIAL, 2 - 4, -1.1F, (new Item.Properties()).fireResistant().rarity(CSRarityTypes.CELESTIAL)));
    public static final RegistryObject<Item> RAINFALL_SERENITY = ITEMS.register("rainfall_serenity", () -> new RainfallSerenityItem((new Item.Properties()).fireResistant().durability(1200).rarity(CSRarityTypes.CELESTIAL)));
    public static final RegistryObject<Item> FROSTBOUND = ITEMS.register("frostbound", () -> new FrostboundItem(CSItemTiers.CELESTIAL,  9 - 4, -2.7F, (new Item.Properties()).fireResistant().rarity(CSRarityTypes.CELESTIAL)));
    public static final RegistryObject<Item> KERES = ITEMS.register("keres", () -> new KeresItem(CSItemTiers.CELESTIAL,  10 - 4, -2.9F, (new Item.Properties()).fireResistant().rarity(CSRarityTypes.CELESTIAL)));

    public static final RegistryObject<Item> CELESTIAL_DEBUGGER = ITEMS.register("celestial_debugger", () -> new CelestialDebuggerItem(new Item.Properties().rarity(Rarity.EPIC)));

}
