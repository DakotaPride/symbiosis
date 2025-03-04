package net.dakotapride.symbiosis.registrar;

import net.dakotapride.symbiosis.Symbiosis;
import net.dakotapride.symbiosis.block.BasicBulbShrubBlock;
import net.dakotapride.symbiosis.block.BlazeThornBushBlock;
import net.dakotapride.symbiosis.block.BulbLayerBlock;
import net.dakotapride.symbiosis.block.BulbShrubBlockWithEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

public class BlockRegistrar {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Symbiosis.MOD_ID);

    public static final DeferredBlock<BasicBulbShrubBlock> BULB_SHRUB = register("bulb_shrub", () -> new BasicBulbShrubBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noOcclusion().noCollission()));


    public enum BulbRegistrar {
        REGENERATION(Potions.REGENERATION),
        STRENGTH(Potions.STRENGTH),
        SPEED(Potions.SWIFTNESS),
        SLOW_FALLING(Potions.SLOW_FALLING),;

        final DeferredBlock<BasicBulbShrubBlock> bulbShrub;
        final DeferredBlock<BulbLayerBlock> bulbLayer;
        final Holder<Potion> potionHolder;

        BulbRegistrar(Holder<Potion> potion) {
            String id = name().toLowerCase(Locale.ROOT);
            this.potionHolder = potion;

            this.bulbLayer = register(id + "_bulb", () -> new BulbLayerBlock(potion, BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS).noOcclusion().noCollission()));
            this.bulbShrub = register(id + "_bulb_shrub", () -> new BulbShrubBlockWithEffect(potion, bulbLayer::asItem, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noOcclusion().noCollission()));
        }

        public DeferredBlock<BulbLayerBlock> getBulbLayer() {
            return bulbLayer;
        }

        public DeferredBlock<BasicBulbShrubBlock> getBulbShrub() {
            return bulbShrub;
        }

        public Holder<Potion> getPotionHolder() {
            return potionHolder;
        }

        static void load() {}
    }

    public static final DeferredBlock<BlazeThornBushBlock> BLAZE_THORN_BUSH = register("blaze_thorn_bush", () -> new BlazeThornBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WITHER_ROSE).noOcclusion().noCollission()));





    public static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = registerWithoutBlockItem(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static <T extends Block> DeferredItem<BlockItem> registerBlockItem(String name, DeferredBlock<T> block) {
        return ItemRegistrar.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }






    public static void registrar(IEventBus bus) {
        BLOCKS.register(bus);
        BulbRegistrar.load();
    }
}
