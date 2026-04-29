public class RunSRG {
    public static void main(String[] args) throws java.io.IOException {
        // Ο SRG πρέπει να τρέχει στην 9999 για να τον βρίσκουν οι Workers
        new SafeRandomGenerator(9999).start();
    }
}