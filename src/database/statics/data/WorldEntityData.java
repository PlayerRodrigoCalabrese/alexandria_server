package database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.statics.AbstractDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Locos on 15/09/2015.
 */
public class WorldEntityData extends AbstractDAO<Object> {

    private int nextMountId, nextObjectId, nextQuestId, nextGuildId, nextPetId;

    public WorldEntityData(HikariDataSource dataSource) {
        super(dataSource);
        this.load(null);
    }

    @Override
    public void load(Object obj) {
        Result result = null;
        try {
            result = getData("SELECT MIN(id) AS min FROM `world.entity.mounts`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextMountId = RS.getInt("min");
            else this.nextMountId = -1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
        try {
            result = getData("SELECT MAX(id) AS max FROM `world.entity.objects`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextObjectId = RS.getInt("max");
            else this.nextObjectId = 1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
        try {
            result = getData("SELECT MAX(id) AS max FROM `world.entity.players.quests`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextQuestId = RS.getInt("max");
            else this.nextQuestId = 1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
        try {
            result = getData("SELECT MAX(id) AS max FROM `world.entity.guilds`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextGuildId = RS.getInt("max");
            else this.nextGuildId = 1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
        try {
            result = getData("SELECT MAX(id) AS max FROM `world.entity.pets`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextPetId = RS.getInt("max");
            else this.nextPetId = 1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public synchronized int getNextMountId() {
        return --nextMountId;
    }

    public synchronized int getNextObjectId() {
        return ++nextObjectId;
    }

    public synchronized int getNextQuestPlayerId() {
        return ++nextQuestId;
    }

    public synchronized int getNextGuildId() {
        return ++nextGuildId;
    }

    public synchronized int getNextPetId() {
        return ++nextPetId;
    }
}
