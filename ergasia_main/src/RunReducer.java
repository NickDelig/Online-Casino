public class RunReducer {
    public static void main(String[] args) {
        try {
            // Ο Reducer στην 8888 για τα στατιστικά του MapReduce
            new Reducer(8888).start();
        } catch (java.io.IOException e) {
            System.err.println("Σφάλμα Reducer: " + e.getMessage());
        }
    }
}