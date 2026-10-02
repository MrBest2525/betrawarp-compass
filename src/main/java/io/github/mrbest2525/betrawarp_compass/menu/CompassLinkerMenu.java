package io.github.mrbest2525.betrawarp_compass.menu;

import io.github.mrbest2525.betrawarp_compass.ModDataStorages;
import io.github.mrbest2525.betrawarp_compass.ModTranslationKeys;
import io.github.mrbest2525.betrawarp_compass.datastorage.DataStorageUtil;
import io.github.mrbest2525.betrawarp_compass.recipe.WarpEnergyManager;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public class CompassLinkerMenu extends AbstractContainerMenu {
    
    public static final int ROW = 5;
    
    private final Container container = new SimpleContainer(ROW * 9);
    
    protected CompassLinkerMenu(int containerId, Inventory inventory, ItemStack warpItem) {
        super(MenuType.GENERIC_9x5, containerId);
        int left = 8;
        int top = 10;
        int inventoryTop = 18 + ROW * 18 + 13;
        
        ItemContainerContents compassContainer = warpItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStack compass = compassContainer.copyOne();
        
        container.setItem(20, compass.copy());
        container.setItem(22, new ItemStack(Items.WRITABLE_BOOK));
        setupBackgroundDecorations();
        
        for(int y = 0; y < ROW; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new WarpItemMenuSlot(container, x + y * 9, left + x * 18, top + y * 18, warpItem));
            }
        }
        this.addStandardInventorySlots(inventory, 8, inventoryTop);
    }
    
    private void setupBackgroundDecorations() {
        TooltipDisplay noTooltip = new TooltipDisplay(true, new LinkedHashSet<>());
        
        for (int i = 0; i < container.getContainerSize(); i++) {
            // 例外スロット（プレイヤーが操作する場所）はスキップ
            if (i == 20 || i == 22 || i == 24) continue;
            
            ItemStack glass;
            switch (i) {
                case 10, 12, 28, 30 -> glass = new ItemStack(Items.STAINED_GLASS_PANE.magenta());
                case 11, 19, 21, 29 -> glass = new ItemStack(Items.STAINED_GLASS_PANE.lightBlue());
                case 14, 16, 32, 34 -> glass = new ItemStack(Items.STAINED_GLASS_PANE.yellow());
                case 15, 23, 25, 33 -> glass = new ItemStack(Items.STAINED_GLASS_PANE.lime());
                default -> glass = new ItemStack(Items.STAINED_GLASS_PANE.white());
            }
            glass.set(DataComponents.TOOLTIP_DISPLAY, noTooltip);
            container.setItem(i, glass.copy());
        }
    }
    
    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < ROW * 9) {
                if (!this.moveItemStackTo(stack, ROW * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, ROW * 9, false)) {
                return ItemStack.EMPTY;
            }
            
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return clicked;
    }
    
    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }
    
