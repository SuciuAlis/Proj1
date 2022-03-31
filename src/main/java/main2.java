import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class main2 {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        int numar = 40001;
        ByteBuffer bb = ByteBuffer.allocate(1000);
        bb.putInt(numar);
        bb.putChar('1');
        byte[] array = bb.array();
        for (byte b:array) {
            System.out.println("*****"+b);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        int numarBack = byteBuffer.getInt();
        char numarBack2 = byteBuffer.getChar();

        System.out.println("*******___"+numarBack+"______"+numarBack2);

    }
}
