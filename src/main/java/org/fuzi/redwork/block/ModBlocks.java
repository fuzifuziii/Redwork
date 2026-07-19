package org.fuzi.redwork.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.fuzi.redwork.Redwork;
import org.fuzi.redwork.block.breezecollector.BreezeCollectorBlock;
import org.fuzi.redwork.block.chute.ChuteBlock;
import org.fuzi.redwork.block.drill.DrillBlock;
import org.fuzi.redwork.block.extractor.ExtractorBlock;
import org.fuzi.redwork.block.placer.PlacerBlock;
import org.fuzi.redwork.block.copperobserver.CopperObserverBlock;
import org.fuzi.redwork.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Redwork.MODID);

    public static final TagKey<Block> CHUTES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Redwork.MODID, "chutes"));

    public static final DeferredBlock<Block> BREEZE_COLLECTOR = registerBlock("breeze_collector",
            () -> new BreezeCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).sound(SoundType.METAL).requiresCorrectToolForDrops().isRedstoneConductor(ModBlocks::neverConductor)));

    public static final DeferredBlock<Block> CHUTE = registerBlock("chute",
            () -> new ChuteBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).sound(SoundType.METAL).requiresCorrectToolForDrops().isRedstoneConductor(ModBlocks::neverConductor)));

    public static final DeferredBlock<Block> EXTRACTOR = registerBlock("extractor",
            () -> new ExtractorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).sound(SoundType.METAL).requiresCorrectToolForDrops().isRedstoneConductor(ModBlocks::neverConductor)));

    public static final DeferredBlock<Block> DRILL = registerBlock("drill",
            () -> new DrillBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).sound(SoundType.METAL).requiresCorrectToolForDrops().isRedstoneConductor(ModBlocks::neverConductor)));

    public static final DeferredBlock<Block> PLACER = registerBlock("placer",
            () -> new PlacerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).sound(SoundType.METAL).requiresCorrectToolForDrops().isRedstoneConductor(ModBlocks::neverConductor)));

    public static final DeferredBlock<Block> COPPER_OBSERVER = registerBlock("copper_observer",
            () -> new CopperObserverBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).sound(SoundType.METAL).requiresCorrectToolForDrops().isRedstoneConductor(ModBlocks::neverConductor)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, true);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, boolean registerItem) {
        DeferredBlock<T> returned = BLOCKS.register(name, block);

        if (registerItem)
            registerBlockItem(name, returned);

        return returned;
    }

    private static boolean neverConductor(BlockState p1, BlockGetter p2, BlockPos p3) {
        return false;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
