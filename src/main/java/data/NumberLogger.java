package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class NumberLogger implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(NumberLogger.class);
    private Set<Integer> totalUnique = new HashSet<Integer>();
    private WindowDataStore window;
    private final String filename = "numbers.log";
    private Writer wr;

    public NumberLogger(WindowDataStore window) {
        this.window = window;
        init();
    }

    public void init() {
        try {
            wr = new FileWriter(filename);
        } catch (IOException e) {
            logger.error("unable to create FileWriter to {}", filename);
        }
    }

    public void shutdown() {
        if (wr != null) {
            try {
                logger.debug("closing Writer");
                wr.close();
            } catch (IOException e) {
                logger.error("unable to close {} due to IOException", filename);
            }
        }
    }

    public void run() {
        Set<Integer> windowKeys;
        int totalWindowRequestCount;

        synchronized (WindowDataStore.class) {
            windowKeys = new HashSet<Integer>(window.getConcurrentSet());
            window.getConcurrentSet().clear();
            totalWindowRequestCount = window.getWindowRequestCount().getAndSet(0);
        }

        windowKeys.removeAll(totalUnique);
        writeToFile(windowKeys);

        totalUnique.addAll(windowKeys);
        int windowUnique = windowKeys.size();
        int windowDuplicates = totalWindowRequestCount - windowUnique;
        System.out
                .printf("Received %s unique numbers, %s duplicates. UniqueTotal: %s\n", windowUnique, windowDuplicates,
                        totalUnique.size());
        logger.info("Received {} unique numbers, {} duplicates. UniqueTotal: {}", windowUnique, windowDuplicates,
                totalUnique.size());
    }

    private void writeToFile(Set<Integer> windowUniqueKeys) {
        if (windowUniqueKeys.isEmpty())
            return;

        logger.debug("adding {} unique keys to {}", windowUniqueKeys.size(), filename);
        try {
            for (Integer i : windowUniqueKeys) {
                wr.write(i + "\n");
            }
            wr.flush();
        } catch (IOException e) {
            logger.error("unable to write data to {}", filename);
        } catch (Exception e) {
            logger.error("another error", e);
        }
    }
}
