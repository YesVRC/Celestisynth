package com.aqutheseal.celestisynth.common.network.s2c;

import com.aqutheseal.celestisynth.api.item.TieredItemStats;
import com.aqutheseal.celestisynth.common.registry.CSDataLoaders;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class UpdateStatsPacket {
    private static final Codec<Map<ResourceLocation, TieredItemStats>> MAPPER = Codec.unboundedMap(ResourceLocation.CODEC, TieredItemStats.CODEC);
    private final Map<ResourceLocation, TieredItemStats> data;

    public UpdateStatsPacket(Map<ResourceLocation, TieredItemStats> data) {
        this.data = data;
    }

    public UpdateStatsPacket(FriendlyByteBuf buf) {
        this.data = MAPPER.parse(NbtOps.INSTANCE, buf.readNbt()).result().orElseGet(Object2ObjectLinkedOpenHashMap::new);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt((CompoundTag) MAPPER.encodeStart(NbtOps.INSTANCE, data).result().orElse(new CompoundTag()));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(this::setAnimMetadata);
        ctx.get().setPacketHandled(true);

        return true;
    }

    private void setAnimMetadata() {
        CSDataLoaders.TIERED_ITEM_STATS.setData(data);
    }
}
