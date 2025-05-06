package com.Teo.HaruneGumi.event;

import com.Teo.HaruneGumi.client.renderer.HaruneRenderer;
import com.Teo.HaruneGumi.entity.HaruneEntity;
import com.Teo.HaruneGumi.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = "harune_friends", value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HARUNE.get(), HaruneRenderer::new);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;

            // 世界に存在するHaruneEntityの検索
            Optional<HaruneEntity> haruneOpt = mc.level.getEntitiesOfClass(HaruneEntity.class, mc.player.getBoundingBox().inflate(100))
                    .stream()
                    .findFirst();

            haruneOpt.ifPresent(harune -> {
                // Haruneが死んでいて、復活カウントダウン中の場合にのみ監視処理を行う（表示・演出など）
                if (!harune.isAlive()) {
                    // 例えば何かエフェクトを出したりUIに「復活まで○秒」と表示したいならここ
                    // 今回は処理なし。必要があれば追加してください。
                }
            });
        }
    }
}
