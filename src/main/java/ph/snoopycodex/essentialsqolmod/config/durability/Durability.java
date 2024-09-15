package ph.snoopycodex.essentialsqolmod.config.durability;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import ph.snoopycodex.essentialsqolmod.EssentialsQOLMod;
import ph.snoopycodex.essentialsqolmod.config.EssentialsQOLModConfig;

public class Durability {
    public static class Notifier {
        public static int getPercentage() {
            return EssentialsQOLModConfig.durabilityNotifier.general.percentage;
       }

       public static boolean shouldShowMessageWhenNotified() {
           return EssentialsQOLModConfig.durabilityNotifier.message.sendMessage;
       }

       public static ChatFormatting getMessageColor() {
           return ChatFormatting.getByName(EssentialsQOLModConfig.durabilityNotifier.message.messageColor);
       }

       public static boolean shouldPlayNotificationSound() {
           return EssentialsQOLModConfig.durabilityNotifier.sound.playSound;
       }

       public static float getNotificationSoundVolume() {
           return (float) EssentialsQOLModConfig.durabilityNotifier.sound.volume;
       }

        /*public static String getNotificationSoundLocation() {
            return EssentialsQOLModConfig.durabilityNotifier.sound.soundLocation;
        }*/

        public static SoundEvent getChosenNotificationSound() {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(EssentialsQOLModConfig.durabilityNotifier.sound.soundLocation);
            if (resourceLocation != null) {
                SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(resourceLocation);

                if (sound != null)
                    return sound;
                else {
                    EssentialsQOLMod.LOGGER.warn("Could not locate the following sound: {}. Perhaps you misspelled it. Falling back to default!", resourceLocation);
                    return SoundEvents.NOTE_BLOCK_PLING.value();
                }
            }

            return null;
        }
    }
}
