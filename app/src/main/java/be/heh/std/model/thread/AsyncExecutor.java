package be.heh.std.model.thread;

import java.util.concurrent.Executor;

public class AsyncExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
