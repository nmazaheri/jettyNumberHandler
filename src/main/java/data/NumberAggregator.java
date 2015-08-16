package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class NumberAggregator implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WindowDataStore.class);
    private Set<Integer> totalUnique = new HashSet<Integer>();
    private WindowDataStore window;

    public NumberAggregator(WindowDataStore window) {
        this.window = window;
    }

    public void run() {
        logger.debug("starting NumberAggregator");
        Set<Integer> windowKeys;
        int totalWindowRequestCount;

        synchronized (WindowDataStore.class) {
            windowKeys = new HashSet<Integer>(window.getConcurrentSet());
            window.getConcurrentSet().clear();
            totalWindowRequestCount = window.getWindowRequestCount().getAndSet(0);
        }

        windowKeys.removeAll(totalUnique);
        writeToFile(windowKeys, "numbers.log");

        totalUnique.addAll(windowKeys);
        int windowUnique = windowKeys.size();
        int windowDuplicates = totalWindowRequestCount - windowUnique;
        logger.info("Received {} unique numbers, {} duplicates. UniqueTotal: {}", windowUnique, windowDuplicates,
                totalUnique.size());
    }

    private void writeToFile(Set<Integer> windowUniqueKeys, String filename) {
        if(windowUniqueKeys.isEmpty())
            return;

        logger.debug("writing {} unique keys to {}", windowUniqueKeys.size(), filename);
        Writer wr = null;
        try {
            wr = new FileWriter(filename);
            for (Integer i : windowUniqueKeys) {
                wr.write(i + "\n");
            }
        } catch (IOException e) {
            logger.error("unable to update {} due to IOException", filename);
        }

        if (wr != null)
            try {
                wr.close();
            } catch (IOException e) {
                logger.error("unable to close {} due to IOException", filename);
            }
    }

}
