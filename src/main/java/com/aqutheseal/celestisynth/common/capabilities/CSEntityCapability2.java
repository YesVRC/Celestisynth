package com.aqutheseal.celestisynth.common.capabilities;

import com.aqutheseal.celestisynth.manager.CSNetworkManager;
import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.simple.SimpleChannel;

public class CSEntityCapability2 extends LivingEntityCapability implements CSCapabilityHelper {

    protected CSEntityCapability2(LivingEntity entity) {
        super(entity);
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag nbt = new CompoundTag();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {

    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.livingEntity.getId(), CSEntityCapabilityProvider.CS_ENTITY_CAP_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return CSNetworkManager.INSTANCE;
    }
}
