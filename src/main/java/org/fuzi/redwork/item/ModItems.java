package org.fuzi.redwork.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.fuzi.redwork.Redwork;
import org.fuzi.redwork.other.ModData;
import org.fuzi.redwork.other.ModOther;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Redwork.MODID);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
