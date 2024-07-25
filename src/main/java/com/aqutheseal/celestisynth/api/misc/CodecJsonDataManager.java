package com.aqutheseal.celestisynth.api.misc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

// Shoutout to DataBuddy (written by Commoble) for this implementation
public class CodecJsonDataManager<T> extends SimpleJsonResourceReloadListener {
    private static final Gson STANDARD_GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Codec<T> codec;
    protected final String folderName;
    protected Map<ResourceLocation, T> data;

    public CodecJsonDataManager(String folderName, Codec<T> codec) {
        this(folderName, codec, STANDARD_GSON);
    }

    public CodecJsonDataManager(String folderName, Codec<T> codec, Gson gson) {
        super(gson, folderName);
        this.data = new HashMap<>();
        this.folderName = folderName;
        this.codec = codec;
    }

    public Map<ResourceLocation, T> getData() {
        return this.data;
    }

    public void setData(Map<ResourceLocation, T> data) {
        this.data = data;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        LOGGER.info("Beginning loading of data for data loader: {}", this.folderName);
        Map<ResourceLocation, T> updatedData = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> resourceLocationJsonElementEntry : jsons.entrySet()) {
            Map.Entry<ResourceLocation, JsonElement> entry = resourceLocationJsonElementEntry;
            ResourceLocation key = entry.getKey();
            JsonElement element = entry.getValue();

            this.codec.decode(JsonOps.INSTANCE, element).get().ifLeft((result) -> updatedData.put(key, result.getFirst())).ifRight((partial) -> LOGGER.error("Failed to parse data json for {} due to: {}", key, partial.message()));
        }

        this.data = updatedData;
        LOGGER.info("Data loader for {} loaded {} jsons", this.folderName, this.data.size());
    }

    public <PACKET> CodecJsonDataManager<T> subscribeAsSyncable(SimpleChannel channel, Function<Map<ResourceLocation, T>, PACKET> packetFactory) {
        MinecraftForge.EVENT_BUS.addListener(getDatapackSyncListener(channel, packetFactory));
        return this;
    }

    protected <PACKET> Consumer<OnDatapackSyncEvent> getDatapackSyncListener(SimpleChannel channel, Function<Map<ResourceLocation, T>, PACKET> packetFactory) {
        return (datapackSyncEvent) -> {
            ServerPlayer targetPlayer = datapackSyncEvent.getPlayer();
            PACKET curPayload = packetFactory.apply(data);
            PacketDistributor.PacketTarget packetTarget = targetPlayer == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> targetPlayer);

            channel.send(packetTarget, curPayload);
        };
    }
}
