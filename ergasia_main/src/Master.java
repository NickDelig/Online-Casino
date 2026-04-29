import java.io.*;
import java.net.*;
import java.util.*;

public class Master {
    private int port;
    private List<WorkerInfo> workers;

    public Master(int port, List<WorkerInfo> workers) {
        this.port = port;
        this.workers = workers;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[Master] Server ξεκίνησε στη θύρα " + port);
        System.out.println("[Master] Διαθέσιμοι Workers: " + workers.size());

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                // Εξυπηρέτηση Manager ή Player σε νέο Thread
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                System.err.println("[Master] Σφάλμα αποδοχής σύνδεσης: " + e.getMessage());
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                Object input = in.readObject();
                if (!(input instanceof String)) return;

                String requestType = (String) input;
                System.out.println("[Master] Λήψη αιτήματος: " + requestType);

                switch (requestType) {
                    case "ADD_GAME":
                        handleAddGame(in, out);
                        break;
                    case "SEARCH":
                    case "SEARCH_FILTER":
                        handleSearch(in, out);
                        break;
                    case "PLAY":
                        handlePlay(in, out);
                        break;
                    case "STATS_PROVIDER":
                        handleStats(out);
                        break;
                    default:
                        out.writeObject("Error: Unknown Request Type");
                }
            } catch (Exception e) {
                System.err.println("[Master] Σφάλμα Handler: " + e.getMessage());
            } finally {
                try { clientSocket.close(); } catch (IOException e) {}
            }
        }

        private void handleAddGame(ObjectInputStream in, ObjectOutputStream out) throws Exception {
            Map<String, Object> gameData = (Map<String, Object>) in.readObject();
            String gameName = (String) gameData.get("GameName");

            if (gameName == null) {
                out.writeObject("Error: GameName is missing in JSON");
                return;
            }

            // Hashing για επιλογή Worker: Node = H(key) mod N
            int workerIndex = Math.abs(gameName.hashCode()) % workers.size();
            WorkerInfo target = workers.get(workerIndex);

            System.out.println("[Master] Προώθηση " + gameName + " στον Worker: " + target.port);

            // ΠΡΟΣΟΧΗ: Στέλνουμε "STORE_GAME" γιατί αυτό περιμένει ο Worker.java
            Object response = forwardToWorker(target, "STORE_GAME", gameData);
            out.writeObject(response);
        }

        private void handleSearch(ObjectInputStream in, ObjectOutputStream out) throws Exception {
            Object filters = in.readObject();
            List<Map<String, Object>> allResults = new ArrayList<>();

            // Map Phase: Ρωτάμε όλους τους Workers
            for (WorkerInfo w : workers) {
                Object res = forwardToWorker(w, "SEARCH_FILTER", filters);
                if (res instanceof List) {
                    allResults.addAll((List<Map<String, Object>>) res);
                }
            }
            out.writeObject(allResults);
        }

        private void handlePlay(ObjectInputStream in, ObjectOutputStream out) throws Exception {
            Map<String, Object> playData = (Map<String, Object>) in.readObject();
            String gameName = (String) playData.get("GameName");

            int workerIndex = Math.abs(gameName.hashCode()) % workers.size();
            WorkerInfo target = workers.get(workerIndex);

            // ΠΡΟΣΟΧΗ: Στέλνουμε "EXECUTE_PLAY" όπως ορίζει ο Worker.java
            Object result = forwardToWorker(target, "EXECUTE_PLAY", playData);
            out.writeObject(result);
        }

        private void handleStats(ObjectOutputStream out) throws Exception {
            // 1. Δίνουμε εντολή στους Workers να κάνουν MAP τα στατιστικά τους στον Reducer
            for (WorkerInfo w : workers) {
                forwardToWorker(w, "MAP_STATS", null);
            }

            // 2. Ζητάμε το τελικό REDUCE από τον Reducer (θύρα 8888)
            try (Socket s = new Socket("localhost", 8888);
                 ObjectOutputStream redOut = new ObjectOutputStream(s.getOutputStream());
                 ObjectInputStream redIn = new ObjectInputStream(s.getInputStream())) {

                redOut.writeObject("REDUCE_STATS");
                Object stats = redIn.readObject();
                out.writeObject(stats);
            } catch (Exception e) {
                out.writeObject(new HashMap<String, Double>());
                System.err.println("[Master] Reducer offline: " + e.getMessage());
            }
        }

        private Object forwardToWorker(WorkerInfo w, String cmd, Object data) {
            try (Socket s = new Socket(w.host, w.port);
                 ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

                s.setSoTimeout(2000); // Timeout 2 δευτερόλεπτα για να μην κολλάει ο Master
                out.writeObject(cmd);
                if (data != null) out.writeObject(data);
                out.flush();

                return in.readObject();
            } catch (Exception e) {
                return "Worker Error (" + w.port + "): " + e.getMessage();
            }
        }
    }
}