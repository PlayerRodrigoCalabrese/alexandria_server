package fight.arena;

import area.map.GameMap;
import client.Player;
import client.other.Party;
import game.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TeamMatch {

    public final static byte PER_TEAM = 3, QUANTITY = 3;
    public final static short KAMAS = 2500, OBJECT = 25001;
    private final static int MINIMUM_LEVEL = 50;

    private final List<Player> firstTeam, secondTeam = new ArrayList<>();

    TeamMatch(Player player) {
        this.firstTeam = new ArrayList<>();
        this.firstTeam.add(player);
        player.sendMessage("Vous venez de vous inscrire au Kolizeum, en attente de d'autres joueurs...");
        player.kolizeum = this;
        FightManager.addTeamMatch(this);
    }

    TeamMatch(Party party) {
        this.firstTeam = party.getPlayers();
        this.sendToAll("Vous venez de vous inscrire au Kolizeum avec votre groupe.");
        party.getPlayers().forEach(player -> player.kolizeum = this);
        FightManager.addTeamMatch(this);
    }

    public void start() {
        this.sendToAll("Le Kolizeum va commencer, veuillez être isAvailable d'ici quelques secondes.");

        FightManager.scheduler.schedule(() -> getAllPlayers().forEach(player -> {
            if (player.getFight() != null) {
                sendToAll("Le joueur " + player.getName() + " est en combat, il vient d'être désinscrit...");
                unsubscribe(player);
            }
        }), 5, TimeUnit.SECONDS);

        GameMap map = World.world.getMap(FightManager.maps[new Random().nextInt(FightManager.maps.length)]);

        FightManager.scheduler.schedule(() -> {
            if ((firstTeam.size() + secondTeam.size()) != PER_TEAM * 2) {
                sendToAll("Recherche de nouveaux joueurs un cours...");
            } else {

                getAllPlayers().forEach(player -> {
                    player.setOldPosition();
                    player.teleport(map, 0);
                });
                FightManager.scheduler.schedule((Runnable) () -> map.newKolizeum(TeamMatch.this), 4, TimeUnit.SECONDS);
            }
        }, 10, TimeUnit.SECONDS);

    }

    void subscribe(Player player) {
        player.kolizeum = this;
        player.sendMessage("Vous venez de vous inscrire au Kolizeum, en attente de d'autres joueurs...");
        sendToAll("Nous avons trouvé " + (firstTeam.size() + secondTeam.size() + 1) + "/"
                + PER_TEAM * 2 + " joueurs pour commencer le Kolizeum.");
        if (firstTeam.size() < PER_TEAM) {
            firstTeam.add(player);
        } else {
            secondTeam.add(player);
        }
        verify();
    }

    public void unsubscribe(Player player) {
        if (firstTeam.contains(player)) {
            firstTeam.remove(player);
        } else {
            secondTeam.remove(player);
        }
        player.sendMessage("Vous venez d'�tre d�sinscrit du Kolizeum.");
        player.kolizeum = null;
    }

    void subscribe(Party party) {
        sendToAll("Nous avons trouvé " + (this.firstTeam.size() + this.secondTeam.size() + PER_TEAM) + "/"
                + PER_TEAM * 2 + " joueurs pour commencer le Kolizeum .");

        if(this.firstTeam.isEmpty()) {
            party.getPlayers().stream().filter(player -> this.firstTeam.size() < PER_TEAM).forEach(this.firstTeam::add);
            party.getPlayers().forEach(player -> {
                player.sendMessage("Vous venez de vous inscrire au Kolizeum avec votre groupe, en attente de d'autres joueurs...");
                player.kolizeum = this;
            });
        } else {
            party.getPlayers().stream().filter(player -> this.secondTeam.size() < PER_TEAM).forEach(this.secondTeam::add);
            party.getPlayers().forEach(player -> {
                player.sendMessage("Vous venez de vous inscrire au Kolizeum avec votre groupe, en attente de d'autres joueurs...");
                player.kolizeum = this;
            });
        }

        verify();
    }

    public void unsubscribe(Party party) {
        party.getPlayers().forEach(player -> {
            player.sendMessage("Vous venez de vous désinscrire du Kolizeum avec votre groupe.");
            player.kolizeum = null;
        });

        if(this.firstTeam.contains(party.getChief())) {
            this.firstTeam.clear();
        } else {
            this.secondTeam.clear();
        }
    }

    boolean isAvailable(Player player, boolean group) {
        if(group) {
            if(!firstTeam.isEmpty() && !secondTeam.isEmpty()) return false;

            int level = getTotalLevel(player.getParty().getPlayers());
            int level2 = getTotalLevel(firstTeam.isEmpty() ? secondTeam : firstTeam);
            if(level > level2) {
                if((level - level2) > MINIMUM_LEVEL) {
                    return false;
                }
            } else {
                if((level2 - level) > MINIMUM_LEVEL) {
                    return false;
                }
            }
            return firstTeam.isEmpty() || secondTeam.isEmpty();
        } else {
            if(((firstTeam.size() + secondTeam.size()) == PER_TEAM * 2))
                return false;
            for(Player other : getAllPlayers()) {
                if(other.getLevel() > player.getLevel() && (other.getLevel() - player.getLevel()) > MINIMUM_LEVEL)
                    return false;
                else if((player.getLevel() - other.getLevel()) > MINIMUM_LEVEL)
                    return false;
            }
        }
        return !((firstTeam.size() + secondTeam.size()) == PER_TEAM * 2);
    }

    public void verify() {
        if ((this.firstTeam.size() + this.secondTeam.size()) == PER_TEAM * 2) {
            start();
        }
    }

    public List<Player> getTeam(boolean first) {
        return first ? this.firstTeam : this.secondTeam;
    }

    public List<Player> getAllPlayers() {
        List<Player> all = new ArrayList<>();
        all.addAll(this.firstTeam);
        all.addAll(this.secondTeam);
        return all;
    }

    private void sendToAll(String message) {
        this.getAllPlayers().forEach(player -> player.sendMessage(message));
    }

    private short getTotalLevel(List<Player> players) {
        short average = 0;

        for(Player player : players) {
            average += player.getLevel();
        }

        return average;
    }
}
