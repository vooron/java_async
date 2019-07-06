package async.promise;

public class ThreadInterruptedException extends RuntimeException {

    private static final long serialVersionUID = -1363291294668044172L;

    public ThreadInterruptedException(String description) {
        super(description);
    }
}
