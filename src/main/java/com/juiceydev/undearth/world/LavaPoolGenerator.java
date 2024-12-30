package com.juiceydev.undearth.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;

import com.juiceydev.undearth.world.MagmaVeinGenerator;

public class LavaPoolGenerator {
    private static final Random random = new Random();
    public static final int MAX_POOL_SIZE = 6;
    
    public static void generatePool(ChunkAccess chunk, BlockPos center, int size) {
        // Only generate pools below the main lava layer
        if (center.getY() >= -234) return;
        
        for (int dx = -size; dx <= size; dx++) {
            for (int dz = -size; dz <= size; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance <= size) {
                    double edgeNoise = random.nextDouble() * 1.5;
                    if (distance <= size - edgeNoise) {
                        BlockPos poolPos = center.offset(dx, 0, dz);
                        if (Utils.isInChunkBounds(poolPos, chunk)) {
                            // Lava pools now flow upward to connect with the main lava layer
                            int height = -234 - center.getY();
                            for (int dy = 0; dy <= height; dy++) {
                                BlockPos lavaPos = poolPos.offset(0, dy, 0);
                                if (Utils.isValidLavaPosition(chunk, lavaPos) && 
                                    (chunk.getBlockState(lavaPos).isAir() || dy == 0)) {
                                    chunk.setBlockState(lavaPos, Blocks.LAVA.defaultBlockState(), false);
                                }
                            }
                            
                            // Generate magma only at the bottom
                            if (Utils.isValidLavaPosition(chunk, poolPos.below())) {
                                MagmaVeinGenerator.generateVein(chunk, poolPos.below());
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean hasExposedSide(ChunkAccess chunk, BlockPos pos, int depth) {
        for (int dy = 0; dy >= -depth; dy--) {
            BlockPos checkPos = pos.offset(0, dy, 0);
            if (Utils.hasExposedSides(chunk, checkPos)) {
                return true;
            }
        }
        return false;
    }

    private static void generateLavaColumn(ChunkAccess chunk, BlockPos pos, int depth) {
        for (int dy = 0; dy >= -depth; dy--) {
            BlockPos lavaPos = pos.offset(0, dy, 0);
            if (lavaPos.getY() >= -256 && Utils.isValidLavaPosition(chunk, lavaPos)) {
                chunk.setBlockState(lavaPos, Blocks.LAVA.defaultBlockState(), false);
            }
        }
    }
}
