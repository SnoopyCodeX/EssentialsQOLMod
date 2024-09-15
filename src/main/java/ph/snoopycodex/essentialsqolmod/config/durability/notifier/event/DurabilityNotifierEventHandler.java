package ph.snoopycodex.essentialsqolmod.config.durability.notifier.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import ph.snoopycodex.essentialsqolmod.EssentialsQOLMod;
import ph.snoopycodex.essentialsqolmod.config.durability.Durability;
import ph.snoopycodex.essentialsqolmod.config.durability.notifier.util.CooldownUtil;

public class DurabilityNotifierEventHandler {
    public static void checkDurability(ItemStack stack, Player player) {
        double durabilityPercentage = 1 - (Durability.Notifier.getPercentage() / 100.0);

        if (!stack.isEmpty())
            checkDurability(stack, player, durabilityPercentage);
    }

    public static void checkDurability(ItemStack stack, Player playerIn, double durabilityThreshold) {
        if (stack != null && stack.isDamageableItem() && stack.getMaxDamage() != 0) {
            if (((double) stack.getDamageValue() / stack.getMaxDamage()) > durabilityThreshold) {
                if (Durability.Notifier.shouldSendMessageWhenNotified()) {
                    sendMessage(playerIn, stack);
                }

                if (Durability.Notifier.shouldPlayNotificationSound() && CooldownUtil.isNotOnCooldown(stack, 500L)) {
                    playSound(playerIn);
                }
            }
        }
    }

    public static void sendMessage(Player player, ItemStack stack) {
        ChatFormatting messageColor = Durability.Notifier.getMessageColor();
        if (messageColor == null) {
            EssentialsQOLMod.LOGGER.warn("Invalid chat color in config, please check the config!");
            messageColor = ChatFormatting.YELLOW;
        }

        MutableComponent part1 = Component.translatable("essentialsqolmod.durability.notifier.warning.part1", stack.getDisplayName()).withStyle(messageColor);
        MutableComponent part2 = Component.translatable("essentialsqolmod.durability.notifier.warning.part2").withStyle(messageColor);
        MutableComponent percentage = Component.literal(Durability.Notifier.getPercentage() + "% ").withStyle(ChatFormatting.RED);
        MutableComponent part3 = Component.translatable("essentialsqolmod.durability.notifier.warning.part3").withStyle(messageColor);
        player.displayClientMessage(part1.append(part2).append(percentage).append(part3), true);
    }

    public static void playSound(Player player) {
        SoundEvent chosenNotificationSound = Durability.Notifier.getChosenNotificationSound();
        if (chosenNotificationSound != null)
            player.playSound(chosenNotificationSound, Durability.Notifier.getNotificationSoundVolume(), 1F);
        else
            EssentialsQOLMod.LOGGER.warn("Could not locate the following sound: {}. Perhaps you misspelled it.", Durability.Notifier.getChosenNotificationSound());
    }
}
