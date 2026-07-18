package org.fuzi.redwork.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.fuzi.redwork.Redwork;
import org.fuzi.redwork.block.ModBlocks;

import java.util.function.Supplier;

public class ModOther {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>>
            LOOT_MODIFIERS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
                    Redwork.MODID
            );

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Redwork.MODID);
    public static final Supplier<CreativeModeTab> AUTOWORK_TAB = CREATIVE_MODE_TABS.register("redwork_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.CRAFTER))
                    .title(Component.translatable("itemGroup.redwork"))
                    .displayItems((idp, output) -> {
                        output.accept(ModBlocks.CHUTE);
                        output.accept(ModBlocks.DRILL);
                        output.accept(ModBlocks.EXTRACTOR);
                        output.accept(ModBlocks.PLACER);
                        output.accept(ModBlocks.BREEZE_COLLECTOR);
                    })
                    .build());

    public static final TagKey<Item> TOOL_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Redwork.MODID, "tools"));

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
        CREATIVE_MODE_TABS.register(bus);
    }
}
