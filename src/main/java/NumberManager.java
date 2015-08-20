import data.NumberLogger;
import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        WindowDataStore windowDataStore = new WindowDataStore();
        NumberLogger numberLogger = new NumberLogger(windowDataStore, filename);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService
                .scheduleAtFixedRate(numberLogger, secondsBetweenLogAggregation, secondsBetweenLogAggregation,
                        TimeUnit.SECONDS);
        new ServerListener(windowDataStore).start();
        logger.debug("Server thread has finished; shutting down scheduledExecutorService");
        scheduledExecutorService.shutdown();
        numberLogger.shutdown();
        logger.debug("Shutdown complete");
    }

}
