import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
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
            //byteBuffer.put(0,(byte)0);
            byteBuffer.put((byte)0);        //windows, merge si asa pe ubuntu, poate ca nu de la aia era problema :?
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

//    public String convertMapToString(Map<String,String> routingTableMap) {
//        System.out.println("DA 5");
//        StringBuilder routingTableMapAsString = new StringBuilder("{");   //nu mergea cu StringBuilder
////        String routingTableMapAsString = "{";
//        System.out.println("DA 6");
//        for (String key : routingTableMap.keySet()) {
//            routingTableMapAsString.append(key).append("=").append(routingTableMap.get(key)).append(", ");
//        }
////        System.out.println("DA 7");
////        routingTableMapAsString.deleteCharAt(routingTableMapAsString.length()-2);
////        routingTableMapAsString.deleteCharAt(routingTableMapAsString.length()-1);
////        System.out.println("DA 7...2");
//        routingTableMapAsString.delete(routingTableMapAsString.length()-2, routingTableMapAsString.length()).append("}");
////        routingTableMapAsString.append("}");
//
//        // folosit List<String> in loc de StringBuilder pentru constructia de String
//        List<String> keyValues = new ArrayList<>(routingTableMap.keySet());
//        List<String> values = new ArrayList<>(routingTableMap.values());
//        int sizeKeyValues = keyValues.size();
//        int sizeValues = values.size();
//
//        System.out.println("KEY VALUES:"+keyValues);
//        System.out.println("VALUES:"+values);
//
//        System.out.println("SIZE KEY VALUES:"+keyValues.size());
//        System.out.println("SIZE VALUES:"+values.size());
//        System.out.println("DA 7");
//
//
//        //merge
////        for(int i=0;i<sizeKeyValues;i++){
////            if (i==sizeKeyValues-1){
////                routingTableMapAsString = routingTableMapAsString + keyValues.get(i) + "=" + values.get(i) + "}";
////            }
////            else{
////                routingTableMapAsString = routingTableMapAsString + keyValues.get(i) + "=" + values.get(i) + ", ";
////            }
////        }
//
////        System.out.println("ROUTING TABLE STR ____________"+ routingTableMapAsString);
////        System.out.println("DA 8");
////            routingTableMapAsString = routingTableMapAsString + keyValues.get(sizeKeyValues-1) + "=" + values.get(sizeValues-1) + "}";
//
////        routingTableMapAsString = routingTableMapAsString + keyValues.get(sizeKeyValues) + "=" + values.get(sizeValues) + "}";
////        for(int i=0;i<keyValues.size()-1;i++) {
////            routingTableMapAsString = keyValues.get(i) + "=" + values.get(i) + ", ";
////        }
////        routingTableMapAsString = keyValues.get(sizeKeyValues) + "=" + values.get(sizeKeyValues) + "}";
//        System.out.println("ROUTING TABLE STR ____________"+ routingTableMapAsString);
////        int length = routingTableMap.size();
////        for(int i=0;i<keyValues.size()-1;i++){
////            routingTableMapAsString.append(keyValues.get(i)).append("=").append(routingTableMap.get(keyValues.get(i))).append(", ");
////        }
////            routingTableMapAsString.append(keyValues.get(keyValues.size())).append("=").append(routingTableMap.get(keyValues.get(keyValues.size()))).append("}");
//        System.out.println("ROUTING TABLE STR ____________"+ routingTableMapAsString);
////        for(String i=0;i<routingTableMap.keySet().size()-1;i++){
////            routingTableMapAsString.append(routingTableMap.get(i)).append("=").append(routingTableMap.get(i)).append(", ");
////        }
//        System.out.println("DA 8");
//        return routingTableMapAsString.toString();
//    }

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
        for (String key: nodesForMap){
            System.out.println("*****key*****"+key);
        }
        String[] nodesForMapInv = new String[nodesForMap.length];
        int invC = 0;
        //partial
//        for(int i=nodesForMap.length-1;i>=0;i--){
//            nodesForMapInv[invC] = nodesForMap[i];
//            invC++;
//        }

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
        for (String key: nodesForMap){
            System.out.println("*****key*****"+key);
        }

////        System.out.println("DA 42");
//        List<String> arrayList = Arrays.asList(nodesForMap);
//        System.out.println("DA 43");
//        Collections.reverse(arrayList);     //nu mergea cu Collections
//        System.out.println("Se opreste dupa");
//        String[] testnodesForMap = (String[]) arrayList.toArray();
////        System.out.println("DA 45");
////        System.out.println("NODES FOR MAPPPPP+++++"+ Arrays.toString(nodesForMap));
//
//        for (String s: nodesForMapInv){
//            System.out.println("NODES MAPPPPP INV+++++"+s);
//        }
//        Map<String,String> nodesMap = new HashMap<>();
//        System.out.println("DA 41");
//        for (int i=0;i<routing_table_map.size()-1;i++){
//            nodesMap.put(nodesForMapInv[i],convertMapToString(routing_table_map.get(nodesForMapInv[i])));
//        }
        Map<String,String> nodesMap = new HashMap<>();
//        for (int i=0;i<routing_table_map.size()-1;i++){
        for (int i=0;i<routing_table_map.size()-1;i++){
            nodesMap.put(nodesForMap[i],convertMapToString(routing_table_map.get(nodesForMap[i])));
        }
//        System.out.println("NODES MAP"+nodesMap);
        return convertMapToString(nodesMap);
    }
}
