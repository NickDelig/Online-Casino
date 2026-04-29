import java.util.*;

public class RunMaster {
    public static void main(String[] args) throws java.io.IOException {
        List<WorkerInfo> workerList = new ArrayList<>();
        workerList.add(new WorkerInfo("localhost", 4001));
        workerList.add(new WorkerInfo("localhost", 4002));

        new Master(1234, workerList).start();
    }
}