import java.io.IOException;

public class ProcessMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessBuild.compP();
        ProcessBuild.startProcess("127.0.0.15","5016");
    }
}
