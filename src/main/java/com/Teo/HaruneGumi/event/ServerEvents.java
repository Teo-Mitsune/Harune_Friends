package com.Teo.HaruneGumi.event;

import com.Teo.HaruneGumi.entity.HaruneEntity;
import com.Teo.HaruneGumi.entity.ModEntities;
import com.Teo.HaruneGumi.harune_friends;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = harune_friends.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel level)) return;

        // HaruneEntity がすでに存在しているかを確認
        boolean exists = !level.getEntities(ModEntities.HARUNE.get(), Entity::isAlive).isEmpty();

        if (exists) return;

        // Harune がいない場合はスポーン
        BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, level.getSharedSpawnPos());
        HaruneEntity harune = new HaruneEntity(ModEntities.HARUNE.get(), level);
        harune.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        level.addFreshEntity(harune);
    }
}
