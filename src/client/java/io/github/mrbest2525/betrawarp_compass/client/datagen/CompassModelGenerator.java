package io.github.mrbest2525.betrawarp_compass.client.datagen;

import com.google.gson.JsonObject;
import io.github.mrbest2525.betrawarp_compass.BetraWarp_Compass;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class CompassModelGenerator implements DataProvider {
    private final FabricPackOutput output;
    
    public CompassModelGenerator(FabricPackOutput output) {
        this.output = output;
    }
    
    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cachedOutput) {
        // パス: resources/assets/betrawarp_compass/models/item/
        PackOutput.PathProvider pathProvider = output.createPathProvider(
                FabricPackOutput.Target.RESOURCE_PACK, "models/item"
        );
        
        // 32個分ループして出力
        for (int i = 0; i < 32; i++) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", "betrawarp_compass:item/compass_linker");
            
            JsonObject textures = new JsonObject();
            textures.addProperty("1", String.format("minecraft:item/compass_%02d", i));
            model.add("textures", textures);
            
            // ファイル名: compass_00.json ～ compass_31.json
            String fileName = String.format("compass_linker_%02d", i);
            DataProvider.saveStable(cachedOutput, model, pathProvider.file(Identifier.fromNamespaceAndPath(BetraWarp_Compass.MOD_ID, fileName), "json"));
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public @NonNull String getName() {
        return "Compass Model Generator";
    }
}
