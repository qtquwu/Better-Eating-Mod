package me.qtq.bettereating.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Shadow protected ItemStack activeItemStack;

    @Inject(method = "consumeItem", at = @At("HEAD"))
    protected void catchPotionConsumption(CallbackInfo Info) {
        // Intentionally left blank
    }
}
