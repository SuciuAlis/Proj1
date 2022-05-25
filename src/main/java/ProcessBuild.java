import java.io.File;
import java.io.IOException;

public class ProcessBuild {

    public static void compP(String javaFileName) throws IOException, InterruptedException {
//        ProcessBuilder builder = new ProcessBuilder("javac","D:\\ProjL\\src\\main\\java\\"+javaFileName+".java");
        ProcessBuilder builder = new ProcessBuilder("javac","-cp","D:\\ProjL\\src\\main\\java","D:\\ProjL\\src\\main\\java\\"+javaFileName+".java");

        builder.inheritIO();
        Process process = builder.start();
        if( process.getErrorStream().read() != -1 ){
            System.out.println("Compilation Errors"+process.getErrorStream());
        }
        // wait 10 seconds
        System.out.println("Waiting");
        Thread.sleep(5000);
    }

    public static void startProcess(String javaClassFileName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.inheritIO();
//        processBuilder.command("java","NodeMain",localAddress, localPort);
        processBuilder.command("java",javaClassFileName);
        processBuilder.directory(new File("D:\\ProjL\\src\\main\\java"));
        Process process1 = processBuilder.start();
        //process1.waitFor();
        // kill the process
        //process1.destroy();
    }

}
