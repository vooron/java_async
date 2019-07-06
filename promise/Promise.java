package async.promise;

import java.util.LinkedList;

public class Promise<T> {

    private PromiseTask<T> task;
    private LinkedList<ThenHandler<T>> thenHandlerList = new LinkedList<>();
    private CatchHandler catchHandler = null;

    public Promise(PromiseTask<T> task) {
        this.task = task;
    }

    private void execute() {
        new Thread(() -> {

            try {
                T result = task.run();
                while (!thenHandlerList.isEmpty()) {
                    result = thenHandlerList.pop().handle(result);
                }
            } catch (Exception e) {
                if (catchHandler != null) {
                    catchHandler.handle(e);
                }
            }

        }).start();
    }

    public Promise<T> then(ThenHandler<T> thenHandler, CatchHandler catchHandler) {
        if (thenHandler == null) {
            throw new IllegalArgumentException("Runnable can`t be null");
        }

        thenHandlerList.add(thenHandler);
        if (catchHandler != null && this.catchHandler != null) {
            this.catchHandler = catchHandler;
        }

        if (thenHandlerList.size() == 1) {
            execute();
        }

        return this;
    }

    public Promise<T> then(ThenHandler<T> thenHandler) {
        return then(thenHandler, null);
    }

    public Promise<T> then(ThenHandlerWithoutReturn<T> thenHandlerWithoutReturn) {

        ThenHandler<T> thenHandler = (data) -> {
            thenHandlerWithoutReturn.handle(data);
            return null;
        };

        return then(thenHandler, null);
    }

    public Promise<T> catchException(CatchHandler catchHandler) {

        if (catchHandler != null && this.catchHandler != null) {
            this.catchHandler = catchHandler;
        }
        return this;
    }

}
