package me.qtq.bettereating.mixin;

import me.qtq.bettereating.BetterEatingMod;
import me.qtq.bettereating.ConsumptionCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class EatingTimerMixin {

	// This sends a callback event when food is eaten, as well as starting the food timer
	@Inject(at = @At("HEAD"), method = "eatFood", cancellable = true)
	private void atConsume(final World world, final ItemStack stack, final CallbackInfoReturnable<Boolean> info) {
		ActionResult result = ConsumptionCallback.EVENT.invoker().interact((PlayerEntity) (Object) this, world, stack);

		if (result == ActionResult.FAIL) {
			info.cancel();
		}
		//TODO: add a config to allow end user to modify the food timer
		BetterEatingMod.foodTimerTicks = BetterEatingMod.foodTimerLength;
	}
	// This prevents the user from interacting with blocks immediately after eating
	@Inject(at = @At("RETURN"), method = "shouldCancelInteraction", cancellable = true)
	private void stopFromInteracting(CallbackInfoReturnable<Boolean> info) {
		if (BetterEatingMod.foodTimerTicks > 0) {
			info.setReturnValue(true);
		}
	}
	// This is perhaps the most essential method: it counts down the food timer every tick the player experiences
	@Inject(at = @At("HEAD"), method = "tick")
	private void countDownFoodTimer(final CallbackInfo info) {
		if(BetterEatingMod.foodTimerTicks > 0) {
			BetterEatingMod.foodTimerTicks--;
		}
	}
}