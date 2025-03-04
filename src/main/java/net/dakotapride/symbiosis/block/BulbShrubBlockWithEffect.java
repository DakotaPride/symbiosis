package net.dakotapride.symbiosis.block;

import net.dakotapride.symbiosis.registrar.BlockRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class BulbShrubBlockWithEffect extends BasicBulbShrubBlock {
    public BulbShrubBlockWithEffect(Properties properties) {
        super(properties);
    }

    public static Holder<Potion> potionHolder;
    public static Supplier<Item> bulbItem;

    public BulbShrubBlockWithEffect(Holder<Potion> potionHolder, Supplier<Item> bulbItem, Properties properties) {
        super(properties);
        this.potionHolder = potionHolder;
        this.bulbItem = bulbItem;
    }

    public static Holder<Potion> getPotion() {
        return potionHolder;
    }

    public static Item getBulbItem() {
        return bulbItem.get();
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        BlockState north = level.getBlockState(pos.north());
        BlockState south = level.getBlockState(pos.south());
        BlockState west = level.getBlockState(pos.west());
        BlockState east = level.getBlockState(pos.east());


//        if (random.nextInt(2) > 0) {
//            if (state == Mutations.MINING_FATIGUE.getMutationBase().defaultBlockState() && (
//                    north == Mutations.MINING_FATIGUE.getMutationAddition().defaultBlockState()
//                            || south == Mutations.MINING_FATIGUE.getMutationAddition().defaultBlockState()
//                            || west == Mutations.MINING_FATIGUE.getMutationAddition().defaultBlockState()
//                            || east == Mutations.MINING_FATIGUE.getMutationAddition().defaultBlockState())) {
//                level.setBlockAndUpdate(pos, Blocks.DIAMOND_BLOCK.defaultBlockState());
//            }
//        }
/**/
        if (random.nextInt(2) == 1) {
            for (BasicBulbShrubBlock.Mutations mutations : BasicBulbShrubBlock.Mutations.values()) {
                if (state == mutations.getMutationBase().defaultBlockState()) {
                    if (north.is(mutations.getMutationAddition())) {
                        level.setBlockAndUpdate(pos, mutations.getResultMutation().defaultBlockState());
                        sendParticles(pos, level, state);
                    }
                    if (south.is(mutations.getMutationAddition())) {
                        level.setBlockAndUpdate(pos, mutations.getResultMutation().defaultBlockState());
                        sendParticles(pos, level, state);
                    }
                    if (west.is(mutations.getMutationAddition())) {
                        level.setBlockAndUpdate(pos, mutations.getResultMutation().defaultBlockState());
                        sendParticles(pos, level, state);
                    }
                    if (east.is(mutations.getMutationAddition())) {
                        level.setBlockAndUpdate(pos, mutations.getResultMutation().defaultBlockState());
                        sendParticles(pos, level, state);
                    }
                }
            }
        }

    }

    private static int sendParticles(BlockPos blockPos, ServerLevel serverLevel, BlockState blockState) {
        Vec3 vec3 = blockPos.getCenter().add(0.0, 0.5, 0.0);
        int i = (int) Mth.clamp(50.0F * 0.5F, 0.0F, 200.0F);
        return serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), vec3.x, vec3.y, vec3.z, i, 0.3F, 0.3F, 0.3F, 0.15F);
    }

    public enum Bulbs {
        REGENERATION(BlockRegistrar.BulbRegistrar.REGENERATION),
        STRENGTH(BlockRegistrar.BulbRegistrar.STRENGTH),
        SPEED(BlockRegistrar.BulbRegistrar.SPEED),
        SLOW_FALLING(BlockRegistrar.BulbRegistrar.SLOW_FALLING),;

        final Block shrub;
        final Block bulb;

        Bulbs(BlockRegistrar.BulbRegistrar registrar) {
            this.shrub = registrar.getBulbShrub().get();
            this.bulb = registrar.getBulbLayer().get();
        }

        public Block getShrub() {
            return shrub;
        }

        public Block getBulb() {
            return bulb;
        }
    }
}
