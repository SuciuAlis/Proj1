import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class C2 {
    public static int numar = 0;
    public static ServerSocket serverC2 = null;
    public static Socket socketC1 = null;
    public static Socket socketC2 = null;
    public static DataInputStream input = null;
    public static DataOutputStream output2 = null;

    public static void main(String[] args) throws IOException {
        try{
            socketC1 = new Socket("127.0.0.1", 5000);
            System.out.println("Connected to C1");
            input  = new DataInputStream(socketC1.getInputStream());

            serverC2 = new ServerSocket(5001);
            System.out.println("Server C2 started");
            System.out.println("Waiting for C3 as a client ...");
            socketC2 = serverC2.accept();
            System.out.println("Client C3 accepted");
            output2 = new DataOutputStream(socketC2.getOutputStream());
        }catch (IOException e) {
            e.printStackTrace();
        }
        while(numar<1000 && socketC1.isConnected() && socketC2.isConnected()){
            try{
            numar = input.readInt() + 1;
            if (numar < 1000){
                System.out.println("**"+numar+"**");
            }
            output2.writeInt(numar);
            }catch(IOException i){
                System.out.println(i);
            }
        }

        System.out.println("Closing connection");
            try{
                input.close();
                output2.close();
                socketC1.close();
                socketC2.close();
            }catch(IOException i){
                System.out.println(i);
            }
    }
}
