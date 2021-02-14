package database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import client.Account;
import database.statics.AbstractDAO;
import event.EventReward;
import event.type.Event;
import event.type.EventFindMe;
import event.type.EventSmiley;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Locos on 02/10/2016.
 */
public class EventData extends AbstractDAO<Account> {

    public EventData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {

    }

    @Override
    public boolean update(Account obj) {
        return false;
    }

    public Event[] load() {
        Result result = null;
        Event[] events = new Event[this.getNumberOfEvent()];

        try {
            result = getData("SELECT * FROM `world.event.type`;");

            if (result != null) {
                ResultSet RS = result.resultSet;
                byte i = 0;

                while (RS.next()) {
                    Event event = this.getEventById(RS.getByte("id"), RS);

                    if (event != null) {
                        events[i] = event;
                        i++;
                    }
                }
            }
        } catch(SQLException e) {
            super.sendError("EventData load", e);
        } finally {
            close(result);
        }
        return events;
    }

    private byte getNumberOfEvent() {
        Result result = null;
        byte numbers = 0;

        try {
            result = getData("SELECT COUNT(id) AS numbers FROM `world.event.type`;");

            if (result != null) {
                ResultSet RS = result.resultSet;
                RS.next();
                numbers = RS.getByte("numbers");
            }
        } catch(SQLException e) {
            super.sendError("EventData getNumberOfEvent", e);
        } finally {
            close(result);
        }
        return numbers;
    }

    private byte loadFindMeRow() {
        Result result = null;
        byte numbers = 0;

        try {
            result = getData("SELECT COUNT(id) AS numbers FROM `world.event.findme`;");

            if (result != null) {
                ResultSet RS = result.resultSet;
                while(RS.next()) {
                    EventFindMe.FindMeRow row = new EventFindMe.FindMeRow(RS.getShort("map"), RS.getShort("cell"), RS.getString("indices").split("\\|"));
                }
                numbers = RS.getByte("numbers");
            }
        } catch(SQLException e) {
            super.sendError("EventData getNumberOfEvent", e);
        } finally {
            close(result);
        }
        return numbers;
    }

    private Event getEventById(byte id, ResultSet result) throws SQLException {
        switch(id) {
            case 1:
                return new EventSmiley(id, result.getByte("maxPlayers"), result.getString("name"), result.getString("description"), EventReward.parse(result.getString("firstWinner")));
            default:
                return null;
        }
    }
}
