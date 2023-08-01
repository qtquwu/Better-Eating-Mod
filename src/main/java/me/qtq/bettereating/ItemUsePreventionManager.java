package me.qtq.bettereating;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.item.ShieldItem;

public class ItemUsePreventionManager {
    public static boolean itemIsExempt(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ShieldItem) {
            return true;
        }
        if (BlockItem.class.isAssignableFrom(item.getClass())) {
            return true;
        }
        if (item.isFood()) {
            return true;
        }
        return false;
    }
}
