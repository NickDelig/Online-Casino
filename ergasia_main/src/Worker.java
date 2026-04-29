import java.io.*;
import java.net.*;
import java.util.*;

public class Worker {
    private final Map<String, Game> games = new HashMap<>();
    private final int port;

    public Worker(int port) { this.port = port; }

    public void start() throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Worker " + port + " online.");
        while (true) {
            Socket s = ss.accept();
            new Thread(() -> handle(s)).start();
        }
    }

    private void handle(Socket s) {
        try (ObjectInputStream in = new ObjectInputStream(s.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())) {
            String cmd = (String) in.readObject();
            Object data = in.readObject();

            if (cmd.equals("STORE_GAME")) {
                Game g = new Game((Map<String, Object>) data);
                games.put(g.name, g); // Χρήση g.name αντί για gameName
                out.writeObject("OK από " + port);
            } else if (cmd.equals("SEARCH_FILTER")) {
                out.writeObject(filter((Map<String, Object>) data));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private List<Map<String, Object>> filter(Map<String, Object> f) {
        List<Map<String, Object>> res = new ArrayList<>();
        for (Game g : games.values()) {
            // Υλοποίηση φιλτραρίσματος βάσει αστεριών [cite: 16, 17]
            if (f.containsKey("MinStars") && g.stars < (int)f.get("MinStars")) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("GameName", g.name);
            m.put("Stars", g.stars);
            res.add(m);
        }
        return res;
    }

    // Η εσωτερική κλάση Game με διορθωμένα πεδία
    class Game implements Serializable {
        String name; // Πρέπει να είναι name (όχι gameName αν το καλείς έτσι)
        String provider;
        String riskLevel;
        int stars;
        double profit = 0;
        Queue<Integer> buffer = new LinkedList<>();

        Game(Map<String, Object> d) {
            this.name = (String) d.get("GameName");
            this.provider = (String) d.get("ProviderName");
            this.riskLevel = (String) d.get("RiskLevel");
            this.stars = d.get("Stars") != null ? (int) d.get("Stars") : 0;
            new Thread(new RandomFill(this)).start();
        }
    }

    // Διόρθωση του Producer για τον buffer τυχαίων αριθμών [cite: 81]
    class RandomFill implements Runnable {
        Game g;
        RandomFill(Game g) { this.g = g; }
        public void run() {
            while (true) {
                try (Socket s = new Socket("localhost", 9999);
                     DataInputStream in = new DataInputStream(s.getInputStream())) {
                    int num = in.readInt();
                    synchronized(g) {
                        if (g.buffer.size() < 50) {
                            g.buffer.add(num);
                            g.notifyAll();
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    try { Thread.sleep(2000); } catch (InterruptedException ex) {}
                }
            }
        }
    }
}