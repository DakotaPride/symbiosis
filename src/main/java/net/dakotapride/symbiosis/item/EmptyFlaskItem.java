package net.dakotapride.symbiosis.item;

import net.dakotapride.symbiosis.Symbiosis;
import net.dakotapride.symbiosis.block.BulbLayerBlock;
import net.dakotapride.symbiosis.block.BulbShrubBlockWithEffect;
import net.dakotapride.symbiosis.registrar.BlockRegistrar;
import net.dakotapride.symbiosis.registrar.ItemRegistrar;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class EmptyFlaskItem extends Item {
    public EmptyFlaskItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.MISS) {
            if (blockhitresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = blockhitresult.getBlockPos();
                if (!level.mayInteract(player, blockpos)) {
                    return InteractionResultHolder.pass(itemstack);
                }

                if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);
                    return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem(itemstack, player, PotionContents.createItemStack(Items.POTION, Potions.WATER)), level.isClientSide());
                }
            }

        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(pos);
        InteractionHand hand = context.getHand();

        if (itemStack.is(ItemRegistrar.EMPTY_FLASK)) {
            for (AvailableEffects availableEffects : AvailableEffects.values()) {
                if (context.getPlayer() instanceof Player player) {
                    if (blockState.is(availableEffects.getBlock())) {
                        player.getItemInHand(hand).shrink(1);

                        ItemStack filledFlaskStack = ItemRegistrar.FILLED_FLASK.get().getDefaultInstance();
                        filledFlaskStack.set(DataComponents.POTION_CONTENTS, availableEffects.getPotionContents());

                        player.addItem(filledFlaskStack);


                        level.setBlock(pos, BlockRegistrar.BULB_SHRUB.get().defaultBlockState(), 11);

                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }

//            if (blockState.is(Symbiosis.EFFECT_BULB_SHRUBS)) {
//                if (context.getPlayer() instanceof Player player) {
//                    player.getItemInHand(hand).shrink(1);
//
//                    //player.getCooldowns().addCooldown(itemStack.getItem(), 200);
//
//                    Holder<Potion> holder = BulbShrubBlockWithEffect.getPotion();
//
//                    ItemStack filledFlaskStack = ItemRegistrar.FILLED_FLASK.get().getDefaultInstance();
//                    filledFlaskStack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
//
//                    player.addItem(filledFlaskStack);
//
//                    //BasicBulbBlock.result(itemStack, blockState, level);
//                }
//
//                level.setBlock(pos, BlockRegistrar.BULB_SHRUB.get().defaultBlockState(), 11);
//
//                return InteractionResult.sidedSuccess(level.isClientSide);
//            }
        }

        return InteractionResult.PASS;
    }

    protected ItemStack turnBottleIntoItem(ItemStack bottleStack, Player player, ItemStack filledBottleStack) {
        player.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.createFilledResult(bottleStack, player, filledBottleStack);
    }

    enum AvailableEffects {
        REGENERATION(BlockRegistrar.BulbRegistrar.REGENERATION),
        STRENGTH(BlockRegistrar.BulbRegistrar.STRENGTH),
        SPEED(BlockRegistrar.BulbRegistrar.SPEED),
        SLOW_FALLING(BlockRegistrar.BulbRegistrar.SLOW_FALLING),;

        final Block block;
        final PotionContents potionContents;

        AvailableEffects(BlockRegistrar.BulbRegistrar registrar) {
            this.block = registrar.getBulbShrub().get();
            this.potionContents = new PotionContents(registrar.getPotionHolder());
        }

        public Block getBlock() {
            return block;
        }

        public PotionContents getPotionContents() {
            return potionContents;
        }
    }
}
