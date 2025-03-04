package net.dakotapride.symbiosis.block;

import net.dakotapride.symbiosis.registrar.BlockRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

public class BasicBulbShrubBlock extends Block {
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

    public BasicBulbShrubBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SAPLING_SHAPE;
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock || (this.defaultBlockState().is(BlockRegistrar.BulbRegistrar.SLOW_FALLING.getBulbShrub()) && state.is(Blocks.END_STONE));
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState belowBlockState = level.getBlockState(blockpos);
        TriState soilDecision = belowBlockState.canSustainPlant(level, blockpos, Direction.UP, state);
        return !soilDecision.isDefault() ? soilDecision.isTrue() : this.mayPlaceOn(belowBlockState, level, blockpos);
    }

    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return pathComputationType == PathComputationType.AIR && !this.hasCollision || super.isPathfindable(state, pathComputationType);
    }

    //    public static InteractionResultHolder<ItemStack> result(ItemStack stack, BlockState state, Level level) {
//        if (stack.is(Items.GLASS_BOTTLE) && state.is(BlockRegistrar.REGENERATION_BULB)) {
//            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
//        }
//
//        return InteractionResultHolder.pass(stack);
//    }

    enum Mutations {
        //BLINDNESS(),
        //SLOWNESS(),
        STRENGTH_FROM_REGENERATION(BlockRegistrar.BulbRegistrar.REGENERATION.getBulbShrub().get(), BlockRegistrar.BLAZE_THORN_BUSH.get(), BlockRegistrar.BulbRegistrar.STRENGTH.getBulbShrub().get()),
        SLOW_FALLING_FROM_SPEED(BlockRegistrar.BulbRegistrar.SPEED.getBulbShrub().get(), Blocks.CHORUS_FLOWER, BlockRegistrar.BulbRegistrar.SLOW_FALLING.getBulbShrub().get()),;

        final Block mutationBase;
        final Block mutationAddition;
        final Block resultMutation;

        Mutations(Block base, Block addition, Block result) {
            this.mutationBase = base;
            this.mutationAddition = addition;
            this.resultMutation = result;
        }

        public Block getMutationBase() {
            return mutationBase;
        }

        public Block getMutationAddition() {
            return mutationAddition;
        }

        public Block getResultMutation() {
            return resultMutation;
        }
    }
}
