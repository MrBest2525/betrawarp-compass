package io.github.mrbest2525.betrawarp_compass.client;

import io.github.mrbest2525.betrawarp_compass.netwark.ModInstallStatusPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BetraWarp_CompassClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // サーバーへ「MODを入れている」という情報を送る
            ClientPlayNetworking.send(new ModInstallStatusPacket());
        });
    }
}
