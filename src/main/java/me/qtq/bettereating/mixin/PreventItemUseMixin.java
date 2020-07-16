package me.qtq.bettereating.mixin;

import me.qtq.bettereating.BetterEatingMod;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//The purpose of this code is to stop the player from placing blocks immediately after eating
@Mixin(BlockItem.class)
public class PreventItemUseMixin {
    @Inject(at = @At("HEAD"), method = "place", cancellable = true)
    private void preventItemUse(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> Info) {
        if (BetterEatingMod.foodTimerTicks > 0) {
            Info.setReturnValue(ActionResult.FAIL);
        }
    }
}
