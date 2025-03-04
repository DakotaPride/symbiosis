package net.dakotapride.symbiosis;

import net.dakotapride.symbiosis.block.BasicBulbShrubBlock;
import net.dakotapride.symbiosis.block.BulbShrubBlockWithEffect;
import net.dakotapride.symbiosis.item.EmptyFlaskItem;
import net.dakotapride.symbiosis.registrar.BlockRegistrar;
import net.dakotapride.symbiosis.registrar.CreativeModeTabRegistrar;
import net.dakotapride.symbiosis.registrar.ItemRegistrar;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Symbiosis.MOD_ID)
public class Symbiosis {
    public static final String MOD_ID = "symbiosis";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static TagKey<Block> EFFECT_BULB_SHRUBS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "effect_bulb_shrubs"));

    public Symbiosis(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BlockRegistrar.registrar(modEventBus);
        ItemRegistrar.registrar(modEventBus);
        CreativeModeTabRegistrar.registrar(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabRegistrar.SYMBIOSIS.getKey())
//            event.accept(BlockRegistrar.BULB_ITEM);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public InteractionResult rightClickOnBulbLikeBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState blockState = level.getBlockState(pos);
        InteractionHand hand = event.getHand();

        if (itemStack.is(Items.SHEARS) && event.getEntity() instanceof Player player) {
            for (BulbShrubBlockWithEffect.Bulbs bulbs : BulbShrubBlockWithEffect.Bulbs.values()) {
                if (blockState.is(bulbs.getShrub())) {
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, itemStack);
                    }

                    player.addItem(new ItemStack(bulbs.getBulb().asItem(), 3));

                    level.setBlockAndUpdate(pos, BlockRegistrar.BULB_SHRUB.get().defaultBlockState());
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, BlockRegistrar.BULB_SHRUB.get().defaultBlockState()));

                    itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

//        if (itemStack.is(Items.SHEARS) && blockState.is(EFFECT_BULB_SHRUBS)) {
//            if (event.getEntity() instanceof Player player) {
//
//                if (player instanceof ServerPlayer) {
//                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, itemStack);
//                }
//
//                //itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
//
//                Item bulbItem = BulbShrubBlockWithEffect.getBulbItem();
//
//                player.addItem(new ItemStack(bulbItem, 3));
//
//
//                level.setBlockAndUpdate(pos, BlockRegistrar.BULB_SHRUB.get().defaultBlockState());
//                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, BlockRegistrar.BULB_SHRUB.get().defaultBlockState()));
//
//                itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
//            }
//
//            //level.setBlock(pos, BlockRegistrar.BULB_SHRUB.get().defaultBlockState(), 11);
//
//            return InteractionResult.sidedSuccess(level.isClientSide);
//        }

        return InteractionResult.PASS;

    }

    @SubscribeEvent
    public void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        //builder.addContainer(ItemRegistrar.EMPTY_FLASK.get());
        builder.addContainer(ItemRegistrar.FILLED_FLASK.get());
//
//        ItemStack stack = ItemRegistrar.FILLED_FLASK.toStack();
//
        ItemStack awkwardPotion = ItemRegistrar.FILLED_FLASK.get().getDefaultInstance();
        ItemStack awkwardFlask = ItemRegistrar.FILLED_FLASK.get().getDefaultInstance();
        awkwardFlask.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
        awkwardPotion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));


        builder.addContainerRecipe(Items.POTION, BlockRegistrar.BULB_SHRUB.get().asItem(), ItemRegistrar.FILLED_FLASK.get());

        ItemStack regenerationStack = ItemRegistrar.FILLED_FLASK.toStack();
        regenerationStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.REGENERATION));
        ItemStack strengthStack = ItemRegistrar.FILLED_FLASK.toStack();
        strengthStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRENGTH));
        ItemStack speedStack = ItemRegistrar.FILLED_FLASK.toStack();
        speedStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SWIFTNESS));
        ItemStack slowFallingStack = ItemRegistrar.FILLED_FLASK.toStack();
        slowFallingStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOW_FALLING));

        builder.addRecipe(Ingredient.of(awkwardFlask), Ingredient.of(BlockRegistrar.BulbRegistrar.REGENERATION.getBulbLayer().asItem()), regenerationStack);
        builder.addRecipe(Ingredient.of(awkwardFlask), Ingredient.of(BlockRegistrar.BulbRegistrar.STRENGTH.getBulbLayer().asItem()), strengthStack);
        builder.addRecipe(Ingredient.of(awkwardFlask), Ingredient.of(BlockRegistrar.BulbRegistrar.SPEED.getBulbLayer().asItem()), speedStack);
        builder.addRecipe(Ingredient.of(awkwardFlask), Ingredient.of(BlockRegistrar.BulbRegistrar.SLOW_FALLING.getBulbLayer().asItem()), slowFallingStack);

//        for (BlockRegistrar.BulbRegistrar registrar : BlockRegistrar.BulbRegistrar.values()) {
//            stack.set(DataComponents.POTION_CONTENTS, new PotionContents(registrar.getPotionHolder()));
//            builder.addRecipe(Ingredient.of(awkwardFlask), Ingredient.of(registrar.getBulbLayer().asItem()), stack);
//        }

        //builder.addRecipe(Ingredient.of(awkwardPotion), Ingredient.of(BlockRegistrar.BulbRegistrar.REGENERATION.getBulbLayer().get().asItem()), stack);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //LOGGER.info("HELLO FROM CLIENT SETUP");
            //LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            //ItemBlockRenderTypes.setRenderLayer(BlockRegistrar.BulbRegistrar.SLOW_FALLING.getBulbShrub().get(), RenderType.translucent());
        }

        @SubscribeEvent
        public static void registerColourHandlers(RegisterColorHandlersEvent.Item event) {
            event.register((itemStack, i) -> i > 0 ? -1 : FastColor.ARGB32.opaque(
                            itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor()),
                    ItemRegistrar.FILLED_FLASK.get());
        }

        @SubscribeEvent
        public static void registerColourHandlers(RegisterColorHandlersEvent.Block event) {
//            event.register(
//                    (blockState, tintGetter, blockPos, i) -> tintGetter != null && blockPos != null
//                            ? BiomeColors.getAverageGrassColor(tintGetter, blockPos)
//                            : GrassColor.getDefaultColor(),
//                    BlockRegistrar.BULB.get()
//            );
            event.register((state, world, pos, tintIndex) -> world != null && pos != null
                            ? BiomeColors.getAverageGrassColor(world, pos)
                            : GrassColor.get(0.5D, 1.0D),
                    BlockRegistrar.BULB_SHRUB.get());

            for (BlockRegistrar.BulbRegistrar registrar : BlockRegistrar.BulbRegistrar.values()) {
                event.register((state, world, pos, tintIndex) -> world != null && pos != null
                                ? BiomeColors.getAverageGrassColor(world, pos)
                                : GrassColor.get(0.5D, 1.0D),
                        registrar.getBulbShrub().get());
            }
        }
    }
}
