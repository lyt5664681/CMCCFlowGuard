package com.primeton.cmcc.cmccflowguard.handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogFileWatcher {

    private final Path logFilePath;
    private final ExecutorService executorService;
    private WatchService watchService;
    private final StringBuilder logTail;

    public LogFileWatcher(Path logFilePath) {
        this.logFilePath = logFilePath;
        this.executorService = Executors.newSingleThreadExecutor();
        this.logTail = new StringBuilder();
    }

    public void start() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
//        Paths.get(logFilePath)
        this.logFilePath.getParent().register(this.watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        executorService.execute(this::watchLogChanges);
    }

    private void watchLogChanges() {
        try (RandomAccessFile raf = new RandomAccessFile(logFilePath.toFile(), "r");
             FileChannel channel = raf.getChannel()) {
            long pointer = raf.length();

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();
                    if (fileName.equals(this.logFilePath.getFileName())) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(Channels.newInputStream(channel)));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line += "\n";
                            logTail.append(line);
                        }
                        pointer = channel.position();
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLogTail() {
        return logTail.toString();
    }
}

