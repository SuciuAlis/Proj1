import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{

    public GUI(){
        this.setBounds(10,10,100,100);
        this.setTitle("App");
        this.setResizable(false);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(null);

    }
}
