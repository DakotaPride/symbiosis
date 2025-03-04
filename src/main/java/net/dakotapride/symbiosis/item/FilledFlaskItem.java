package net.dakotapride.symbiosis.item;

import net.dakotapride.symbiosis.registrar.ItemRegistrar;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;
import java.util.Objects;

public class FilledFlaskItem extends PotionItem {
    public FilledFlaskItem(Item.Properties properties) {
        super(properties);
    }

    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        return itemstack;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player ? (Player)entityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!level.isClientSide) {
            PotionContents potioncontents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            potioncontents.forEachEffect((effect) -> {
                effect.duration = (effect.getDuration() / 2);

                if ((effect.getEffect().value()).isInstantenous()) {
                    (effect.getEffect().value()).applyInstantenousEffect(player, player, entityLiving, effect.getAmplifier(), 1.0);
                } else {
                    entityLiving.addEffect(effect);
                }

            });
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.consume(1, player);
        }

        if (player == null || !player.hasInfiniteMaterials()) {
            if (stack.isEmpty()) {
                return new ItemStack(ItemRegistrar.EMPTY_FLASK.get());
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(ItemRegistrar.EMPTY_FLASK.get()));
            }
        }

        entityLiving.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        PotionContents potioncontents = itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        BlockState blockstate = level.getBlockState(blockpos);
        if (context.getClickedFace() != Direction.DOWN && blockstate.is(BlockTags.CONVERTABLE_TO_MUD) && potioncontents.is(Potions.WATER)) {
            level.playSound(null, blockpos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(itemstack, player, new ItemStack(ItemRegistrar.EMPTY_FLASK.get())));
            player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
            if (!level.isClientSide) {
                ServerLevel serverlevel = (ServerLevel)level;

                for(int i = 0; i < 5; ++i) {
                    serverlevel.sendParticles(ParticleTypes.SPLASH, (double)blockpos.getX() + level.random.nextDouble(), (blockpos.getY() + 1), (double)blockpos.getZ() + level.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                }
            }

            level.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
            level.setBlockAndUpdate(blockpos, Blocks.MUD.defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

//        if (blockstate.is(BlockRegistrar.BULB.get()) && player != null) {
//            if (potioncontents.is(Potions.REGENERATION)) {
//                level.setBlock(blockpos, BlockRegistrar.REGENERATION_BULB.get().defaultBlockState(), 11);
//                //return InteractionResult.sidedSuccess(level.isClientSide);
//            }
//
//            itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
//
//            return InteractionResult.sidedSuccess(level.isClientSide);
//        }

        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return Potion.getName((stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion(), this.getDescriptionId() + ".effect.");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        PotionContents potioncontents = stack.get(DataComponents.POTION_CONTENTS);
        if (potioncontents != null) {
            Objects.requireNonNull(tooltipComponents);
            potioncontents.addPotionTooltip(tooltipComponents::add, 0.5F, context.tickRate());
        }

    }

}
