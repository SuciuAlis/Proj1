import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

public class Node{
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

    public Node(){}

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

    public String getM_destinationAddress() {
        return m_destinationAddress;
    }

    public void setM_destinationAddress(String m_destinationAddress) {
        this.m_destinationAddress = m_destinationAddress;
    }

    public void sendPackage() throws IOException {
        Socket socket = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
        System.out.println("[INFO - sendPackageTo(endNode)] - The socket was opened for target address and port: " + m_targetAddress + ":" + m_targetPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.println("[INFO - sendPackageTo(endNode)] - The DataOutputStream was created with the socket's output stream");
        System.out.println("[INFO - sendPackageTo(endNode)] - Destination address was set: " + m_destinationAddress);
        byte[] array = initialAllocateByteBufferAndPutData(100).array();
        System.out.println("[INFO - sendPackageTo(endNode)] - The byte array was initialized with the ByteBuffer");
        out.write(array);
        socket.shutdownOutput();
        socket.close();
        System.out.println("[INFO - sendPackageTo(endNode)] - Socket was closed");
    }

    public void sendRoutingTable(Map<String, Map<String, String>> routing_table_map) throws IOException {
        Socket socket = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
        System.out.println("[INFO - sendRoutingTable(nodeTarget,routingTable)] - The socket was opened for target address and port: "+m_targetAddress+":"+m_targetPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.println("[INFO - sendRoutingTable(nodeTarget,routingTable)] - The DataOutputStream was created with the socket's output stream");
        m_routing_table_map = routing_table_map;
        byte[] array = initialAllocateByteBufferAndPutData(1024).array();
        System.out.println("[INFO - sendRoutingTable(nodeTarget,routingTable)] - The byte array was initialized with the ByteBuffer");
        out.write(array);
        System.out.println("[INFO - sendRoutingTableT(nodeTarget,routingTable)] - The data has been written on the data output stream");
        socket.shutdownOutput();
        socket.close();
        System.out.println("[INFO - sendRoutingTable(nodeTarget,routingTable)] - Socket was closed");
    }

    private ByteBuffer initialAllocateByteBufferAndPutData(int size){
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - ByteBuffer was allocated with the size: "+size);
        if(size == 100){
            byteBuffer.put((byte)1);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - First byte for the data packet type command has been set as 1");
            byte[] destinationAddressBytes = m_destinationAddress.getBytes();
            byteBuffer.putInt(destinationAddressBytes.length);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - Length of the string with destination address was put in the bytebuffer");
            byteBuffer.put(destinationAddressBytes);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - The string with the destination address was put in the bytebuffer");
            byteBuffer.putInt(40000);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - An integer was put in the bytebuffer");
        }else if(size == 1024){
            byteBuffer.put((byte)0);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - First byte for the routing table type command has been set as 0 in the bytebuffer");
            String routingTableString = getRoutingTableMapString(m_routing_table_map);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - The routing table was converted in a String:"+routingTableString);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - The length of the string is:"+routingTableString.length());
            byte[] routingTableBytes = routingTableString.getBytes();
            byteBuffer.putInt(routingTableBytes.length);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - Length of the string with routing table was put in the bytebuffer");
            byteBuffer.put(routingTableBytes);
            System.out.println("[INFO - initialAllocateByteBufferAndPutData(size)] - The string with the routing table was put in the bytebuffer");
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
        String[] nodesForMap = routing_table_map.keySet().toArray(new String[0]);       //ordine diferita fata de Windows a key urilor
        System.out.println("[INFO - getRoutingTableMapString(routingTableMap)] - The routing table keys were saved in an array of type String");
        System.out.println("[INFO - getRoutingTableMapString(routingTableMap)] - The length of the array:"+nodesForMap.length);
        System.out.println("[INFO - getRoutingTableMapString(routingTableMap)] - The keys from the routing table:");
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
        System.out.println("[INFO - getRoutingTableMapString(routingTableMap)] - The sorted keys from the routing table:");
        Map<String,String> nodesMap = new HashMap<>();
        for (int i=0;i<routing_table_map.size();i++){
            nodesMap.put(nodesForMap[i],convertMapToString(routing_table_map.get(nodesForMap[i])));
        }
        return convertMapToString(nodesMap);
    }
}
