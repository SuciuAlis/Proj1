import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class main {

    // C ipLocal    portLocal  ipDest     portDest
    // C 127.0.0.1  5001       127.0.0.2  5002
    // C 127.0.0.2  5002       127.0.0.3  5003
    // C 127.0.0.3  5003       127.0.0.1  5001

//    public static void main(String[] args) throws IOException, InterruptedException {
////        ProcessBuild processBuild = new ProcessBuild();
////        processBuild.compP();
////        processBuild.startProcess("127.0.0.1","5001","127.0.0.2","5002");
////        processBuild.startProcess("127.0.0.2","5002","127.0.0.3","5003");
////        processBuild.startProcess("127.0.0.3","5003","127.0.0.1","5001");
////
////    }
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
    //System.out.println("**"+args[0]+"**"+args[1]+"**"+args[2]+"**"+args[3]);
    //Node node1 = new Node(args[0],Integer.parseInt(args[1]),args[2],Integer.parseInt(args[3]));
//        Subject subject = new Subject();
//    Node node1 = new Node("127.0.0.1",5001,"127.0.0.2",5002);
//    Node node2 = new Node("127.0.0.2",5002,"127.0.0.3",5003);
//    Node node3 = new Node("127.0.0.3",5003,"127.0.0.1",5001);
//        subject.attach(node2);
    System.out.println("Initialize communication");
        Thread t1 =new Thread(new Node("127.0.0.1",5001,"127.0.0.2",5002));
        Thread t2 =new Thread(new Node("127.0.0.2",5002,"127.0.0.3",5003));
        Thread t3 =new Thread(new Node("127.0.0.3",5003,"127.0.0.1",5001));
        t2.start();
        t3.start();
        t1.start();
}
}
