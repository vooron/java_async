package async.promise;

public interface ThenHandlerWithoutReturn<T> {
    void handle(T result) throws Exception;
}