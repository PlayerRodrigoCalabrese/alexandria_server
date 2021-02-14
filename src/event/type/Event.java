package event.type;

import area.map.GameMap;
import client.Player;
import event.EventReward;
import event.IEvent;

/**
 * Created by Locos on 02/10/2016.
 */
public abstract class Event extends Thread implements IEvent {

    protected final byte id, maxPlayers;
    protected final String name;
    protected final String description;
    protected GameMap map;
    protected EventReward[] first, second, third;

    public Event(byte id, byte maxPlayers, String name, String description, EventReward[] first) {
        super.setName("Event-" + name);
        super.setDaemon(true);
        super.start();
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.description = description;
        this.first = first;
    }

    public Event(byte id, byte maxPlayers, String name, String description, EventReward[] first, EventReward[] second, EventReward[] third) {
        this(id, maxPlayers, name, description, first);
        this.second = second;
        this.third = third;
    }

    public byte getEventId() {
        return id;
    }

    public byte getMaxPlayers() {
        return maxPlayers;
    }

    public String getEventName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GameMap getMap() {
        return map;
    }

    public EventReward[] getFirst() {
        return first;
    }

    public EventReward[] getSecond() {
        return second;
    }

    public EventReward[] getThird() {
        return third;
    }

    public static void wait(int time) {
        long newTime = System.currentTimeMillis() + time;

        while (System.currentTimeMillis() < newTime) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void kickPlayer(Player player);
}
