package com.aqutheseal.celestisynth.common.entity.helper;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.entity.mob.misc.StarMonolith;
import com.aqutheseal.celestisynth.common.entity.mob.natural.Traverser;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public enum MonolithRunes {
    NO_RUNE((ResourceLocation) null, 0, 0, 0, MonolithRunes::noRuneTick, MonolithRunes::noRuneConsume),
    BLOOD_RUNE("blood", 400, 10, 3, MonolithRunes::bloodRuneTick, MonolithRunes::bloodRuneConsume);

    public final @Nullable ResourceLocation runeTexture;
    public final int summonInterval;
    public final int summonRange;
    public final int summonLimit;
    public final BiConsumer<StarMonolith, Level> ambientTick;
    public final TriConsumer<StarMonolith, BlockPos, ServerLevel> summonAction;

    MonolithRunes(@Nullable ResourceLocation runeTexture, int summonInterval, int summonRange, int summonLimit, BiConsumer<StarMonolith, Level> ambientTick, TriConsumer<StarMonolith, BlockPos, ServerLevel> summonAction) {
        this.runeTexture = runeTexture;
        this.summonInterval = summonInterval;
        this.summonRange = summonRange;
        this.summonLimit = summonLimit;
        this.ambientTick = ambientTick;
        this.summonAction = summonAction;
    }

    MonolithRunes(String runeId, int summonInterval, int summonRange, int summonLimit, BiConsumer<StarMonolith, Level> ambientTick, TriConsumer<StarMonolith, BlockPos, ServerLevel> summonAction) {
        this(Celestisynth.prefix("textures/entity/mob/star_monolith/runes/star_monolith_rune_" + runeId + ".png"), summonInterval, summonRange, summonLimit, ambientTick, summonAction);
    }

    public static void noRuneTick(StarMonolith monolith, Level level) {
    }

    public static void noRuneConsume(StarMonolith monolith, BlockPos summonPosition, ServerLevel level) {
    }

    public static void bloodRuneTick(StarMonolith monolith, Level level) {
        Vec3 rot = new Vec3(monolith.getRandom().nextGaussian(), monolith.getRandom().nextGaussian(), monolith.getRandom().nextGaussian());
        ParticleUtil.sendParticle(level, CSParticleTypes.KERES_OMEN.get(), monolith.position().add(rot).add(0, 1, 0), rot.scale(0.1));
    }

    public static void bloodRuneConsume(StarMonolith monolith, BlockPos summonPosition, ServerLevel level) {
        Traverser traverser = CSEntityTypes.TRAVERSER.get().create(level);
        traverser.moveTo(summonPosition, 0, 0);
        traverser.setMonolith(monolith);
        level.addFreshEntity(traverser);

        Vec3 from = monolith.position().add(0, 1.25, 0);
        Vec3 to = traverser.position();
        double distance = from.distanceTo(to);
        Vec3 direction = to.subtract(from).normalize();
        for (double i = 0; i <= distance; i += 0.2) {
            Vec3 particlePos = from.add(direction.scale(i));
            ParticleUtil.sendParticles(level, CSParticleTypes.KERES_OMEN.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0);
        }
    }
}
