package game.scheduler.entity;

import common.Formulas;
import common.SocketManager;
import game.scheduler.Updatable;
import util.TimerWaiter;

import java.util.ArrayList;

public class WorldPub extends Updatable {

    public final static ArrayList<String> pubs = new ArrayList<>();
    public final static Updatable updatable = new WorldPub(1200000);

    public WorldPub(int wait) {
        super(wait);
    }

    @Override
    public void update() {
        if(!WorldPub.pubs.isEmpty()) {
            if (this.verify()) {
                String pub = WorldPub.pubs.get(Formulas.getRandomValue(0, WorldPub.pubs.size() - 1));
                SocketManager.GAME_SEND_MESSAGE_TO_ALL("(Message Auto) : " + pub, "046380");
                TimerWaiter.update();
            }
        }
    }

    @Override
    public Object get() {
        return null;
    }
}