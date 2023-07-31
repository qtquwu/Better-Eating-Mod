package me.qtq.bettereating;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterEatingMod implements ModInitializer {

	// This variable is used to restrict various actions when it is greater than zero
	private static int foodTimerTicks;

	private static BetterEatingConfig config;
	public static Logger LOGGER = LoggerFactory.getLogger("BetterEatingMod");

	@Override
	public void onInitialize() {

		//Setup Config
		AutoConfig.register(BetterEatingConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(BetterEatingConfig.class).getConfig();

		// This listens for consuming food and then updates the foodTimerLength on the client
		ConsumptionCallback.EVENT.register((player, world, stack) -> {
			startFoodTimer();
			return ActionResult.PASS;
		});
	}

	public static boolean blockPlacementRestricted() {
		return config.restrictPlaceBlocks && !foodTimerDone();
	}

	public static boolean blockUsageRestricted() {
		return config.restrictUseBlocks && !foodTimerDone();
	}

	public static boolean itemUsageRestricted() { return config.restrictUseItems && !foodTimerDone();}

	public static void startFoodTimer() {
		foodTimerTicks = config.foodTimerLength;
	}

	public static void countDownFoodTimer() {
		if (foodTimerTicks > 0) {
			foodTimerTicks--;
		}
	}
	public static boolean foodTimerDone() {
		return !(foodTimerTicks > 0);
	}
}
