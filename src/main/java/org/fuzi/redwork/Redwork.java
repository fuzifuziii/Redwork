package org.fuzi.redwork;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.fuzi.redwork.block.ModBlockEntities;
import org.fuzi.redwork.block.ModBlocks;
import org.fuzi.redwork.block.drill.DrillBlockEntity;
import org.fuzi.redwork.block.placer.PlacerBlockEntity;
import org.fuzi.redwork.item.ModItems;
import org.fuzi.redwork.other.ModData;
import org.fuzi.redwork.other.ModOther;
import org.slf4j.Logger;

@Mod(Redwork.MODID)
public class Redwork {
    public static final String MODID = "redwork";
    private static final Logger LOGGER = LogUtils.getLogger();
    public Redwork(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModData.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModOther.register(modEventBus);

        modEventBus.addListener(this::registerCapabilityProvider);

        NeoForge.EVENT_BUS.register(this);

        org.fuzi.redwork.compat.sable.SableCompat.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public void registerCapabilityProvider(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DRILL_BE.get(),
                DrillBlockEntity::getCapability
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.PLACER_BE.get(),
                PlacerBlockEntity::getCapability
        );
    }
}
