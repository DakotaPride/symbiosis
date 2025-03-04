package net.dakotapride.symbiosis.registrar;

import net.dakotapride.symbiosis.Symbiosis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeModeTabRegistrar {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Symbiosis.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SYMBIOSIS = CREATIVE_MODE_TABS.register("symbiosis", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.symbiosis.symbiosis"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> BlockRegistrar.BULB_SHRUB.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                //output.accept(ItemRegistrar.BULB_HARVESTER.get());
                output.accept(BlockRegistrar.BULB_SHRUB.get().asItem());

                for (BlockRegistrar.BulbRegistrar registrar : BlockRegistrar.BulbRegistrar.values()) {
                    output.accept(registrar.getBulbShrub().get().asItem());
                    output.accept(registrar.getBulbLayer().get().asItem());
                }

                output.accept(BlockRegistrar.BLAZE_THORN_BUSH.get().asItem());
                output.accept(ItemRegistrar.EMPTY_FLASK.get().asItem());
                parameters.holders().lookup(Registries.POTION).ifPresent(
                        registryLookup -> generatePotionEffectTypes(
                                output, registryLookup, ItemRegistrar.FILLED_FLASK.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, parameters.enabledFeatures()
                        )
                );
            }).build());

    private static void generatePotionEffectTypes(
            CreativeModeTab.Output output, HolderLookup<Potion> holderLookup, Item item, CreativeModeTab.TabVisibility tabVisibility, FeatureFlagSet featureFlagSet
    ) {
        holderLookup.listElements()
                .filter(reference -> (reference.value()).isEnabled(featureFlagSet))
                .map(reference -> PotionContents.createItemStack(item, reference))
                .forEach(itemStack -> output.accept(itemStack, tabVisibility));
    }

    public static void registrar(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
