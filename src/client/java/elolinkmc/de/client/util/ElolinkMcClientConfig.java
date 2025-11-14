package elolinkmc.de.client.util;

import com.mojang.serialization.Codec;
import elolinkmc.de.util.config.ConfigFile;
import elolinkmc.de.util.config.ConfigOption;
import elolinkmc.de.util.config.encoder.JsonConfigEncoder;

public class ElolinkMcClientConfig {
    public static final ConfigFile FILE = new ConfigFile(JsonConfigEncoder.getInstance(), "elolink_mc.json");
    public static final ConfigOption<String> SERVER_URL = FILE.newOption(
      "server_url", Codec.STRING, "[SERVER_URL_HERE]",
      "The Url of the Elolink server that should be used"
    );

    public static void initialize() {
        FILE.initialize();
    }
}
