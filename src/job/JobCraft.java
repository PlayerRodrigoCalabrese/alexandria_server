package job;

import client.Player;
import common.SocketManager;
import util.TimerWaiter;

import java.util.concurrent.TimeUnit;

public class JobCraft {

    private JobAction jobAction;
    private int time = 0;
    private boolean itsOk = true;

    JobCraft(JobAction jobAction, Player player) {
        this.jobAction = jobAction;

        TimerWaiter.addNext(() -> {
            if (itsOk) jobAction.craft(false);
        }, 1200, TimeUnit.MILLISECONDS, TimerWaiter.DataType.MAP);
        TimerWaiter.addNext(() -> {
            if (!itsOk) repeat(time, time, player);
        }, 1200, TimeUnit.MILLISECONDS, TimerWaiter.DataType.MAP);
    }

    public JobAction getJobAction() {
        return jobAction;
    }

    public void setAction(int time) {
        this.time = time;
        this.jobAction.broken = false;
        this.itsOk = false;
    }

    private void repeat(final int time1, final int time2, final Player player) {
        final int j = time1 - time2;
        this.jobAction.player = player;
        this.jobAction.isRepeat = true;

        if (!this.check(player, j, time2) || time2 <= 0) {
            this.end();
        } else {
            TimerWaiter.addNext(() -> this.repeat(time1, (time2 - 1), player), 1200, TimeUnit.MILLISECONDS, TimerWaiter.DataType.MAP);
        }
    }

    private boolean check(final Player player, int j, int time2) {
        if (this.jobAction.broke || this.jobAction.broken || player.getExchangeAction() == null || !player.isOnline()) {
            if (player.getExchangeAction() == null)
                this.jobAction.broken = true;
            if (player.isOnline())
                SocketManager.GAME_SEND_Ea_PACKET(this.jobAction.player, this.jobAction.broken ? "2" : "4");
            return false;
        } else {
            SocketManager.GAME_SEND_EA_PACKET(this.jobAction.player, String.valueOf(time2));
            this.jobAction.craft(this.jobAction.isRepeat);
            this.jobAction.ingredients.clear();
            this.jobAction.ingredients.putAll(this.jobAction.lastCraft);
            return true;
        }
    }

    private void end() {
        SocketManager.GAME_SEND_Ea_PACKET(this.jobAction.player, "1");
        this.jobAction.isRepeat = false;
        this.jobAction.setJobCraft(null);
    }
}