package me.qtq.bettereating;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
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

		registerListeners();

	}

	public static boolean blockPlacementRestricted() {
		return config.restrictPlaceBlocks && !foodTimerDone();
	}

	public static boolean blockUsageRestricted() {
		return config.restrictUseBlocks && !foodTimerDone();
	}

	public static boolean itemUsageRestricted() { return config.restrictUseItems && !foodTimerDone();}

	public static boolean entityInteractionRestricted() {
		return config.restrictEntityInteraction && !foodTimerDone();
	}

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


	/* I discovered that the fabric API has listeners for exactly the events I'm looking at (UseItem, UseBlock, UseEntity)
	 * So, this makes the design significantly better!
	 */
	private static void registerListeners() {

		// UseItemCallback affects item usage that is independent of a block (think ender pearls, food, armor, fishing rods, etc.)
		UseItemCallback.EVENT.register((player, world, hand) ->
		{
			// don't waste time processing if the food timer isn't active anyway
			if (BetterEatingMod.foodTimerDone())
				return TypedActionResult.pass(ItemStack.EMPTY);

			ItemStack itemStack = player.getStackInHand(hand);

			// Don't stop the usage of food or other exempt items (such as shields)
			if (BetterEatingMod.itemUsageRestricted() && !ItemUsePreventionManager.itemIsExempt(itemStack)) {
				return TypedActionResult.fail(itemStack);
			}
			return TypedActionResult.pass(itemStack);
		});
		// UseBlockCallback involves 3 things: block placement, block interaction, and item usage
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
		{
			if (BetterEatingMod.foodTimerDone())
				return ActionResult.PASS;

			// We need to distinguish what event is currently being handled by the method and, if necessary cancel it
			boolean holdingItem = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
			boolean useBlock = !(player.shouldCancelInteraction() && holdingItem);
			// if useBlock is true, bl2 from interactBlockInternal is false, so it runs the code using the block. So, we have to too
			if (useBlock) {
				BlockState blockState = player.getWorld().getBlockState(hitResult.getBlockPos());
				ActionResult blockUseResult = blockState.onUse(player.getWorld(), player, hand, hitResult);
				// if the block use is accepted but it's not supposed to be, intercept that. If not, pass on the success
				if (blockUseResult.isAccepted()) {
					return BetterEatingMod.blockUsageRestricted() ? ActionResult.FAIL : blockUseResult;
				}
			}

			// No longer are we interacting with the block, now we need to determine the item interactions
			ItemStack itemStack = player.getStackInHand(hand);
			boolean itemIsBlock = BlockItem.class.isAssignableFrom(itemStack.getItem().getClass());

			if (itemIsBlock) {
				if (BetterEatingMod.blockPlacementRestricted())
					return ActionResult.FAIL;
				else
					return ActionResult.PASS;
			}
			// this next line stops block placement from being unrestricted without item usage being unrestricted. Why?
			else if (!itemIsBlock && BetterEatingMod.itemUsageRestricted() && !ItemUsePreventionManager.itemIsExempt(itemStack)) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> BetterEatingMod.entityInteractionRestricted() ? ActionResult.FAIL : ActionResult.PASS);
	}
}
