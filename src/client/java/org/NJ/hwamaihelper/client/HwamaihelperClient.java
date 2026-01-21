package org.NJ.hwamaihelper.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.NJ.hwamaihelper.client.logic.ConnectionHandler;
import org.NJ.hwamaihelper.client.logic.KeyBindingHandler;

public class HwamaihelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(KeyBindingHandler::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(org.NJ.hwamaihelper.client.logic.InventoryHandler::onClientTick);
        ClientPlayConnectionEvents.JOIN.register(ConnectionHandler::onJoin);
    }
}
