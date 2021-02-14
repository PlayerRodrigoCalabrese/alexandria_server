package fight.arena;

import area.map.GameMap;
import client.Player;
import common.Formulas;
import game.world.World;
import object.GameObject;

import java.util.concurrent.TimeUnit;

public class DeathMatch {

	public Player first, second;
	public GameObject winObject;

	DeathMatch(Player first) {
		FightManager.addDeathMatch(this);
		this.first = first;
		this.first.sendMessage("Vous venez de vous inscrire à un <b>match à mort</b>.");
		this.first.deathMatch = this;
	}

	public void start() {
		if (first.getFight() != null) {
			this.first.sendMessage("Vous êtes en combat, match à mort impossible.");
			this.second.sendMessage("Le joueur <b>" + this.first.getName() + "</b> est en combat, il vient d'être désincrit.");
			this.first = this.second;
			this.second = null;
			return;
		} else if (second.getFight() != null) {
			this.second.sendMessage("Vous êtes en combat, match à mort impossible.");
			this.first.sendMessage("Le joueur <b>" + this.first.getName() + "</b> est en combat, il vient d'être désincrit.");
			this.second = null;
			return;
		}

		this.first.sendMessage("Lancement du match à mort en cours.");
		this.second.sendMessage("Lancement du match à mort en cours.");
		this.first.setOldPosition();
		this.second.setOldPosition();


		final GameMap map = World.world.getMap(FightManager.maps[Formulas.random.nextInt(FightManager.maps.length)]);

		FightManager.scheduler.schedule(() -> {
			first.teleport(map, 0);
			second.teleport(map, 0);
		}, 1, TimeUnit.SECONDS);

		FightManager.scheduler.schedule((Runnable) () -> map.newDeathmatch(first, second, DeathMatch.this), 4, TimeUnit.SECONDS);
	}

	public void finish(Player winner) {
		Player looser = winner.getId() == first.getId() ? second : first;
		GameObject winObject = this.getRandomObject(looser);
		this.winObject = winObject;
		looser.removeItem(winObject.getGuid(), 1, true, false);
		winObject.setPosition(-1);
		winner.addObjet(winObject, true);
		winner.sendMessage("Vous venez de gagner l'objet : <b>" + winObject.getTemplate().getName() + "</b>.");
		looser.sendMessage("Vous venez de perdre l'objet : <b>" + winObject.getTemplate().getName() + "</b>.");

		looser.deathMatch = null;
		winner.deathMatch = null;
		FightManager.removeDeathMatch(this);
	}

	void subscribe(Player player) {
		this.second = player;
		this.second.deathMatch = this;
		this.second.sendMessage("Vous venez de rentrer dans le match à mort de <b>" + this.first.getName() + "</b>.");
		this.first.sendMessage("Le joueur <b>" + this.second.getName() + "</b> vient de rentrer dans votre match à mort.");
		this.start();
	}

	boolean isAvailable(Player player) {
		if (this.first == null) {
			FightManager.removeDeathMatch(this);
			return false;
		}
		if (this.second == null) {
			if (this.first.getLevel() > player.getLevel()) {
				return (this.first.getLevel() - player.getLevel()) < 15;
			} else {
				return (player.getLevel() - this.first.getLevel()) < 15;
			}
		}
		return false;
	}

	private GameObject getRandomObject(Player player) {
		return player.getEquippedObjects().get(Formulas.random.nextInt(player.getEquippedObjects().size()));
	}
}
