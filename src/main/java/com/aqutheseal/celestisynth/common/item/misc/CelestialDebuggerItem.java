package com.aqutheseal.celestisynth.common.item.misc;

import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.entity.mob.misc.RainfallTurret;
import com.aqutheseal.celestisynth.common.entity.mob.natural.Traverser;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.object.Color;

public class CelestialDebuggerItem extends Item implements CSWeaponUtil {
    public CelestialDebuggerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        DoubleArrayList angles = new DoubleArrayList();
        angles.add(0);
        if (!pPlayer.isShiftKeyDown()) {
            if (!pLevel.isClientSide()) {
                for (int i = 0; i < 45; i++) {
                    Traverser traverser = CSEntityTypes.TRAVERSER.get().create(pLevel);
                    traverser.moveTo(pPlayer.position().add(Mth.sin(i) * 12, 1, Mth.cos(i) * 12));
                    pLevel.addFreshEntity(traverser);

                    Traverser traverser2 = CSEntityTypes.TRAVERSER.get().create(pLevel);
                    traverser2.moveTo(pPlayer.position().add(Mth.sin(i) * 24, 1, Mth.cos(i) * 24));
                    pLevel.addFreshEntity(traverser2);
                }
            }

            this.chantMessage(pPlayer, "ARISE. (wont be an actual feature in the mod)", 40, Color.RED.argbInt());
            this.flashScreen(pPlayer, 255, 15);
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
