package database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.AbstractDAO;
import game.world.World;
import object.ObjectSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectSetData extends AbstractDAO<ObjectSet> {
    public ObjectSetData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(ObjectSet obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * from itemsets");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addItemSet(new ObjectSet(RS.getInt("id"), RS.getString("items"), RS.getString("bonus")));
            }
            close(result);
        } catch (SQLException e) {
            super.sendError("ItemsetData load", e);
        } finally {
            close(result);
        }
    }
}
