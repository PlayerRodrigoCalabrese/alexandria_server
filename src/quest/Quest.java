package quest;

import client.Player;
import common.SocketManager;
import database.Database;
import entity.npc.NpcTemplate;
import game.action.ExchangeAction;
import game.world.World;
import game.world.World.Couple;
import kernel.Config;
import kernel.Constant;
import object.GameObject;
import object.ObjectTemplate;
import other.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Quest {

    //region Static method
    private static Map<Integer, Quest> questList = new HashMap<>();

    public static Map<Integer, Quest> getQuestList() {
        return questList;
    }

    public static Quest getQuestById(int id) {
        return questList.get(id);
    }

    public static void addQuest(Quest quest) {
        questList.put(quest.getId(), quest);
    }
    //endregion

    private int id;
    private ArrayList<QuestStep> questSteps = new ArrayList<>();
    private ArrayList<QuestObjectif> questObjectifList = new ArrayList<>();
    private NpcTemplate npc = null;
    private ArrayList<Action> actions = new ArrayList<>();
    private boolean delete;
    private Couple<Integer, Integer> condition = null;

    public Quest(int id, String steps, String objectifs, int npc, String action, String args, boolean delete, String condition) {
        this.id = id;
        this.delete = delete;
        try {
            if (!steps.equalsIgnoreCase("")) {
                String[] split = steps.split(";");

                if (split.length > 0) {
                    for (String qEtape : split) {
                        QuestStep q_Etape = QuestStep.getQuestStepById(Integer.parseInt(qEtape));
                        q_Etape.setQuestData(this);
                        questSteps.add(q_Etape);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!objectifs.equalsIgnoreCase("")) {
                String[] split = objectifs.split(";");

                if (split.length > 0) {
                    for (String qObjectif : split) {
                        questObjectifList.add(QuestObjectif.getQuestObjectifById(Integer.parseInt(qObjectif)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!condition.equalsIgnoreCase("")) {
            try {
                String[] split = condition.split(":");
                if (split.length > 0) {
                    this.condition = new Couple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.npc = World.world.getNPCTemplate(npc);
        try {
            if (!action.equalsIgnoreCase("") && !args.equalsIgnoreCase("")) {
                String[] arguments = args.split(";");
                int nbr = 0;
                for (String loc0 : action.split(",")) {
                    int actionId = Integer.parseInt(loc0);
                    String arg = arguments[nbr];
                    actions.add(new Action(actionId, arg, -1 + "", null));
                    nbr++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            World.world.logger.error("Erreur avec l action et les args de la quete " + this.id + ".");
        }
    }

    public int getId() {
        return id;
    }

    public boolean isDelete() {
        return this.delete;
    }

    public NpcTemplate getNpcTemplate() {
        return npc;
    }

    public ArrayList<QuestStep> getQuestSteps() {
        return questSteps;
    }

    private boolean haveRespectCondition(QuestPlayer questPlayer, QuestStep questStep) {
        switch (questStep.getCondition()) {
            case "1": //Valider les etapes d'avant
                boolean loc2 = true;
                for (QuestStep step : this.questSteps) {
                    if (step != null && step.getId() != questStep.getId() && !questPlayer.isQuestStepIsValidate(step)) {
                        loc2 = false;
                    }
                }
                return loc2;

            case "0":
                return true;
        }
        return false;
    }

    public String getGmQuestDataPacket(Player player) {
        QuestPlayer questPlayer = player.getQuestPersoByQuest(this);
        int loc1 = getObjectifCurrent(questPlayer);
        int loc2 = getObjectifPrevious(questPlayer);
        int loc3 = getNextObjectif(QuestObjectif.getQuestObjectifById(getObjectifCurrent(questPlayer)));
        StringBuilder str = new StringBuilder();
        str.append(id).append("|");
        str.append(loc1 > 0 ? loc1 : "");
        str.append("|");

        StringBuilder str_prev = new StringBuilder();
        boolean loc4 = true;
        // Il y a une exeption dans le code ici pour la seconde �tape de papotage
        for (QuestStep qEtape : questSteps) {
            if (qEtape.getObjectif() != loc1)
                continue;
            if (!haveRespectCondition(questPlayer, qEtape))
                continue;
            if (!loc4)
                str_prev.append(";");
            str_prev.append(qEtape.getId());
            str_prev.append(",");
            str_prev.append(questPlayer.isQuestStepIsValidate(qEtape) ? 1 : 0);
            loc4 = false;
        }
        str.append(str_prev);
        str.append("|");
        str.append(loc2 > 0 ? loc2 : "").append("|");
        str.append(loc3 > 0 ? loc3 : "");
        if (npc != null) {
            str.append("|");
            str.append(npc.getInitQuestionId(player.getCurMap().getId())).append("|");
        }
        return str.toString();
    }

    public QuestStep getCurrentQuestStep(QuestPlayer questPlayer) {
        for (QuestStep step : getQuestSteps()) {
            if (!questPlayer.isQuestStepIsValidate(step)) {
                return step;
            }
        }
        return null;
    }

    private int getObjectifCurrent(QuestPlayer questPlayer) {
        for (QuestStep step : questSteps) {
            if (!questPlayer.isQuestStepIsValidate(step)) {
                return step.getObjectif();
            }
        }
        return 0;
    }

    private int getObjectifPrevious(QuestPlayer questPlayer) {
        if (questObjectifList.size() == 1)
            return 0;
        else {
            int previous = 0;
            for (QuestObjectif qObjectif : questObjectifList) {
                if (qObjectif.getId() == getObjectifCurrent(questPlayer)) return previous;
                else previous = qObjectif.getId();
            }
        }
        return 0;
    }

    private int getNextObjectif(QuestObjectif questObjectif) {
        if (questObjectif == null)
            return 0;
        for (QuestObjectif objectif : questObjectifList) {
            if (objectif.getId() == questObjectif.getId()) {
                int index = questObjectifList.indexOf(objectif);
                if (questObjectifList.size() <= index + 1)
                    return 0;
                return questObjectifList.get(index + 1).getId();
            }
        }
        return 0;
    }

    public void applyQuest(Player player) {
        if (this.condition != null) {
            switch (this.condition.first) {
                case 1: // Niveau
                    if (player.getLevel() < this.condition.second) {
                        SocketManager.GAME_SEND_MESSAGE(player, "Votre niveau est insuffisant pour apprendre la quête.");
                        return;
                    }
                    break;
            }
        }

        QuestPlayer questPlayer = new QuestPlayer(Database.getStatics().getQuestPlayerData().getNextId(), id, false, player.getId(), "");
        player.addQuestPerso(questPlayer);
        SocketManager.GAME_SEND_Im_PACKET(player, "054;" + this.id);
        Database.getStatics().getQuestPlayerData().add(questPlayer);
        SocketManager.GAME_SEND_MAP_NPCS_GMS_PACKETS(player.getGameClient(), player.getCurMap());

        if (!this.actions.isEmpty()) {
            for (Action aAction : this.actions) {
                aAction.apply(player, player, -1, -1);
            }
        }

        Database.getStatics().getPlayerData().update(player);
    }

    public void updateQuestData(Player player, boolean validation, int type) {
        QuestPlayer questPlayer = player.getQuestPersoByQuest(this);
        for (QuestStep questStep : this.questSteps) {
            if (questStep.getValidationType() != type || questPlayer.isQuestStepIsValidate(questStep)) //On a d�j� valid� la questEtape on passe
                continue;
            if (questStep.getObjectif() != getObjectifCurrent(questPlayer) || !haveRespectCondition(questPlayer, questStep))
                continue;

            boolean refresh = false;

            if (validation)
                refresh = true;
            switch (questStep.getType()) {
                case 3://Donner item
                    if (player.getExchangeAction() != null && player.getExchangeAction().getType() ==
                            ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player
                            .getExchangeAction().getValue()).getTemplate().getId() == questStep.getNpc().getId()) {
                        for (Entry<Integer, Integer> entry : questStep.getItemNecessaryList().entrySet()) {
                            if (player.hasItemTemplate(entry.getKey(), entry.getValue())) { //Il a l'item et la quantit�
                                player.removeByTemplateID(entry.getKey(), entry.getValue()); //On supprime donc
                                refresh = true;
                            }
                        }
                    }
                    break;

                case 0:
                case 1://Aller voir %
                case 9://Retourner voir %
                    if (questStep.getCondition().equalsIgnoreCase("1")) { //Valider les questEtape avant
                        if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player.getExchangeAction().getValue()).getTemplate().getId() == questStep.getNpc().getId()) {
                            if (haveRespectCondition(questPlayer, questStep)) {
                                refresh = true;
                            }
                        }
                    } else {
                        if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player.getExchangeAction().getValue()).getTemplate().getId() == questStep.getNpc().getId())
                            refresh = true;
                    }
                    break;

                case 6: // monstres
                    for (Entry<Integer, Short> entry : questPlayer.getMonsterKill().entrySet())
                        if (entry.getKey() == questStep.getMonsterId() && entry.getValue() >= questStep.getQua())
                            refresh = true;
                    break;

                case 10://Ramener prisonnier
                    if (player.getExchangeAction() != null && player.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && player.getCurMap().getNpc((Integer) player.getExchangeAction().getValue()).getTemplate().getId() == questStep.getNpc().getId()) {
                        GameObject follower = player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR);
                        if (follower != null) {
                            Map<Integer, Integer> itemNecessaryList = questStep.getItemNecessaryList();
                            for (Entry<Integer, Integer> entry2 : itemNecessaryList.entrySet()) {
                                if (entry2.getKey() == follower.getTemplate().getId()) {
                                    refresh = true;
                                    player.setMascotte(0);
                                }
                            }
                        }
                    }
                    break;
            }

            if (refresh) {
                QuestObjectif ansObjectif = QuestObjectif.getQuestObjectifById(getObjectifCurrent(questPlayer));
                questPlayer.setQuestStepValidate(questStep);
                SocketManager.GAME_SEND_Im_PACKET(player, "055;" + id);
                if (haveFinish(questPlayer, ansObjectif)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "056;" + id);
                    applyButinOfQuest(player, ansObjectif);
                    questPlayer.setFinish(true);
                } else {
                    if (getNextObjectif(ansObjectif) != 0) {
                        if (questPlayer.overQuestStep(ansObjectif))
                            applyButinOfQuest(player, ansObjectif);
                    }
                }
                Database.getStatics().getPlayerData().update(player);
            }
        }
    }

    private boolean haveFinish(QuestPlayer questPlayer, QuestObjectif questObjectif) {
        return questPlayer.overQuestStep(questObjectif) && getNextObjectif(questObjectif) == 0;
    }

    private void applyButinOfQuest(Player player, QuestObjectif questObjectif) {
        long xp; int kamas;

        if ((xp = questObjectif.getXp()) > 0) { //Xp a donner
            player.addXp(xp * ((int) Config.INSTANCE.getRATE_XP()));
            SocketManager.GAME_SEND_Im_PACKET(player, "08;" + (xp * ((int) Config.INSTANCE.getRATE_XP())));
            SocketManager.GAME_SEND_STATS_PACKET(player);
        }

        if (questObjectif.getObjects().size() > 0) { //Item a donner
            for (Entry<Integer, Integer> entry : questObjectif.getObjects().entrySet()) {
                ObjectTemplate template = World.world.getObjTemplate(entry.getKey());
                int quantity = entry.getValue();
                GameObject object = template.createNewItem(quantity, false);

                if (player.addObjet(object, true)) {
                    World.world.addGameObject(object, true);
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + quantity + "~" + template.getId());
            }
        }

        if ((kamas = questObjectif.getKamas()) > 0) { //Kams a donner
            player.setKamas(player.getKamas() + (long) kamas);
            SocketManager.GAME_SEND_Im_PACKET(player, "045;" + kamas);
            SocketManager.GAME_SEND_STATS_PACKET(player);
        }

        if (getNextObjectif(questObjectif) != questObjectif.getId()) { //On passe au nouveau objectif on applique les actions
            for (Action action : questObjectif.getActions()) {
                action.apply(player, null, 0, 0);
            }
        }
    }
}
