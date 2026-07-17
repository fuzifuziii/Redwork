package org.fuzi.redwork.other;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.fuzi.redwork.Redwork;

import java.util.function.Supplier;

public class ModData {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Redwork.MODID);

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
