package io.github.mrbest2525.betrawarp_compass.netwark;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import static io.github.mrbest2525.betrawarp_compass.BetraWarp_Compass.MOD_ID;

public record ModInstallStatusPacket() implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ModInstallStatusPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "mod_installed"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ModInstallStatusPacket> STREAM_CODEC =
            StreamCodec.unit(new ModInstallStatusPacket());
    
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
