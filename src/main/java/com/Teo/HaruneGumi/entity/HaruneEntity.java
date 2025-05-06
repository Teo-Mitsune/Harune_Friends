package com.Teo.HaruneGumi.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;



import com.Teo.HaruneGumi.entity.ai.FollowPlayerGoal;



public class HaruneEntity extends PathfinderMob {

    private HaruneMode currentMode = HaruneMode.NORMAL;

    public HaruneEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.getAvailableGoals().clear();

        switch (this.getCurrentMode()) {
            case FOLLOW:
                this.goalSelector.addGoal(1, new FollowPlayerGoal(this, 1.0D, 3.0F));
                break;
            case WAIT:
                // Do nothing
                break;
            case NORMAL:
            default:
                this.goalSelector.addGoal(1, new FloatGoal(this));
                this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
                this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
                this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
                break;
        }
    }


    public HaruneMode getCurrentMode() {
        return currentMode != null ? currentMode : HaruneMode.NORMAL;
    }

    public void setCurrentMode(HaruneMode mode) {
        this.currentMode = mode != null ? mode : HaruneMode.NORMAL;
        this.refreshGoals();
    }

    public void refreshGoals() {
        this.goalSelector.getAvailableGoals().clear();
        this.registerGoals();
    }

    private static final ResourceLocation SPEECH_FILE = new ResourceLocation("harune_friends", "speech/harune_lines.json");
    private long lastSpeakTime = 0;
    private String currentText = "こんにちは！";

    public boolean shouldShowText() {
        return this.level().getGameTime() - lastSpeakTime < 60;
    }

    public Component getDisplayText() {
        return Component.literal(currentText);
    }

    public void say(String message) {
        this.currentText = message;
        this.lastSpeakTime = this.level().getGameTime();
    }

    private String getRandomSpeechLine() {
        try {
            MinecraftServer server = this.level().getServer();
            if (server == null) return null;

            ResourceManager manager = server.getResourceManager();
            Optional<Resource> optional = manager.getResource(SPEECH_FILE);

            if (optional.isEmpty()) return null;

            try (InputStreamReader reader = new InputStreamReader(optional.get().open())) {
                JsonObject json = GsonHelper.parse(reader);
                JsonArray linesArray = json.getAsJsonArray("lines");

                List<String> lines = new ArrayList<>();
                for (int i = 0; i < linesArray.size(); i++) {
                    lines.add(linesArray.get(i).getAsString());
                }

                if (lines.isEmpty()) return null;

                return lines.get(new Random().nextInt(lines.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (!this.level().isClientSide) {
            String message = getRandomSpeechLine();
            if (source.getEntity() instanceof ServerPlayer player && message != null) {
                player.sendSystemMessage(Component.literal(message));
            }
        }

        return result;
    }
    private static HaruneMode lastKnownMode = HaruneMode.NORMAL; // staticで保存

    @Override
    public void die(DamageSource source) {
        lastKnownMode = this.getCurrentMode(); // モードを保存
        super.die(source);

        if (!this.level().isClientSide() && this.getHealth() <= 0.0F) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            HaruneRespawner.scheduleRespawn(this, serverLevel);
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("HaruneMode", this.getCurrentMode().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HaruneMode")) {
            try {
                this.setCurrentMode(HaruneMode.valueOf(tag.getString("HaruneMode")));
            } catch (IllegalArgumentException e) {
                this.setCurrentMode(HaruneMode.NORMAL); // 不正値ならデフォルト
            }
        }
    }


}
