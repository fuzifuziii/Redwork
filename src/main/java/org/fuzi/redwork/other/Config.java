package org.fuzi.redwork.other;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends String>> GEODE_ACCELERATOR_TARGET_BLOCKS =
            BUILDER.comment("Block IDs (e.g. \"minecraft:budding_amethyst\") that the Geode Accelerator will speed up.")
                    .defineListAllowEmpty(
                            "geodeAccelerator.targetBlocks",
                            List.of("minecraft:budding_amethyst"),
                            () -> "minecraft:budding_amethyst",
                            obj -> obj instanceof String
                    );

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static Set<Block> getGeodeAcceleratorTargetBlocks() {
        Set<Block> blocks = new HashSet<>();

        for (String id : GEODE_ACCELERATOR_TARGET_BLOCKS.get()) {
            ResourceLocation location = ResourceLocation.tryParse(id);

            if (location == null) continue;

            BuiltInRegistries.BLOCK.getOptional(location).ifPresent(blocks::add);
        }

        return blocks;
    }
}
