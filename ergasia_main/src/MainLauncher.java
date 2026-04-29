import java.util.Scanner;

public class MainLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) { // Επαναλαμβανόμενο κεντρικό μενού
            System.out.println("\n=== ΚΕΝΤΡΙΚΟ ΣΥΣΤΗΜΑ CASINO ===");
            System.out.println("1. Λειτουργία MANAGER");
            System.out.println("2. Λειτουργία PLAYER");
            System.out.println("3. ΤΕΡΜΑΤΙΣΜΟΣ ΕΦΑΡΜΟΓΗΣ");
            System.out.print("Επιλογή: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    new ManagerConsoleApp().start(); // Το start() έχει δικό του while εσωτερικά
                    break;
                case "2":
                    new PlayerDummyApp().start(); // Το start() έχει δικό του while εσωτερικά
                    break;
                case "3":
                    running = false;
                    System.out.println("Έξοδος...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Λάθος επιλογή.");
            }
        }
    }
}
