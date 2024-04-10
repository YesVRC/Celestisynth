package com.aqutheseal.celestisynth.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PulsationParticle extends SimpleAnimatedParticle {

    PulsationParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites, 0.0F);
        this.lifetime = 10;
        this.quadSize = 0.75F;
        this.hasPhysics = false;
        setSpriteFromAge(pSprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            PulsationParticle particle = new PulsationParticle(pLevel, pX, pY, pZ, this.sprites);
            return particle;
        }
    }
}
