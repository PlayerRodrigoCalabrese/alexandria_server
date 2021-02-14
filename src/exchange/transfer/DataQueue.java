package exchange.transfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Locos on 15/09/2015.
 */
public class DataQueue {

    private final Map<Long, DataType<?>> queue;
    private long count;

    public DataQueue() {
        this.queue = new HashMap<>();
        this.count = 0;
    }

    public Map<Long, DataType<?>> getQueue() {
        return queue;
    }

    public synchronized long count() {
        return count++;
    }
}
