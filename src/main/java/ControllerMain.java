import java.io.IOException;

public class ControllerMain {
    public static void main(String[] args) throws IOException, InterruptedException {
//        String localAddress = "";
//
//        //get all known network interfaces on the host
//        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
//        while(interfaces.hasMoreElements()) {
//            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
//            Enumeration addressEnumeration = networkInterface.getInetAddresses();
//            while (addressEnumeration.hasMoreElements()) {
//                InetAddress i = (InetAddress) addressEnumeration.nextElement();
//                System.out.println(i.getHostAddress());
//                if (!i.isLoopbackAddress()) {
//                    localAddress = i.getHostAddress();
//                }
//            }
//        }
//        System.out.println("ASTA:"+localAddress);
//        Controller controller = new Controller(localAddress,5015);
//        controller.run();

        ProcessBuild.compP("Controller");
        ProcessBuild.startProcess("Controller");
    }
}
