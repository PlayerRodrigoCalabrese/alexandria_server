package fight.ia.type;

import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractIA;
import fight.ia.util.Function;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA21 extends AbstractIA  {

    public IA21(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            Function.getInstance().buffIfPossibleKrala(this.fight, this.fighter, this.fighter);
            Function.getInstance().invoctantaIfPossible(this.fight, this.fighter);
            addNext(this::decrementCount, 1000);
        } else {
            this.stop = true;
        }
    }
}