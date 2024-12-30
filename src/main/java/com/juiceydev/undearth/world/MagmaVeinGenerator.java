package com.juiceydev.undearth.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;

public class MagmaVeinGenerator {
    private static final Random random = new Random();
    private static final int VEIN_SIZE = 4;
    private static final double VEIN_THRESHOLD = 0.5;
    
    public static void generateVein(ChunkAccess chunk, BlockPos center) {
        for (int dx = -VEIN_SIZE; dx <= VEIN_SIZE; dx++) {
            for (int dy = -VEIN_SIZE; dy <= VEIN_SIZE; dy++) {
                for (int dz = -VEIN_SIZE; dz <= VEIN_SIZE; dz++) {
                    BlockPos veinPos = center.offset(dx, dy, dz);
                    if (Utils.isInChunkBounds(veinPos, chunk) && veinPos.getY() >= -256) {
                        // Use 3D noise to create natural vein patterns
                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        double noise = random.nextDouble() * (1.0 - (distance / VEIN_SIZE));
                        
                        if (noise > VEIN_THRESHOLD && Utils.isValidLavaPosition(chunk, veinPos)) {
                            boolean placeMagma = random.nextDouble() < (1.0 - distance / VEIN_SIZE);
                            chunk.setBlockState(veinPos, 
                                placeMagma ? Blocks.MAGMA_BLOCK.defaultBlockState() 
                                         : Blocks.DEEPSLATE.defaultBlockState(), 
                                false);
                        }
                    }
                }
            }
        }
    }
}
