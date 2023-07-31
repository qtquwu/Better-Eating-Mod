package me.qtq.bettereating.mixin;

import me.qtq.bettereating.BetterEatingMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPreventionMixin {

    @Shadow
    MinecraftClient client;

    // This method stops the player from placing a block if they are about to and the eating timer is active
    @Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
    private void preventClientBlockUse(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> Info) {
        if(!BetterEatingMod.foodTimerDone()) {

            ItemStack itemStack = player.getStackInHand(hand);

            boolean holdingItem = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
            boolean itemIsBlock = BlockItem.class.isAssignableFrom(itemStack.getItem().getClass());

            boolean useBlockResult = (holdingItem && player.shouldCancelInteraction());
            boolean useItemResult = (!itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack.getItem()));

            if(BetterEatingMod.blockUsageRestricted() && useBlockResult) {
                // See what happens when the block is used, and cancel it if the block ends up being used
                World world = client.world;
                ActionResult blockUseResult = world.getBlockState(hitResult.getBlockPos()).onUse(world, player, hand, hitResult);
                if (blockUseResult.isAccepted()) {
                    Info.setReturnValue(ActionResult.PASS);
                }
            }
            if(BetterEatingMod.blockPlacementRestricted() && useItemResult && itemIsBlock) {
                Info.setReturnValue(ActionResult.PASS);
            }
        }
    }
    // This method stops the client from using non-food items while the eating timer is counting down
    @Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
    private void preventClientItemUse(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> Info) {
        if(!BetterEatingMod.foodTimerDone()) {
            ItemStack itemStack = player.getStackInHand(hand);

            boolean itemIsFood = itemStack.isFood();
            if(BetterEatingMod.itemUsageRestricted() && !itemIsFood) {
                Info.setReturnValue(ActionResult.PASS);
            }
        }
    }
}
