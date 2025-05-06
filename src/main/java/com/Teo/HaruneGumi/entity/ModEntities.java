package com.Teo.HaruneGumi.entity;

import com.Teo.HaruneGumi.harune_friends;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@EventBusSubscriber(modid = harune_friends.MODID, bus = Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, harune_friends.MODID);

    public static final RegistryObject<EntityType<HaruneEntity>> HARUNE =
            ENTITY_TYPES.register("harune", () ->
                    EntityType.Builder.<HaruneEntity>of(HaruneEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f)
                            .build(new ResourceLocation(harune_friends.MODID, "harune").toString())
            );

    public static void register() {
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(HARUNE.get(), HaruneEntity.createAttributes().build());
    }
}
