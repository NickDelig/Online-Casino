public class RunWorkers {
    public static void main(String[] args) {
        new Thread(() -> {
            try { new Worker(4001).start(); } catch (Exception e) { e.printStackTrace(); }
        }).start();

        new Thread(() -> {
            try { new Worker(4002).start(); } catch (Exception e) { e.printStackTrace(); }
        }).start();

        System.out.println("Workers online (Ports: 4001, 4002)");
    }
}