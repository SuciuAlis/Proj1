import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node implements Runnable{

    public ServerSocket m_serverSocket;
    public Socket m_socket;
    public String m_localAddress;
    public int m_localPort;
    public String m_targetAddress;
    public int m_targetPort;
    Map<String, List<String>> m_routing_table_map = new HashMap<String, List<String>>();
    public String destinationAddress = "127.0.0.5";

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

    public void initializeCommunication() throws IOException {
        System.out.println("_____init_Target address****"+m_targetAddress);
        System.out.println("_____init_Target port****"+ m_targetPort);
        Socket socket = new Socket(m_targetAddress,m_targetPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        byte[] destinationAddressBytes = destinationAddress.getBytes();
        byteBuffer.putInt(destinationAddressBytes.length);
        byteBuffer.put(destinationAddressBytes);
        byteBuffer.putInt(40000);
        byte[] array = byteBuffer.array();
        System.out.println("FIRST SENT DATA: "+Arrays.toString(array));
        out.write(array);
        out.flush();
        socket.shutdownOutput();
        socket.close();
    }

    @Override
    public void run() {
        System.out.println("Thread is running...");
        if(m_localAddress.equalsIgnoreCase("127.0.0.1")){
            try {
                initializeCommunication();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    System.out.println("___________Destination____"+destination);
                    int number = byteBuffer.getInt() + 1;

                    //char c = byteBuffer.getChar();
                    //System.out.println("***********Number:"+ number+"*******");
                    //System.out.println("***********Char:"+ c+"*******");
//                    if (number==40100){//40100
//                        break;
//                    }
                    System.out.println("*_____ ****NR "+number);
                    System.out.println("*_____ ****"+m_localAddress);
                    System.out.println("*_____ ****"+m_targetAddress);
                    System.out.println("*_____ ****"+ m_targetPort);
                    if (m_localAddress.equalsIgnoreCase(destination)){
                        break;
                    }

                    Socket socket2 = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
                    DataOutputStream out = new DataOutputStream(socket2.getOutputStream());
                    ByteBuffer byteBuffer2 = ByteBuffer.allocate(1000);
                    byte[] destinationAddressBytes = destinationAddress.getBytes();
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
}
