package net.dakotapride.symbiosis.registrar;

import net.dakotapride.symbiosis.Symbiosis;
import net.dakotapride.symbiosis.block.BasicBulbBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistrar {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Symbiosis.MOD_ID);

    public static final DeferredBlock<BasicBulbBlock> BULB = BLOCKS.register("bulb", () -> new BasicBulbBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noOcclusion().noCollission()));
    public static final DeferredItem<BlockItem> BULB_ITEM = ItemRegistrar.ITEMS.registerSimpleBlockItem("bulb", BULB);

    public static void registrar(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
