package net.dakotapride.symbiosis.registrar;

import net.dakotapride.symbiosis.Symbiosis;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeModeTabRegistrar {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Symbiosis.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SYMBIOSIS = CREATIVE_MODE_TABS.register("symbiosis", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.symbiosis.symbiosis"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> BlockRegistrar.BULB.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(BlockRegistrar.BULB.get().asItem());
            }).build());

    public static void registrar(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
