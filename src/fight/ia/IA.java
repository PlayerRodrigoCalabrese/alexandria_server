package fight.ia;

import fight.Fight;
import fight.Fighter;

/**
 * Created by Locos on 18/09/2015.
 */
public interface IA {

    Fight getFight();
    Fighter getFighter();
    boolean isStop();
    void setStop(boolean stop);
    void addNext(Runnable runnable, Integer time);

    void apply();
    void endTurn();
}
