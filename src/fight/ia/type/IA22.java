package fight.ia.type;

import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractIA;
import fight.ia.util.Function;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA22 extends AbstractIA  {

    public IA22(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            Fighter ennemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);

            if (Function.getInstance().IfPossibleRasboulvulner(this.fight, this.fighter, this.fighter) == 0)
                if (Function.getInstance().moveFarIfPossible(this.fight, this.fighter) == 0)
                    if (Function.getInstance().tpIfPossibleRasboul(this.fight, this.fighter, ennemy) == 0)
                        Function.getInstance().invocIfPossible(this.fight, this.fighter);

            addNext(this::decrementCount, 1000);
        } else {
            this.stop = true;
        }
    }
}