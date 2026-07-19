package org.fuzi.redwork.block.striker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.fuzi.redwork.block.ModBlockEntities;
import org.fuzi.redwork.block.ModBlocks;
import org.fuzi.redwork.other.ModOther;
import org.fuzi.redwork.other.ModUtils;

public class StrikerBlockEntity extends BlockEntity {
    private static final float BASE_DAMAGE = 1.0f;
    private static final float BASE_ATTACK_SPEED = 4.0f;
    private static final float MIN_ATTACK_SPEED = 0.5f;

    public IItemHandler toolHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int i) {
            return tool;
        }

        @Override
        public ItemStack insertItem(int i, ItemStack itemStack, boolean b) {
            if (tool.isEmpty() && b) {
                tool = itemStack;
                setChanged();
                return ItemStack.EMPTY;
            }

            return itemStack;
        }

        @Override
        public ItemStack extractItem(int i, int i1, boolean b) {
            if (!tool.isEmpty()) {
                var ret = tool;

                if (!b) {
                    tool = ItemStack.EMPTY;
                }

                return ret;
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int i) {
            return 1;
        }

        @Override
        public boolean isItemValid(int i, ItemStack itemStack) {
            return itemStack.is(ModOther.MELEE_TOOL_TAG);
        }
    };

    private ItemStack tool = ItemStack.EMPTY.copy();
    private int cooldown = 0;

    public boolean putTool(ItemStack tool) {
        if (this.tool.isEmpty()) {
            this.tool = tool.copy();
            setChanged();
            return true;
        }
        return false;
    }

    public boolean hasTool() {
        return !tool.isEmpty();
    }

    public int getToolSignal() {
        if (!hasTool()) {
            return 0;
        } else {
            try {
                var x = (int) (((float) tool.get(DataComponents.DAMAGE) / (float) tool.get(DataComponents.MAX_DAMAGE)) * 15f);
                return Mth.clamp(x, 0, 15);
            } catch (Exception ex) {
                return 0;
            }
        }
    }

    public ItemStack extractTool() {
        if (!tool.isEmpty()) {
            var toReturn = this.tool;
            this.tool = ItemStack.EMPTY.copy();
            setChanged();
            return toReturn;
        }
        return ItemStack.EMPTY.copy();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        tool = ItemStack.parseOptional(provider, tag.getCompound("tool"));
        cooldown = tag.getInt("cooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!tool.isEmpty()) {
            tag.put("tool", tool.save(provider));
        }
        tag.putInt("cooldown", cooldown);
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos) {
        if (hasTool()) {
            if (!state.getValue(StrikerBlock.HAS_TOOL)) {
                level.setBlockAndUpdate(pos, state.setValue(StrikerBlock.HAS_TOOL, true));
            }
        } else {
            if (state.getValue(StrikerBlock.HAS_TOOL)) {
                level.setBlockAndUpdate(pos, state.setValue(StrikerBlock.HAS_TOOL, false));
            }
        }

        var face = state.getValue(StrikerBlock.FACING);
        boolean anySignal = ModUtils.hasNeighborSignal(level, pos, face);

        if (!anySignal) {
            if (state.getValue(StrikerBlock.POWERED)) {
                level.setBlockAndUpdate(pos, state.setValue(StrikerBlock.POWERED, false));
            }
            return;
        }
        if (!state.getValue(StrikerBlock.POWERED)) {
            level.setBlockAndUpdate(pos, state.setValue(StrikerBlock.POWERED, true));
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        var front = ModUtils.lookTo(pos, face);

        var target = findTarget(level, front);

        if (target == null) {
            return;
        }

        float damage = BASE_DAMAGE;
        float attackSpeed = BASE_ATTACK_SPEED;

        if (hasTool()) {
            try {
                var modifiers = tool.getAttributeModifiers();
                for (var entry : modifiers.modifiers()) {
                    if (entry.slot() != EquipmentSlotGroup.MAINHAND && entry.slot() != EquipmentSlotGroup.ANY) {
                        continue;
                    }
                    if (entry.modifier().operation() != AttributeModifier.Operation.ADD_VALUE) {
                        continue;
                    }
                    if (entry.attribute().is(Attributes.ATTACK_DAMAGE)) {
                        damage += entry.modifier().amount();
                    } else if (entry.attribute().is(Attributes.ATTACK_SPEED)) {
                        attackSpeed += entry.modifier().amount();
                    }
                }
            } catch (Exception ignored) {
            }

            int sharpness = tool.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS));
            if (sharpness > 0) {
                damage += sharpness * 0.5f + 0.5f;
            }
        }

        if (attackSpeed < MIN_ATTACK_SPEED) {
            attackSpeed = MIN_ATTACK_SPEED;
        }

        cooldown = Math.max(1, Math.round(20f / attackSpeed));

        var source = level.damageSources().generic();
        target.hurt(source, damage);

        level.playSound(null, pos, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.BLOCKS, 1f, 1f);

        if (hasTool() && tool.has(DataComponents.DAMAGE) && tool.has(DataComponents.MAX_DAMAGE)) {
            int unbreakingLevel = tool.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING));

            boolean applyDamage = true;
            if (unbreakingLevel > 0) {
                applyDamage = level.random.nextInt(unbreakingLevel + 1) == 0;
            }

            if (applyDamage) {
                var dmgCurrent = tool.get(DataComponents.DAMAGE) + 1;
                var dmgMax = tool.get(DataComponents.MAX_DAMAGE);
                if (dmgCurrent >= dmgMax) {
                    level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1, 1.5f);
                    tool = ItemStack.EMPTY;
                } else {
                    tool.set(DataComponents.DAMAGE, dmgCurrent);
                }
            }
        }

        level.sendBlockUpdated(pos, state, state, 2);
        this.setChanged();
    }

    private @Nullable LivingEntity findTarget(ServerLevel level, BlockPos front) {
        var candidates = level.getEntitiesOfClass(LivingEntity.class, new AABB(front),
                entity -> entity.isAlive() && !(entity instanceof Player player && (player.isSpectator() || player.isCreative())));

        return candidates.isEmpty() ? null : candidates.get(0);
    }

    public @Nullable IItemHandler getCapability(@Nullable Direction direction) {
        return toolHandler;
    }

    public static void staticTick(Level level, BlockPos pos, BlockState state, StrikerBlockEntity blockEntity) {
        if (!level.isClientSide) {
            blockEntity.tick(state, (ServerLevel) level, pos);
            level.updateNeighborsAt(pos, ModBlocks.STRIKER.get());
        }
    }

    public StrikerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.STRIKER_BE.get(), pos, blockState);
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
