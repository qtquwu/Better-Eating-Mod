package me.qtq.bettereating.mixin;

import me.qtq.bettereating.BetterEatingMod;
import me.qtq.bettereating.ConsumptionCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends EatingTimerMixin {
    @Override
    protected void atConsume(final World world, final ItemStack stack, final CallbackInfoReturnable<Boolean> info) {
        ActionResult result = ConsumptionCallback.EVENT.invoker().interact((PlayerEntity) (Object) this, world, stack);

        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }

    @Override
    protected void countDownFoodTimer(final CallbackInfo info) {
        BetterEatingMod.countDownFoodTimer();
    }

    @Override
    protected void catchPotionConsumption(CallbackInfo Info) {
        // First we want to make sure that we aren't catching when the item isn't actually consumed
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)(this);
        ItemStack activeStack = this.activeItemStack;
        if (activeStack.isEmpty() || !player.isUsingItem())
            return;
        if (!player.getStackInHand(player.getActiveHand()).equals(activeStack)) {
            return;
        }
        // and now we know that it is being consumed, so let's check if it's a potion!
        if (activeStack.getItem() instanceof PotionItem) {
            BetterEatingMod.startFoodTimer();
        }

    }

}
