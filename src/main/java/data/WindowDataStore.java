package data;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by navid.mazaheri on 8/16/15.
 * <p/>
 * Used for storage of numbers
 */
public class WindowDataStore {
    private static final Logger logger = LoggerFactory.getLogger(WindowDataStore.class);
    private ConcurrentHashMap<Integer, Short> numberFrequencyMap = new ConcurrentHashMap<Integer, Short>();
    private AtomicInteger windowRequestCount = new AtomicInteger();

    public void updateWindowIfValid(String in) {
        if (!isValidNumber(in))
            return;

        try {
            Integer integer = Integer.parseInt(in);
            updateWindow(integer);
        } catch (NumberFormatException e) {
            logger.warn("dropping {}; unable to convert to integer", in);
        }
    }

    private boolean isValidNumber(String input) {
        return (NumberUtils.isNumber(input) && input.length() == 9);
    }

    private void updateWindow(Integer dataKey) {
        Short count = 1;
        if (numberFrequencyMap.containsKey(dataKey)) {
            count = numberFrequencyMap.get(dataKey);
            logger.debug("{} already exists in numberFrequencyMap and has been seen {} times", dataKey, count);
            count++;
        } else {
            logger.debug("{} is a new key", dataKey);
        }
        windowRequestCount.incrementAndGet();
        numberFrequencyMap.put(dataKey, count);
    }

    public ConcurrentHashMap<Integer, Short> getNumberFrequencyMap() {
        return numberFrequencyMap;
    }

    public AtomicInteger getWindowRequestCount() {
        return windowRequestCount;
    }
}
