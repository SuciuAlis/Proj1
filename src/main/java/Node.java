import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Node implements Runnable{

    private ServerSocket m_serverSocket;
    private Socket m_socket;
    private String m_localAddress;
    private int m_localPort;
    private String m_targetAddress;
    private int m_targetPort;
    private Map<String, Map<String,String>> m_routing_table_map = new HashMap<>();
    private String m_destinationAddress = "";
    private boolean sentData = false;
    private boolean sentRoutingTable = false;

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

    @Override
    public void run() {
        System.out.println("Thread is running...");
        if (m_localAddress.equalsIgnoreCase("127.0.0.15")){

        }else{
            try {
                m_serverSocket = new ServerSocket(m_localPort,5, InetAddress.getByName(m_localAddress));
                System.out.println("Server started: "+m_localAddress+"-"+m_localPort);
                System.out.println("Waiting for a client ...");
                while(true){
//                if(sentData) {
                    m_socket = m_serverSocket.accept();
                    if (m_socket.isConnected()) {
                        if(!sentRoutingTable){
                            DataInputStream in = new DataInputStream(m_socket.getInputStream());
                            byte[] buffer = new byte[1000];
                            System.out.println(in.read(buffer));
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                            int length = byteBuffer.getInt();
                            byte[] tableBytes = new byte[length];
                            byteBuffer.get(tableBytes);
                            String routingTable = new String(tableBytes);
                            m_routing_table_map = convertStringToMap(routingTable);
                            System.out.println("ROUTING TABLE FOR NODE::_______"+ routingTable);
                            sentRoutingTable = true;
                            if(m_localAddress.equalsIgnoreCase("127.0.0.5")){
//                                break;
                                m_socket.close();
                            }else{
                                Socket socket3 = new Socket(m_targetAddress, m_targetPort, InetAddress.getByName(m_localAddress), 0);
                                DataOutputStream out = new DataOutputStream(socket3.getOutputStream());
                                byte[] array = byteBuffer.array();
                                out.write(array);
                                //out.flush();
                                socket3.shutdownOutput();
                                socket3.close();
                            }
                        }else{
                            DataInputStream in = new DataInputStream(m_socket.getInputStream());
                            byte[] buffer = new byte[100];
                            System.out.println(in.read(buffer));
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                            int len = byteBuffer.getInt();
                            byte[] destinationBytes = new byte[len];
                            byteBuffer.get(destinationBytes);
                            String destination = new String(destinationBytes);
                            m_destinationAddress = destination;
                            System.out.println("___________Destination____" + destination);
                            int number = byteBuffer.getInt() + 1;
                            System.out.println("*_____ NR**** " + number);
                            System.out.println("*_____ LOCAL ADR****" + m_localAddress);
                            System.out.println("*_____ TARGET ADR****" + m_targetAddress);
                            System.out.println("*_____ TARGET PORT****" + m_targetPort);
                            if (m_localAddress.equalsIgnoreCase(destination)) {
                                System.out.println("AM AJUNS LA DST:::"+destination);
                                System.out.println("AM AJUNS LA DST:::"+m_destinationAddress);
//                                break;
                                m_socket.close();
                            }else{
                                Socket socket2 = new Socket(m_targetAddress, m_targetPort, InetAddress.getByName(m_localAddress), 0);
                                DataOutputStream out = new DataOutputStream(socket2.getOutputStream());
                                ByteBuffer byteBuffer2 = ByteBuffer.allocate(100);
                                byte[] destinationAddressBytes = m_destinationAddress.getBytes();
                                byteBuffer2.putInt(destinationAddressBytes.length);
                                byteBuffer2.put(destinationAddressBytes);
                                byteBuffer2.putInt(number);
                                byte[] array = byteBuffer2.array();
                                out.write(array);
                                //out.flush();
                                socket2.shutdownOutput();
                                socket2.close();
                                //socket.close();
                                m_socket.close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    };

    public void sendPackageTo(Node endNode) throws IOException {
        System.out.println("_____init_Local address****"+m_localAddress);
        System.out.println("_____init_Target address****"+m_targetAddress);
//        System.out.println("_____init_Target port****"+ m_targetPort);
        Socket socket = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        m_destinationAddress = endNode.getM_localAddress();
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byte[] destinationAddressBytes = m_destinationAddress.getBytes();
        byteBuffer.putInt(destinationAddressBytes.length);
        byteBuffer.put(destinationAddressBytes);
        byteBuffer.putInt(40000);
        byte[] array = byteBuffer.array();
//        System.out.println("FIRST SENT DATA: "+Arrays.toString(array));
        out.write(array);
        socket.shutdownOutput();
        socket.close();
    }

    public void sendRoutingTableTo(Node node, Map<String, Map<String, String>> routing_table_map) throws IOException {
        Socket socket = new Socket(node.m_localAddress,node.m_localPort, InetAddress.getByName(m_localAddress),0);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        String routingTableString = getRoutingTableMapString(routing_table_map);
        //System.out.println("Routing Table String"+routingTableString);
        byte[] routingTableBytes = routingTableString.getBytes();
        byteBuffer.putInt(routingTableBytes.length);
        byteBuffer.put(routingTableBytes);
        byte[] array = byteBuffer.array();
        out.write(array);
        socket.shutdownOutput();
        socket.close();
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
        //....
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

}
