package com.aqutheseal.celestisynth.common.entity.goals.traverser;

import com.aqutheseal.celestisynth.common.entity.goals.MoveToTargetGoal;
import com.aqutheseal.celestisynth.common.entity.mob.natural.Traverser;

public class TraverserMoveToTargetGoal extends MoveToTargetGoal {
    public final Traverser mob;

    public TraverserMoveToTargetGoal(Traverser pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen, double reach) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen, reach);
        this.mob = pMob;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && mob.hasNoAction();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && mob.hasNoAction();
    }
}
