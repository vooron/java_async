package async.promise;

public interface ThenHandler<T> {
    T handle(T result) throws Exception;
}
