package io.github.mrbest2525.betrawarp_compass.client.datagen;

import io.github.mrbest2525.betrawarp_compass.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class BetraWarp_CompassRecipeProvider extends FabricRecipeProvider {
    
    public BetraWarp_CompassRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }
    
    @Override
    protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registries, @NonNull RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
                
                shaped(RecipeCategory.COMBAT, ModItems.COMPASS_LINKER)
                        .pattern("ANA")
                        .pattern("NEN")
                        .pattern("ANA")
                        .define('A', Items.AMETHYST_SHARD)
                        .define('E', Items.ENDER_EYE)
                        .define('N', Items.NETHERITE_SCRAP)
                        .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                        .save(output);
            }
        };
    }
    
    @Override
    public @NonNull String getName() {
        return "BetraWarp_CompassRecipeProvider";
    }
}
