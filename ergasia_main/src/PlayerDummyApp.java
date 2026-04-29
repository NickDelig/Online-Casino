import java.io.*;
import java.net.*;
import java.util.*;

public class PlayerDummyApp {
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n--- PLAYER ---");
            System.out.println("1. Search\n2. Play\n3. Back");
            String choice = scanner.nextLine();
            if (choice.equals("1")) search();
            else if (choice.equals("2")) play();
            else if (choice.equals("3")) break;
        }
    }

    private void search() {
        talk("SEARCH", Map.of("MinStars", 1));
    }

    private void play() {
        System.out.print("Game Name: "); String name = scanner.nextLine();
        talk("PLAY", Map.of("GameName", name, "BetAmount", 1.0));
    }

    private void talk(String cmd, Object data) {
        try (Socket s = new Socket("localhost", 1234);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeObject(cmd); out.writeObject(data);
            System.out.println("Server says: " + in.readObject());
        } catch (Exception e) { e.printStackTrace(); }
    }
}