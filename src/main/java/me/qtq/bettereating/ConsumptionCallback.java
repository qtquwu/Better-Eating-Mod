package me.qtq.bettereating;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public interface ConsumptionCallback {
    Event<ConsumptionCallback> EVENT = EventFactory.createArrayBacked(ConsumptionCallback.class,
            (listeners) -> (player, world, stack) -> {
                for (ConsumptionCallback listener : listeners) {
                    ActionResult result = listener.interact(player, world, stack);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            return ActionResult.PASS;
    });

    ActionResult interact(PlayerEntity player, World world, ItemStack stack);
}