//    @Override
//    public void removed(@NonNull Player player) {
//        if (this.warpItem != null && !this.warpItem.isEmpty()) {
//
//            ItemContainerContents compassContainer = this.warpItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
//            int currentSize = (int) compassContainer.allItemsCopyStream().count();
//            int requiredSize = Math.max(1, currentSize);
//            NonNullList<ItemStack> tempItems = NonNullList.withSize(requiredSize, ItemStack.EMPTY);
//            compassContainer.copyInto(tempItems);
//
//            tempItems.set(0, this.container.getItem(20));
//
//            ItemContainerContents newContents = ItemContainerContents.fromItems(tempItems);
//            this.warpItem.set(DataComponents.CONTAINER, newContents);
//
//            if (player instanceof ServerPlayer) {
//                player.containerMenu.broadcastChanges();
//            }
//        }
//
//        super.removed(player);
//    }
    
    
    
    public static class WarpItemMenuSlot extends Slot {
        
        protected final int slot;
        protected ItemStack warpItem;
        public WarpItemMenuSlot(Container container, int slot, int x, int y, ItemStack warpItem) {
            super(container, slot, x, y);
            this.slot = slot;
            this.warpItem = warpItem;
        }
        
        @Override
        public boolean mayPlace(@NonNull ItemStack stack) {
            if (this.slot == 20) {
                return stack.has(DataComponents.LODESTONE_TRACKER);
            } else if (this.slot == 24) {
                int energy = WarpEnergyManager.getEnergy(stack);
                return energy > 0;
            } else {
                return false;
            }
        }
        
        @Override
        public boolean mayPickup(@NonNull Player player) {
            return this.slot == 20;
        }
        
        @Override
        public int getMaxStackSize() {
            if (this.slot == 24) {
                return this.container.getMaxStackSize();
            } else {
                return 1;
            }
        }
        
        @Override
        public void set(@NonNull ItemStack itemStack) {
            if (this.slot == 24) {
                int energy = WarpEnergyManager.getEnergy(itemStack);
                DataStorageUtil.setData(warpItem, ModDataStorages.WARP_ENERGY, energy * itemStack.count() + DataStorageUtil.getDataOrDefault(warpItem, ModDataStorages.WARP_ENERGY));
                
                this.setChanged();
            } else if (this.slot == 20) {
                if (this.warpItem != null && !this.warpItem.isEmpty()) {
                    ItemContainerContents compassContainer = this.warpItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                    int currentSize = (int) compassContainer.nonEmptyItemCopyStream().count();
                    int requiredSize = Math.max(1, currentSize);
                    NonNullList<ItemStack> tempItems = NonNullList.withSize(requiredSize, ItemStack.EMPTY);
                    compassContainer.copyInto(tempItems);
                    
                    tempItems.set(0, itemStack);
                    
                    ItemContainerContents newContents = ItemContainerContents.fromItems(tempItems);
                    this.warpItem.set(DataComponents.CONTAINER, newContents);
                    
                    LodestoneTracker tracker = itemStack.getOrDefault(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.empty(), false));
                    this.warpItem.set(DataComponents.LODESTONE_TRACKER, tracker);
                }
                this.setChanged();
                super.set(itemStack);
            }else {
                super.set(itemStack);
            }
        }
        
        @Override
        public @NonNull ItemStack getItem() {
            if (this.slot == 22) {
                ItemStack book = super.getItem();
                List<Component> loreList = new ArrayList<>();
                loreList.add(Component.translatable(ModTranslationKeys.Menu.BetrawarpCompass.CompassLinker.Info.Lore.INFO));
                
                ItemContainerContents container = warpItem.get(DataComponents.CONTAINER);
                if (container != null) {
                    ItemContainerContents compassContainer = warpItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                    ItemStack compass = compassContainer.copyOne();
                    LodestoneTracker lodestoneTracker = compass.get(DataComponents.LODESTONE_TRACKER);
                    if (lodestoneTracker != null && lodestoneTracker.target().isPresent()) {
                        GlobalPos globalPos = lodestoneTracker.target().get();
                        Identifier id = globalPos.dimension().identifier();
                        String translationKey = "dimension." + id.getNamespace() + "." + id.getPath();
                        
                        loreList.add(Component.translatable(ModTranslationKeys.Menu.BetrawarpCompass.CompassLinker.Info.Lore.TARGET_POS, Component.translatable(translationKey), globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ()));
                    }
                }
                int energy = DataStorageUtil.getDataOrDefault(warpItem, ModDataStorages.WARP_ENERGY);
                loreList.add(Component.translatable(ModTranslationKeys.Menu.BetrawarpCompass.CompassLinker.Info.Lore.ENERGY, energy));
                
                book.set(DataComponents.ITEM_NAME, Component.translatable(ModTranslationKeys.Menu.BetrawarpCompass.CompassLinker.Info.NAME));
                book.set(DataComponents.LORE, new ItemLore(loreList));
            }
            return super.getItem();
        }
    }
}
