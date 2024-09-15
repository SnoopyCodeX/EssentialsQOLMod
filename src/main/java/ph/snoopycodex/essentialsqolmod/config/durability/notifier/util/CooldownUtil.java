package ph.snoopycodex.essentialsqolmod.config.durability.notifier.util;

import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CooldownUtil {
    public static Map<ItemStack, Long> cooldownMap = Collections.synchronizedMap(new HashMap<>());

    public static void putCooldown(ItemStack stack) {
        cooldownMap.put(stack, System.currentTimeMillis());
    }

    public static boolean isNotOnCooldown(ItemStack stack, long time) {
        if (isAvailable(stack, time)) {
            putCooldown(stack);
            return true;
        }

        return false;
    }

    public static boolean isAvailable(ItemStack stack, long time) {
        if (cooldownMap.containsKey(stack)) {
            long lastUsed = cooldownMap.get(stack);
            return System.currentTimeMillis() >= (lastUsed + time);
        }

        return true;
    }
}
