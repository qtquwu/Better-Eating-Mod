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
public class EatingTimerMixin extends LivingEntityMixin {

	// This sends a callback event when food is eaten
	@Inject(at = @At("HEAD"), method = "eatFood", cancellable = true)
	protected void atConsume(final World world, final ItemStack stack, final CallbackInfoReturnable<Boolean> info) {

	}
	// This method counts down the food timer every tick the player experiences
	@Inject(at = @At("HEAD"), method = "tick")
	protected void countDownFoodTimer(final CallbackInfo info) {

	}


}
