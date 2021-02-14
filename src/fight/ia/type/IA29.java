package fight.ia.type;

import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractIA;
import fight.ia.util.Function;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA29 extends AbstractIA  {

    public IA29(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            Fighter ennemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);

            if(!Function.getInstance().buffIfPossibleTortu(this.fight, this.fighter, this.fighter))
                Function.getInstance().moveNearIfPossible(this.fight, this.fighter, ennemy);
            Function.getInstance().moveNearIfPossible(this.fight, this.fighter, ennemy);

            addNext(this::decrementCount, 1000);
        } else {
            this.stop = true;
        }
    }
}