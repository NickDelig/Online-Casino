import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SafeRandomGenerator {
    private int port;
    private SecureRandom random = new SecureRandom();
    private final String SECRET = "shared_secret_123";

    public SafeRandomGenerator(int port) { this.port = port; }

    public void start() throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("[SRG] Online port: " + port);
        while (true) {
            Socket s = ss.accept();
            new Thread(() -> {
                try (DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
                    int num = random.nextInt(1000000);
                    String hash = computeSHA256(num + SECRET);
                    out.writeInt(num);
                    out.writeUTF(hash);
                } catch (Exception e) { e.printStackTrace(); }
            }).start();
        }
    }

    private String computeSHA256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}