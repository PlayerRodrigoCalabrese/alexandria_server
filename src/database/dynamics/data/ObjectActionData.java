package database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.AbstractDAO;
import game.world.World;
import kernel.Main;
import object.ObjectAction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectActionData extends AbstractDAO<ObjectAction> {
    public ObjectActionData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(ObjectAction obj) {
        return false;
    }

    public int load() {
        Result result = null;
        int nbr = 0;
        try {
            result = getData("SELECT * FROM objectsactions");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                int id = RS.getInt("template");
                String type = RS.getString("type");
                String args = RS.getString("args");
                if (World.world.getObjTemplate(id) == null)
                    continue;
                World.world.getObjTemplate(id).addAction(new ObjectAction(type, args, ""));
                nbr++;
            }
        } catch (SQLException e) {
            super.sendError("ObjectsactionData load", e);
            Main.INSTANCE.stop("unknown");
        } finally {
            close(result);
        }
        return nbr;
    }

    public int reload() {
        Result result = null;
        int nbr = 0;
        try {
            result = getData("SELECT * FROM objectsactions");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                int id = RS.getInt("template");
                String type = RS.getString("type");
                String args = RS.getString("args");
                if (World.world.getObjTemplate(id) == null)
                    continue;
                World.world.getObjTemplate(id).getOnUseActions().clear();
                World.world.getObjTemplate(id).addAction(new ObjectAction(type, args, ""));
                nbr++;
            }
            close(result);
        } catch (SQLException e) {
            super.sendError("ObjectsactionData reload", e);
        } finally {
            close(result);
        }
        return nbr;
    }
}
