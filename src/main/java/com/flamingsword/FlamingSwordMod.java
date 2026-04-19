package com.flamingsword;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FlamingSwordMod implements ModInitializer {
    public static final String MOD_ID = "flaming_gold_sword";

    public static final Item FLAMING_GOLD_SWORD = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "flaming_gold_sword"),
            new FlamingSwordItem(
                    ToolMaterials.NETHERITE,
                    new Item.Settings()
                            .fireproof()
                            .attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 5, -2.4f))
            )
    );

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries ->
                entries.add(FLAMING_GOLD_SWORD));

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClient) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);
            if (!stack.isOf(FLAMING_GOLD_SWORD)) return ActionResult.PASS;
            if (direction != Direction.UP) return ActionResult.PASS;

            BlockPos placePos = pos.up();
            BlockState existing = world.getBlockState(placePos);
            if (!existing.isAir() && !existing.isReplaceable()) return ActionResult.PASS;

            world.setBlockState(placePos, Blocks.LAVA.getDefaultState());

            if (world instanceof ServerWorld sw) {
                sw.playSound(null, placePos, SoundEvents.ITEM_BUCKET_EMPTY_LAVA,
                        SoundCategory.PLAYERS, 1f, 1f);
                sw.spawnParticles(ParticleTypes.FLAME,
                        placePos.getX() + 0.5, placePos.getY() + 0.5, placePos.getZ() + 0.5,
                        20, 0.3, 0.3, 0.3, 0.05);
            }
            return ActionResult.SUCCESS;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof BlazeEntity)) return;
            if (entity.getRandom().nextFloat() > 0.05f) return;
            if (!(entity.getWorld() instanceof ServerWorld world)) return;

            ItemStack stack = new ItemStack(FLAMING_GOLD_SWORD);
            ItemEntity drop = new ItemEntity(world,
                    entity.getX(), entity.getY(), entity.getZ(), stack);
            world.spawnEntity(drop);

            world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_TOTEM_USE,
                    SoundCategory.HOSTILE, 0.8f, 0.7f);
        });
    }
}
