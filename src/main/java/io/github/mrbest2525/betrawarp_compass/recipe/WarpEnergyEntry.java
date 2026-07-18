package io.github.mrbest2525.betrawarp_compass.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public record WarpEnergyEntry(Ingredient ingredient, int energy) {
    public static final Codec<WarpEnergyEntry> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Ingredient.CODEC
                                    .fieldOf("ingredient")
                                    .forGetter(WarpEnergyEntry::ingredient),
                            
                            Codec.INT
                                    .fieldOf("energy")
                                    .forGetter(WarpEnergyEntry::energy)
                    
                    ).apply(instance, WarpEnergyEntry::new)
            );
}
