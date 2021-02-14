package util.lang.type;

import kernel.Config;
import util.lang.AbstractLang;

/**
 * Created by Locos on 09/12/2015.
 */
public class French extends AbstractLang {

    private final static French singleton = new French();

    public static French getInstance() {
        return singleton;
    }

    public void initialize() {
        int index = 0;
        this.sentences.add(index, "Votre canal général est désactivé."); index++;
        this.sentences.add(index, "Les caractères point virgule, chevrons et tildé sont désactivé."); index++;
        this.sentences.add(index, "Tu dois attendre encore #1 seconde(s)."); index++;
        this.sentences.add(index, "Vous avez activé le canal général."); index++;
        this.sentences.add(index, "Vous avez désactivé le canal général."); index++;
        this.sentences.add(index, "Liste des membres du staff connectés :"); index++;
        this.sentences.add(index, "Il n'y a aucun membre du staff connecté."); index++;
        this.sentences.add(index, "Vous n'êtes pas bloquer.."); index++;
        this.sentences.add(index, "<b>" + Config.INSTANCE.getNAME() + " - <a href='" + Config.INSTANCE.getUrl() + "'>Site</a></b>\nEn ligne depuis : #1j #2h #3m #4s."); index++;
        this.sentences.add(index, "\nJoueurs en ligne : #1"); index++;
        this.sentences.add(index, "\nJoueurs uniques en ligne : #1"); index++;
        this.sentences.add(index, "\nRecord de connexion : #1"); index++;
        this.sentences.add(index, "Les commandes disponnibles sont :\n"
                + "<b>.infos</b> - Permet d'obtenir des informations sur le serveur.\n"
                + "<b>.deblo</b> - Permet de vous débloquer en vous téléportant à une cellule libre.\n"
                + "<b>.staff</b> - Permet de voir les membres du staff connectés.\n"
                + "<b>.all</b> - Permet d'envoyer un message à tous les joueurs.\n"
                + "<b>.noall</b> - Permet de ne plus recevoir les messages du canal général.\n" +
                "<b>.maitre</b> - Active le mode maitre sur le personnage indiqué, néccesite d'être le chef du groupe.\n" +
                (Config.INSTANCE.getTEAM_MATCH() ? "<b>.kolizeum</b> - Vous inscrit/désincrit de la liste d'attente de Kolizeum.\n" : "") +
                (Config.INSTANCE.getDEATH_MATCH() ? "<b>.deathmatch</b> - Vous inscrit/désincrit de la liste d'attente de DeathMatch.\n" : "") +
                "<b>.banque</b> - Ouvre votre banque.\n" +
                "<b>.transfert</b> - Nécessite d'être dans sa banque et permet de transférer toutes vos ressources.\n" +
                "<b>.groupe</b> - Groupe vos mules.\n"); index++;
        this.sentences.add(index, "Vous pouvez dès à présent voter, <b><a href='" + Config.INSTANCE.getUrl() + "'>clique ici</a></b> !"); index++;
        this.sentences.add(index, "Vous ne pouvez plus combattre jusqu'à nouvelle ordre."); index++;//14
        this.sentences.add(index, "Vous pouvez désormais combattre.");
    }
}
