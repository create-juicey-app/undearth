package com.juiceydev.undearth.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class Utils {
    public static boolean isInChunkBounds(BlockPos pos, ChunkAccess chunk) {
        int relX = pos.getX() - chunk.getPos().getMinBlockX();
        int relZ = pos.getZ() - chunk.getPos().getMinBlockZ();
        return relX >= 0 && relX < 16 && relZ >= 0 && relZ < 16;
    }

    public static boolean hasExposedSides(ChunkAccess chunk, BlockPos pos) {
        BlockPos[] adjacentPositions = {
            pos.above(), pos.below(),
            pos.north(), pos.south(),
            pos.east(), pos.west()
        };

        for (BlockPos adjacent : adjacentPositions) {
            if (isInChunkBounds(adjacent, chunk)) {
                BlockState state = chunk.getBlockState(adjacent);
                if (state.isAir() || state.getFluidState().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidLavaPosition(ChunkAccess chunk, BlockPos pos) {
        BlockState current = chunk.getBlockState(pos);
        return !current.is(Blocks.BEDROCK) && !current.is(Blocks.LAVA) && 
               !current.is(Blocks.MAGMA_BLOCK) && !current.is(Blocks.GLOWSTONE);
    }

    public static boolean isSimpleFloorCheck(ChunkAccess chunk, BlockPos pos) {
        return chunk.getBlockState(pos.below()).is(Blocks.DEEPSLATE);
    }
}
