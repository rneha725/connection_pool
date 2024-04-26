import java.io.IOException;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        HTTPConnectionPool pool = new HTTPConnectionPool(2);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    String threadId = Thread.currentThread().getName();
                    URLConnection connection = pool.getConnection(threadId);
                    TimeUnit.MILLISECONDS.sleep(1);
                    pool.returnConnection(connection, threadId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Executor interrupted during shutdown.");
        }
        pool.printAssignments();
    }
}
