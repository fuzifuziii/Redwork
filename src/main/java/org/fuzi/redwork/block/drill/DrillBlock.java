package org.fuzi.redwork.block.drill;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.fuzi.redwork.block.ModBlockEntities;
import org.fuzi.redwork.blockhelp.BlockHelpInfo;
import org.fuzi.redwork.blockhelp.BlockHelpProvider;
import org.fuzi.redwork.other.ModOther;
import org.fuzi.redwork.other.ModUtils;

public class DrillBlock extends BaseEntityBlock implements BlockHelpProvider {
    public static final MapCodec<DrillBlock> CODEC = simpleCodec(DrillBlock::new);
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty HAS_TOOL = BooleanProperty.create("has_tool");

    public DrillBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.DRILL_BE.get(), DrillBlockEntity::staticTick);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof DrillBlockEntity be && direction == state.getValue(FACING).getOpposite()) {
            return be.getToolSignal();
        }

        return super.getDirectSignal(state, level, pos, direction);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof DrillBlockEntity be && direction == state.getValue(FACING)) {
            return be.getToolSignal();
        }

        return super.getSignal(state, level, pos, direction);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock()) && !movedByPiston) {
            if (level.getBlockEntity(pos) instanceof DrillBlockEntity be) {
                ModUtils.dropItemHandlerContents(be.handler, level, pos);

                if (be.hasTool()) {
                    var toolEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), be.extractTool());

                    level.addFreshEntity(toolEntity);
                }
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof DrillBlockEntity be && be.hasTool() && player.getMainHandItem().isEmpty()) {
                player.getInventory().add(be.extractTool());
                return InteractionResult.SUCCESS;
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_316140_) {
        if (!level.isClientSide) {
            if (stack.is(ModOther.TOOL_TAG) && level.getBlockEntity(pos) instanceof DrillBlockEntity be && !be.hasTool()) {
                if(be.putTool(stack))
                    stack.setCount(0);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, p_316140_);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, HAS_TOOL);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, false).setValue(HAS_TOOL, false).setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ModBlockEntities.DRILL_BE.get().create(blockPos, blockState);
    }

    @Override
    public BlockHelpInfo getHelp() {
        return BlockHelpInfo.builder()
                .front("blockhelp.redwork.drill.front")
                .details("blockhelp.redwork.drill.details")
                .storage()
                .only_when_powered()
                .other("blockhelp.redwork.drill.addition")
                .build();
    }
}
