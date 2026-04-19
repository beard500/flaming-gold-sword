package com.flamingsword;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlamingSwordItem extends SwordItem implements PolymerItem {
    private static final int SPECIAL_COOLDOWN_TICKS = 30 * 20;

    public FlamingSwordItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public Item getPolymerItem(ItemStack stack, @Nullable ServerPlayerEntity player) {
        return Items.NETHERITE_SWORD;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack stack, @Nullable ServerPlayerEntity player) {
        return PolymerResourcePackUtils.hasMainPack(player) ? 7777 : 0;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(6);

        if (attacker.getWorld() instanceof ServerWorld world) {
            double cx = target.getX();
            double cy = target.getY() + target.getHeight() * 0.5;
            double cz = target.getZ();

            world.spawnParticles(ParticleTypes.FLAME, cx, cy, cz, 40, 0.3, 0.5, 0.3, 0.08);
            world.spawnParticles(ParticleTypes.SMALL_FLAME, cx, cy + 0.2, cz, 25, 0.4, 0.5, 0.4, 0.05);
            world.spawnParticles(ParticleTypes.LAVA, cx, cy - 0.2, cz, 4, 0.2, 0.3, 0.2, 0.0);
            world.spawnParticles(ParticleTypes.LARGE_SMOKE, cx, cy, cz, 8, 0.3, 0.3, 0.3, 0.02);

            if (attacker instanceof PlayerEntity player) {
                DamageSource splash = world.getDamageSources().playerAttack(player);
                Box box = target.getBoundingBox().expand(2);
                List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class, box,
                        e -> e != target && e != attacker && e.isAlive());
                for (LivingEntity other : nearby) {
                    other.damage(splash, 3);
                    other.setOnFireFor(4);
                }
            }
        }

        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        if (user.isSneaking()) {
            selfIgnite(world, user);
        } else {
            flameWave(world, user);
        }

        user.getItemCooldownManager().set(this, SPECIAL_COOLDOWN_TICKS);
        return TypedActionResult.success(stack);
    }

    private void selfIgnite(World world, PlayerEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0, false, true, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 0, false, true, true));
        if (world instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.FLAME,
                    user.getX(), user.getY() + 1, user.getZ(),
                    80, 0.5, 1, 0.5, 0.1);
            sw.spawnParticles(ParticleTypes.LAVA,
                    user.getX(), user.getY() + 0.5, user.getZ(),
                    6, 0.4, 0.4, 0.4, 0);
            sw.playSound(null, user.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE,
                    SoundCategory.PLAYERS, 1.2f, 0.6f);
        }
    }

    private void flameWave(World world, PlayerEntity user) {
        if (!(world instanceof ServerWorld sw)) return;

        Vec3d look = user.getRotationVec(1f);
        Vec3d origin = user.getEyePos();
        DamageSource ds = sw.getDamageSources().playerAttack(user);

        for (int i = 1; i <= 8; i++) {
            double x = origin.x + look.x * i;
            double y = origin.y + look.y * i;
            double z = origin.z + look.z * i;
            sw.spawnParticles(ParticleTypes.FLAME, x, y, z, 10, 0.4, 0.4, 0.4, 0.05);
            sw.spawnParticles(ParticleTypes.SMALL_FLAME, x, y, z, 6, 0.3, 0.3, 0.3, 0.02);
            sw.spawnParticles(ParticleTypes.LAVA, x, y, z, 1, 0.3, 0.3, 0.3, 0);
        }

        Box box = user.getBoundingBox().expand(8, 4, 8);
        List<LivingEntity> nearby = sw.getEntitiesByClass(LivingEntity.class, box,
                e -> e != user && e.isAlive());
        for (LivingEntity target : nearby) {
            Vec3d toTarget = target.getPos().subtract(user.getPos()).normalize();
            double dot = toTarget.dotProduct(look);
            double dist = target.distanceTo(user);
            if (dot > 0.6 && dist <= 8) {
                target.damage(ds, 6);
                target.setOnFireFor(8);
            }
        }

        sw.playSound(null, user.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE,
                SoundCategory.PLAYERS, 1.5f, 0.5f);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!selected || world.isClient || !(world instanceof ServerWorld serverWorld)) return;
        if (!(entity instanceof PlayerEntity player)) return;

        if (world.getTime() % 20 == 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 40, 0, false, false, false));
        }

        if (world.getTime() % 4 == 0) {
            double jitterX = (player.getRandom().nextDouble() - 0.5) * 0.4;
            double jitterY = player.getRandom().nextDouble() * 0.3;
            double jitterZ = (player.getRandom().nextDouble() - 0.5) * 0.4;
            serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME,
                    player.getX() + jitterX,
                    player.getY() + 1.2 + jitterY,
                    player.getZ() + jitterZ,
                    2, 0.05, 0.1, 0.05, 0.01);
        }
    }
}
