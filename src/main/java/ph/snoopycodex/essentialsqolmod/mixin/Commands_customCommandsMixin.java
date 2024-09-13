package ph.snoopycodex.essentialsqolmod.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.snoopycodex.essentialsqolmod.commands.registry.CustomCommandRegistry;

@Mixin(Commands.class)
public class Commands_customCommandsMixin {
    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(Commands.CommandSelection selection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        CustomCommandRegistry.registerCommands(this.dispatcher, selection, commandBuildContext);
        LogManager.getLogger().info("Custom commands has been registered!");
    }
}
