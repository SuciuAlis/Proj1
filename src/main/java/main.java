import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

//        Node node1 = new Node("127.0.0.1",5001,"127.0.0.2",5002);
//        Node node2 = new Node("127.0.0.2",5002,"127.0.0.3",5003);
//        Node node3 = new Node("127.0.0.3",5003,"127.0.0.4",5004);
//        Node node4 = new Node("127.0.0.4",5004,"127.0.0.5",5005);
//        Node node5 = new Node("127.0.0.5",5005,"127.0.0.1",5001);
        Node controller = new Node("127.0.0.15",5015);
        Node node1 = new Node("127.0.0.1",5002);
        Node node2 = new Node("127.0.0.2",5003);
        Node node3 = new Node("127.0.0.3",5004);
        Node node4 = new Node("127.0.0.4",5005);
        Node node5 = new Node("127.0.0.5",5001);

        List<Node> nodeList = new ArrayList<>();
        nodeList.add(node1);
        nodeList.add(node2);
        nodeList.add(node3);
        nodeList.add(node4);
        nodeList.add(node5);
        nodeList.add(controller);

        GUI gui = new GUI(nodeList);
        gui.setVisible(true);

        while(!gui.isM_startThreads()){
            Thread.sleep(100);
        }

        Thread t1 = new Thread(node1);
        Thread t2 = new Thread(node2);
        Thread t3 = new Thread(node3);
        Thread t4 = new Thread(node4);
        Thread t5 = new Thread(node5);
        Thread tc = new Thread(controller);

        tc.start();
        t5.start();
        t4.start();
        t3.start();
        t2.start();
        t1.start();

}
}
