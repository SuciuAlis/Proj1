//import java.io.File;
//import org.ini4j.*;
//public class readInit {
//
//    public static void main(String[] args){
//        try{
//            Wini ini = new Wini(new File("C:\\Users\\sdkca\\Desktop\\myinifile.ini"));
//            int age = ini.get("owner", "age", int.class);
//            double height = ini.get("owner", "height", double.class);
//            String server = ini.get("database", "server");
//
//
//            System.out.print("Age: " + age + "\n");
//            System.out.print("Geight: " + height + "\n");
//            System.out.print("Server IP: " + server + "\n");
//            // To catch basically any error related to finding the file e.g
//            // (The system cannot find the file specified)
//        }catch(Exception e){
//            System.err.println(e.getMessage());
//        }
//
//    }
//}
