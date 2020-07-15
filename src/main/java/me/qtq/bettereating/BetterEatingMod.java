package me.qtq.bettereating;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;

public class BetterEatingMod implements ModInitializer {

	// This variable is used to restrict various actions when it is greater than zero
	public static int foodTimerTicks;

	public static int foodTimerLength = 5;

	BetterEatingConfig config;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		//Setup Config
		AutoConfig.register(BetterEatingConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(BetterEatingConfig.class).getConfig();

		// This listens for consuming food and then updates the foodTimerLength on the client
		ConsumptionCallback.EVENT.register((player, world, stack) -> {
			if (world.isClient()) {
				foodTimerLength = config.foodTimerLength;
			}
			return ActionResult.PASS;
		});
	}
}
