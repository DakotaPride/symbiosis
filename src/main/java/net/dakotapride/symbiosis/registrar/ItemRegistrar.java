package net.dakotapride.symbiosis.registrar;

import net.dakotapride.symbiosis.Symbiosis;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistrar {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Symbiosis.MOD_ID);

//    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
//            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static void registrar(IEventBus bus) {
        ITEMS.register(bus);
    }

}
