package database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.AbstractDAO;
import game.world.World;
import kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExperienceData extends AbstractDAO<World.ExpLevel> {
    public ExperienceData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(World.ExpLevel obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * from experience");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addExpLevel(RS.getInt("lvl"), new World.ExpLevel(RS.getLong("perso"), RS.getInt("metier"), RS.getInt("dinde"), RS.getInt("pvp"), RS.getLong("tourmenteurs"), RS.getLong("bandits")));
            }
        } catch (SQLException e) {
            super.sendError("ExperienceData load", e);
            Main.INSTANCE.stop("unknown");
        } finally {
            close(result);
        }
    }
}
