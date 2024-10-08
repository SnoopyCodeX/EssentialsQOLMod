package ph.snoopycodex.essentialsqolmod.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.snoopycodex.essentialsqolmod.callback.PlayerTickCallback;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void postTick(CallbackInfo ci) {
        PlayerTickCallback.EVENT.invoker().tick((Player) (Object) this);
    }
}
