package me.qtq.bettereating.mixin;

import me.qtq.bettereating.BetterEatingMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPreventionMixin {
    @Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
    private void preventClientBlockUse(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitresult, CallbackInfoReturnable<ActionResult> Info) {
        if(!BetterEatingMod.foodTimerDone()) {
            ItemStack itemStack = player.getStackInHand(hand);
            boolean holdingItem = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
            boolean itemIsBlock = BlockItem.class.isAssignableFrom(itemStack.getItem().getClass());

            boolean useBlockResult = (holdingItem && player.shouldCancelInteraction());
            boolean useItemResult = (!itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack.getItem()));

            if(BetterEatingMod.blockPlacementRestricted() && useItemResult && itemIsBlock) {
                Info.setReturnValue(ActionResult.PASS);
            }
            if(BetterEatingMod.blockUsageRestricted() && useBlockResult && (!useItemResult || !itemIsBlock)) {
                Info.setReturnValue(ActionResult.PASS);
            }
        }
    }
}
