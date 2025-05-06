package com.Teo.HaruneGumi.entity.ai;

import com.Teo.HaruneGumi.entity.HaruneEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final HaruneEntity harune;
    private final double speed;
    private final float stopDistance;

    private Player targetPlayer;

    public FollowPlayerGoal(HaruneEntity harune, double speed, float stopDistance) {
        this.harune = harune;
        this.speed = speed;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (harune.level().isClientSide) return false;

        // 常に最も近いプレイヤーをターゲットにする
        targetPlayer = harune.level().getNearestPlayer(harune, 128); // 遠距離でもOK
        return targetPlayer != null;
    }

    @Override
    public void start() {
        if (targetPlayer != null) {
            harune.getNavigation().moveTo(targetPlayer, speed);
        }
    }

    @Override
    public void stop() {
        harune.getNavigation().stop();
        targetPlayer = null;
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            double distance = harune.distanceTo(targetPlayer);
            if (distance > stopDistance) {
                harune.getNavigation().moveTo(targetPlayer, speed);
            } else {
                harune.getNavigation().stop();
            }
        }
    }
}
