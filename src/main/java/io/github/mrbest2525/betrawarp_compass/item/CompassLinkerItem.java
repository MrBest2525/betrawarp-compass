package io.github.mrbest2525.betrawarp_compass.item;

import io.github.mrbest2525.betrawarp_compass.ModDataStorages;
import io.github.mrbest2525.betrawarp_compass.ModTags;
import io.github.mrbest2525.betrawarp_compass.ModTranslationKeys;
import io.github.mrbest2525.betrawarp_compass.datastorage.DataStorageUtil;
import io.github.mrbest2525.betrawarp_compass.menu.ModMenuProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

public class CompassLinkerItem extends Item {
    
    public static final long[] WARP_SEARCH_OFFSETS_LONG = new long[]{
            // y = 0
            BlockPos.asLong(0, 0, -1), BlockPos.asLong(-1, 0, 0), BlockPos.asLong(0, 0, 1), BlockPos.asLong(1, 0, 0),
            BlockPos.asLong(1, 0, -1), BlockPos.asLong(-1, 0, -1), BlockPos.asLong(-1, 0, 1), BlockPos.asLong(1, 0, 1),
            // y = +1
            BlockPos.asLong(0, 1, 0),
            BlockPos.asLong(0, 1, -1), BlockPos.asLong(-1, 1, 0), BlockPos.asLong(0, 1, 1), BlockPos.asLong(1, 1, 0),
            BlockPos.asLong(1, 1, -1), BlockPos.asLong(-1, 1, -1), BlockPos.asLong(-1, 1, 1), BlockPos.asLong(1, 1, 1),
            // y = -1
            BlockPos.asLong(0, -1, 0),
            BlockPos.asLong(0, -1, -1), BlockPos.asLong(-1, -1, 0), BlockPos.asLong(0, -1, 1), BlockPos.asLong(1, -1, 0),
            BlockPos.asLong(1, -1, -1), BlockPos.asLong(-1, -1, -1), BlockPos.asLong(-1, -1, 1), BlockPos.asLong(1, -1, 1),
    };
    
