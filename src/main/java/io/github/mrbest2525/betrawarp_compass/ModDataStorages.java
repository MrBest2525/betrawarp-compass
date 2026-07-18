package io.github.mrbest2525.betrawarp_compass;

import com.mojang.serialization.Codec;
import io.github.mrbest2525.betrawarp_compass.datastorage.DataStorageType;
import net.minecraft.resources.Identifier;

public class ModDataStorages {
    
    public static final DataStorageType<Integer> WARP_ENERGY = new DataStorageType<Integer>(
            Identifier.fromNamespaceAndPath(BetraWarp_Compass.MOD_ID, "warp_energy"),
            Codec.INT,
            0
    );
    
    public static final DataStorageType<Boolean> IS_COMPASS_LINKER = new DataStorageType<Boolean>(
            Identifier.fromNamespaceAndPath(BetraWarp_Compass.MOD_ID, "is_compass_linker"),
            Codec.BOOL,
            false
    );
    
    public static void init() {
    
    }
}
