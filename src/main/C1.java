import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class C1 {
    public static int numar = 0;
    public static ServerSocket serverC1 = null;
    public static Socket socket = null;
    public static Socket socketC3 = null;
    public static DataInputStream in = null;
    public static DataOutputStream out = null;

    public static void main(String[] args) throws IOException {
        try{
            serverC1 = new ServerSocket(5000);
            System.out.println("Server C1 started");
            System.out.println("Waiting for C2 as a client ...");
            socket = serverC1.accept();
            System.out.println("Client C2 accepted");
            out = new DataOutputStream(socket.getOutputStream());

            socketC3 = new Socket("127.0.0.3",5002);
            System.out.println("Connected to C3");
            in = new DataInputStream(socketC3.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        numar = 1;
        System.out.println("**"+numar+"**");
        out.writeInt(numar);
        while(numar<1000 && socket.isConnected() && socketC3.isConnected()){
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
            socketC3.close();
            in.close();
            out.close();
        }
    }
}
