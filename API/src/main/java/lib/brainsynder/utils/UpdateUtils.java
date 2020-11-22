package lib.brainsynder.utils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import lib.brainsynder.web.WebConnector;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class UpdateUtils {
    private static Properties properties;
    private static Plugin plugin;
    private static BukkitRunnable updateTask;

    public static void initiate (Plugin plugin) {
        UpdateUtils.plugin = plugin;


        Properties prop = null;
        try {
            prop = new Properties();
            prop.load(plugin.getClass().getResourceAsStream("/jenkins.properties"));
        } catch (IOException ignored) {} // If it fails, it means there is no 'jenkins.properties' file

        properties = prop;
    }


    public static void startUpdateTask (UpdateResult result, long ticks, TimeUnit unit) {
        if (updateTask == null) updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkUpdate(result);
            }
        };

        updateTask.runTaskTimer(plugin, 0, Utilities.toUnit(ticks, unit));
    }

    /**
     * Will cancel the update task and remove the instance
     */
    public static void stopTask () {
        if (updateTask == null) return;
        updateTask.cancel();
        updateTask = null;
    }


    /**
     * Will check for an update once without re-checking
     */
    public static void checkUpdate(UpdateResult result) {
        if (properties == null) return;
        int build = Integer.parseInt(properties.getProperty("buildnumber"));
        WebConnector.getInputStreamString("http://pluginwiki.us/version/?repo=" + properties.getProperty("repo"), plugin, string -> {
            JsonObject main = (JsonObject) Json.parse(string);
            if (!main.isEmpty()) {
                if (main.names().contains("error")) {
                    return;
                }

                int latestBuild = main.getInt("build", -1);

                // New build found
                if (latestBuild > build) {

                } else {

                }
            }
        });
    }

    public class UpdateResult {
        private final ReturnValue<JsonObject> newBuild;
        private final Runnable onError;
        private final Runnable noNewBuilds;

        public UpdateResult (ReturnValue<JsonObject> newBuild) {
            this (newBuild, () -> {});
        }

        public UpdateResult (ReturnValue<JsonObject> newBuild, Runnable noNewBuilds) {
            this (newBuild, () -> {}, () -> {});
        }

        public UpdateResult (ReturnValue<JsonObject> newBuild, Runnable noNewBuilds, Runnable onError) {
            this.newBuild = newBuild;
            this.noNewBuilds = noNewBuilds;
            this.onError = onError;
        }

        public ReturnValue<JsonObject> getNewBuild() {
            return newBuild;
        }

        public Runnable getNoNewBuilds() {
            return noNewBuilds;
        }

        public Runnable getOnError() {
            return onError;
        }
    }
}
