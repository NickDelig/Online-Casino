import java.io.*;
import java.net.*;
import java.util.*;

public class ManagerConsoleApp {
    private static final String MASTER_HOST = "localhost";
    private static final int MASTER_PORT = 1234;
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- MANAGER CONSOLE ---");
            System.out.println("1. Προσθήκη Παιχνιδιού (ADD)");
            System.out.println("2. Στατιστικά ανά Πάροχο (STATS)");
            System.out.println("3. Έξοδος");
            System.out.print("Επιλογή: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": addGame(); break;
                case "2": getProviderStats(); break;
                case "3": running = false; break;
            }
        }
    }

    private void addGame() {
        System.out.print("Path αρχείου (π.χ. game.jason): ");
        String path = scanner.nextLine();
        Map<String, Object> data = parseJson(path); // Διόρθωση ονόματος μεθόδου
        if (data != null) {
            sendToMaster("ADD_GAME", data);
        }
    }

    // Η μέθοδος που έλειπε ή είχε λάθος όνομα
    private Map<String, Object> parseJson(String path) {
        Map<String, Object> data = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("{","").replace("}","").replace("\"","").replace(",","").trim();
                String[] parts = line.split(":");
                if (parts.length < 2) continue;
                String k = parts[0].trim(), v = parts[1].trim();
                if (k.equals("Stars")) data.put(k, Integer.parseInt(v));
                else if (k.equals("MinBet")) data.put(k, Double.parseDouble(v));
                else data.put(k, v);
            }
            return data;
        } catch (Exception e) {
            System.out.println("Σφάλμα ανάγνωσης JSON: " + e.getMessage());
            return null;
        }
    }

    private void sendToMaster(String cmd, Object data) {
        try (Socket s = new Socket(MASTER_HOST, MASTER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeObject(cmd);
            out.writeObject(data);
            System.out.println("Απάντηση Master: " + in.readObject());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void getProviderStats() {
        sendToMaster("STATS_PROVIDER", new HashMap<>());
    }
}