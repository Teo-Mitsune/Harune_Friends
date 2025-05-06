package com.Teo.HaruneGumi;

import com.Teo.HaruneGumi.client.renderer.HaruneRenderer;
import com.Teo.HaruneGumi.command.ModCommands;
import com.Teo.HaruneGumi.entity.ModEntities;
import com.Teo.HaruneGumi.event.ServerEvents;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(harune_friends.MODID)
public class harune_friends {
    public static final String MODID = "harune_friends";

    public harune_friends() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ModEntities.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
        MinecraftForge.EVENT_BUS.register(this); // サーバーイベント用
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // クライアントセットアップ（不要なら空でOK）
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // コマンド登録
        ModCommands.register(event.getServer().getCommands().getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            EntityRenderers.register(ModEntities.HARUNE.get(), HaruneRenderer::new);
        }
    }
}
