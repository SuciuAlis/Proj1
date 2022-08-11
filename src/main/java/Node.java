import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

public class Node implements Runnable{
    private ServerSocket m_serverSocket;
    private Socket m_socket;
    private String m_localAddress;
    private int m_localPort;
    private String m_targetAddress;
    private int m_targetPort;
    private Map<String, Map<String,String>> m_routing_table_map = new HashMap<>();
    private String m_destinationAddress = "";

    public Node(String m_localAddress, int m_localPort) {
        this.m_localAddress = m_localAddress;
        this.m_localPort = m_localPort;
    }

    public String getM_localAddress() {
        return m_localAddress;
    }

    public int getM_localPort() {
        return m_localPort;
    }

    public String getM_targetAddress() {
        return m_targetAddress;
    }

    public int getM_targetPort() {
        return m_targetPort;
    }

    public void setM_targetAddress(String m_targetAddress) {
        this.m_targetAddress = m_targetAddress;
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

    @Override
    public void run() {
        System.out.println("Thread is running...");
            try {
                m_serverSocket = new ServerSocket(m_localPort,5, InetAddress.getByName(m_localAddress));
                System.out.println("Server started: "+m_localAddress+"-"+m_localPort);
                System.out.println("Waiting for a client ...");
                while(true){
                    m_socket = m_serverSocket.accept();
                    if (m_socket.isConnected()) {
                        DataInputStream in = new DataInputStream(m_socket.getInputStream());
                        byte[] buffer0 = new byte[1];
                        in.read(buffer0);
                        ByteBuffer byteBuffer0 = ByteBuffer.wrap(buffer0);
                        byte b = byteBuffer0.get();
                        System.out.println("First byte from the packet: "+b);
                        if (b==0){
                            byte[] buffer = new byte[1023];
                            in.read(buffer);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                            int length = byteBuffer.getInt();
                            System.out.println("Length of the routing table: "+length);
                            byte[] tableBytes = new byte[length];
                            byteBuffer.get(tableBytes);
                            String routingTable = new String(tableBytes);
                            setM_routing_table_map(convertStringToMap(routingTable));
                            System.out.println("Received routing table: "+ routingTable);
                            if(m_localAddress.equalsIgnoreCase("127.0.0.5")){
                                m_socket.close();
                            }else{
                                Socket socket = new Socket(m_targetAddress, m_targetPort, InetAddress.getByName(m_localAddress), 0);
                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);
                                String routingTable2 = getRoutingTableMapString(m_routing_table_map);
                                byte[] routingTableBytes = routingTable2.getBytes();
                                byteBuffer2.put((byte)0);
                                byteBuffer2.putInt(routingTableBytes.length);
                                byteBuffer2.put(routingTableBytes);
                                byte[] array = byteBuffer2.array();
                                out.write(array);
                                socket.shutdownOutput();
                                socket.close();
                            }
                        }else{
                            byte[] buffer = new byte[99];
                            in.read(buffer);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                            int length = byteBuffer.getInt();
                            byte[] destinationBytes = new byte[length];
                            byteBuffer.get(destinationBytes);
                            String destination = new String(destinationBytes);
                            m_destinationAddress = destination;
                            int number = byteBuffer.getInt() + 1;
                            System.out.println("_____Destination address_____" + destination);
                            System.out.println("_____The number received_____" + number);
                            System.out.println("_____Local address_____" + m_localAddress);
                            System.out.println("_____Local port_____" + m_localPort);
                            System.out.println("_____Target address_____" + m_targetAddress);
                            System.out.println("_____Target port_____" + m_targetPort);
                            if (m_localAddress.equalsIgnoreCase(destination)) {
                                System.out.println("Packet arrived at destination: "+destination);
                            }else{
                                Socket socket = new Socket(m_targetAddress, m_targetPort, InetAddress.getByName(m_localAddress), 0);
                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                ByteBuffer byteBuffer2 = ByteBuffer.allocate(100);
                                byteBuffer2.put((byte) 1);
                                byte[] destinationAddressBytes = m_destinationAddress.getBytes();
                                byteBuffer2.putInt(destinationAddressBytes.length);
                                byteBuffer2.put(destinationAddressBytes);
                                byteBuffer2.putInt(number);
                                byte[] array = byteBuffer2.array();
                                out.write(array);
                                socket.shutdownOutput();
                                socket.close();
                            }
                            m_socket.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    };

    public void sendPackageTo(Node endNode) throws IOException {
        System.out.println("_____Initial Local Address_____"+m_localAddress);
        System.out.println("_____Initial Target Address_____"+m_targetAddress);
        Socket socket = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        m_destinationAddress = endNode.getM_localAddress();
        byte[] array = allocateByteBufferAndPutData(100).array();
        out.write(array);
        socket.shutdownOutput();
        socket.close();
    }

    public void sendRoutingTable(Map<String, Map<String, String>> routing_table_map) throws IOException {
        Socket socket = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        m_routing_table_map = routing_table_map;
        byte[] array = allocateByteBufferAndPutData(1024).array();
        out.write(array);
        socket.shutdownOutput();
        socket.close();
    }

    private ByteBuffer allocateByteBufferAndPutData(int size){
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        if(size == 100){
            byteBuffer.put((byte)1);
            byte[] destinationAddressBytes = m_destinationAddress.getBytes();
            byteBuffer.putInt(destinationAddressBytes.length);
            byteBuffer.put(destinationAddressBytes);
            byteBuffer.putInt(40000);
        }else if(size == 1024){
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
}
