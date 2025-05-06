package com.Teo.HaruneGumi.client.renderer;

import com.Teo.HaruneGumi.entity.HaruneEntity;
import com.Teo.HaruneGumi.harune_friends;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HaruneRenderer extends MobRenderer<HaruneEntity, PlayerModel<HaruneEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(harune_friends.MODID, "textures/entity/harune.png");

    public HaruneRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(HaruneEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(HaruneEntity entity, PoseStack poseStack, float partialTickTime) {
        // スケール調整したい場合はここで倍率を変更（例：子供キャラにしたいとき）
        poseStack.scale(1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(HaruneEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }




}
