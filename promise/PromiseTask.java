package async.promise;

public interface PromiseTask<T> {
    T run() throws Exception;
}