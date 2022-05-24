import java.io.File;
import java.io.IOException;

public class ProcessBuild {

    public static void compP() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("javac","D:\\ProjL\\src\\main\\java\\NodeMain.java");
        builder.inheritIO();
        Process process = builder.start();
        if( process.getErrorStream().read() != -1 ){
            System.out.println("Compilation Errors"+process.getErrorStream());
        }
        // wait 10 seconds
        System.out.println("Waiting");
        Thread.sleep(5000);
    }

    public static void startProcess(String localAddress, String localPort) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.inheritIO();
        processBuilder.command("java","NodeMain",localAddress, localPort);
        processBuilder.directory(new File("D:\\ProjL\\src\\main\\java"));
        Process process1 = processBuilder.start();
        process1.waitFor();
        // kill the process
        process1.destroy();
    }

}
