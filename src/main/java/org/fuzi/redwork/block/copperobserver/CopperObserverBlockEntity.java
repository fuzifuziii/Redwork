package org.fuzi.redwork.block.copperobserver;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.fuzi.redwork.block.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class CopperObserverBlockEntity extends BlockEntity {
    private static final int PULSE_LENGTH = 2;
    private static final int SCAN_INTERVAL = 4;
    private static final double SCAN_RADIUS = 0.5;
    
    private static final int PULSE_COOLDOWN = 8; 

    private int pulseTicksLeft = 0;
    private int scanCooldown = 0;
    private int pulseCooldown = 0;

    public CopperObserverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COPPER_OBSERVER_BE.get(), pos, state);
    }

    public static void staticTick(Level level, BlockPos pos, BlockState state, CopperObserverBlockEntity be) {
        if (!level.isClientSide) {
            be.tick(state, (ServerLevel) level, pos);
        }
    }

    private void tick(BlockState state, ServerLevel level, BlockPos pos) {
        if (state.getValue(CopperObserverBlock.POWERED)) {
            if (--pulseTicksLeft <= 0) {
                BlockState off = state.setValue(CopperObserverBlock.POWERED, false);
                level.setBlock(pos, off, Block.UPDATE_CLIENTS);
                CopperObserverBlock.updateNeighbors(level, pos, off);
                
                pulseCooldown = PULSE_COOLDOWN;
            }
            return;
        }

        if (pulseCooldown > 0) {
            pulseCooldown--;
        }

        if (--scanCooldown > 0) {
            return;
        }
        scanCooldown = SCAN_INTERVAL;

        boolean detectedNow = hasEntityInFront(level, pos, state.getValue(CopperObserverBlock.FACING));
        boolean detectedBefore = state.getValue(CopperObserverBlock.DETECTED);

        if (detectedNow != detectedBefore) {
            BlockState newState = state.setValue(CopperObserverBlock.DETECTED, detectedNow);
            
            if (pulseCooldown <= 0) {
                newState = newState.setValue(CopperObserverBlock.POWERED, true);
                pulseTicksLeft = PULSE_LENGTH;
            }
            
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
            
            if (newState.getValue(CopperObserverBlock.POWERED)) {
                CopperObserverBlock.updateNeighbors(level, pos, newState);
            }
        }
    }

    private boolean hasEntityInFront(ServerLevel level, BlockPos pos, Direction facing) {
        BlockPos target = pos.relative(facing);
        Vec3 localCenter = Vec3.atCenterOf(target);
        Vec3 worldCenter = SableCompanion.INSTANCE.projectOutOfSubLevel(level, localCenter);
        AABB box = new AABB(
                worldCenter.x - SCAN_RADIUS, worldCenter.y - SCAN_RADIUS, worldCenter.z - SCAN_RADIUS,
                worldCenter.x + SCAN_RADIUS, worldCenter.y + SCAN_RADIUS, worldCenter.z + SCAN_RADIUS
        );
        return !level.getEntitiesOfClass(
                Entity.class,
                box,
                entity -> entity.isAlive() && !entity.isSpectator()
        ).isEmpty();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        pulseTicksLeft = tag.getInt("pulse_ticks_left");
        scanCooldown = tag.getInt("scan_cooldown");
        pulseCooldown = tag.getInt("pulse_cooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("pulse_ticks_left", pulseTicksLeft);
        tag.putInt("scan_cooldown", scanCooldown);
        tag.putInt("pulse_cooldown", pulseCooldown);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
