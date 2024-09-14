package ph.snoopycodex.essentialsqolmod.commands.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import ph.snoopycodex.essentialsqolmod.commands.BaseCustomCommand;
import ph.snoopycodex.essentialsqolmod.commands.FlyCommand;
import ph.snoopycodex.essentialsqolmod.commands.HealCommand;
import ph.snoopycodex.essentialsqolmod.commands.TradeCommand;

import java.util.List;

public class CustomCommandRegistry {
    private static final List<BaseCustomCommand> customCommands = List.of(
            new TradeCommand(),
            new FlyCommand(),
            new HealCommand()
    );

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext) {
        for (BaseCustomCommand command : customCommands)
            command.registerCommand(dispatcher, commandSelection, commandBuildContext);
    }
}
