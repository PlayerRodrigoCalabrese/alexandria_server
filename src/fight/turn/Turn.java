package fight.turn;

import fight.Fight;
import fight.Fighter;
import kernel.Constant;
import util.TimerWaiter;

import java.util.concurrent.TimeUnit;

public class Turn implements Runnable {

    private final Fight fight;
    private final Fighter fighter;
    private final long start;
    private boolean stop = false;

    public Turn(Fight fight, Fighter fighter) {
        this.fight = fight;
        this.fighter = fighter;
        TimerWaiter.addNext(this, Constant.TIME_BY_TURN + 2000, TimeUnit.MILLISECONDS, TimerWaiter.DataType.FIGHT);
        this.start = System.currentTimeMillis();
    }

    public long getStartTime() {
        return start;
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        if (this.stop || this.fighter.isDead()) {
            this.stop();
            return;
        }

        if (this.fight.getOrderPlaying() == null) {
            this.stop();
            return;
        }

        if (this.fight.getOrderPlaying().get(this.fight.getCurPlayer()) == null) {
            this.stop();
            return;
        }

        if (this.fight.getOrderPlaying().get(this.fight.getCurPlayer()) != this.fighter) {
            this.stop();
            return;
        }
        this.fight.endTurn(false);
    }
}