import java.net.*;
import java.util.*;

public class Controller {
    public static void main(String[] args) throws SocketException {
        //Mininet
        Node controller = new Node(getLocalIpAddress(),26150);
        controller.setM_targetAddress("192.168.0.2");
        controller.setM_targetPort(26150);
        System.out.println("[INFO - main()] - Node controller was created with the station ip address and port: "+getLocalIpAddress()+":26150");
        Node node1 = new Node("192.168.0.2",26150);
        System.out.println("[INFO - main()] - Node1 was created with the ip address and port: 192.168.0.2:26150");
        Node node2 = new Node("192.168.0.3",26150);
        System.out.println("[INFO - main()] - Node2 was created with the ip address and port: 192.168.0.3:26150");
        Node node3 = new Node("192.168.0.4",26150);
        System.out.println("[INFO - main()] - Node3 was created with the ip address and port: 192.168.0.4:26150");
        Node node4 = new Node("192.168.0.5",26150);
        System.out.println("[INFO - main()] - Node4 was created with the ip address and port: 192.168.0.5:26150");
        Node node5 = new Node("192.168.0.6",26150);
        System.out.println("[INFO - main()] - Node5 was created with the ip address and port: 192.168.0.6:26150");
        //Windows
//        Node controller = new Node("127.0.0.15",26150);
//        controller.setM_targetAddress("127.0.0.1");
//        controller.setM_targetPort(26150);
//        Node node1 = new Node("127.0.0.1",26150);
//        Node node2 = new Node("127.0.0.2",26150);
//        Node node3 = new Node("127.0.0.3",26150);
//        Node node4 = new Node("127.0.0.4",26150);
//        Node node5 = new Node("127.0.0.5",26150);
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(node1);
        nodeList.add(node2);
        nodeList.add(node3);
        nodeList.add(node4);
        nodeList.add(node5);
        System.out.println("[INFO - main()] - Nodes were added to the nodes list");
        nodeList.add(controller);
        System.out.println("[INFO - main()] - Node controller was added the last in the nodes list");
        GUI gui = new GUI(nodeList);
        gui.setVisible(true);
    }

    public static String getLocalIpAddress() throws SocketException {
        String localAddress = "";
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
            Enumeration addressEnumeration = networkInterface.getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress i = (InetAddress) addressEnumeration.nextElement();
                if (!i.isLoopbackAddress()) {
                    localAddress = i.getHostAddress();
                }
            }
        }
        return localAddress;
    }
}
