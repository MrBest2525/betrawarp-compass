package io.github.mrbest2525.betrawarp_compass.menu;

import io.github.mrbest2525.betrawarp_compass.ModTranslationKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ModMenuProviders {
    
    public static MenuProvider openCompassLinkerMenu(ItemStack compass) {
        return new MenuProvider() {
            @Override
            public @NonNull Component getDisplayName() {
                return Component.translatable(ModTranslationKeys.Menu.BetrawarpCompass.CompassLinker.TITLE);
            }
            
            @Override
            public @Nullable AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory, @NonNull Player player) {
                return new CompassLinkerMenu(containerId, inventory, compass);
            }
        };
    }
}
