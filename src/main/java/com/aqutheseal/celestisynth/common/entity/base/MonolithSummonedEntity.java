package com.aqutheseal.celestisynth.common.entity.base;

import com.aqutheseal.celestisynth.common.entity.mob.misc.StarMonolith;

import javax.annotation.Nullable;

public interface MonolithSummonedEntity {

    void setMonolith(@Nullable StarMonolith monolith);

    @Nullable
    StarMonolith getMonolith();
}
