import java.io.*;
import java.net.*;
import java.util.*;

public class Reducer {
    private int port;
    private final Map<String, List<Double>> intermediateData = new HashMap<>();

    public Reducer(int port) { this.port = port; }

    public void start() throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("[Reducer] Online port: " + port);
        while (true) {
            Socket s = ss.accept();
            new Thread(() -> handle(s)).start();
        }
    }

    private void handle(Socket s) {
        try (ObjectInputStream in = new ObjectInputStream(s.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())) {
            String cmd = (String) in.readObject();
            if ("EMIT".equals(cmd)) {
                String key = (String) in.readObject();
                Double val = (Double) in.readObject();
                synchronized (intermediateData) {
                    intermediateData.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
                }
                out.writeObject("ACK");
            } else if ("REDUCE_STATS".equals(cmd)) {
                Map<String, Double> results = new HashMap<>();
                synchronized (intermediateData) {
                    intermediateData.forEach((k, v) -> results.put(k, v.stream().mapToDouble(d -> d).sum()));
                    intermediateData.clear();
                }
                out.writeObject(results);
            }
        } catch (Exception e) {}
    }
}