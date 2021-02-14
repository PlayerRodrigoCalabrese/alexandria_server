package command;

import area.map.entity.House;
import client.Player;
import client.other.Party;
import common.PathFinding;
import common.SocketManager;
import event.EventManager;
import exchange.ExchangeClient;
import fight.arena.FightManager;
import fight.arena.TeamMatch;
import game.GameServer;
import game.action.ExchangeAction;
import game.world.World;
import kernel.Config;
import kernel.Constant;
import kernel.Logging;
import object.GameObject;
import util.lang.Lang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandPlayer {

    public final static String canal = "Général";
    public static boolean canalMute = false;

    public static boolean analyse(Player player, String msg) {
        if (msg.charAt(0) == '.' && msg.charAt(1) != '.') {
            if (command(msg, "all") && msg.length() > 5) {
                if (player.isInPrison())
                    return true;
                if(canalMute && player.getGroupe() == null) {
                    player.sendMessage("Le canal est indisponible pour une durée indéterminée.");
                    return true;
                }
                if (player.noall) {
                    player.sendMessage(Lang.get(player, 0));
                    return true;
                }
                if (player.getGroupe() == null && System.currentTimeMillis() - player.getGameClient().timeLastTaverne < 10000) {
                    player.sendMessage(Lang.get(player, 2).replace("#1", String.valueOf(10 - ((System.currentTimeMillis() - player.getGameClient().timeLastTaverne) / 1000))));
                    return true;
                }

                player.getGameClient().timeLastTaverne = System.currentTimeMillis();

                String prefix = "<font color='#C35617'>[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] (" + canal + ") (" + (Config.INSTANCE.getNAME().isEmpty() ? getNameServerById(Config.INSTANCE.getSERVER_ID()) : Config.INSTANCE.getNAME()) + ") <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + player.getName() + "'>" + player.getName() + "</a></b>";

                Logging.globalMessage.info("{}:{}", player.getName(), msg.substring(5, msg.length() - 1));

                final String message = "Im116;" + prefix + "~" + msg.substring(5, msg.length() - 1).replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", "") + "</font>";

                World.world.getOnlinePlayers().stream().filter(p -> !p.noall).forEach(p -> p.send(message));
                ExchangeClient.INSTANCE.send("DM" + player.getName() + "|" + getNameServerById(Config.INSTANCE.getSERVER_ID()) + "|" + msg.substring(5, msg.length() - 1).replace("\n", "").replace("\r", "").replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", "") + "|");
                return true;
            } else if (command(msg, "noall")) {
                if (player.noall) {
                    player.noall = false;
                    player.sendMessage(Lang.get(player, 3));
                } else {
                    player.noall = true;
                    player.sendMessage(Lang.get(player, 4));
                }
                return true;
            } else if (command(msg, "staff")) {
                String message = Lang.get(player, 5);
                boolean vide = true;
                for (Player target : World.world.getOnlinePlayers()) {
                    if (target == null)
                        continue;
                    if (target.getGroupe() == null || target.isInvisible())
                        continue;

                    message += "\n- <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + target.getName() + "'>[" + target.getGroupe().getName() + "] " + target.getName() + "</a></b>";
                    vide = false;
                }
                if (vide)
                    message = Lang.get(player, 6);
                player.sendMessage(message);
                return true;
            } else if (command(msg, "house")) {
                String message = "";
                if (!msg.contains("all")) {
                    message = "L'id de la maison la plus proche est : ";
                    short lstDist = 999;
                    House nearest = null;
                    for (House house : World.world.getHouses().values()) {
                        if (house.getMapId() == player.getCurMap().getId()) {
                            short dist = (short) PathFinding.getDistanceBetween(player.getCurMap(), house.getCellId(), player.getCurCell().getId());
                            if (dist < lstDist) {
                                nearest = house;
                                lstDist = dist;
                            }
                        }
                    }
                    if (nearest != null) message += nearest.getId();
                } else {
                    for (House house : World.world.getHouses().values()) {
                        if (house.getMapId() == player.getCurMap().getId()) {
                            message += "Maison " + house.getId() + " | cellId : " + house.getId();
                        }
                    }
                    if (message.isEmpty()) message = "Aucune maison sur cet carte.";
                }
                player.sendMessage(message);
                return true;
            } else if (command(msg, "deblo")) {
                if (player.cantTP())
                    return true;
                if (player.getFight() != null)
                    return true;
                if (player.getCurCell().isWalkable(true)) {
                    player.sendMessage(Lang.get(player, 7));
                    return true;
                }
                player.teleport(player.getCurMap().getId(), player.getCurMap().getRandomFreeCellId());
                return true;
            } else if (command(msg, "infos")) {
                long uptime = System.currentTimeMillis()
                        - Config.INSTANCE.getStartTime();
                int jour = (int) (uptime / (1000 * 3600 * 24));
                uptime %= (1000 * 3600 * 24);
                int hour = (int) (uptime / (1000 * 3600));
                uptime %= (1000 * 3600);
                int min = (int) (uptime / (1000 * 60));
                uptime %= (1000 * 60);
                int sec = (int) (uptime / (1000));
                int nbPlayer = GameServer.getClients().size();
                int nbPlayerIp = GameServer.getPlayersNumberByIp();

                String mess = Lang.get(player, 8).replace("#1", String.valueOf(jour)).replace("#2", String.valueOf(hour)).replace("#3", String.valueOf(min)).replace("#4", String.valueOf(sec));
                if (nbPlayer > 0)
                    mess += Lang.get(player, 9).replace("#1", String.valueOf(nbPlayer));
                if (nbPlayerIp > 0)
                    mess += Lang.get(player, 10).replace("#1", String.valueOf(nbPlayerIp));
                player.sendMessage(mess);
                return true;
            } else if (command(msg, "groupe")) {
                if (!player.getAccount().isSubscribeWithoutCondition()) {
                    player.sendMessage("Tu n'es pas abonné.");
                    return true;
                }
                if (player.isInPrison() || player.getFight() != null)
                    return true;

                World.world.getOnlinePlayers().stream().filter(p -> !p.equals(player) && p.getParty() == null && p.getAccount().getCurrentIp().equals(player.getAccount().getCurrentIp()) && p.getFight() == null && !p.isInPrison()).forEach(p -> {
                    if(player.getParty() == null) {
                        Party party = new Party(player, p);
                        SocketManager.GAME_SEND_GROUP_CREATE(player.getGameClient(), party);
                        SocketManager.GAME_SEND_PL_PACKET(player.getGameClient(), party);
                        SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), party);
                        SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), party);
                        player.setParty(party);
                        p.setParty(party);
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.getGameClient(), party);
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), party);
                    } else {
                        SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(player.getParty(), p);
                        player.getParty().addPlayer(p);;
                        p.setParty(player.getParty());
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PR_PACKET(p);
                    }
                });

                return true;
            } else if (command(msg, "banque")) {
                if (!player.getAccount().isSubscribeWithoutCondition()) {
                    player.sendMessage("Tu n'es pas abonné.");
                    return true;
                }
                if (player.isInPrison() || player.getFight() != null)
                    return true;

                player.openBank();
                return true;
            }else if (command(msg, "transfert")) {
                if (!player.getAccount().isSubscribeWithoutCondition()) {
                    player.sendMessage("Tu n'es pas abonné.");
                    return true;
                }
                if (player.isInPrison() || player.getFight() != null )
                    return true;
                if(player.getExchangeAction() == null || player.getExchangeAction().getType() != ExchangeAction.IN_BANK) {
                    player.sendMessage("Tu n'es pas dans ta banque.");
                    return true;
                }

                player.sendTypeMessage("Bank", "Veuillez patienter quelques instants..");
                int count = 0;

                for (GameObject object : new ArrayList<>(player.getItems().values())) {
                    if (object == null || object.getTemplate() == null || !object.getTemplate().getStrTemplate().isEmpty())
                        continue;
                    switch (object.getTemplate().getType()) {
                        case Constant.ITEM_TYPE_OBJET_VIVANT:case Constant.ITEM_TYPE_PRISME:
                        case Constant.ITEM_TYPE_FILET_CAPTURE:case Constant.ITEM_TYPE_CERTIF_MONTURE:
                        case Constant.ITEM_TYPE_OBJET_UTILISABLE:case Constant.ITEM_TYPE_OBJET_ELEVAGE:
                        case Constant.ITEM_TYPE_CADEAUX:case Constant.ITEM_TYPE_PARCHO_RECHERCHE:
                        case Constant.ITEM_TYPE_PIERRE_AME:case Constant.ITEM_TYPE_BOUCLIER:
                        case Constant.ITEM_TYPE_SAC_DOS:case Constant.ITEM_TYPE_OBJET_MISSION:
                        case Constant.ITEM_TYPE_BOISSON:case Constant.ITEM_TYPE_CERTIFICAT_CHANIL:
                        case Constant.ITEM_TYPE_FEE_ARTIFICE:case Constant.ITEM_TYPE_MAITRISE:
                        case Constant.ITEM_TYPE_POTION_SORT:case Constant.ITEM_TYPE_POTION_METIER:
                        case Constant.ITEM_TYPE_POTION_OUBLIE:case Constant.ITEM_TYPE_BONBON:
                        case Constant.ITEM_TYPE_PERSO_SUIVEUR:case Constant.ITEM_TYPE_RP_BUFF:
                        case Constant.ITEM_TYPE_MALEDICTION:case Constant.ITEM_TYPE_BENEDICTION:
                        case Constant.ITEM_TYPE_TRANSFORM:case Constant.ITEM_TYPE_DOCUMENT:
                        case Constant.ITEM_TYPE_QUETES:
                            break;
                        default:
                            count++;
                            player.addInBank(object.getGuid(), object.getQuantity());
                            break;
                    }
                }

                player.sendTypeMessage("Bank", "Le transfert a été effectué, " + count + " objet(s) ont été déplacés.");
                return true;
            }else if (Config.INSTANCE.getTEAM_MATCH() && command(msg, "kolizeum")) {
                if (player.kolizeum != null) {
                    if (player.getParty() != null) {
                        if (player.getParty().isChief(player.getId())) {
                            player.kolizeum.unsubscribe(player.getParty());
                            return true;
                        }
                        player.kolizeum.unsubscribe(player);
                        player.sendMessage("Vous venez de vous désincrire de la file d'attente.");
                    } else {
                        player.kolizeum.unsubscribe(player);
                        player.sendMessage("Vous venez de vous désincrire de la file d'attente.");
                    }
                    return true;
                } else {
                    if (player.getParty() != null) {
                        if (player.getParty().getPlayers().size() < 2) {
                            player.setParty(null);
                            SocketManager.GAME_SEND_PV_PACKET(player.getGameClient(), "");
                            CommandPlayer.analyse(player, ".kolizeum");
                            return true;
                        }
                        if (!player.getParty().isChief(player.getId())) {
                            player.sendMessage("Vous ne pouvez pas inscrire votre groupe, vous n'en êtes pas le chef.");
                            return true;
                        } else if (player.getParty().getPlayers().size() != TeamMatch.PER_TEAM) {
                            player.sendMessage("Pour vous inscrire, vous devez être exactement " + TeamMatch.PER_TEAM
                                    + " joueurs dans votre groupe.");
                            return true;
                        }
                        FightManager.subscribeKolizeum(player, true);
                    } else {
                        FightManager.subscribeKolizeum(player, false);
                    }
                }
                return true;
            } else  if (Config.INSTANCE.getDEATH_MATCH() && command(msg, "deathmatch")) {
                if(player.cantTP()) return true;
                if (player.deathMatch != null) {
                    FightManager.removeDeathMatch(player.deathMatch);
                    player.deathMatch = null;
                    player.sendMessage("Vous venez de vous désincrire de la file d'attente.");
                } else {
                    if(player.getEquippedObjects().size() == 0) {
                        player.sendMessage("Vous devez avoir des objets équipés.");
                    } else {
                        FightManager.subscribeDeathMatch(player);
                    }
                }
                return true;
            } else if (command(msg, "master") || command(msg, "maitre") || command(msg, "maître")) {
                if(player.cantTP()) return true;
                if (!player.getAccount().isSubscribeWithoutCondition()) {
                    player.sendMessage("Tu n'es pas abonné.");
                    return true;
                }

                final Party party = player.getParty();

                if (party == null) {
                    player.sendMessage("Tu es actuellement dans aucun groupe.");
                    return true;
                }

                final List<Player> players = player.getParty().getPlayers();

                if (!party.getChief().getName().equals(player.getName())) {
                    player.sendMessage("Tu n'es pas le chef du groupe.");
                    return true;
                }

                if (msg.length() <= 8 && party.getMaster() != null) {
                    player.sendMessage("Vous venez de désactiver le mode maitre.");
                    players.stream().filter(follower -> follower != party.getMaster())
                            .forEach(follower -> SocketManager.GAME_SEND_MESSAGE(follower, "Vous ne suivez plus " + party.getMaster().getName() + "."));
                    party.setMaster(null);
                    return true;
                }

                Player target = player;

                if (msg.length() > 8) {
                    String name = msg.substring(8, msg.length() - 1);
                    target = World.world.getPlayerByName(name);
                }

                if (target == null) {
                    player.sendMessage("Le joueur est introuvable.");
                    return true;
                }
                if (target.getParty() == null || !target.getParty().getPlayers().contains(player)) {
                    player.sendMessage("Tu n'es pas dans le groupe du joueur indiquer.");
                    return true;
                }

                party.setMaster(target);

                final String message = "Vous suivez désormais " + target.getName() + ".";
                for (Player follower : players)
                    if(follower != target)
                        SocketManager.GAME_SEND_MESSAGE(follower, message);

                party.moveAllPlayersToMaster(null);
                SocketManager.GAME_SEND_MESSAGE(target, "Vous êtes désormais le maitre.");
                return true;
            } else if(command(msg, "event")) {
                if(player.cantTP()) return true;
                return EventManager.getInstance().subscribe(player) == 1;
            } else {
                player.sendMessage(Lang.get(player, 12));
                return true;
            }
        }
        return false;
    }

    private static boolean command(String msg, String command) {
        return msg.length() > command.length() && msg.substring(1, command.length() + 1).equalsIgnoreCase(command);
    }

    private static String getNameServerById(int id) {
        switch (id) {
            case 13:
                return "Silouate";
            case 19:
                return "Allister";
            case 22:
                return "Oto Mustam";
            case 1:
                return "Jiva";
            case 37:
                return "Nostalgy";
            case 4001:
                return "Alma";
            case 4002:
                return "Aguabrial";
        }
        return "Unknown";
    }
}