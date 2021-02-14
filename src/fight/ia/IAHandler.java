package fight.ia;

import entity.monster.Monster.MobGrade;
import fight.Fight;
import fight.Fighter;
import fight.ia.type.*;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Locos on 18/09/2015.
 */
public class IAHandler {

    private final static ReentrantLock locker = new ReentrantLock();
    // TODO : Changer le switch en map, passer le IAHandler en singleton

    public static void select(final Fight fight, final Fighter fighter) {
        try {
            locker.lock();
            IA ia = new Blank(fight, fighter);
            MobGrade mobGrade = fighter.getMob();

            if (mobGrade == null) {
                if (fighter.isDouble())
                    ia = new IA5(fight, fighter, (byte) 5);
                else if (fighter.isCollector())
                    ia = new IA30(fight, fighter, (byte) 5);

                final IA finalIA = ia;
                ia.addNext(finalIA::endTurn, 2000);
            } else if (mobGrade.getTemplate() == null) {
                ia.setStop(true);
                ia.endTurn();
            } else {
                //region select ia
                switch (mobGrade.getTemplate().getIa()) {
                    case 1://IA BASIQUE attaque,pm,attaque,pm
                        ia = new IA27(fight, fighter, (byte) 4);
                        break;
                    case 2://IA Dragonnet rouge
                        ia = new IA2(fight, fighter, (byte) 6);
                        break;
                    case 5://IA Bloqueuse : Avancer vers ennemis
                        ia = new IA5(fight, fighter, (byte) 5);
                        break;
                    case 6://IA type invocations (Coffre anim�)
                        ia = new IA6(fight, fighter, (byte) 5);
                        break;
                    case 8://IA Surpuissante : Invocation, Buff, Fuite
                        ia = new IA8(fight, fighter, (byte) 4);
                        break;
                    case 9://IA La Fourbe : Attaque[], Fuite
                        ia = new IA9(fight, fighter, (byte) 4);
                        break;
                    case 10://IA Tonneau : Attaque[], Soin si Etat port�e
                        ia = new IA10(fight, fighter, (byte) 8);
                        break;
                    case 12://IA Tofus
                        ia = new IA12(fight, fighter, (byte) 4);
                        break;
                    case 14://IA Tonneau : Attaque[], Soin si Etat port�e
                        ia = new IA14(fight, fighter, (byte) 8);
                        break;
                    case 15://IA BASIQUE buff sois meme,attaque,pm,attaque,pm
                        ia = new IA30(fight, fighter, (byte) 4);
                        break;
                    case 16: // IA Tanu : Tape, va vers l'ennemis, invocation
                        ia = new IA16(fight, fighter, (byte) 8);
                        break;
                    case 17://IA KIMBO
                        ia = new IA17(fight, fighter, (byte) 4);
                        break;
                    case 18://Disciple Kimbo
                        ia = new IA18(fight, fighter, (byte) 4);
                        break;
                    case 19: // IA Des Tynril
                        ia = new IA19(fight, fighter, (byte) 4);
                        break;
                    case 20: // IA Kaskargo
                        ia = new IA20(fight, fighter, (byte) 4);
                        break;
                    case 21: // IA Krala
                        ia = new IA21(fight, fighter, (byte) 4);
                        break;
                    case 22: // IA Rasboul
                        ia = new IA22(fight, fighter, (byte) 4);
                        break;
                    case 23: // IA Rasboul mineur
                        ia = new IA23(fight, fighter, (byte) 3);
                        break;
                    case 24: // IA Sac anim�e
                        ia = new IA24(fight, fighter, (byte) 3);
                        break;
                    case 25: // IA Sacrifier
                        ia = new IA25(fight, fighter, (byte) 4);
                        break;
                    case 26: //IA Kitsou
                        ia = new IA26(fight, fighter, (byte) 4);
                        break;
                    case 27://IA BASIQUE attaque,pm,attaque,pm
                        ia = new IA27(fight, fighter, (byte) 4);
                        break;
                    case 28: //IA sphincter cell
                        ia = new IA28(fight, fighter, (byte) 5);
                        break;
                    case 29: //IA Tortu
                        ia = new IA29(fight, fighter, (byte) 4);
                        break;
                    case 30://IA BASIQUE buff sois meme,attaque,pm,attaque,pm
                        ia = new IA30(fight, fighter, (byte) 4);
                        break;
                    case 31:// rats degoutant
                        ia = new IA31(fight, fighter, (byte) 3);
                        break;
                    case 32://IA ARCHER attaque,pm loin d'enemie,attaque,pmvers enemie
                        ia = new IA32(fight, fighter, (byte) 4);
                        break;
                    case 33://IA BASIQUE buff allier,attaque,pm,attaque,pm
                        ia = new IA33(fight, fighter, (byte) 4);
                        break;
                    case 34://IA GLOUTO attaque tout le monde ,pm,attaque attaque tout le monde,pm
                        ia = new IA34(fight, fighter, (byte) 4);
                        break;
                    case 35: //IA BASIQUE ABraknyde heal sois meme,attaque,pm,attaque,pm
                        ia = new IA35(fight, fighter, (byte) 4);
                        break;
                    case 36: //IA BASIQUE attaque,Bond,pm,attaque,pm
                        ia = new IA36(fight, fighter, (byte) 4);
                        break;
                    case 37: //IA BASIQUE Branche soignante heal amis,attaque,pm,attaque,pm
                        ia = new IA37(fight, fighter, (byte) 4);
                        break;
                    case 38: //IA BASIQUE buffallier si pas denemie a porter,attaque,pm,attaque,pm
                        ia = new IA38(fight, fighter, (byte) 4);
                        break;
                    case 39://IA Corbac aprivoiser attaque,pm en ligne de vue droite,attaque,pm fuite
                        ia = new IA39(fight, fighter, (byte) 8);
                        break;
                    case 40://IA Buveur et momie koalak buff,attaque,pm,attaque,pm
                        ia = new IA40(fight, fighter, (byte) 4);
                        break;
                    case 41://IA Wobot
                        ia = new IA41(fight, fighter, (byte) 4);
                        break;
                    case 42://IA Gonflable
                        ia = new IA42(fight, fighter, (byte) 6);
                        break;
                    case 43://IA Bloqueuse
                        ia = new IA43(fight, fighter, (byte) 4);
                        break;
                    case 44://IA Chaton ecaflip
                        ia = new IA44(fight, fighter, (byte) 4);
                        break;
                    case 45://IA
                        ia = new IA45(fight, fighter, (byte) 6);
                        break;
                    case 46://IA lapino
                        ia = new IA46(fight, fighter, (byte) 6);
                        break;
                    case 47://IA coffre animer
                        ia = new IA47(fight, fighter, (byte) 4);
                        break;
                    case 48://IA Sanglier
                        ia = new IA48(fight, fighter, (byte) 4);
                        break;
                    case 49://IA Chaferfu lancier
                        ia = new IA49(fight, fighter, (byte) 6);
                        break;
                    case 50://IA Gourlo le terrible
                        ia = new IA50(fight, fighter, (byte) 6);
                        break;
                    case 51://IA Workette
                        ia = new IA51(fight, fighter, (byte) 6);
                        break;
                    case 52://IA avance Heal et buff allier plus fuite
                        ia = new IA52(fight, fighter, (byte) 6);
                        break;
                    case 53://IA Peki Peki invisible apres 3 attaque et fuite
                        ia = new IA53(fight, fighter, (byte) 8);
                        break;
                    case 54://IA Bworkmage
                        ia = new IA54(fight, fighter, (byte) 8);
                        break;
                    case 55://IA dopeul feca
                        ia = new IA55(fight, fighter, (byte) 8);
                        break;
                    case 56://IA Chene mou
                        ia = new IA56(fight, fighter, (byte) 8);
                        break;
                    case 57://IA dopeul Osamodas
                        ia = new IA57(fight, fighter, (byte) 8);
                        break;
                    case 58://IA rn
                        ia = new IA58(fight, fighter, (byte) 8);
                        break;
                    case 59://IA dopeul enutrof
                        ia = new IA59(fight, fighter, (byte) 8);
                        break;
                    case 60://IA dopeul sram
                        ia = new IA60(fight, fighter, (byte) 8);
                        break;
                    case 61://IA dopeul xelor
                        ia = new IA61(fight, fighter, (byte) 8);
                        break;
                    case 62://IA dopeul ecflip
                        ia = new IA62(fight, fighter, (byte) 8);
                        break;
                    case 63://IA dopeul eniripsa
                        ia = new IA63(fight, fighter, (byte) 8);
                        break;
                    case 64://IA dopeul iop
                        ia = new IA64(fight, fighter, (byte) 8);
                        break;
                    case 65://IA dopeul cra
                        ia = new IA65(fight, fighter, (byte) 8);
                        break;
                    case 66://IA dopeul sadida
                        ia = new IA66(fight, fighter, (byte) 8);
                        break;
                    case 67://IA dopeul Sacrieur
                        ia = new IA67(fight, fighter, (byte) 8);
                        break;
                    case 68://IA dopeul pandawa
                        ia = new IA68(fight, fighter, (byte) 8);
                        break;
                    case 69://IA Trooll
                        ia = new IA69(fight, fighter, (byte) 8);
                        break;
                    case 70://IA Maitre corbac
                        ia = new IA70(fight, fighter, (byte) 6);
                        break;
                    case 71://IA Ougah
                        ia = new IA71(fight, fighter, (byte) 4);
                        break;


                }
                //endregion
            }

            final IA finalIA = ia;
            ia.addNext(() -> {
                finalIA.apply();
                finalIA.addNext(finalIA::endTurn, 1000);
            }, 0);
        } finally {
            locker.unlock();
        }
    }
}