    public CompassLinkerItem(Properties properties) {
        properties.component(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.empty(), false));
        properties.component(DataComponents.MAX_STACK_SIZE, 1);
        super(properties);
    }
    
    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel level, @NonNull Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);
        if (level.isClientSide()) return;
        if (DataStorageUtil.getData(itemStack, ModDataStorages.IS_COMPASS_LINKER) == null) {
            DataStorageUtil.setData(itemStack, ModDataStorages.IS_COMPASS_LINKER, true);
        }
    }
    
    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack itemStack = serverPlayer.getItemInHand(hand);
                serverPlayer.openMenu(ModMenuProviders.openCompassLinkerMenu(itemStack));
            }
        } else {
            player.startUsingItem(hand);
            if (player instanceof ServerPlayer serverPlayer) {
                playUseStartSound(serverPlayer);
            }
        }
        return super.use(level, player, hand);
    }
    
    @Override
    public int getUseDuration(@NonNull ItemStack itemStack, @NonNull LivingEntity user) {
        return 60;
    }
    
    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.BOW;
    }
    
    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack itemStack, @NonNull Level level, @NonNull LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            ItemContainerContents container = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            ItemStack compass = container.copyOne();
            LodestoneTracker lodestoneTracker = compass.get(DataComponents.LODESTONE_TRACKER);
            if (lodestoneTracker != null && lodestoneTracker.target().isPresent()) {
                GlobalPos globalPos = lodestoneTracker.target().get();
                int costMultiplier = 1;
                if (!player.level().dimension().equals(globalPos.dimension())) costMultiplier = 10;
                Vec3 playerPos = player.position();
                BlockPos targetPos = globalPos.pos();
                
                double distance = playerPos.distanceTo(Vec3.atCenterOf(targetPos));
                int cost = Math.max((int) ((distance / 10) * costMultiplier), 10);
                
                int energy = DataStorageUtil.getDataOrDefault(itemStack, ModDataStorages.WARP_ENERGY);
                // エネルギーが足りているか
                if (cost < energy) {
                    ServerLevel targetLevel = player.level().getServer().getLevel(globalPos.dimension());
                    // ワープ先のディメンションが存在するか(データパックを抜いた後でも動くように)
                    if (targetLevel == null) {
                        player.sendSystemMessage(Component.translatable(ModTranslationKeys.Warp.BetrawarpCompass.DIMENSION_NOT_FOUND));
                        playFailureSound(player);
                    } else {
                        BlockState targetBlockState = targetLevel.getBlockState(targetPos);
                        // ロードストーンがあるか
                        if (targetBlockState.is(ModTags.Blocks.WARP_TARGETS)) {
                            BlockPos safePos = findSafePosition(targetLevel, targetPos, player);
                            // 安全な場所があるか
                            if (safePos != null) {
                                DataStorageUtil.setData(itemStack, ModDataStorages.WARP_ENERGY, energy - cost);
                                player.teleportTo(targetLevel, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, Collections.emptySet(), player.getYRot(), player.getXRot(), true);
                                playSuccessSound(player);
                            } else {
                                player.sendSystemMessage(Component.translatable(ModTranslationKeys.Warp.BetrawarpCompass.SAFE_POSITION_NOT_FOUND));
                                playFailureSound(player);
                            }
                        } else {
                            player.sendSystemMessage(Component.translatable(ModTranslationKeys.Warp.BetrawarpCompass.WARP_TARGET_NOT_FOUND));
                            playFailureSound(player);
                        }
                    }
                } else {
                    player.sendSystemMessage(Component.translatable(ModTranslationKeys.Warp.BetrawarpCompass.NOT_ENOUGH_ENERGY));
                    playFailureSound(player);
                }
            } else {
                player.sendSystemMessage(Component.translatable(ModTranslationKeys.Warp.BetrawarpCompass.NOT_SET_TARGET));
                playFailureSound(player);
            }
            player.getCooldowns().addCooldown(itemStack, 20);
            
        }
        return itemStack;
    }
    
    public void playFailureSound(ServerPlayer player) {
        player.connection.send(new ClientboundSoundPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.GLASS_BREAK),
                SoundSource.PLAYERS,
                player.getX(), player.getY(), player.getZ(),
                1.0f,
                0.4f,
                player.getRandom().nextLong()
        ));
    }
    
    public void playUseStartSound(ServerPlayer player) {
        player.connection.send(new ClientboundSoundPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BEACON_ACTIVATE),
                SoundSource.PLAYERS,
                player.getX(), player.getY(), player.getZ(),
                1.0f,
                0.5f,
                player.getRandom().nextLong()
        ));
    }
    
    public void playSuccessSound(ServerPlayer player) {
        player.connection.send(new ClientboundSoundPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_LEVELUP),
                SoundSource.PLAYERS,
                player.getX(), player.getY(), player.getZ(),
                1.0f,
                0.9f,
                player.getRandom().nextLong()
        ));
    }
    
    private @Nullable BlockPos findSafePosition(@NonNull ServerLevel level, @NonNull BlockPos startPos, @NonNull ServerPlayer player) {
        // 属性（GENERIC_SCALE）によるサイズ変更が自動反映された衝突箱のサイズを取得
        EntityDimensions dimensions = player.getDimensions(player.getPose());
        float width = dimensions.width();
        float height = dimensions.height();
        
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        
        for (long offsetLong : WARP_SEARCH_OFFSETS_LONG) {
            // long値から相対座標（X, Y, Z）をデコードして、checkPosに上書き代入
            int offsetX = BlockPos.getX(offsetLong);
            int offsetY = BlockPos.getY(offsetLong);
            int offsetZ = BlockPos.getZ(offsetLong);
            checkPos.set(startPos).move(offsetX, offsetY, offsetZ);
            
            if (isPositionSafeForSize(level, checkPos, width, height)) {
                return checkPos.immutable();
            }
        }
        
        // 周囲に安全な空間が一つも存在しない場合は null を返す
        return null;
    }
    
    /**
     * 指定されたサイズ（幅・高さ）のエンティティが、その座標で窒息や落下をしないか厳密に判定します。
     */
    private boolean isPositionSafeForSize(@NonNull ServerLevel level, @NonNull BlockPos pos, float width, float height) {
        double halfWidth = width / 2.0;
        
        // 足元の中心 (X+0.5, Z+0.5) をベースに、プレイヤーの現在サイズに合わせた仮の AABB を作成
        AABB playerBox = new AABB(
                pos.getX() + 0.5 - halfWidth, pos.getY(), pos.getZ() + 0.5 - halfWidth,
                pos.getX() + 0.5 + halfWidth, pos.getY() + height, pos.getZ() + 0.5 + halfWidth
        );
        
        // バニラの物理エンジンで、この空間にブロックが衝突（めり込み）しないか判定
        if (!level.noCollision(playerBox)) {
            return false;
        }
        
        // AABBが内包する最小・最大のブロック座標を計算
        BlockPos minPos = BlockPos.containing(playerBox.minX, playerBox.minY, playerBox.minZ);
        BlockPos maxPos = BlockPos.containing(playerBox.maxX, playerBox.maxY, playerBox.maxZ);
        
        // betweenClosed を使って、プレイヤーの体が触れる範囲の全ブロックをループ
        for (BlockPos p : BlockPos.betweenClosed(minPos, maxPos)) {
            // 1つの座標でも液体（水・溶岩など）が存在すれば安全ではないと判定
            if (!level.getFluidState(p).isEmpty()) {
                return false;
            }
        }
        
        // 地面の確認（足元の1マス下が空気、または液体でないか）
        BlockState groundState = level.getBlockState(pos.below());
        return !groundState.isAir() && groundState.getFluidState().isEmpty();
    }
}
