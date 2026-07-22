package org.fuzi.redwork.block.geodeaccelerator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.fuzi.redwork.block.ModBlockEntities;
import org.fuzi.redwork.other.Config;
import org.fuzi.redwork.other.ModUtils;

public class GeodeAcceleratorBlockEntity extends BlockEntity {
    private static final int TICK_INTERVAL = 1;
    private static final int EXTRA_TICKS = 2;

    private int cooldown = 0;

    public GeodeAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEODE_ACCELERATOR_BE.get(), pos, state);
    }

    public static void staticTick(Level level, BlockPos pos, BlockState state, GeodeAcceleratorBlockEntity be) {
        if (!level.isClientSide) {
            be.tick(state, (ServerLevel) level, pos);
        }
    }

    private void tick(BlockState state, ServerLevel level, BlockPos pos) {
        var face = state.getValue(GeodeAcceleratorBlock.FACING);
        boolean hasSignal = ModUtils.hasNeighborSignal(level, pos, face);

        if (state.getValue(GeodeAcceleratorBlock.POWERED) != hasSignal) {
            level.setBlockAndUpdate(pos, state.setValue(GeodeAcceleratorBlock.POWERED, hasSignal));
        }

        if (!hasSignal) {
            return;
        }

        if (--cooldown > 0) {
            return;
        }
        cooldown = TICK_INTERVAL;

        BlockPos front = pos.relative(face);
        BlockState frontState = level.getBlockState(front);

        if (!Config.getGeodeAcceleratorTargetBlocks().contains(frontState.getBlock())) {
            return;
        }

        for (int i = 0; i < EXTRA_TICKS; i++) {
            frontState.randomTick(level, front, level.random);
        }
    }
}
