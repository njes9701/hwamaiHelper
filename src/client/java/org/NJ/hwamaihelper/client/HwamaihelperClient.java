package org.NJ.hwamaihelper.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import fi.dy.masa.malilib.event.InitializationHandler;
import org.NJ.hwamaihelper.client.logic.ConnectionHandler;

public class HwamaihelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new HwamaiInitHandler());
        org.NJ.hwamaihelper.client.logic.ResourcePackHandler.init();
        ClientTickEvents.END_CLIENT_TICK.register(org.NJ.hwamaihelper.client.logic.ResourcePackHandler::tick);
        ClientTickEvents.END_CLIENT_TICK.register(org.NJ.hwamaihelper.client.logic.InventoryHandler::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(org.NJ.hwamaihelper.client.logic.QuickLandLevelingHandler::onClientTick);
        ClientPlayConnectionEvents.JOIN.register(ConnectionHandler::onJoin);
    }
}
