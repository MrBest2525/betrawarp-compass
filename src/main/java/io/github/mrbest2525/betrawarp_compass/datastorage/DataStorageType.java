package io.github.mrbest2525.betrawarp_compass.datastorage;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record DataStorageType<T>(
        Identifier id,
        Codec<T> codec,
        T defaultValue
) {
}
