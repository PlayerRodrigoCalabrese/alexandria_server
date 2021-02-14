package database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import area.map.entity.Trunk;
import database.Database;
import database.statics.AbstractDAO;
import game.world.World;
import kernel.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrunkData extends AbstractDAO<Trunk> {

    public TrunkData(HikariDataSource dataSource)
	{
		super(dataSource);
	}

	@Override
	public void load(Object obj)
	{
    }

	@Override
	public boolean update(Trunk t)
	{
		return false;
	}

	public int load() {
		Result result = null;
		int nbr = 0;
		try {
			result = getData("SELECT * from coffres");
			ResultSet RS = result.resultSet;
			while (RS.next()) {
                Trunk trunk = new Trunk(RS.getInt("id"), RS.getInt("id_house"), RS.getShort("mapid"), RS.getInt("cellid"));
				World.world.addTrunk(trunk);
                Database.getDynamics().getTrunkData().exist(trunk);
				nbr++;
			}
		} catch (SQLException e) {
			super.sendError("CoffreData load", e);
		} finally {
			close(result);
		}
		return nbr;
	}

    public void insert(Trunk trunk) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `coffres` (`id`, `id_house`, `mapid`, `cellid`) " +
                    "VALUES (?, ?, ?, ?)");
            p.setInt(1, trunk.getId());
            p.setInt(2, trunk.getHouseId());
            p.setInt(3, trunk.getMapId());
            p.setInt(4, trunk.getCellId());
            execute(p);

            Database.getDynamics().getTrunkData().insert(trunk);
        } catch (SQLException e) {
            super.sendError("Coffre insert", e);
        } finally {
            close(p);
        }
    }

    public int getNextId() {
        Result result = null;
        int guid = -1;
        try {
            result = getData("SELECT MAX(id) AS max FROM `coffres`");
            ResultSet RS = result.resultSet;

            boolean found = RS.first();

            if (found)
                guid = RS.getInt("max") + 1;
        } catch (SQLException e) {
            super.sendError("CoffreData getNextId", e);
            Main.INSTANCE.stop("unknown");
        } finally {
            close(result);
        }
        return guid;
    }
}
