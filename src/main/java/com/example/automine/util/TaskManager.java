package com.example.automine.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TaskManager {
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Map<String, Future<?>> tasks = new HashMap<>();
    private static int taskIdCounter = 0;

    public static String submitTask(Runnable task) {
        String taskId = "task_" + (taskIdCounter++);
        Future<?> future = executor.submit(task);
        tasks.put(taskId, future);
        return taskId;
    }

    public static String submitDelayedTask(Runnable task, long delayMillis) {
        String taskId = "task_" + (taskIdCounter++);
        Future<?> future = executor.submit(() -> {
            try {
                Thread.sleep(delayMillis);
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        tasks.put(taskId, future);
        return taskId;
    }

    public static boolean cancelTask(String taskId) {
        Future<?> future = tasks.get(taskId);
        if (future != null && !future.isDone()) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                tasks.remove(taskId);
                ModLogger.info("Task " + taskId + " cancelled.");
            }
            return cancelled;
        }
        return false;
    }

    public static void cancelAllTasks() {
        for (Map.Entry<String, Future<?>> entry : tasks.entrySet()) {
            Future<?> future = entry.getValue();
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
        }
        int count = tasks.size();
        tasks.clear();
        ModLogger.info("Cancelled " + count + " tasks.");
    }

    public static void shutdown() {
        cancelAllTasks();
        try {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static int getActiveTaskCount() {
        return tasks.size();
    }
}
