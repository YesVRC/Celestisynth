package com.aqutheseal.celestisynth.common.item.misc;

import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.entity.RainfallTurret;
import com.aqutheseal.celestisynth.common.entity.projectile.KeresSlash;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CelestialDebuggerItem extends Item implements CSWeaponUtil {
    public CelestialDebuggerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        DoubleArrayList angles = new DoubleArrayList();
        angles.add(0);
//        for (double a = 0; a < 45; a = a + ((double) 15 / 2)) {
//            if (a != 0) {
//                angles.add(a);
//                angles.add(-a);
//            }
//        }
        if (!pPlayer.isShiftKeyDown()) {
            if (!pLevel.isClientSide()) {
                for (double angle : angles) {
                    KeresSlash rend = new KeresSlash(CSEntityTypes.KERES_SLASH.get(), pPlayer, pLevel);
                    Vec3 vec31 = pPlayer.getUpVector(1.0F);
                    Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(angle * ((float) Math.PI / 180F), vec31.x, vec31.y, vec31.z);
                    Vec3 vec3 = pPlayer.getViewVector(1.0F);
                    Vector3f shootAngle = vec3.toVector3f().rotate(quaternionf);
                    rend.setRoll((float) (pLevel.random.nextGaussian() * 360));
                    rend.shoot(shootAngle.x, shootAngle.y, shootAngle.z, 1.5F, 0);
                    pLevel.addFreshEntity(rend);
                }
            }
            pPlayer.playSound(CSSoundEvents.SLASH_WATER.get(), 0.2F, (float) (1.5F + (pPlayer.getRandom().nextGaussian() * 0.5)));
        } else {
            if (!pLevel.isClientSide()) {
                for (int i = 0; i < 360; i = i + 40) {
                    RainfallTurret turret = new RainfallTurret(CSEntityTypes.RAINFALL_TURRET.get(), pLevel);
                    turret.moveTo(pPlayer.getX() + (Mth.sin(i) * 30), pPlayer.getY(), pPlayer.getZ() + (Mth.cos(i) * 30));
                    turret.setOwnerUUID(pPlayer.getUUID());
                    pLevel.addFreshEntity(turret);
                }
            }
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
}
