package bz.dcr.geld.logging;

import bz.dcr.geld.Geld;
import org.bukkit.Bukkit;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeldLogger implements Closeable {

    private boolean enabled;
    private File logFile;
    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy | HH:mm");
    private BufferedWriter writer;

    // Constructor
    public GeldLogger(Geld plugin, boolean enabled) {
        this.enabled = enabled;
        if (!enabled)
            return;

        this.logFile = new File("plugins" + File.separatorChar + plugin.getName() + File.separatorChar + "logs" + File.separatorChar + this.getCurrentFilename());

        // Create log file
        if (!this.logFile.exists()) {
            try {
                this.logFile.getParentFile().mkdirs();
                this.logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create writer
        try {
            this.writer = new BufferedWriter(new FileWriter(this.logFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create new file for date
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                writer.flush();
                this.logFile = new File("plugins" + File.separatorChar + plugin.getName() + File.separatorChar + "logs" + File.separatorChar + this.getCurrentFilename());

                if (!this.logFile.exists()) {
                    this.logFile.getParentFile().mkdirs();
                    this.logFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 60000L, 60000L);
    }


    public void log(String message, LogLevel level) {
        if (!this.enabled)
            return;

        try {
            this.writer.write(
                    this.getCurrentTimestamp()
                            + " ["
                            + level.getName()
                            + "] "
                            + message
            );
            this.writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getCurrentTimestamp() {
        return this.timeFormat.format(new Date(System.currentTimeMillis()));
    }

    private String getCurrentFilename() {
        return "log_" + this.dateFormat.format(new Date(System.currentTimeMillis())) + ".log";
    }

    @Override
    public void close() throws IOException {
        if (this.writer != null)
            this.writer.close();
    }

    // Log level enum
    public enum LogLevel {
        TRANSFER("TRANSAKTION"),
        CRITICAL_TRANSFER("KRITISCHE TRANSAKTION"),
        GIVE_NOOBFILTER("GIVE - NOOBFILTER");


        private String name;

        // Constructor
        LogLevel(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
