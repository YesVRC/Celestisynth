package com.aqutheseal.celestisynth.manager;

import com.aqutheseal.celestisynth.api.animation.player.AnimationManager;
import com.aqutheseal.celestisynth.api.animation.player.LayerManager;
import com.aqutheseal.celestisynth.common.network.c2s.UpdateAnimationToAllPacket;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CSCommandsManager {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("celestisynth")
                .then(Commands.literal("animation")
                        .then(Commands.literal("clear").executes(CSCommandsManager::clearAnimation))
                )
        );
    }

    private static int clearAnimation(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player player) {
            CSNetworkManager.sendToAll(new UpdateAnimationToAllPacket(LayerManager.MAIN_LAYER, player.getId(), AnimationManager.AnimationsList.CLEAR.getId()));
            CSNetworkManager.sendToAll(new UpdateAnimationToAllPacket(LayerManager.MIRRORED_LAYER, player.getId(), AnimationManager.AnimationsList.CLEAR.getId()));
            command.getSource().sendSuccess(() -> Component.translatable("commands.celestisynth.clear_animation"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        CSCommandsManager.register(event.getDispatcher());
    }
}
