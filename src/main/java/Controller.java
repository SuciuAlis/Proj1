import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Controller {
    private Socket m_socket;
    private String m_localAddress;
    private int m_localPort;
    private String m_targetAddress;
    private int m_targetPort;
    private Map<String, Map<String,String>> m_routing_table_map = new HashMap<>();
    private String m_destinationAddress = "";

    public Controller(String localAddress, int localPort){
        m_localAddress = localAddress;
        m_localPort = localPort;
    }

    public Controller() {
    }

    public Socket getM_socket() {
        return m_socket;
    }

    public void setM_socket(Socket m_socket) {
        this.m_socket = m_socket;
    }

    public String getM_localAddress() {
        return m_localAddress;
    }

    public void setM_localAddress(String m_localAddress) {
        this.m_localAddress = m_localAddress;
    }

    public int getM_localPort() {
        return m_localPort;
    }

    public void setM_localPort(int m_localPort) {
        this.m_localPort = m_localPort;
    }

    public String getM_targetAddress() {
        return m_targetAddress;
    }

    public void setM_targetAddress(String m_targetAddress) {
        this.m_targetAddress = m_targetAddress;
    }

    public int getM_targetPort() {
        return m_targetPort;
    }

    public void setM_targetPort(int m_targetPort) {
        this.m_targetPort = m_targetPort;
    }

    public Map<String, Map<String, String>> getM_routing_table_map() {
        return m_routing_table_map;
    }

    public void setM_routing_table_map(Map<String, Map<String, String>> m_routing_table_map) {
        this.m_routing_table_map = m_routing_table_map;
    }

    public String getM_destinationAddress() {
        return m_destinationAddress;
    }

    public void setM_destinationAddress(String m_destinationAddress) {
        this.m_destinationAddress = m_destinationAddress;
    }

    public void run(){
        System.out.println("Controller is up");
//        while(true){
//
//
    }
    public void sendPackageTo(Controller endNode) throws IOException {
        System.out.println("_____init_Local address****"+m_localAddress);
        System.out.println("_____init_Target address****"+m_targetAddress);
        Socket socket = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        m_destinationAddress = endNode.getM_localAddress();
        byte[] array = initialAllocateByteBufferAndPutData(100).array();
        out.write(array);
        socket.shutdownOutput();
        socket.close();
    }

    public void sendRoutingTableTo(Controller node, Map<String, Map<String, String>> routing_table_map) throws IOException {
        Socket socket = new Socket(node.m_localAddress,node.m_localPort, InetAddress.getByName(m_localAddress),0);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        m_routing_table_map = routing_table_map;
        byte[] array = initialAllocateByteBufferAndPutData(1000).array();
        out.write(array);
        socket.shutdownOutput();
        socket.close();
    }

    private ByteBuffer initialAllocateByteBufferAndPutData(int size){
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        if(size == 100){
            byteBuffer.put((byte)1);
            byte[] destinationAddressBytes = m_destinationAddress.getBytes();
            byteBuffer.putInt(destinationAddressBytes.length);
            byteBuffer.put(destinationAddressBytes);
            byteBuffer.putInt(40000);
        }else if(size == 1000){
            byteBuffer.put((byte)0);
            String routingTableString = getRoutingTableMapString(m_routing_table_map);
            byte[] routingTableBytes = routingTableString.getBytes();
            byteBuffer.putInt(routingTableBytes.length);
            byteBuffer.put(routingTableBytes);
        }
        return byteBuffer;
    }

    public String convertMapToString(Map<String,String> routingTableMap) {
        StringBuilder routingTableMapAsString = new StringBuilder("{");
        for (String key : routingTableMap.keySet()) {
            routingTableMapAsString.append(key).append("=").append(routingTableMap.get(key)).append(", ");
        }
        routingTableMapAsString.delete(routingTableMapAsString.length()-2, routingTableMapAsString.length()).append("}");
        return routingTableMapAsString.toString();
    }

    public Map<String, Map<String,String>> convertStringToMap(String routingTableString){
        Map<String, Map<String,String>> routingTableMap = new HashMap<>();
        String[] nodesStringFromTable = routingTableString.split("}");
        for (String s : nodesStringFromTable) {
            String[] nodes = s.split("=", 2);
            String[] nodesD = nodes[1].split(",");
            Map<String, String> tableForNode = new HashMap<>();
            for (String value : nodesD) {
                String[] addresses = value.split("=");
                addresses[0] = addresses[0].replace("{", "");
                tableForNode.put(addresses[0].replaceAll("\\s+", ""), addresses[1]);
            }
            nodes[0] = nodes[0].replace("{", "");
            nodes[0] = nodes[0].replace(",", "");
            routingTableMap.put(nodes[0].replaceAll("\\s+", ""), tableForNode);
        }
        return routingTableMap;
    }

    public String getRoutingTableMapString(Map<String, Map<String, String>> routing_table_map){
        String[] nodesForMap = new String[]{"N1", "N2", "N3", "N4","N5"};
        Map<String,String> nodesMap = new HashMap<>();
        for (int i=0;i<routing_table_map.size()-1;i++){
            nodesMap.put(nodesForMap[i],convertMapToString(routing_table_map.get(nodesForMap[i])));
        }
        return convertMapToString(nodesMap);
    }



    public static void main(String[] args) throws SocketException, InterruptedException {
        //Node controller = new Node(setLocalIpAddress(),5015);
        Node controller = new Node("127.0.0.15",5015);
        //controller.run();
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
    }

    public static String setLocalIpAddress() throws SocketException {
        String localAddress = "";
        //get all known network interfaces on the host
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
            Enumeration addressEnumeration = networkInterface.getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress i = (InetAddress) addressEnumeration.nextElement();
                System.out.println(i.getHostAddress());
                if (!i.isLoopbackAddress()) {
                    localAddress = i.getHostAddress();
                }
            }
        }
        return localAddress;
    }

}
