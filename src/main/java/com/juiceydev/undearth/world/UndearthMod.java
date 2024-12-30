package com.juiceydev.undearth.world;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(UndearthMod.MOD_ID)
public class UndearthMod {
    public static final String MOD_ID = "undearth";
    private static final Logger LOGGER = LogManager.getLogger();

    public UndearthMod() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(BedrockManager.class);
        LOGGER.info("Undearth mod constructor initialized");
    }

    @SubscribeEvent
    public void onSetup(FMLCommonSetupEvent event) {
        CaveGenerator.init();
        LOGGER.info("Undearth mod setup completed");
    }
}
