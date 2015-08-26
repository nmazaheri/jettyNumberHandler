package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;

/**
 * Created by navid.mazaheri on 8/26/15.
 */
public class DataLogger implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DataLogger.class);
    private Writer wr;
    private String filename;
    private DataStore dataStore = new DataStore();
    private boolean writeEnabled = true;

    public DataLogger(String filename) throws IOException {
        this.filename = filename;
        wr = new FileWriter(filename);
    }

    public void update(String input) {
        int data = Integer.parseInt(input);
        boolean isUnique = dataStore.process(data);
        if (isUnique && writeEnabled) {
            writeToFile(data);
        }
    }

    private void writeToFile(int dataKey) {
        try {
            wr.write(dataKey + "\n");
        } catch (IOException e) {
            logger.error("unable to write data to {}", filename);
        } catch (Exception e) {
            logger.error("another error", e);
        }
    }

    @Override
    public void run() {
        synchronized (dataStore) {
            printStats();
            dataStore.resetValues();
        }
    }

    private void printStats() {
        DecimalFormat format = new DecimalFormat("###,###,###");
        String stats = String.format("Received %s new uniques, %s duplicates. UniqueTotal: %s",
                format.format(dataStore.getUniques()), format.format(dataStore.getDuplicates()),
                format.format(dataStore.getTotalUniques()));
        System.out.println(stats);
        logger.info(stats);
    }

    public void shutdown() {
        writeEnabled = false;
        try {
            logger.debug("flushing and closing writer");
            wr.close();
        } catch (IOException e) {
            logger.error("unable to close {} due to IOException", filename);
        }
    }
}
