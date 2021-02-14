package fight.ia.type;

import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractIA;
import fight.ia.util.Function;
import fight.spells.Spell.SortStats;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA16 extends AbstractIA  {

    public IA16(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (this.count > 0 && this.fighter.canPlay() && !this.stop) {
            Fighter target = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);

            if (target == null) return;

            int value = Function.getInstance().moveToAttackIfPossible(this.fight, this.fighter), cellId = value - (value / 1000) * 1000;;
            SortStats spellStats = this.fighter.getMob().getSpells().get(value / 1000);

            if (cellId != -1) {
                if (this.fight.canCastSpell1(this.fighter, spellStats, this.fighter.getCell(), cellId)) {
                    this.fight.tryCastSpell(this.fighter, spellStats, cellId);
                } else {
                    if (!Function.getInstance().moveNearIfPossible(this.fight, this.fighter, target))
                        if (!Function.getInstance().invocIfPossible(this.fight, this.fighter))
                            this.stop = true;
                }
            }

            addNext(this::decrementCount, 800);
        }
        else
            this.stop = true;
    }
}