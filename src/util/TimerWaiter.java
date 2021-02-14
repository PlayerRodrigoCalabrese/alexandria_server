package util;

import game.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerWaiter {

    private static int numberOfThread = 15 + 1;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numberOfThread);

    public static void update() {
        numberOfThread = World.world.getNumberOfThread() + 20;
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(numberOfThread);
    }

    public static void addNext(Runnable run, long time, TimeUnit unit, DataType scheduler) {
        TimerWaiter.scheduler.schedule(run, time, unit);
    }

    public static void addNext(Runnable run, long time, DataType scheduler) {
        TimerWaiter.addNext(run, time, TimeUnit.MILLISECONDS, scheduler);
    }

    public enum DataType {
        MAP,
        CLIENT,
        FIGHT
    }
}