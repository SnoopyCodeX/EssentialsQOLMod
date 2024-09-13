package ph.snoopycodex.essentialsqolmod.commands;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class BaseCustomCommand {
    public abstract void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext);
}
