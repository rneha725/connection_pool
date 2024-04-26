import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HTTPConnectionPool {
    private static final URL url;

    static {
        try {
            url = new URI("https://api.github.com/users/rneha725").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            System.err.println("Error while creating the url");
            throw new RuntimeException(e);
        }
    }

    private final ConcurrentLinkedQueue<URLConnection> connections = new ConcurrentLinkedQueue<>();
    Map<Integer, List<String>> assignments = new HashMap<>();

    public HTTPConnectionPool(int poolSize) throws IOException {
        int nConnections = poolSize;
        while (nConnections-- != 0) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            assignments.put(httpsURLConnection.hashCode(), new ArrayList<>());
            System.out.printf("Created connection: %s\n", httpsURLConnection.hashCode());
            this.connections.add(httpsURLConnection);
        }
    }

    public synchronized URLConnection getConnection(String user) throws InterruptedException {
        while (connections.isEmpty()) {
            System.out.printf("User: %s will wait for the connection.\n", user);
            wait();
        }
        URLConnection connection = connections.poll();
        assignments.get(connection.hashCode()).add(user);
        System.out.printf("Got connection for user: %s, connection: %s\n", user, connection.hashCode());
        return connection;
    }

    public synchronized void returnConnection(URLConnection connection, String user) {
        if (connection == null) return;
        connections.add(connection);
        System.out.printf("User: %s release a connection.\n", user);
        notify();
    }

    public void printAssignments() {
        for (Map.Entry<Integer, List<String>> assignmentList : this.assignments.entrySet()) {
            System.out.printf("\nConnection id: %d\n", assignmentList.getKey());
            for (String user : assignmentList.getValue()) {
                System.out.printf("%s\n", user);
            }
        }
    }
}