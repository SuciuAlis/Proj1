import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class C3 {
    public static int numar = 0;
    public static ServerSocket serverC3 = null;
    public static Socket socketC2 = null;
    public static Socket socketC3 = null;
    public static DataInputStream input = null;
    public static DataOutputStream output3 = null;

    public static void main(String[] args) throws IOException {
        try{
            socketC2 = new Socket("127.0.0.2",5001);
            System.out.println("Connected to C2");
            input = new DataInputStream(socketC2.getInputStream());

            serverC3 = new ServerSocket(5002);
            System.out.println("Server C3 started");
            System.out.println("Waiting for C1 as a client ...");
            socketC3 = serverC3.accept();
            System.out.println("Client C1 accepted");
            output3 = new DataOutputStream(socketC3.getOutputStream());
        }catch (IOException e) {
            e.printStackTrace();
        }
        while(numar<1000 && socketC2.isConnected() && socketC3.isConnected()) {
            try{
            numar = input.readInt() + 1;
            if (numar < 1000){
                System.out.println("**" + numar + "**");
            }
            output3.writeInt(numar);
            }catch(IOException i){
                System.out.println(i);
            }
        }
        System.out.println("Closing connection");
        try{
            socketC2.close();
            socketC3.close();
            input.close();
            output3.close();
        }catch(IOException i){
            System.out.println(i);
        }
    }
}
