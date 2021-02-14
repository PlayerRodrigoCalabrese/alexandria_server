package fight.ia.type;

import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractNeedSpell;
import fight.ia.util.Function;
import fight.spells.Spell.SortStats;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA25 extends AbstractNeedSpell  {

    public IA25(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100, maxPo = 1;
            boolean action = false;
            Fighter ennemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);

            for(SortStats spellStats : this.highests)
                if(spellStats != null && spellStats.getMaxPO() > maxPo)
                    maxPo = spellStats.getMaxPO();

            Fighter secondEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(fight, this.fighter, 0, 5);//2 = po min 1 + 1;
            Fighter target = Function.getInstance().getNearestEnnemynbrcasemax(fight, this.fighter, 0, 2);//2 = po min 1 + 1;

            if(target != null) if(target.isHide()) target = null;

            if(this.fighter.getCurPm(this.fight) > 0 && secondEnnemy == null && target == null) {
                int num = Function.getInstance().moveautourIfPossible(fight, this.fighter, ennemy);
                if(num != 0) {
                    time = num;
                    action = true;
                    Function.getInstance().getNearestEnnemynbrcasemax(fight, this.fighter, 0, 5);//2 = po min 1 + 1;
                    target = Function.getInstance().getNearestEnnemynbrcasemax(fight, this.fighter, 0, 2);//2 = po min 1 + 1;
                }
            } else if(this.fighter.getCurPm(this.fight) > 0 && secondEnnemy != null && target == null) {
                int num = Function.getInstance().moveautourIfPossible(fight, this.fighter, secondEnnemy);
                if(num != 0) {
                    time = num;
                    action = true;
                    Function.getInstance().getNearestEnnemynbrcasemax(fight, this.fighter, 0, 5);//2 = po min 1 + 1;
                    target = Function.getInstance().getNearestEnnemynbrcasemax(fight, this.fighter, 0, 2);//2 = po min 1 + 1;
                }
            }

            if(this.fighter.getCurPa(this.fight) > 0 && target != null && !action) {
                int num = Function.getInstance().attackIfPossible(this.fight, this.fighter, this.cacs);
                if(num != 0) {
                    time = num;
                    action = true;
                }
            }

            if(this.fighter.getCurPm(this.fight) > 0 && !action) {
                int num = Function.getInstance().moveautourIfPossible(this.fight, this.fighter, ennemy);
                if(num != 0) time = num;
            }

            if(this.fighter.getCurPa(this.fight) == 0 && this.fighter.getCurPm(this.fight) == 0) this.stop = true;
            addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }
}