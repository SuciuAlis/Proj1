import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame{

    private List<Node> m_nodeList;
    private List<JTextField> jTextFieldList = new ArrayList<>();
    private boolean m_startThreads = false;

    public GUI(List<Node> nodeList){
        m_nodeList = nodeList;
        this.setBounds(10,10,400,400);
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
        JTextField n1TF = new JTextField();
        n1TF.setBounds(50,20,100,20);
        container.add(n1TF);
        jTextFieldList.add(n1TF);
        JTextField n2TF = new JTextField();
        n2TF.setBounds(50,60,100,20);
        container.add(n2TF);
        jTextFieldList.add(n2TF);
        JTextField n3TF = new JTextField();
        n3TF.setBounds(50,100,100,20);
        container.add(n3TF);
        jTextFieldList.add(n3TF);
        JTextField n4TF = new JTextField();
        n4TF.setBounds(50,140,100,20);
        container.add(n4TF);
        jTextFieldList.add(n4TF);
        JTextField n5TF = new JTextField();
        n5TF.setBounds(50,180,100,20);
        container.add(n5TF);
        jTextFieldList.add(n5TF);
        JButton jButton = new JButton("Save");
        jButton.setBounds(50,220,100,20);
        container.add(jButton);

        JLabel startNode = new JLabel("Start Node");
        //startNode.setBounds(20,260,100,20);
        startNode.setBounds(180,80,100,20);
        container.add(startNode);
        JLabel endNode = new JLabel("End Node");
//        endNode.setBounds(20,300,100,20);
        endNode.setBounds(180,120,100,20);
        container.add(endNode);

        String[] nodeStrings = {"N1", "N2", "N3", "N4", "N5"};
        JComboBox startNodeCB = new JComboBox(nodeStrings);
        startNodeCB.setSelectedIndex(4);
//        nodesList.setBounds(110,260,80,20);
        startNodeCB.setBounds(260,80,60,20);
        container.add(startNodeCB);
        //System.out.println("INDEX LA NODE ------------>"+nodesList.getItemCount());
        //nodesList.addActionListener();
        JComboBox endNodeCB = new JComboBox(nodeStrings);
        endNodeCB.setSelectedIndex(4);
//        nodesList2.setBounds(110,300,80,20);
        endNodeCB.setBounds(260,120,60,20);
        container.add(endNodeCB);

        JButton sendData = new JButton("Send Data");
//        sendData.setBounds(200,280,100,20);
        sendData.setBounds(190,160,100,20);
        container.add(sendData);

        jButton.addActionListener(e -> {
            setNodes();
            //setRoutingTable();
        });
        sendData.addActionListener(e -> {
            try {
                System.out.println("NODUL START ALES:"+startNodeCB.getSelectedIndex());
                System.out.println("NODUL END ALES:"+endNodeCB.getSelectedIndex());
                m_nodeList.get(startNodeCB.getSelectedIndex()).sendPackageTo(m_nodeList.get(endNodeCB.getSelectedIndex()));
                //m_nodeList.get(startNodeCB.getSelectedIndex()).sendPackage(m_nodeList.get(endNodeCB.getSelectedIndex()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void setNodes(){
        for(int i=0;i<4;i++) {
            int targetNodeIndex = Integer.parseInt(jTextFieldList.get(i).getText().substring(1))-1;
            System.out.println("Local address for current node:::::"+m_nodeList.get(i).getM_localAddress());
            System.out.println("Local address for target node:::::"+m_nodeList.get(targetNodeIndex).getM_localAddress());
            m_nodeList.get(i).setM_targetAddress(m_nodeList.get(targetNodeIndex).getM_localAddress());
            m_nodeList.get(i).setM_targetPort(m_nodeList.get(targetNodeIndex).getM_localPort());
        }
        setM_startThreads(true);
    }

    public void setRoutingTable(){
        for(Node currentNode: m_nodeList){
            Map<String, String> routing_table_map = new HashMap<>();
            for(int i=m_nodeList.indexOf(currentNode)+1;i<m_nodeList.size();i++){
                routing_table_map.put(m_nodeList.get(i).getM_localAddress(),currentNode.getM_targetAddress());
                System.out.println("**for node***"+currentNode.getM_localAddress()+"****destination***"+m_nodeList.get(i).getM_localAddress()+"***next_hop***"+currentNode.getM_targetAddress());
            }
            currentNode.setM_routing_table_map(routing_table_map);
        }
    }

    public boolean isM_startThreads() {
        return m_startThreads;
    }

    public void setM_startThreads(boolean m_startThreads) {
        this.m_startThreads = m_startThreads;
        System.out.println("Start threads var was set:"+m_startThreads);
    }
}
