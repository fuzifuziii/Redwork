package org.fuzi.redwork.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.fuzi.redwork.Redwork;
import org.fuzi.redwork.block.drill.DrillBlockEntity;
import org.fuzi.redwork.block.placer.PlacerBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Redwork.MODID);

    public static final Supplier<BlockEntityType<DrillBlockEntity>> DRILL_BE =
            BLOCK_ENTITIES.register("drill_be", () -> BlockEntityType.Builder.of(
                    DrillBlockEntity::new, ModBlocks.DRILL.get()
            ).build(null));

    public static final Supplier<BlockEntityType<PlacerBlockEntity>> PLACER_BE =
            BLOCK_ENTITIES.register("placer_be", () -> BlockEntityType.Builder.of(
                    PlacerBlockEntity::new, ModBlocks.PLACER.get()
            ).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
