package exchange.transfer;

import exchange.ExchangeClient;
import game.world.World;

/**
 * Created by Locos on 02/02/2017
 **/
public class DataType<T> {
    private final byte type;
    private T value;

    public DataType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setValue(T value) {
        synchronized (this) {
            this.value = value;
            this.notify();
        }
    }

    public T getValue() {
        try {
            synchronized (this) {
                final DataQueue data = World.world.getDataQueue();
                final long count = data.count();

                data.getQueue().put(count, this);
                ExchangeClient.INSTANCE.send("DI" + this.getType() + count);
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }
}
