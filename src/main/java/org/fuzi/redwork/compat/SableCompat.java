package org.fuzi.redwork.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SableCompat {
    private static boolean isLoaded = false;

    public static void init() {
        try {
            Class.forName("dev.ryanhcode.sable.api.sublevel.SubLevel");
            isLoaded = true;
            System.out.println("Sable detected");
        } catch (ClassNotFoundException ignored) {}
    }

    public static boolean hasNeighborSignal(Level level, BlockPos pos) {
        if (!isLoaded) {
            return level.hasNeighborSignal(pos);
        }

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.isSignalSource() && neighborState.getSignal(level, neighborPos, dir.getOpposite()) > 0) {
                return true;
            }
        }
        return level.hasNeighborSignal(pos);
    }
}
