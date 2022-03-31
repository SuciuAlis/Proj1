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

    public Node(String localAddress, int localPort, String targetAddress, int targetPort){
        m_localAddress = localAddress;
        m_localPort = localPort;
        m_targetAddress = targetAddress;
        m_targetPort = targetPort;
    }

    public void initializeCommunication() throws IOException {
        System.out.println("_____init_Target address****"+m_targetAddress);
        System.out.println("_____init_Target port****"+ m_targetPort);
        Socket socket = new Socket(m_targetAddress,m_targetPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        //out.writeInt(40000);//40000

        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        byteBuffer.putInt(40000);
        byteBuffer.putChar('1');
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
                    byte[] buffer = new byte[10];
                    System.out.println(in.read(buffer));
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                    int number = byteBuffer.getInt() + 1;
                    char c = byteBuffer.getChar();
                    System.out.println("***********Number:"+ number+"*******");
                    System.out.println("***********Char:"+ c+"*******");
                    if (number==40100){//40100
                        break;
                    }
                    //System.out.println("*_____ ****NR "+number);
                    System.out.println("*_____ ****"+m_localAddress);
                    System.out.println("*_____ ****"+m_targetAddress);
                    System.out.println("*_____ ****"+ m_targetPort);
                    Socket socket2 = new Socket(m_targetAddress,m_targetPort, InetAddress.getByName(m_localAddress),0);
                    DataOutputStream out = new DataOutputStream(socket2.getOutputStream());
                    ByteBuffer byteBuffer2 = ByteBuffer.allocate(1000);
                    byteBuffer2.putInt(number);
                    byteBuffer2.putChar('2');
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
