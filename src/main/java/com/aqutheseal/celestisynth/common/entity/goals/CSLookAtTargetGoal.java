package com.aqutheseal.celestisynth.common.entity.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class CSLookAtTargetGoal extends Goal {
    public final Mob mob;

    public CSLookAtTargetGoal(Mob pMob) {
        this.mob = pMob;
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        assert mob.getTarget() != null;
        this.mob.lookAt(mob.getTarget(), 30.0F, 30.0F);
        this.mob.getLookControl().setLookAt(mob.getTarget().getX(), mob.getTarget().getY(), mob.getTarget().getZ(), 30.0F, 30.0F);
    }
}
