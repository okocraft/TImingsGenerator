package net.okocraft.timingsgenerator;

import co.aikar.timings.Timings;
import com.github.siroshun09.sirolibrary.SiroExecutors;
import com.github.siroshun09.sirolibrary.config.BukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TimingsGenerator extends JavaPlugin {
    private static TimingsGenerator INSTANCE;

    private BukkitConfig config;
    private boolean isPaper;

    public TimingsGenerator() {
        INSTANCE = this;
        try {
            Class.forName("co.aikar.timings.Timing");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            isPaper = false;
        }
    }

    public static TimingsGenerator get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (!isPaper) {
            getLogger().severe("This server is not running on Paper!");
            getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        super.onEnable();
        config = new BukkitConfig(this, "config.yml", true);

        if (isEnabledAutoDelete()) {
            SiroExecutors.getExecutor().submit(new LogFileCheckingTask());
        }

        Bukkit.getScheduler().runTaskTimer(this,
                () -> Timings.generateReport(TimingsGeneratorListener.get()), getInterval(), getInterval());
    }

    @Override
    public void onDisable() {
        if (isPaper) {
            super.onDisable();
            Bukkit.getScheduler().cancelTasks(this);
        }
    }

    private long getInterval() {
        return config.getLong("interval", 3) * 3600 * 20;
    }

    private boolean isEnabledAutoDelete() {
        return config.getBoolean("auto-delete", true);
    }
}
