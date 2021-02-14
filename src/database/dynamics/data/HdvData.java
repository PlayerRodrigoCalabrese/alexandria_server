package database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.AbstractDAO;
import game.world.World;
import hdv.Hdv;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HdvData extends AbstractDAO<Hdv> {
    public HdvData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Hdv obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM `hdvs` ORDER BY id ASC");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addHdv(new Hdv(RS.getInt("map"), RS.getFloat("sellTaxe"), RS.getShort("sellTime"), RS.getShort("accountItem"), RS.getShort("lvlMax"), RS.getString("categories")));
            }
            close(result);
        } catch (SQLException e) {
            super.sendError("HdvData load", e);
        } finally {
            close(result);
        }
    }
}
