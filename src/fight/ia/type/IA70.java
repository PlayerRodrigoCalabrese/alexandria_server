package fight.ia.type;

import common.PathFinding;
import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractNeedSpell;
import fight.ia.util.Function;
import fight.spells.Spell;

/**
 * Created by Locos on 24/01/2017.
 */
public class IA70 extends AbstractNeedSpell {

    public IA70(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            //12 PA - 5 PM
            // invocation corbac : 3 PA
            // sanction ténébreuse : 4 PA
            //carapaces d'ailes : 2 PA    Ne joue plus pendant 3 tours
            // Lien volatile : 2 PA   +CC
            int time = 100;
            boolean action = false;

            Spell.SortStats sanction = this.fighter.getMob().getSpells().get(627);
            Spell.SortStats lien = this.fighter.getMob().getSpells().get(628);
            Spell.SortStats invocation = this.fighter.getMob().getSpells().get(629);
            Spell.SortStats carapce = this.fighter.getMob().getSpells().get(630);

            if(!this.fighter.haveInvocation()) {
                int cell = PathFinding.getAvailableCellArround(this.fight, this.fighter.getCell().getId(), null);
                if(this.fight.canCastSpell1(this.fighter, invocation, this.fighter.getCell(), cell)) {
                    this.fight.tryCastSpell(this.fighter, invocation, cell);
                    time = 1500;
                    action = true;
                }
            }

            if (this.fight.canCastSpell1(this.fighter, lien, this.fighter.getCell(), this.fighter.getCell().getId()) && !action) {
                this.fight.tryCastSpell(this.fighter, lien, this.fighter.getCell().getId());
                time = 1000;
                action = true;
            }

            if (this.fight.canCastSpell1(this.fighter, carapce, this.fighter.getCell(), this.fighter.getCell().getId()) && !action) {
                this.fight.tryCastSpell(this.fighter, carapce, this.fighter.getCell().getId());
                time = 1500;
                action = true;
            }

            Fighter ennemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);
            Fighter nearestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, 3);//2 = po min 1 + 1;

            if(nearestEnnemy != null) if(nearestEnnemy.isHide()) nearestEnnemy = null;
            if(ennemy != null) if(ennemy.isHide()) ennemy = null;

            if(this.fighter.getCurPm(this.fight) > 0 && nearestEnnemy != null && !action) {
                Function.getInstance().moveNearIfPossible(this.fight, this.fighter, nearestEnnemy);
                time = 1000;
            } else if(this.fighter.getCurPm(this.fight) > 0 && nearestEnnemy == null && !action) {
                Function.getInstance().moveNearIfPossible(this.fight, this.fighter, ennemy);
                time = 1000;
            }

            if(this.fighter.getCurPa(this.fight) > 0 && nearestEnnemy != null && !action) {
                int value = Function.getInstance().attackIfPossible(this.fight, this.fighter, this.cacs);
                if(value != 0) {
                    time = value;
                    action = true;
                }
            }
            if(this.fighter.getCurPa(this.fight) > 0 && ennemy != null && !action) {
                int value = Function.getInstance().attackIfPossible(this.fight, this.fighter, this.cacs);
                if(value != 0) {
                    time = value;
                    action = true;
                }
            }

            if(this.fighter.getCurPm(this.fight) > 0 && !action) {
                nearestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, 20);//2 = po min 1 + 1;
                Function.getInstance().moveNearIfPossible(this.fight, this.fighter, nearestEnnemy);
                time = 1000;
            }

            if(this.fighter.getCurPa(this.fight) == 0 && this.fighter.getCurPm(this.fight) == 0) this.stop = true;
            addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }
}