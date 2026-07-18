package io.github.mrbest2525.betrawarp_compass.mixin;

import io.github.mrbest2525.betrawarp_compass.ModDataStorages;
import io.github.mrbest2525.betrawarp_compass.ModTranslationKeys;
import io.github.mrbest2525.betrawarp_compass.datastorage.DataStorageUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemLore.class)
public class ItemStackTooltipMixin {
    @Inject(method = "addToTooltip", at = @At("RETURN"))
    private void injectAddToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components, CallbackInfo ci) {
        if (DataStorageUtil.getDataOrDefault(components, ModDataStorages.IS_COMPASS_LINKER)) {
            
            ItemContainerContents container = components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            ItemStack compass = container.copyOne();
            LodestoneTracker tracker = compass.get(DataComponents.LODESTONE_TRACKER);
            if (tracker != null && tracker.target().isPresent()) {
                GlobalPos globalPos = tracker.target().get();
                Identifier id = globalPos.dimension().identifier();
                String translationKey = "dimension." + id.getNamespace() + "." + id.getPath();
                consumer.accept(Component.translatable(ModTranslationKeys.Tooltip.BetrawarpCompass.TARGET_POSITION, Component.translatable(translationKey), globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ()));
            } else {
                consumer.accept(Component.translatable(ModTranslationKeys.Tooltip.BetrawarpCompass.TARGET_POSITION_NOT_FOUND));
            }
            int energy = DataStorageUtil.getDataOrDefault(components, ModDataStorages.WARP_ENERGY);
            consumer.accept(Component.translatable(ModTranslationKeys.Tooltip.BetrawarpCompass.WARP_ENERGY, energy).withStyle(style -> style.withColor(0x55FF55)));
        }
    }
}
