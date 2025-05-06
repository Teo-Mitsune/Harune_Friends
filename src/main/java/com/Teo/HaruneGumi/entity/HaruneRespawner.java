package com.Teo.HaruneGumi.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber
public class HaruneRespawner {

    private static final Map<RespawnTaskKey, RespawnTask> respawnTasks = new HashMap<>();

    public static void scheduleRespawn(HaruneEntity harune, ServerLevel level) {
        if (harune == null || harune.isRemoved()) return;

        ServerPlayer serverPlayer = level.getServer().getPlayerList().getPlayers().stream()
                .filter(p -> p.level() == level)
                .findFirst()
                .orElse(null);

        if (serverPlayer != null) {
            BlockPos pos = serverPlayer.blockPosition();
            HaruneMode savedMode = harune.getCurrentMode();
            RespawnTaskKey key = new RespawnTaskKey(level, serverPlayer);
            respawnTasks.put(key, new RespawnTask(level, serverPlayer, pos, 100, savedMode)); // 5秒 = 100tick
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Iterator<Map.Entry<RespawnTaskKey, RespawnTask>> iterator = respawnTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RespawnTaskKey, RespawnTask> entry = iterator.next();
            RespawnTask task = entry.getValue();
            task.ticksRemaining--;

            if (task.ticksRemaining % 20 == 0 && task.ticksRemaining > 0) {
                int secondsLeft = task.ticksRemaining / 20;
                task.player.sendSystemMessage(Component.literal("Haruneがあと " + secondsLeft + " 秒で復活します。"));
            }

            if (task.ticksRemaining <= 0) {
                HaruneEntity newHarune = ModEntities.HARUNE.get().create(task.level);
                if (newHarune != null) {
                    newHarune.moveTo(task.spawnPos, 0.0F, 0.0F);
                    newHarune.setCurrentMode(task.mode); // 復活時にモードを復元
                    task.level.addFreshEntity(newHarune);
                    task.player.sendSystemMessage(Component.literal("Haruneが復活しました！"));
                }
                iterator.remove();
            }
        }
    }

    private record RespawnTaskKey(ServerLevel level, ServerPlayer player) {}

    private static class RespawnTask {
        final ServerLevel level;
        final ServerPlayer player;
        final BlockPos spawnPos;
        final HaruneMode mode;
        int ticksRemaining;

        RespawnTask(ServerLevel level, ServerPlayer player, BlockPos spawnPos, int ticks, HaruneMode mode) {
            this.level = level;
            this.player = player;
            this.spawnPos = spawnPos;
            this.ticksRemaining = ticks;
            this.mode = mode;
        }
    }
}
