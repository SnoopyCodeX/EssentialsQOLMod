package ph.snoopycodex.essentialsqolmod;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;

public class EssentialsQOLMod implements ModInitializer {
    public static final String MOD_ID = "essentialsqolmod";

    @Override
    public void onInitialize() {
        LogManager.getLogger().info("Mod initialized!");
    }
}
