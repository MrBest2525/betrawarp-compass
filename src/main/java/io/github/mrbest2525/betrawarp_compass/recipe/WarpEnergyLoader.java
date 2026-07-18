package io.github.mrbest2525.betrawarp_compass.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.mrbest2525.betrawarp_compass.BetraWarp_Compass;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class WarpEnergyLoader implements PreparableReloadListener {
    
    private final HolderLookup.Provider registries;
    
    public WarpEnergyLoader(HolderLookup.Provider registries) {
        this.registries = registries;
    }
    
    @Override
    public @NonNull CompletableFuture<Void> reload(@NonNull SharedState currentReload,
                                                   @NonNull Executor taskExecutor,
                                                   @NonNull PreparationBarrier preparationBarrier,
                                                   @NonNull Executor reloadExecutor) {
        return CompletableFuture
                .supplyAsync(() -> this.load(currentReload.resourceManager()), taskExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(
                        WarpEnergyManager::setEntries,
                        reloadExecutor
                );
    }
    
    
    private List<WarpEnergyEntry> load(ResourceManager resourceManager) {
        List<WarpEnergyEntry> result = new ArrayList<>();
        
        resourceManager.listResources(
                "warp_energy",
                path -> path.getPath().endsWith(".json")
        ).forEach((id, resource) -> {
            BetraWarp_Compass.LOGGER.debug("[BetraWarp: Compass] 発見したファイル: {}", id);
            try {
                JsonElement json = GsonHelper.parse(resource.openAsReader());
                
                var parseResult = WarpEnergyEntry.CODEC
                        .parse(this.registries.createSerializationContext(JsonOps.INSTANCE), json);
                
                if (parseResult.result().isPresent()) {
                    result.add(parseResult.result().get());
                    BetraWarp_Compass.LOGGER.debug("[BetraWarp: Compass] 正常にパース完了: {}", id);
                } else {
                    // パース失敗時の理由をコンソールに出す
                    BetraWarp_Compass.LOGGER.error("[BetraWarp: Compass] パース失敗: {} 理由: {}", id, parseResult.error().isPresent() ? parseResult.error().get().message() : null);
                }
                
            } catch (Exception e) {
                BetraWarp_Compass.LOGGER.error("[BetraWarp: Compass] ファイルの読み込み中にエラーが発生: {}", id, e);
            }
        });
        
        return result;
    }
}