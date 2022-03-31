import java.io.File;
import java.io.IOException;

public class ProcessBuild {

    public ProcessBuild(){}

    public void compP() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("javac","D:\\Tema1\\Project1\\src\\main\\java\\Node.java");
        Process process = builder.start();
        if( process.getErrorStream().read() != -1 ){
            System.out.println("Compilation Errors"+process.getErrorStream());
        }
        // wait 10 seconds
        System.out.println("Waiting");
        Thread.sleep(5000);
    }

    public void startProcess(String localAddress, String localPort, String targetAddress, String targetPort) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.inheritIO();
        processBuilder.command("java","Node",localAddress, localPort,targetAddress,targetPort);
        processBuilder.directory(new File("D:\\Tema1\\Project1\\src\\main\\java"));
        Process process1 = processBuilder.start();
        //process1.waitFor();

        // kill the process
//            process.destroy();
        //System.out.println("Processes destroyed");
    }

}
