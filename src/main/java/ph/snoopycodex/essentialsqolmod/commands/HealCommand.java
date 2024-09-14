package ph.snoopycodex.essentialsqolmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import ph.snoopycodex.essentialsqolmod.EssentialsQOLMod;
import ph.snoopycodex.essentialsqolmod.utils.Messenger;

import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HealCommand extends BaseCustomCommand {
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal("heal")
            .requires(Permissions.require(EssentialsQOLMod.MOD_ID + ".commands.heal", 2))
            .executes(context -> healPlayer(context.getSource(), null))
            .then(argument("targetPlayer", EntityArgument.player())
                .requires(Permissions.require(EssentialsQOLMod.MOD_ID + ".commands.heal.others", 2))
                .executes(context -> healPlayer(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer")))
            );

        dispatcher.register(command);
    }

    private int healPlayer(CommandSourceStack source, @Nullable Entity targetEntity) {
        // If command was executed from the server console without the <targetEntity> argument
        if (source.getPlayer() == null && targetEntity == null) {
            Messenger.m(source, "r This command must be executed by a player!");

            return 0;
        }

        // Check if the command executor has the appropriate permissions
        if (!Permissions.check(source.getPlayer(), EssentialsQOLMod.MOD_ID + ".commands.heal", 2) && targetEntity == null) {
            Messenger.m(source, "r You do not have permission to use this command!");
            return 0;
        } else if (!Permissions.check(source.getPlayer(), EssentialsQOLMod.MOD_ID + ".commands.heal.others", 2) && targetEntity != null && !targetEntity.getUUID().equals(Objects.requireNonNull(source.getEntity()).getUUID())) {
            Messenger.m(source, "r You do not have permission to heal other players!");
            return 0;
        }

        ServerPlayer player = (targetEntity == null ? source.getPlayer() : source.getServer().getPlayerList().getPlayer(targetEntity.getUUID()));

        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, MobEffectInstance.MAX_AMPLIFIER, true, false, false));
            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1F, 1F);

            Messenger.m(source, (targetEntity == null ? ("l You have been healed!") : ("l You have healed %s!".formatted(player.getName().getString()))));
            return 1;
        }

        Messenger.m(source, "r Target player not found!");
        return 0;
    }
}
