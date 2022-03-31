import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class C {
    public static int numar = 0;
    public static ServerSocket server = null;
    public static Socket socket = null;
    public static Socket socket2 = null;
    public static DataInputStream in = null;
    public static DataOutputStream out = null;

    public C(int portS, String ip, int port, String first) throws IOException {
        try{
            server = new ServerSocket(portS);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");
            if (first.equalsIgnoreCase("1")){
                out = new DataOutputStream(socket.getOutputStream());
                socket2 = new Socket(ip,port);
                System.out.println("Connected");
                numar = 1;
                out.writeInt(numar);
            }
            else{
                socket = server.accept();
                System.out.println("Client accepted");
            }
//            socket = server.accept();
//            System.out.println("Client accepted");
//            out = new DataOutputStream(socket.getOutputStream());
//
//            socket2 = new Socket(ip,port);
//            System.out.println("Connected");
//            in = new DataInputStream(socket2.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("****");

//        if (first.equalsIgnoreCase("1")){
//            numar = 1;
//        }
        System.out.println("**"+numar+"**");
        out.writeInt(numar);
        while(numar<1000 && socket.isConnected() && socket2.isConnected()){
            try{
                numar = in.readInt() + 1;
                System.out.println("**"+numar+"**");
                out.writeInt(numar);
            }catch(IOException i){
                System.out.println(i);
            }
        }
        if (numar == 1000){
            System.out.println("Closing connection");
            socket.close();
            socket2.close();
            in.close();
            out.close();
        }
    }

    // C portS ip port numar
    // C 5000 127.0.0.3 5002 1
    // C 5001 127.0.0.1 5000 0
    // C 5002 127.0.0.2 5001 0


    public static void main(String[] args) throws IOException {
    //System.out.println("**"+args[0]+"**"+args[1]+"**"+args[2]+"**"+args[3]);
        C cp1 = new C(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]),args[3]);

    }
}
