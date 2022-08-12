import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private ServerSocket m_serverSocket;
    private Socket m_socket;
    private String m_localAddress;
    private int m_localPort;
    private String m_targetAddress;
    private int m_targetPort;
    private Map<String, Map<String,String>> m_routing_table_map = new HashMap<>();
    private String m_destinationAddress = "";

    public Node(String m_localAddress, int m_localPort ) {
        this.m_localAddress = m_localAddress;
        this.m_localPort = m_localPort;
    }

    public Node(){}

    public void run() {
        try {
            m_serverSocket = new ServerSocket(m_localPort,5, InetAddress.getByName(m_localAddress));
            System.out.println("[INFO - run()] - Server started:"+ m_localAddress + ":" + m_localPort);
            System.out.println("[INFO - run()] - Waiting for a client ...");
            while(true){
                m_socket = m_serverSocket.accept();
                if (m_socket.isConnected()) {
                    DataInputStream in = new DataInputStream(m_socket.getInputStream());
                    System.out.println("[INFO - run()] - The DataOutputStream was created with the socket's output stream");
                    byte[] buffer0 = new byte[1];
                    System.out.println(in.read(buffer0));
                    ByteBuffer byteBuffer0 = ByteBuffer.wrap(buffer0);
                    byte b = byteBuffer0.get();
                    System.out.println("[INFO - run()] - The first received byte is: " + b);
                    if (b==0){
                        System.out.println("[INFO - run()] - Routing table will be received: ");
                        byte[] buffer = new byte[1023];
                        System.out.println(in.read(buffer));
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        int length = byteBuffer.getInt();
                        System.out.println("[INFO - run()] - The received length of the routing table is: " + length);
                        byte[] tableBytes = new byte[length];
                        byteBuffer.get(tableBytes);
                        String routingTable = new String(tableBytes);
                        System.out.println("[INFO - run()] - The received routing table is: " + routingTable);
                        m_routing_table_map = convertStringToMap(routingTable);
                        System.out.println("[INFO - run()] - The routing table was converted to a map and saved");
                        if(m_localAddress.equalsIgnoreCase("192.168.0.6")){
//                        if(m_localAddress.equalsIgnoreCase("127.0.0.5")){
                            m_socket.close();
                            System.out.println("[INFO - run()] - Socket is closed");
                        }else{
                            setTargetAddressAndPort();
                            Socket socket = new Socket(m_targetAddress, m_targetPort, InetAddress.getByName(m_localAddress), 0);
                            System.out.println("[INFO - run()] - The socket was opened for target address and port: "+m_targetAddress+":"+m_targetPort);
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            System.out.println("[INFO - run()] - The DataOutputStream was created with the socket's output stream");
                            ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);
                            String routingTable2 = getRoutingTableMapString(m_routing_table_map);
                            byte[] routingTableBytes = routingTable2.getBytes();
                            byteBuffer2.put((byte)0);
                            System.out.println("[INFO - run()] - First byte for the routing table type command has been set as 0 in the bytebuffer");
                            byteBuffer2.putInt(routingTableBytes.length);
                            System.out.println("[INFO - run()] - Length of the string with routing table was put in the bytebuffer");
                            byteBuffer2.put(routingTableBytes);
                            System.out.println("[INFO - run()] - The string with the routing table was put in the bytebuffer");
                            byte[] array = byteBuffer2.array();
                            out.write(array);
                            System.out.println("[INFO - run()] - The data has been written on the data output stream");
                            socket.shutdownOutput();
                            socket.close();
                            System.out.println("[INFO - run()] - Socket was closed");
                        }
                    }else{
                        System.out.println("[INFO - run()] - Data packet will be received: ");
                        byte[] buffer = new byte[100];
                        System.out.println(in.read(buffer));
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        int length = byteBuffer.getInt();
                        System.out.println("[INFO - run()] - The received length of the destination address is: " + length);
                        byte[] destinationBytes = new byte[length];
                        byteBuffer.get(destinationBytes);
                        String destination = new String(destinationBytes);
                        m_destinationAddress = destination;
                        System.out.println("[INFO - run()] - The destination address is: " + destination);
                        int number = byteBuffer.getInt() + 1;
                        if (m_localAddress.equalsIgnoreCase(destination)) {
                            System.out.println("[INFO - run()] - ******** Packet arrived at destination ********" + m_localAddress);
                            m_socket.close();
                            System.out.println("[INFO - run()] - Socket was closed");
                        }else{
                            Socket socket = new Socket(m_targetAddress, m_targetPort, InetAddress.getByName(m_localAddress), 0);
                            System.out.println("[INFO - run()] - The socket was opened for target address and port: "+m_targetAddress+":"+m_targetPort);
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            System.out.println("[INFO - run()] - The DataOutputStream was created with the socket's output stream");
                            ByteBuffer byteBuffer2 = ByteBuffer.allocate(100);
                            byteBuffer2.put((byte) 1);
                            System.out.println("[INFO - run()] - First byte for the data packet type command has been set as 1 in the bytebuffer");
                            byte[] destinationAddressBytes = m_destinationAddress.getBytes();
                            byteBuffer2.putInt(destinationAddressBytes.length);
                            System.out.println("[INFO - run()] - Length of the string with routing table was put in the bytebuffer");
                            byteBuffer2.put(destinationAddressBytes);
                            System.out.println("[INFO - run()] - The string with the destination address was put in the bytebuffer");
                            byteBuffer2.putInt(number);
                            System.out.println("[INFO - run()] - An integer was put in the bytebuffer");
                            byte[] array = byteBuffer2.array();
                            out.write(array);
                            System.out.println("[INFO - run()] - The data has been written on the data output stream");
                            socket.shutdownOutput();
                            socket.close();
                            m_socket.close();
                            System.out.println("[INFO - run()] - Socket was closed");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void setTargetAddressAndPort(){
        //Linux
        String[] addressAndPort = m_routing_table_map.get(m_localAddress).get("192.168.0.6").split(":");
        System.out.println("[INFO - setTargetAddressAndPort()] - Target address and port are: " + Arrays.toString(addressAndPort));
        //Windows
        //String[] addressAndPort = m_routing_table_map.get(m_localAddress).get("127.0.0.5").split(":");
        m_targetAddress = addressAndPort[0];
        m_targetPort = Integer.parseInt(addressAndPort[1]);
        System.out.println("[INFO - setTargetAddressAndPort()] - The local address and port are: " + m_localAddress + ":" + m_localPort);
        System.out.println("[INFO - setTargetAddressAndPort()] - The target address and port were set: " + m_targetAddress + ":" + m_targetPort);
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

    public String convertMapToString(Map<String,String> routingTableMap) {
        StringBuilder routingTableMapAsString = new StringBuilder("{");
        for (String key : routingTableMap.keySet()) {
            routingTableMapAsString.append(key).append("=").append(routingTableMap.get(key)).append(", ");
        }
        routingTableMapAsString.delete(routingTableMapAsString.length()-2, routingTableMapAsString.length()).append("}");
        return routingTableMapAsString.toString();
    }


    public String getRoutingTableMapString(Map<String, Map<String, String>> routing_table_map){
        String[] nodesForMap = routing_table_map.keySet().toArray(new String[0]);
        for(int i=0;i<nodesForMap.length;i++){
            for(int j=0;j<nodesForMap.length-1;j++){
                int l = nodesForMap[i].length()-1;
                if(nodesForMap[i].charAt(l)<nodesForMap[j].charAt(l)){
                    String aux=nodesForMap[i];
                    nodesForMap[i]=nodesForMap[j];
                    nodesForMap[j] = aux;
                }
            }
        }
        Map<String,String> nodesMap = new HashMap<>();
        for (int i=0;i<routing_table_map.size();i++){
            nodesMap.put(nodesForMap[i],convertMapToString(routing_table_map.get(nodesForMap[i])));
        }
        return convertMapToString(nodesMap);
    }

    public static void main(String[] args) throws SocketException, InterruptedException {
        System.out.println("[INFO - main()] - The local IP address is: " + getLocalIpAddress());
        Node node = new Node(getLocalIpAddress(),26150);
//        System.out.println("[INFO - main()] - Node was created with address and port: " + getLocalIpAddress() + ":" + 26150);
//        Node node = new Node("127.0.0.1",26150);
        node.run();
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
