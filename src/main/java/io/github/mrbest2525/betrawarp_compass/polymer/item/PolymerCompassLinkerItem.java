package io.github.mrbest2525.betrawarp_compass.polymer.item;

import com.mojang.authlib.GameProfile;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import io.github.mrbest2525.betrawarp_compass.ModClientManager;
import io.github.mrbest2525.betrawarp_compass.item.CompassLinkerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PolymerCompassLinkerItem extends CompassLinkerItem implements PolymerItem, PolymerClientDecoded {
    
    private final Item baseItem = Items.COMPASS;
    
    public PolymerCompassLinkerItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        GameProfile gameProfile = context.get(PacketContext.GAME_PROFILE);
        
        if (gameProfile != null && ModClientManager.hasMod(gameProfile.id())) {
            // MODが入っているクライアントには、本物のアイテムIDを渡す
            return this;
        }
        return baseItem;
    }
}
