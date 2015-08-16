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
    private ConcurrentHashMap<Integer, Short> window;
    private AtomicInteger windowCount;

    public NumberAggregator(ConcurrentHashMap<Integer, Short> window, AtomicInteger windowCount) {
        this.window = window;
        this.windowCount = windowCount;
    }

    public void run() {
        Set<Integer> windowKeys = window.keySet();
        window.clear();

        int totalWindowCount = windowCount.get();
        windowCount.set(0);

        Set<Integer> windowUniqueKeys = new HashSet<Integer>(windowKeys);
        windowUniqueKeys.removeAll(totalUnique);
        totalUnique.addAll(windowUniqueKeys);

        writeToFile(windowUniqueKeys, "numbers.log");

        int windowUniques = windowUniqueKeys.size();
        int windowDuplicates = totalWindowCount - windowUniques;
        logger.info("Received {} unique numbers, {} duplicates. UniqueTotal: {}", windowUniques, windowDuplicates,
                totalUnique.size());
    }

    private void writeToFile(Set<Integer> windowUniqueKeys, String filename) {
        Writer wr = null;
        try {
            wr = new FileWriter(filename);
            for (Integer i : windowUniqueKeys) {
                wr.write(i + "");
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
