package org.fuzi.redwork.block.breezecollector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import dev.ryanhcode.sable.companion.SableCompanion;
import org.jetbrains.annotations.Nullable;
import org.fuzi.redwork.blockhelp.BlockHelpInfo;
import org.fuzi.redwork.blockhelp.BlockHelpProvider;
import net.minecraft.world.phys.EntityHitResult;
import org.fuzi.redwork.other.ModUtils;

import java.util.List;

public class BreezeCollectorBlock extends Block implements BlockHelpProvider {
    private static final int AABB_SIDE = 4;
    private static final int TICK_INTERVAL = 4;

    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BreezeCollectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        boolean hasSignal = ModUtils.hasNeighborSignal(level, pos);
        boolean isPowered = state.getValue(POWERED);

        if (hasSignal && !isPowered) {
            state = state.setValue(POWERED, true);
            level.setBlockAndUpdate(pos, state);
        } else if (!hasSignal && isPowered) {
            state = state.setValue(POWERED, false);
            level.setBlockAndUpdate(pos, state);
        }

        if (!hasSignal) {
            level.scheduleTick(pos, asBlock(), TICK_INTERVAL);
            return;
        }

        runCollectorLogic(state, level, pos);
        level.scheduleTick(pos, asBlock(), TICK_INTERVAL);
    }

    private void runCollectorLogic(BlockState state, ServerLevel level, BlockPos pos) {
        var facing = state.getValue(FACING);
        
        BlockPos backPos = pos.relative(facing.getOpposite());
        var container = level.getCapability(Capabilities.ItemHandler.BLOCK, backPos, facing);

        if (container == null) {
            return;
        }

        Vec3 localCollectorFace = Vec3.atCenterOf(pos).add(
                facing.getStepX() * 0.5,
                facing.getStepY() * 0.5,
                facing.getStepZ() * 0.5
        );

        Vec3 localSearchCenter = localCollectorFace.add(
                facing.getStepX() * 2.0,
                facing.getStepY() * 2.0,
                facing.getStepZ() * 2.0
        );

        Vec3 collectorFace = SableCompanion.INSTANCE.projectOutOfSubLevel(level, localCollectorFace);
        Vec3 searchCenter = SableCompanion.INSTANCE.projectOutOfSubLevel(level, localSearchCenter);

        AABB aabb = new AABB(
                searchCenter.x - 2.0, searchCenter.y - 2.0, searchCenter.z - 2.0,
                searchCenter.x + 2.0, searchCenter.y + 2.0, searchCenter.z + 2.0
        );

        List<Entity> entities = level.getEntitiesOfClass(
                Entity.class,
                aabb,
                et -> et != null && et.isAlive() && (et instanceof ItemEntity || et.getType().toString().contains("block") || et.getType().toString().contains("sable"))
        );

        for (Entity entity : entities) {
            if (entity.isRemoved()) continue;

            Vec3 position = SableCompanion.INSTANCE.projectOutOfSubLevel(level, entity.position());
            double distSqr = entity.distanceToSqr(collectorFace);

            if (entity instanceof ItemEntity itemEntity) {
                if (distSqr <= 0.56) {
                    var remains = ItemHandlerHelper.insertItem(container, itemEntity.getItem(), false);
                    if (remains.isEmpty()) {
                        level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                        entity.remove(Entity.RemovalReason.DISCARDED);
                    } else {
                        itemEntity.setItem(remains);
                    }
                    continue;
                }
            } else {
                if (distSqr <= 0.8) {
                    ItemStack stackToInsert = getStackFromEntity(entity);

                    if (!stackToInsert.isEmpty()) {
                        var remains = ItemHandlerHelper.insertItem(container, stackToInsert, false);
                        if (remains.isEmpty()) {
                            level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                            entity.remove(Entity.RemovalReason.DISCARDED);
                        } else {
                            entity.setDeltaMovement(Vec3.ZERO);
                            entity.hurtMarked = true;
                        }
                    } else {
                        entity.setDeltaMovement(Vec3.ZERO);
                        entity.hurtMarked = true;
                    }
                    continue;
                }
            }

            Vec3 direction = collectorFace.subtract(position);
            double distance = direction.length();

            if (distance > 0.1) {
                Vec3 normDirection = direction.normalize();
                double speed = 0.22; 
                Vec3 newMotion = normDirection.scale(speed);
                
                entity.setDeltaMovement(newMotion);
                entity.hurtMarked = true;
            }
        }
    }

    private ItemStack getStackFromEntity(Entity entity) {
        EntityHitResult hitResult = new EntityHitResult(entity, entity.position());
        ItemStack stack = entity.getPickedResult(hitResult);
        if (!stack.isEmpty()) return stack;

        if (entity instanceof FallingBlockEntity fallingBlock) {
            return new ItemStack(fallingBlock.getBlockState().getBlock());
        }

        try {
            var method = entity.getClass().getMethod("getItem");
            return (ItemStack) method.invoke(entity);
        } catch (Exception ignored) {}

        try {
            var method = entity.getClass().getMethod("getBlockState");
            var state = (net.minecraft.world.level.block.state.BlockState) method.invoke(entity);
            return new ItemStack(state.getBlock());
        } catch (Exception ignored) {}

        return ItemStack.EMPTY;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) return;

        Direction facing = state.getValue(FACING);
        Vec3 front = Vec3.atCenterOf(pos)
                .add(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        for (int i = 0; i < 2; i++) { 
            double spread = AABB_SIDE * 1.5;
            double x = front.x + (random.nextDouble() - 0.5) * spread;
            double y = front.y + (random.nextDouble() - 0.5) * spread;
            double z = front.z + (random.nextDouble() - 0.5) * spread;

            Vec3 particlePos = new Vec3(x, y, z);
            Vec3 velocity = front.subtract(particlePos).normalize().scale(0.1);

            level.addParticle(ParticleTypes.CLOUD,
                    particlePos.x, particlePos.y, particlePos.z,
                    velocity.x, velocity.y, velocity.z);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, asBlock(), 1);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) {
            level.scheduleTick(pos, asBlock(), 1);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(POWERED, false)
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockHelpInfo getHelp() {
        return BlockHelpInfo.builder()
                .front("blockhelp.redwork.breeze_collector.front")
                .storage_required_back()
                .details("blockhelp.redwork.breeze_collector.details")
                .no_storage()
                .only_when_powered()
                .build();
    }
}
