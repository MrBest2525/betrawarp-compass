package io.github.mrbest2525.betrawarp_compass.client.datagen;

import io.github.mrbest2525.betrawarp_compass.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class BetraWarp_CompassRecipeProvider extends FabricRecipeProvider {
    
    public BetraWarp_CompassRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }
    
    @Override
    protected @NonNull RecipeProvider createRecipeProvider(
            HolderLookup.@NonNull Provider registries,
            @NonNull BootstrapContext<Recipe<?>> recipeContext,
            @NonNull BootstrapContext<Advancement> advancementContext
    ) {
        return new RecipeProvider(recipeContext, advancementContext) {
            @Override
            public void buildRecipes() {
                
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
