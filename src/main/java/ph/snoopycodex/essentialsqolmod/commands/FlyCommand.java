package ph.snoopycodex.essentialsqolmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import ph.snoopycodex.essentialsqolmod.EssentialsQOLMod;
import ph.snoopycodex.essentialsqolmod.utils.Messenger;

import java.util.Objects;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;

public class FlyCommand extends BaseCustomCommand {
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext) {
        boolean luckPermsLoaded = FabricLoader.getInstance().isModLoaded("luckperms");

        LiteralArgumentBuilder<CommandSourceStack> command = literal("fly")
            .then(literal("on")
                .executes(context -> fly(context.getSource(), true, null))
                .then(argument("targetPlayer", EntityArgument.player())
                    .requires(luckPermsLoaded
                        ? Permissions.require(EssentialsQOLMod.MOD_ID + ".commands.fly.others", 2)
                        : source -> source.hasPermission(2)
                    )
                    .executes(context -> fly(context.getSource(), true, EntityArgument.getPlayer(context, "targetPlayer")))
                )
            )
            .then(literal("off")
                .executes(context -> fly(context.getSource(), false, null))
                .then(argument("targetPlayer", EntityArgument.player())
                    .requires(luckPermsLoaded
                        ? Permissions.require(EssentialsQOLMod.MOD_ID + ".commands.fly.others", 2)
                        : source -> source.hasPermission(2)
                    )
                    .executes(context -> fly(context.getSource(), false, EntityArgument.getPlayer(context, "targetPlayer")))
                )
            );

        if (luckPermsLoaded) {
            command.requires(Permissions.require(EssentialsQOLMod.MOD_ID + ".commands.fly", 2));
        } else {
            command.requires(source -> source.hasPermission(2));
        }

        dispatcher.register(command);
    }

    private int fly(CommandSourceStack source, boolean enable, @Nullable Entity targetEntity) {
        if (source.getEntity() == null && targetEntity == null) {
            Messenger.m(source, "r This command must be executed by a real player!");
            return 0;
        }

        if (!Permissions.check(Objects.requireNonNull(source.getEntity()), EssentialsQOLMod.MOD_ID + ".commands.fly", 2) && targetEntity == null) {
            Messenger.m(source, "r You have no permission to use this command!");
            return 0;
        } else if (!Permissions.check(Objects.requireNonNull(source.getEntity()), EssentialsQOLMod.MOD_ID + ".commands.fly.others", 2) && targetEntity != null && !targetEntity.getUUID().equals(source.getEntity().getUUID())) {
            Messenger.m(source, "r You have no permission to enable/disable flight for others!");
            return 0;
        }

        if (targetEntity != null) {
            MinecraftServer server = source.getServer();

            if (server.getPlayerList().getPlayer(targetEntity.getUUID()) == null) {
                Messenger.m(source, "r Target player was not found! The target player might be offline");
                return 0;
            }
        }

        Player player = (Player) (targetEntity == null ? source.getEntity() : targetEntity);

        if (player.getAbilities().mayfly && enable) {
            Messenger.m(source, targetEntity == null ? "r Flight is already enabled!" : "r Flight is already enabled for %s!".formatted(player.getName().getString()));
            return 0;
        } else if (!player.getAbilities().mayfly && !enable) {
            Messenger.m(source, targetEntity == null ? "r Flight is already disabled!" : "r Flight is already disabled for %s!".formatted(player.getName().getString()));
            return 0;
        }

        // Disable player's `flying` state when flight is disabled
        if (player.getAbilities().mayfly && !enable) {
            player.getAbilities().flying = false;
        }

        player.getAbilities().mayfly = enable;
        player.onUpdateAbilities();

        Messenger.m(source, targetEntity == null
            ? "l Flight has been %s!".formatted(enable ? "enabled" : "disabled")
            : "l Flight has been %s for %s!".formatted(enable ? "enabled" : "disabled", player.getName().getString())
        );

        return 1;
    }
}