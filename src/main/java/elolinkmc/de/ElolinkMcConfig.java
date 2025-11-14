package elolinkmc.de;

import com.mojang.serialization.Codec;
import elolinkmc.de.util.config.ConfigFile;
import elolinkmc.de.util.config.ConfigOption;
import elolinkmc.de.util.config.encoder.JsonConfigEncoder;

public class ElolinkMcConfig {
    public static final ConfigFile FILE = new ConfigFile(JsonConfigEncoder.getInstance(), "elolink_mc.json");
    public static final ConfigOption<Boolean> RUN_SERVER_IN_SINGLEPLAYER = FILE.newOption(
      "run_server_in_singleplayer", Codec.BOOL, false,
      "If the EloLink Server should be stated in singleplayer as well"
    );

    public static void initialize() {
        FILE.initialize();
    }
}
