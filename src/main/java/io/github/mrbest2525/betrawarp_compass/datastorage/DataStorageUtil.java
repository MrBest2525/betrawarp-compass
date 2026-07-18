package io.github.mrbest2525.betrawarp_compass.datastorage;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStorageUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStorageUtil.class);
    
    private DataStorageUtil() {}
    
    public static <T> CustomData setData(@NonNull CustomData customData, @NonNull DataStorageType<T> dataType, @NonNull T data) {
        CompoundTag customDataTag = customData.copyTag();
        dataType.codec()
                .encodeStart(NbtOps.INSTANCE, data)
                .resultOrPartial(error ->
                        LOGGER.error(
                                "Failed to encode {}: {}",
                                dataType.id(),
                                error)
                )
                .ifPresentOrElse(
                        tag -> customDataTag.put(dataType.id().toString(), tag),
                        () -> customDataTag.remove(dataType.id().toString())
                );
        return CustomData.of(customDataTag);
    }
    
    public static <T> void setData(@NonNull ItemStack itemStack, @NonNull DataStorageType<T> dataType, @NonNull T data) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        itemStack.set(DataComponents.CUSTOM_DATA, setData(customData, dataType, data));
    }
    
    public static <T> @Nullable T getData(@NonNull CustomData customData, @NonNull DataStorageType<T> dataType) {
        Tag tag = customData.copyTag().get(dataType.id().toString());
        if (tag == null) {return null;}
        return dataType.codec()
                .parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(error ->
                        LOGGER.error(
                                "Failed to decode {}: {}",
                                dataType.id(),
                                error
                        )
                )
                .orElse(null);
    }
    
    public static <T> @Nullable T getData(@NonNull DataComponentGetter components, @NonNull DataStorageType<T> dataType) {
        return getData(components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY), dataType);
    }
    
    public static <T> @Nullable T getData(@NonNull ItemStack itemStack, @NonNull DataStorageType<T> dataType) {
        return getData(itemStack.getComponents(), dataType);
    }
    
    public static <T> @NonNull T getDataOrDefault(@NonNull CustomData customData, @NonNull DataStorageType<T> dataType, @NonNull T defaultValue) {
        T data = getData(customData, dataType);
        return data == null ? defaultValue : data;
    }
    
    public static <T> @NonNull T getDataOrDefault(@NonNull DataComponentGetter components, @NonNull DataStorageType<T> dataType, @NonNull T defaultValue) {
        return getDataOrDefault(components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY), dataType, defaultValue);
    }
    
    public static <T> @NonNull T getDataOrDefault(@NonNull ItemStack itemStack, @NonNull DataStorageType<T> dataType, @NonNull T defaultValue) {
        return getDataOrDefault(itemStack.getComponents(), dataType, defaultValue);
    }
    
    public static <T> @NonNull T getDataOrDefault(@NonNull CustomData customData, @NonNull DataStorageType<T> dataType) {
        return getDataOrDefault(customData, dataType, dataType.defaultValue());
    }
    
    public static <T> @NonNull T getDataOrDefault(@NonNull DataComponentGetter components, @NonNull DataStorageType<T> dataType) {
        return getDataOrDefault(components, dataType, dataType.defaultValue());
    }
    
    public static <T> @NonNull T getDataOrDefault(@NonNull ItemStack itemStack, @NonNull DataStorageType<T> dataType) {
        return getDataOrDefault(itemStack.getComponents(), dataType, dataType.defaultValue());
    }
}
