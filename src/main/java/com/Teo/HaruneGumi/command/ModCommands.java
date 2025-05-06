package com.Teo.HaruneGumi.command;

import com.Teo.HaruneGumi.entity.HaruneEntity;
import com.Teo.HaruneGumi.entity.HaruneMode;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setmode")
                .then(Commands.literal("normal").executes(ctx -> setMode(ctx.getSource(), HaruneMode.NORMAL)))
                .then(Commands.literal("follow").executes(ctx -> setMode(ctx.getSource(), HaruneMode.FOLLOW)))
                .then(Commands.literal("wait").executes(ctx -> setMode(ctx.getSource(), HaruneMode.WAIT)))
        );
    }

    private static int setMode(CommandSourceStack source, HaruneMode mode) {
        ServerLevel level = source.getLevel();
        for (Entity entity : level.getEntities().getAll()) {
            if (entity instanceof HaruneEntity harune) {
                harune.setCurrentMode(mode);
                harune.refreshGoals();
                source.sendSuccess(() -> harune.getName().plainCopy().append(" のモードを " + mode + " に変更しました"), false);
                return 1;
            }
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("HaruneEntity が見つかりません"));
        return 0;
    }
}
