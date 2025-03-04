package net.dakotapride.symbiosis.registrar;

import net.dakotapride.symbiosis.Symbiosis;
import net.dakotapride.symbiosis.item.BulbHarvesterToolItem;
import net.dakotapride.symbiosis.item.EmptyFlaskItem;
import net.dakotapride.symbiosis.item.FilledFlaskItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistrar {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Symbiosis.MOD_ID);

    public static final DeferredItem<EmptyFlaskItem> EMPTY_FLASK = ITEMS.register("empty_flask", () -> new EmptyFlaskItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<FilledFlaskItem> FILLED_FLASK = ITEMS.register("filled_flask", () -> new FilledFlaskItem(new Item.Properties().stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)));

    //public static final DeferredItem<BulbHarvesterToolItem> BULB_HARVESTER = ITEMS.register("bulb_harvester", () -> new BulbHarvesterToolItem(new Item.Properties().stacksTo(1).durability(54).component(DataComponents.TOOL, BulbHarvesterToolItem.createToolProperties())));

    //public static final DeferredItem<Item> REGENERATION_BULB = ITEMS.register("regeneration_bulb", () -> new Item(new Item.Properties()));

//    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
//            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static void registrar(IEventBus bus) {
        ITEMS.register(bus);
    }

}
