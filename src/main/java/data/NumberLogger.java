package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class NumberLogger implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(NumberLogger.class);
    private String filename;
    private Set<Integer> totalUnique = new HashSet<Integer>();
    private WindowDataStore window;
    private Writer wr;

    public NumberLogger(WindowDataStore window, String filename) throws IOException {
        this.window = window;
        this.filename = filename;
        wr = new FileWriter(filename);
    }

    public void shutdown() {
        if (wr != null) {
            try {
                logger.debug("closing writer");
                wr.close();
            } catch (IOException e) {
                logger.error("unable to close {} due to IOException", filename);
            }
        }
    }

    public void run() {
        Set<Integer> windowKeys;
        int totalWindowRequestCount;

        synchronized (window) {
            windowKeys = new HashSet(window.getConcurrentSet());
            window.getConcurrentSet().clear();
            totalWindowRequestCount = window.getWindowRequestCount().getAndSet(0);
        }

        windowKeys.removeAll(totalUnique);
        writeToFile(windowKeys);

        totalUnique.addAll(windowKeys);
        int windowUnique = windowKeys.size();
        int windowDuplicates = totalWindowRequestCount - windowUnique;
        printStats(windowUnique, windowDuplicates);
    }

    private void writeToFile(Set<Integer> windowUniqueKeys) {
        if (windowUniqueKeys.isEmpty())
            return;

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

    private void printStats(int windowUnique, int windowDuplicates) {
        DecimalFormat format = new DecimalFormat("###,###,###");
        String stats = String
                .format("Received %s unique numbers, %s duplicates. UniqueTotal: %s", format.format(windowUnique),
                        format.format(windowDuplicates), format.format(totalUnique.size()));
        System.out.println(stats);
        logger.info(stats);
    }
}
