import data.NumberLogger;
import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class NumberHandler {
    private static final Logger logger = LoggerFactory.getLogger(NumberHandler.class);
    private long secondsBetweenLogAggregation = 10l;
    private WindowDataStore windowDataStore = new WindowDataStore();

    public static void main(String[] args) {
        try {
            new NumberHandler().startProgram();
        } catch (Exception e) {
            logger.error("program killed", e);
        }
    }

    public void startProgram() throws Exception {
        NumberLogger numberLogger = new NumberLogger(windowDataStore);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService
                .scheduleAtFixedRate(numberLogger, secondsBetweenLogAggregation, secondsBetweenLogAggregation,
                        TimeUnit.SECONDS);
        ServerListener server = new ServerListener(windowDataStore);
        server.start();
        server.join();
        logger.info("Server thread has finished; shutting down NumberLogger");
        numberLogger.shutdown();
        logger.debug("Shutting down scheduledExecutorService");
        scheduledExecutorService.shutdownNow();
    }

}
