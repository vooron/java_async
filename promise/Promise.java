package async.promise;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Promise<T> {

    private PromiseTask<T> task;
    private LinkedList<ThenHandler<T>> thenHandlerList = new LinkedList<>();
    private CatchHandler catchHandler = null;

    private T result;

    private boolean isTerminateOperatorPresent = false;
    private boolean isStarted = false;
    private boolean isFinished = false;

    public Promise(PromiseTask<T> task) {
        this.task = task;
    }

    private void execute() {

        new Thread(() -> {
            try {
                result = task.run();

                synchronized (this) {

                    while (!isTerminateOperatorPresent && !thenHandlerList.isEmpty()) {

                        result = thenHandlerList.pop().handle(result);

                        while (thenHandlerList.isEmpty()) {
                            wait();
                        }
                    }

                }
            } catch (Exception e) {
                if (catchHandler != null) {
                    catchHandler.handle(e);
                }
            }

            isFinished = true;

        }).start();
    }

    public synchronized Promise<T> then(ThenHandler<T> thenHandler) {

        if (isTerminateOperatorPresent) {
            throw new ThreadInterruptedException("Thread was alredy interrupted");
        }

        if (thenHandler == null) {
            throw new IllegalArgumentException("Runnable can`t be null");
        }

        thenHandlerList.add(thenHandler);

        if (!isStarted) {
            execute();
            isStarted = true;
        }

        notifyAll();

        return this;
    }

    public Promise<T> then(ThenHandlerWithoutReturn<T> thenHandlerWithoutReturn) {

        ThenHandler<T> thenHandler = (data) -> {
            thenHandlerWithoutReturn.handle(data);
            return null;
        };

        return then(thenHandler);
    }

    public void catchException(CatchHandler catchHandler) {
        this.catchHandler = catchHandler;
        this.isTerminateOperatorPresent = true;

        // synchronized (this) {
        // notify();
        // }
    }

}
