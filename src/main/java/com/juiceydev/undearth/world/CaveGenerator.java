package com.juiceydev.undearth.world;

import com.juiceydev.undearth.world.noise.FastNoiseLite;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;

public class CaveGenerator {
    private static FastNoiseLite noise;
    private static final float CAVE_THRESHOLD = 0.3f;
    private static final Long2FloatOpenHashMap noiseCache = new Long2FloatOpenHashMap();
    
    public static void init() {
        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFrequency(0.02f); // Increased frequency for better performance
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(3); // Reduced octaves for better performance
        noise.SetFractalLacunarity(2.0f);
        noise.SetFractalGain(0.5f);
        noise.SetSeed((int) System.currentTimeMillis());
        noiseCache.clear();
    }
    
    public static boolean shouldGenerateCave(int x, int y, int z) {
        if (y >= -64 || noise == null) return false;
        
        long key = ((long)x << 32) | ((long)z << 16) | (y & 0xFFFF);
        float noiseValue = noiseCache.computeIfAbsent(key, k -> noise.GetNoise(x, y * 0.7f, z));
        float yScale = 1.0f - Math.abs((y + 300) / -300f);
        float threshold = CAVE_THRESHOLD * yScale;
        
        return noiseValue > threshold;
    }
    
    public static void clearCache() {
        noiseCache.clear();
    }
}
