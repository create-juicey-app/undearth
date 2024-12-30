package com.juiceydev.undearth.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = UndearthMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BedrockManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();
    private static boolean initialized = false;
    
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (initialized || !(event.getLevel() instanceof ServerLevel)) {
            return;
        }
        initialized = true;
        LOGGER.info("Initializing Undearth world generation");
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel)) {
            return;
        }

        var chunk = event.getChunk();
        if (chunk instanceof ChunkAccess) {
            try {
                processChunk((ChunkAccess)chunk);
            } catch (Exception e) {
                LOGGER.error("Error processing chunk: ", e);
            }
        }
    }

    public static void processChunk(ChunkAccess chunk) {
        int minBlockX = chunk.getPos().getMinBlockX();
        int minBlockZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Process in smaller batches
        for (int x = 0; x < 16; x += 2) {
            for (int z = 0; z < 16; z += 2) {
                processSection(chunk, x + minBlockX, z + minBlockZ, pos);
            }
        }
        chunk.setUnsaved(true);
    }

    private static void processSection(ChunkAccess chunk, int worldX, int worldZ, BlockPos.MutableBlockPos pos) {
        for (int y = -256; y <= -64; y++) {
            pos.set(worldX, y, worldZ);
            
            if (y <= -244 && (y == -256 || random.nextDouble() < 0.3)) {
                chunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
                continue;
            }

            if (!CaveGenerator.shouldGenerateCave(worldX, y, worldZ)) {
                chunk.setBlockState(pos, Blocks.DEEPSLATE.defaultBlockState(), false);
            } else if (y <= -234) {
                chunk.setBlockState(pos, Blocks.LAVA.defaultBlockState(), false);
            }
        }

        // Process adjacent blocks
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                processAdjacentBlock(chunk, worldX + dx, worldZ + dz, pos);
            }
        }
    }

    private static void processAdjacentBlock(ChunkAccess chunk, int x, int z, BlockPos.MutableBlockPos pos) {
        if (!Utils.isInChunkBounds(pos.set(x, 0, z), chunk)) return;
        
        for (int y = -64; y < 0; y++) {
            pos.setY(y);
            if (chunk.getBlockState(pos).is(Blocks.BEDROCK)) {
                chunk.setBlockState(pos, Blocks.DEEPSLATE.defaultBlockState(), false);
            }
        }
    }
}
