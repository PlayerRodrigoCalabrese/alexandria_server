package fight.ia.type;

import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractNeedSpell;
import fight.ia.util.Function;
import fight.spells.Spell.SortStats;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA30 extends AbstractNeedSpell  {

    public IA30(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100, maxPo = 1;
            boolean action = false;
            Fighter ennemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);

            for(SortStats S : this.highests)
                if(S != null && S.getMaxPO() > maxPo)
                    maxPo = S.getMaxPO();

            Fighter longestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 1, maxPo + 1);// pomax +1;
            Fighter nearestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, 2);//2 = po min 1 + 1;

            if(maxPo == 1) longestEnnemy = null;
            if(nearestEnnemy != null) if(nearestEnnemy.isHide()) nearestEnnemy = null;
            if(longestEnnemy != null) if(longestEnnemy.isHide()) longestEnnemy = null;

            if(this.fighter.getCurPm(this.fight) > 0 && longestEnnemy == null && nearestEnnemy == null) {
                int value = Function.getInstance().moveautourIfPossible(this.fight, this.fighter, ennemy);
                if(value != 0) {
                    time = value;
                    action = true;
                    longestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 1, maxPo + 1);// pomax +1;
                    nearestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, 2);//2 = po min 1 + 1;
                    if(maxPo == 1) longestEnnemy = null;
                }
            }

            if(this.fighter.getCurPa(this.fight) > 0 && this.fighter.nbInvocation() < 2) {
                if (Function.getInstance().invocIfPossible(this.fight, this.fighter, this.invocations)) {
                    time = 2000;
                    action = true;
                }
            }


            if(this.fighter.getCurPa(this.fight) > 0 && !action) {
                if (Function.getInstance().buffIfPossible(this.fight, this.fighter, this.fighter, this.buffs)) {
                    time = 400;
                    action = true;
                }
            }

            if(this.fighter.getCurPa(this.fight) > 0 && longestEnnemy != null && nearestEnnemy == null && !action) {
                int value = Function.getInstance().attackIfPossible(this.fight, this.fighter, this.highests);
                if(value != 0) {
                    time = value;
                    action = true;
                }
            } else if(this.fighter.getCurPa(this.fight) > 0 && nearestEnnemy != null && !action) {
                int value = Function.getInstance().attackIfPossible(this.fight, this.fighter, this.cacs);
                if(value != 0) {
                    time = value;
                    action = true;
                }
            }

            if(this.fighter.getCurPa(this.fight) > 0 && nearestEnnemy != null && !action) {
                int value = Function.getInstance().attackIfPossible(this.fight, this.fighter, this.highests);
                if(value != 0) {
                    time = value;
                    action = true;
                }
            }

            if(this.fighter.getCurPm(this.fight) > 0 && !action) {
                int value = Function.getInstance().moveautourIfPossible(this.fight, this.fighter, ennemy);
                if(value != 0) time = value;
            }

            if(this.fighter.getCurPa(this.fight) == 0 && this.fighter.getCurPm(this.fight) == 0) this.stop = true;
            addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }
}