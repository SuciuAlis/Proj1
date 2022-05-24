import java.net.InetAddress;
import java.net.UnknownHostException;

public class Controller {
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("PLM");
        System.out.println("Initialize communication");
        String localAddress = InetAddress.getLocalHost().getHostAddress();
        //System.out.println(localAddress);
    }
}
