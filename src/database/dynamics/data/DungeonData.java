package database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.AbstractDAO;
import game.world.World.Couple;
import kernel.Main;
import other.Dopeul;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DungeonData extends AbstractDAO<Object> {
    public DungeonData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM donjons");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                Dopeul.getDonjons().put(RS.getInt("map"), new Couple<>(RS.getInt("npc"), RS.getInt("key")));
            }
        } catch (SQLException e) {
            super.sendError("DonjonData load", e);
            Main.INSTANCE.stop("unknown");
        } finally {
            close(result);
        }
    }

    public String get_all_keys() {
        Result result = null;
        try {
            result = getData("SELECT key FROM donjons");
            ResultSet RS = result.resultSet;
            String keys = "";
            while (RS.next()) {
                String key = Integer.toHexString(RS.getInt("key"));
                keys += (keys.isEmpty() ? key : "," + key);
            }
            return keys;
        } catch (SQLException e) {
            super.sendError("DonjonData get_all_keys", e);
        } finally {
            close(result);
        }
        return "";
    }
}
