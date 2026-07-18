package org.fuzi.redwork.item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.fuzi.redwork.Redwork;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Redwork.MODID);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
