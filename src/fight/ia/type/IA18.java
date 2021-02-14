package fight.ia.type;

import common.SocketManager;
import fight.Fight;
import fight.Fighter;
import fight.ia.AbstractIA;
import fight.ia.util.Function;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA18 extends AbstractIA  {

    public IA18(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            Fighter kimbo = null;
            boolean kPair = false, kImpair = false, dPair = false, dImpair = false;

            if(this.fighter.haveState(29)) dImpair = true;
            if(this.fighter.haveState(30)) dPair = true;

            for(Fighter fighter : this.fight.getTeam1().values()) {
                if(fighter.getMob() !=  null) {
                    System.out.println(fighter.getMob().getTemplate().getId());
                    if(fighter.getMob().getTemplate().getId() == 1045) {
                        if(fighter.haveState(30)) {
                            fighter.setState(30, 0);
                            kPair = true;
                            this.fighter.setState(30, 1);
                        }
                        if(fighter.haveState(29)) {
                            fighter.setState(29, 0);
                            kImpair = true;
                            this.fighter.setState(29, 1);
                        }
                        kimbo = fighter;
                    }
                }
            }

            if(kimbo == null) {
                for (Fighter fighter : this.fight.getTeam0().values()) {
                    if (fighter.getMob() != null) {
                        System.out.println(fighter.getMob().getTemplate().getId());
                        if (fighter.getMob().getTemplate().getId() == 1045) {
                            if (fighter.haveState(30)) {
                                fighter.setState(30, 0);
                                kPair = true;
                                this.fighter.setState(30, 1);
                            }
                            if (fighter.haveState(29)) {
                                fighter.setState(29, 0);
                                kImpair = true;
                                this.fighter.setState(29, 1);
                            }
                            kimbo = fighter;
                        }
                    }
                }
            }

            if(kImpair && dImpair) {
                this.fighter.setState(29, 0);
                int attack = Function.getInstance().attackIfPossibleDisciplepair(this.fight, this.fighter, kimbo);

                if (attack != 0) {
                    this.fight.getAllGlyphs().stream().filter(glyph -> glyph.getCell().getId() == this.fighter.getCell().getId()).forEach(glyph -> {
                        this.fighter.addBuff(128, 1, 1, 1, true, 3500, "", this.fighter, true);
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 78, this.fighter.getId() + "", this.fighter.getId() + "," + "" + "," + 1);
                    });
                    Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
                }
            } else if(kPair && dPair) {
                this.fighter.setState(30, 0);
                int attack = Function.getInstance().attackIfPossibleDiscipleimpair(this.fight, this.fighter, kimbo);

                if (attack != 0) {
                    this.fight.getAllGlyphs().stream().filter(entry -> entry.getCell().getId() == this.fighter.getCell().getId()).forEach(entry -> {
                        this.fighter.addBuff(128, 1, 1, 1, true, 3500, "", this.fighter, true);
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 78, this.fighter.getId() + "", this.fighter.getId() + "," + "" + "," + 1);
                    });
                    Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
                }
            } else if(kPair) {
                int attack = Function.getInstance().attackIfPossibleDisciplepair(this.fight, this.fighter, kimbo);

                if (attack != 0) {
                    this.fight.getAllGlyphs().stream().filter(entry -> entry.getCell().getId() == this.fighter.getCell().getId()).forEach(entry -> {
                        this.fighter.addBuff(128, 1, 1, 1, true, 3500, "", this.fighter, true);
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 78, this.fighter.getId() + "", this.fighter.getId() + "," + "" + "," + 1);
                    });
                    Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
                }
            } else if(kImpair) {
                int attack = Function.getInstance().attackIfPossibleDiscipleimpair(this.fight, this.fighter, kimbo);

                if (attack != 0) {
                    this.fight.getAllGlyphs().stream().filter(entry -> entry.getCell().getId() == this.fighter.getCell().getId()).forEach(entry -> {
                        this.fighter.addBuff(128, 1, 1, 1, true, 3500, "", this.fighter, true);
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 78, this.fighter.getId() + "", this.fighter.getId() + "," + "" + "," + 1);
                    });
                    Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
                }
            }else {
                this.stop = true;
            }

            addNext(this::decrementCount, 1000);
        } else {
            this.stop = true;
        }
    }
}