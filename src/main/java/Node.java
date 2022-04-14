import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
    private Map<String, String> m_routing_table_map = new HashMap<String, String>();
    private String m_destinationAddress = "";
    private String m_startAddress = "";
    private boolean sendData = false;
    //private DataOutputStream out;
    //private Socket socket;
    //private String destinationAddress;
    //private DataOutputStream out;
//    public Node(String localAddress, int localPort, String targetAddress, int targetPort){
//        m_localAddress = localAddress;
//        m_localPort = localPort;
//        m_targetAddress = targetAddress;
//        m_targetPort = targetPort;
//    }

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

    public Map<String, String> getM_routing_table_map() {
        return m_routing_table_map;
    }

    public void setM_routing_table_map(Map<String, String> m_routing_table_map) {
        this.m_routing_table_map = m_routing_table_map;
    }

    public void startCommunication() throws IOException {
        System.out.println("_____init_Target address****"+m_targetAddress);
        System.out.println("_____init_Target port****"+ m_targetPort);
//        socket = new Socket(m_targetAddress,m_targetPort);
//        out = new DataOutputStream(socket.getOutputStream());
//        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
//        byte[] destinationAddressBytes = m_destinationAddress.getBytes();
//        byteBuffer.putInt(destinationAddressBytes.length);
//        byteBuffer.put(destinationAddressBytes);
//        byteBuffer.putInt(40000);
//        byte[] array = byteBuffer.array();
//        //System.out.println("FIRST SENT DATA: "+Arrays.toString(array));
//        out.write(array);
//        out.flush();
//        socket.shutdownOutput();
//        socket.close();
    }

    @Override
    public void run() {
        System.out.println("Thread is running...");
//        if(m_localAddress.equalsIgnoreCase("127.0.0.1")){
//            try {
//                startCommunication();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if(m_localAddress.equalsIgnoreCase("127.0.0.15")){
//
//        }
        try {
            m_serverSocket = new ServerSocket(m_localPort,5, InetAddress.getByName(m_localAddress));
            System.out.println("Server started: "+m_localAddress+"-"+m_localPort);
            System.out.println("Waiting for a client ...");
            while(true){
                m_socket = m_serverSocket.accept();
                if (m_socket.isConnected()){
                    DataInputStream in = new DataInputStream(m_socket.getInputStream());
                    byte[] buffer = new byte[100];
                    System.out.println(in.read(buffer));
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

                    int len = byteBuffer.getInt();
                    byte[] destinationBytes = new byte[len];
                    byteBuffer.get(destinationBytes);
                    String destination = new String(destinationBytes);
                    m_destinationAddress = destination;
                    System.out.println("___________Destination____"+destination);
                    int number = byteBuffer.getInt() + 1;

                    //char c = byteBuffer.getChar();
                    //System.out.println("***********Number:"+ number+"*******");
                    //System.out.println("***********Char:"+ c+"*******");
//                    if (number==40100){//40100
//                        break;
//                    }
                    System.out.println("*_____ NR**** "+number);
                    System.out.println("*_____ LOCAL ADR****"+m_localAddress);
                    System.out.println("*_____ TARGET ADR****"+m_targetAddress);
                    System.out.println("*_____ TARGET PORT****"+ m_targetPort);
                    if (m_localAddress.equalsIgnoreCase(destination)){
                        System.out.println("-------************************AM AJUNS LA DESTINATIE----"+m_destinationAddress);
                        break;
                    }
                    Socket socket2 = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
                    DataOutputStream out = new DataOutputStream(socket2.getOutputStream());
                    ByteBuffer byteBuffer2 = ByteBuffer.allocate(1000);
                    byte[] destinationAddressBytes = m_destinationAddress.getBytes();
                    byteBuffer2.putInt(destinationAddressBytes.length);
                    byteBuffer2.put(destinationAddressBytes);
                    byteBuffer2.putInt(number);
                    byte[] array = byteBuffer2.array();
                    out.write(array);
                    //out.flush();
                    socket2.close();
                    //socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void sendPackageTo(Node endNode) throws IOException {
        System.out.println("_____init_Target address****"+m_targetAddress);
        System.out.println("_____init_Target port****"+ m_targetPort);
        Socket socket = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        sendData = true;
        m_startAddress = getM_localAddress();
       // m_destinationAddress = endNode.getM_localAddress();
        m_destinationAddress = endNode.getM_localAddress();
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byte[] destinationAddressBytes = m_destinationAddress.getBytes();
        byteBuffer.putInt(destinationAddressBytes.length);
        byteBuffer.put(destinationAddressBytes);
        byteBuffer.putInt(40000);
        byte[] array = byteBuffer.array();
        //System.out.println("FIRST SENT DATA: "+Arrays.toString(array));
        out.write(array);
        socket.shutdownOutput();
        socket.close();
    }
}
