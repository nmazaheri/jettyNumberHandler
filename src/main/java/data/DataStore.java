package data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by navid.mazaheri on 8/16/15.
 * <p/>
 * Used for storage of numbers
 */
public class DataStore {
    protected Set<Integer> values = new HashSet();
    protected int duplicates = 0;
    protected int uniques = 0;

    public synchronized boolean process(int data) {
        if (values.contains(data)) {
            duplicates++;
            return false;
        }

        uniques++;
        values.add(data);
        return true;
    }

    public final void resetValues() {
        duplicates = 0;
        uniques = 0;
    }

    int getDuplicates() {
        return duplicates;
    }

    int getUniques() {
        return uniques;
    }

    int getTotalUniques() {
        return values.size();
    }
}
