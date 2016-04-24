package org.f0w.k2i.gui;

import com.typesafe.config.Config;
import org.f0w.k2i.core.Client;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class ClientExecutor {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Client client;
    private ExecutorService executor;

    public void init(Path filePath, Config config, boolean cleanRun, List<?> listeners) {
        client = new Client(filePath, config, cleanRun, listeners);
    }

    public void run() {
        if (running.get()) {
            return;
        }

        running.set(true);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(client);
        executor.shutdown();
    }

    public boolean isRunning() {
        return running.get();
    }

    public void terminate() {
        if (!running.get() || executor == null) {
            return;
        }

        executor.shutdownNow();
        running.set(false);
    }
}