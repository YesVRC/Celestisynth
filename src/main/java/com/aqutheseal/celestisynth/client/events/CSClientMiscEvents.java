package com.aqutheseal.celestisynth.client.events;

import com.aqutheseal.celestisynth.api.item.CSWeapon;
import com.aqutheseal.celestisynth.api.mixin.PlayerMixinSupport;
import com.aqutheseal.celestisynth.client.renderers.entity.layer.FrostboundGeoLayer;
import com.aqutheseal.celestisynth.client.renderers.misc.CSTooltipRenderer;
import com.aqutheseal.celestisynth.common.attack.aquaflora.AquafloraSlashFrenzyAttack;
import com.aqutheseal.celestisynth.common.capabilities.CSEntityCapabilityProvider;
import com.aqutheseal.celestisynth.common.item.weapons.AquafloraItem;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.event.GeoRenderEvent;

public class CSClientMiscEvents {

    @SubscribeEvent
    public static void onGeoEntityRender(GeoRenderEvent.Entity.CompileRenderLayers event) {
        event.addLayer(new FrostboundGeoLayer<>(event.getRenderer()));
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Opening event) {
        if (Minecraft.getInstance().player != null) {
            ItemStack itemR = Minecraft.getInstance().player.getMainHandItem();
            ItemStack itemL = Minecraft.getInstance().player.getOffhandItem();

            if (itemR.getItem() instanceof CSWeapon) {
                if (itemR.getTagElement(CSWeapon.CS_CONTROLLER_TAG_ELEMENT) != null && itemR.getTagElement(CSWeapon.CS_CONTROLLER_TAG_ELEMENT).getBoolean(CSWeapon.ANIMATION_BEGUN_KEY)) {
                    event.setCanceled(true);
                }
            }
            if (itemL.getItem() instanceof CSWeapon) {
                if (itemL.getTagElement(CSWeapon.CS_CONTROLLER_TAG_ELEMENT) != null && itemL.getTagElement(CSWeapon.CS_CONTROLLER_TAG_ELEMENT).getBoolean(CSWeapon.ANIMATION_BEGUN_KEY)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingRender(RenderLivingEvent event) {
        CSEntityCapabilityProvider.get(event.getEntity()).ifPresent(data -> {
            if (data.getTrueInvisibility() > 0) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        CSTooltipRenderer.manageTooltipColors(event);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent event) {
        if (Minecraft.getInstance().player instanceof PlayerMixinSupport mixinPlayer) {
            if (mixinPlayer.getPulseScale() > 0) {
                Color pulse = Color.ofRGBA(255, 255, 255, Mth.clamp(mixinPlayer.getPulseScale(), 0, 255));
                event.getGuiGraphics().fill(0, 0, event.getWindow().getWidth() / 2, event.getWindow().getHeight() / 2, pulse.argbInt());
            }

            int pointerX = event.getWindow().getWidth() / 4;
            int pointerY = (event.getWindow().getHeight() / 4) + 10;
            if (mixinPlayer.getChantMark() < 20) {
                Component text = Component.translatable(mixinPlayer.getChantMessage());
                int lerp = (int) Mth.lerp(mixinPlayer.getChantMark() / 20F, 255F, 0);
                Color base = new Color(mixinPlayer.getChantColor());
                Color pulse = Color.ofRGBA(base.getRed(), base.getGreen(), base.getBlue(), Mth.clamp(lerp, 0, 255));
                event.getGuiGraphics().drawCenteredString(Minecraft.getInstance().font, text, pointerX, pointerY, pulse.argbInt());

                int lerpBack = (int) Mth.lerp(mixinPlayer.getChantMark() / 20F, 50F, 0);
                Color back = Color.ofRGBA(0, 0, 0,  Mth.clamp(lerpBack, 0, 255));
                event.getGuiGraphics().fill(0, pointerY - 2, (event.getWindow().getWidth() / 2), pointerY + 10, back.argbInt());
            }

        }
    }

    // Credits to BobMowzie for camera math
    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        PlayerMixinSupport supportedPlayer = (PlayerMixinSupport) player;

        if (!mc.options.getCameraType().isFirstPerson()) {
            checkAndSetAngle(event, player.getOffhandItem());
            checkAndSetAngle(event, player.getMainHandItem());
        }

        float delta = Minecraft.getInstance().getFrameTime();
        float ticksExistedDelta = player.tickCount + delta;
        float intensity = supportedPlayer.getScreenShakeIntensity();
        float duration = supportedPlayer.getScreenShakeDuration();

        if (duration > 0 && !Minecraft.getInstance().isPaused() && mc.level.isClientSide()) {
            event.setPitch((float) (event.getPitch() + intensity * Math.cos(ticksExistedDelta * 3 + 2) * 25));
            event.setYaw((float) (event.getYaw() + intensity * Math.cos(ticksExistedDelta * 5 + 1) * 25));
            event.setRoll((float) (event.getRoll() + intensity * Math.cos(ticksExistedDelta * 4) * 25));
        }
    }

    @SubscribeEvent
    public static void onCameraZoom(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.getCameraType().isFirstPerson()) {
            checkAndSetFOV(event, mc.player.getOffhandItem());
            checkAndSetFOV(event, mc.player.getMainHandItem());
        }
    }

    @SubscribeEvent
    public static void onToolTipComponent(RenderTooltipEvent.GatherComponents event) {
        CSTooltipRenderer.manageCelestialTooltips(event);
    }

    private static void checkAndSetAngle(ViewportEvent.ComputeCameraAngles event, ItemStack itemStack) {
        if (itemStack.getItem() instanceof AquafloraItem) {
            CompoundTag tagElement = itemStack.getTagElement(CSWeapon.CS_CONTROLLER_TAG_ELEMENT);
            if (tagElement != null && tagElement.getBoolean(CSWeapon.ANIMATION_BEGUN_KEY) && tagElement.getBoolean(AquafloraSlashFrenzyAttack.ATTACK_ONGOING)) event.setPitch(90);
        }
    }

    private static void checkAndSetFOV(ViewportEvent.ComputeFov event, ItemStack itemStack) {
        CompoundTag tagElement = itemStack.getTagElement(CSWeapon.CS_CONTROLLER_TAG_ELEMENT);

        if (tagElement != null && itemStack.getItem() instanceof AquafloraItem aq) {
            if (tagElement.getBoolean(CSWeapon.ANIMATION_BEGUN_KEY) && tagElement.getBoolean(AquafloraSlashFrenzyAttack.ATTACK_ONGOING)) event.setFOV(140);
        }
    }
}
