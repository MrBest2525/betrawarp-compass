package io.github.mrbest2525.betrawarp_compass;

import io.github.mrbest2525.betrawarp_compass.item.CompassLinkerItem;
import io.github.mrbest2525.betrawarp_compass.polymer.item.PolymerCompassLinkerItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ModItems {
    
    // Item Classの呼び出し分け
    public static final Function<Item.Properties, Item> COMPASS_LINKER_CLASS = FabricLoader.getInstance().isModLoaded("polymer-core") ? PolymerCompassLinkerItem::new : CompassLinkerItem::new;
    
    public static final Item COMPASS_LINKER = register("compass_linker", COMPASS_LINKER_CLASS, new Item.Properties());
    
    
    
    public static final ResourceKey<CreativeModeTab> MOD_CREATIVE_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(BetraWarp_Compass.MOD_ID, "creative_tab")
    );
    public static final CreativeModeTab MOD_CUSTOM_CREATIVE_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.COMPASS_LINKER))
            .title(Component.translatable(ModTranslationKeys.CreativeTab.BETRAWARP_COMPASS))
            .displayItems((params, output) -> {
                output.accept(ModItems.COMPASS_LINKER);
            })
            .build();
    
    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MOD_CREATIVE_TAB_KEY, MOD_CUSTOM_CREATIVE_TAB);
    }
    
    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        // Create the inputItem key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BetraWarp_Compass.MOD_ID, name));
        
        // Create the inputItem instance.
        T item = itemFactory.apply(settings.setId(itemKey));
        
        // Register the inputItem.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        
        return item;
    }
}
