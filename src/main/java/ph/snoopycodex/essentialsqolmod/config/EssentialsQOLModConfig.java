package ph.snoopycodex.essentialsqolmod.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ph.snoopycodex.essentialsqolmod.EssentialsQOLMod;

public class EssentialsQOLModConfig {
    public static ConfigClassHandler<EssentialsQOLModConfig> HANDLER = ConfigClassHandler.createBuilder(EssentialsQOLModConfig.class)
        .id(ResourceLocation.fromNamespaceAndPath(EssentialsQOLMod.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                .setPath(FabricLoader.getInstance().getConfigDir().resolve(EssentialsQOLMod.MOD_ID + ".json"))
                .build()
            )
        .build();

    @SerialEntry
    public static DurabilityNotifier durabilityNotifier = new DurabilityNotifier();

    public static class DurabilityNotifier {
        @SerialEntry
        public General general = new General();

        @SerialEntry
        public Message message = new Message();

        @SerialEntry
        public Sound sound = new Sound();

        public static class General {
            @SerialEntry
            public int percentage = 10;

            @SerialEntry
            public boolean checkArmor = true;
        }

        public static class Message {
            @SerialEntry
            public boolean sendMessage = true;

            @SerialEntry
            public String messageColor = "YELLOW";
        }

        public static class Sound {
            @SerialEntry
            public boolean playSound = true;

            @SerialEntry
            public String soundLocation = "minecraft:block.note_block.pling";

            @SerialEntry
            public double volume = 0.6d;
        }
    }

    public static Screen getScreen(@Nullable Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("EssentialsQOL Mod"))
            .save(HANDLER::save)
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Durability Notifier"))
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("General"))
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Minimum percentage threshold"))
                        .description(OptionDescription.of(Component.literal("A warning will show when the durability of your tool is lower than this")))
                        .binding(10, () -> durabilityNotifier.general.percentage, (newVal) -> durabilityNotifier.general.percentage = newVal)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                            .range(0, 100)
                            .formatValue(value -> Component.literal(value.toString() + "%"))
                            .step(1)
                        )
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Check Armor"))
                        .description(OptionDescription.of(Component.literal("If this mod should also check the durability of your armors")))
                        .binding(true, () -> durabilityNotifier.general.checkArmor, (newVal) -> durabilityNotifier.general.checkArmor = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .build()
                )
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Message"))
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show message when notified"))
                        .description(OptionDescription.of(Component.literal("Should a message be shown when your tool reaches the threshold")))
                        .binding(true, () -> durabilityNotifier.message.sendMessage, (newVal) -> durabilityNotifier.message.sendMessage = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<MessageColor>createBuilder()
                        .name(Component.literal("Message color"))
                        .description(OptionDescription.of(Component.literal("The color of the message that will be sent")))
                        .binding(MessageColor.YELLOW, () -> MessageColor.valueOf(durabilityNotifier.message.messageColor.replaceAll(" ", "_")), (newVal) -> durabilityNotifier.message.messageColor = newVal.toString())
                        .controller(opt -> EnumControllerBuilder.create(opt)
                            .enumClass(MessageColor.class)
                        )
                        .build()
                    )
                    .build()
                )
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Notification Sound"))
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Play notification sound"))
                        .description(OptionDescription.of(Component.literal("If enabled, a notification sound will play when reaching the threshold")))
                        .binding(true, () -> durabilityNotifier.sound.playSound, (newVal) -> durabilityNotifier.sound.playSound = newVal)
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Double>createBuilder()
                        .name(Component.literal("Volume"))
                        .description(OptionDescription.of(Component.literal("The volume of the notification sound")))
                        .binding(0.6d, () -> durabilityNotifier.sound.volume, (newVal) -> durabilityNotifier.sound.volume = newVal)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                .range(0d, 1d)
                                .formatValue(value -> Component.literal((Math.round(value * 100)) + "%"))
                                .step(0.01d)
                        )
                        .build()
                    )
                    .option(Option.<String>createBuilder()
                        .name(Component.literal("Notification Sound file"))
                        .description(OptionDescription.of(Component.literal("The sound to play. [default: minecraft:block.note_block.pling]")))
                        .binding("minecraft:block.note_block.pling", () -> durabilityNotifier.sound.soundLocation, (newVal) -> durabilityNotifier.sound.soundLocation = newVal)
                        .controller(StringControllerBuilder::create)
                        .build()
                    )
                    .build()
                )
                .build()
            )
            .build()
            .generateScreen(parent);
    }

    public enum MessageColor implements NameableEnum {
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW,
        WHITE;

        @Override
        public Component getDisplayName() {
            return Component.literal(this.toString().replaceAll("_", " ")).withStyle(ChatFormatting.getByName(this.toString()));
        }
    }
}
