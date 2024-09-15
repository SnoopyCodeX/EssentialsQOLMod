package ph.snoopycodex.essentialsqolmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ph.snoopycodex.essentialsqolmod.callback.ClickAirCallback;
import ph.snoopycodex.essentialsqolmod.callback.PlayerTickCallback;
import ph.snoopycodex.essentialsqolmod.config.EssentialsQOLModConfig;
import ph.snoopycodex.essentialsqolmod.config.durability.notifier.event.DurabilityNotifierEventHandler;

public class EssentialsQOLMod implements ModInitializer {
    public static final String MOD_ID = "essentialsqolmod";
    public static final String LOGGER_NAME = "EssentialsQOLMod";
    public static Logger LOGGER = LogManager.getLogger(EssentialsQOLMod.LOGGER_NAME);

    @Override
    public void onInitialize() {
        EssentialsQOLModConfig.HANDLER.load();

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            DurabilityNotifierEventHandler.checkDurability(player.getItemInHand(hand), player);
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            DurabilityNotifierEventHandler.checkDurability(player.getItemInHand(hand), player);
            return InteractionResult.PASS;
        });

        ClickAirCallback.EVENT.register((player, hand) -> {
            DurabilityNotifierEventHandler.checkDurability(player.getItemInHand(hand), player);
            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            DurabilityNotifierEventHandler.checkDurability(player.getItemInHand(hand), player);
            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            DurabilityNotifierEventHandler.checkDurability(player.getItemInHand(hand), player);
            return InteractionResult.PASS;
        });

        PlayerTickCallback.EVENT.register(player -> {
            Level level = player.level();

            if (level.isClientSide() && level.getGameTime() % 80 == 0) {
                if (EssentialsQOLModConfig.durabilityNotifier.general.checkArmor) {
                    for (ItemStack stack : player.getInventory().armor) {
                        DurabilityNotifierEventHandler.checkDurability(stack, player);
                    }
                }
            }

            return InteractionResult.PASS;
        });

        EssentialsQOLMod.LOGGER.info("Mod initialized!");
    }
}
