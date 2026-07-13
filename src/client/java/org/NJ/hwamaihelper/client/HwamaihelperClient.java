package org.NJ.hwamaihelper.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import fi.dy.masa.malilib.event.InitializationHandler;

public class HwamaihelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new HwamaiInitHandler());
        ClientTickEvents.END_CLIENT_TICK.register(org.NJ.hwamaihelper.client.logic.InventoryHandler::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(org.NJ.hwamaihelper.client.logic.QuickLandLevelingHandler::onClientTick);
    }
}
