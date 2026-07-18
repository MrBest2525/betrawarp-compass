package io.github.mrbest2525.betrawarp_compass.recipe;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WarpEnergyManager {
    private static List<WarpEnergyEntry> entries = List.of();
    
    
    public static void setEntries(List<WarpEnergyEntry> newEntries) {
        entries = newEntries;
    }
    
    
    public static int getEnergy(ItemStack stack) {
        
        return entries.stream()
                .filter(entry -> entry.ingredient().test(stack))
                .mapToInt(WarpEnergyEntry::energy)
                .findFirst()
                .orElse(0);
    }
}
