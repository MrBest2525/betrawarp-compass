package io.github.mrbest2525.betrawarp_compass;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        // タグの定義 (例: "your_mod_id:warp_targets")
        public static final TagKey<Block> WARP_TARGETS = TagKey.create(
                Registries.BLOCK,
                Identifier.fromNamespaceAndPath(BetraWarp_Compass.MOD_ID, "warp_targets")
        );
    }
}
