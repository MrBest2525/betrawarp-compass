package io.github.mrbest2525.betrawarp_compass;

import io.github.mrbest2525.betrawarp_compass.netwark.ModInstallStatusPacket;
import io.github.mrbest2525.betrawarp_compass.polymer.PolymerMain;
import io.github.mrbest2525.betrawarp_compass.recipe.WarpEnergyLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetraWarp_Compass implements ModInitializer {
    
    public static final String MOD_ID = "betrawarp_compass";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        ModItems.init();
        ModDataStorages.init();
        
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((creativeTab) -> creativeTab.accept(ModItems.COMPASS_LINKER));
        
        DataResourceLoader.get().registerReloadListener(
                Identifier.fromNamespaceAndPath(MOD_ID, "warp_energy"),
                WarpEnergyLoader::new
        );
        
        PayloadTypeRegistry.serverboundPlay().register(ModInstallStatusPacket.TYPE, ModInstallStatusPacket.STREAM_CODEC);
        
        ServerPlayNetworking.registerGlobalReceiver(ModInstallStatusPacket.TYPE, (packet, context) -> {
            // パケットを送ってきたプレイヤーを特定し、状態を保存
            ServerPlayer player = context.player();
            ModClientManager.setHasMod(player.getUUID(), true);
        });
        
        // 切断時にマップからUUIDを削除
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ModClientManager.removeHasMod(handler.getPlayer().getUUID());
        });
        
        // Polymer
        if (FabricLoader.getInstance().isModLoaded("polymer-core")) {
            PolymerMain.onInitialize();
        }
    }
}
