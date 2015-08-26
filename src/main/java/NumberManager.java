import data.DataLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerListener;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class NumberManager {
    private static final Logger logger = LoggerFactory.getLogger(NumberManager.class);
    private long secondsBetweenLogAggregation = 10l;
    private final int socket = 4000;
    private final int maxClients = 5;
    private final static String filename = "numbers.log";

    public static void main(String[] args) {
        try {
            new NumberManager().startProgram();
        } catch (Exception e) {
            logger.error("program killed", e);
        }
    }

    public void startProgram() throws IOException {
        logger.debug("starting NumberManager");

        DataLogger dataLogger = new DataLogger(filename);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService
                .scheduleAtFixedRate(dataLogger, secondsBetweenLogAggregation, secondsBetweenLogAggregation,
                        TimeUnit.SECONDS);

        new ServerListener(dataLogger, socket, maxClients).start();
        logger.debug("Server thread has finished; shutting down scheduledExecutorService");
        scheduledExecutorService.shutdown();
        dataLogger.shutdown();
        logger.debug("Shutdown complete");
    }

}
