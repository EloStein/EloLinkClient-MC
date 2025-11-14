package elolinkmc.de;

import com.sun.net.httpserver.HttpServer;
import elolinkmc.de.util.ExceptionUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ElolinkMc implements ModInitializer {
    public static final String MOD_ID = "elolink_mc";
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
    public static final ModMetadata METADATA = MOD_CONTAINER.getMetadata();
    public static final String MOD_NAME = METADATA.getName();
    public static final Version MOD_VERSION = METADATA.getVersion();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("Loading {} v{}", MOD_NAME, MOD_VERSION.getFriendlyString());
        ElolinkMcConfig.initialize();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER
          || ElolinkMcConfig.RUN_SERVER_IN_SINGLEPLAYER.getValue()
        ) {
            Thread elolinkServerThread = new Thread(ElolinkMc::runElolinkServer);
            elolinkServerThread.setName("Elolink-Server");
            elolinkServerThread.setUncaughtExceptionHandler((t, e) ->
              LOGGER.error("Elolink-Server has crashed: {}", ExceptionUtil.makePretty(e))
            );
            elolinkServerThread.start();
        }
    }

    private static void runElolinkServer() {
        LOGGER.info("Stating Elolink-Server on port: 8080");
        try {
            // Not the actual server, but I wanted it to already bind the port
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
