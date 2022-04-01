import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame{

    private final JTextField N1TF;
    private final JTextField N2TF;
    private final JTextField N3TF;
    private final JTextField N4TF;
    private final JTextField N5TF;
    List<Node> m_nodeList = new ArrayList<>();
    List<JTextField> jTextFieldList = new ArrayList<>();
    boolean m_startThreads = false;

    public GUI(List<Node> nodeList){
        m_nodeList = nodeList;
        this.setBounds(10,10,300,300);
        this.setTitle("Set the path");
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(null);
        JLabel N1 = new JLabel("N1");
        N1.setBounds(20,20,20,20);
        container.add(N1);
        JLabel N2 = new JLabel("N2");
        N2.setBounds(20,60,20,20);
        container.add(N2);
        JLabel N3 = new JLabel("N3");
        N3.setBounds(20,100,20,20);
        container.add(N3);
        JLabel N4 = new JLabel("N4");
        N4.setBounds(20,140,20,20);
        container.add(N4);
        JLabel N5 = new JLabel("N5");
        N5.setBounds(20,180,20,20);
        container.add(N5);
        N1TF = new JTextField();
        N1TF.setBounds(50,20,100,20);
        container.add(N1TF);

        N2TF = new JTextField();
        N2TF.setBounds(50,60,100,20);
        container.add(N2TF);

        N3TF = new JTextField();
        N3TF.setBounds(50,100,100,20);
        container.add(N3TF);

        N4TF = new JTextField();
        N4TF.setBounds(50,140,100,20);
        container.add(N4TF);

        N5TF = new JTextField();
        N5TF.setBounds(50,180,100,20);
        container.add(N5TF);

        JButton jButton = new JButton("Save");
        jButton.setBounds(180,180,80,20);
        container.add(jButton);

        jButton.addActionListener(e -> {
            jTextFieldList.add(N1TF);
            jTextFieldList.add(N2TF);
            jTextFieldList.add(N3TF);
            jTextFieldList.add(N4TF);
            jTextFieldList.add(N5TF);
            setNodes();
        });
    }


    public void setNodes(){
        for(int i=0;i<5;i++) {
            int targetNodeIndex = Integer.parseInt(jTextFieldList.get(i).getText().substring(1))-1;
            System.out.println("TargetIndex:::::"+targetNodeIndex);
            System.out.println("Local address for current node:::::"+m_nodeList.get(i).getM_localAddress());
            System.out.println("Local address for target node:::::"+m_nodeList.get(targetNodeIndex).getM_localAddress());
            m_nodeList.get(i).setM_targetAddress(m_nodeList.get(targetNodeIndex).getM_localAddress());
            m_nodeList.get(i).setM_targetPort(m_nodeList.get(targetNodeIndex).getM_localPort());
        }
        setM_startThreads(true);
    }

    public boolean isM_startThreads() {
        return m_startThreads;
    }

    public void setM_startThreads(boolean m_startThreads) {
        this.m_startThreads = m_startThreads;
        System.out.println("Start threads var was set:"+m_startThreads);
    }
}
