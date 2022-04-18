import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame{

    private List<Node> m_nodeList;
    private final List<JTextField> jTextFieldList = new ArrayList<>();
    private boolean m_startThreads = false;
    private final JComboBox startNodeCB;
    private final JComboBox endNodeCB;
    Container container;

    public GUI(List<Node> nodeList){
        m_nodeList = nodeList;
        this.setBounds(10,10,400,400);
        this.setTitle("Set the path");
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container = getContentPane();
        container.setLayout(null);

        addLabel("N1",20,20,20,20);
        addLabel("N2",20,60,20,20);
        addLabel("N3",20,100,20,20);
        addLabel("N4",20,140,20,20);
        addLabel("Start Node",180,80,100,20);
        addLabel("End Node",180,120,100,20);

        addTextField(50,20,100,20);
        addTextField(50,60,100,20);
        addTextField(50,100,100,20);
        addTextField(50,140,100,20);

        String[] nodeStrings = {"N1", "N2", "N3", "N4", "N5"};
        startNodeCB = new JComboBox(nodeStrings);
        startNodeCB.setSelectedIndex(4);
//        nodesList.setBounds(110,260,80,20);
        startNodeCB.setBounds(260,80,60,20);
        container.add(startNodeCB);
        //System.out.println("INDEX LA NODE ------------>"+nodesList.getItemCount());
        //nodesList.addActionListener();
        endNodeCB = new JComboBox(nodeStrings);
        endNodeCB.setSelectedIndex(4);
//        nodesList2.setBounds(110,300,80,20);
        endNodeCB.setBounds(260,120,60,20);
        container.add(endNodeCB);

        JButton jButton = new JButton("Save");
        jButton.setBounds(50,220,100,20);
        container.add(jButton);

        JButton sendData = new JButton("Send Data");
        sendData.setBounds(190,160,100,20);
        container.add(sendData);

        jButton.addActionListener(e -> {
            try {
                setNodes();
                setController();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        sendData.addActionListener(e -> {
            sendData();
        });
    }

    public void addLabel(String text, int x, int y, int width, int height){
        JLabel label = new JLabel(text);
        label.setBounds(x,y,width,height);
        container.add(label);
    }

    public void addTextField(int x, int y, int width, int height){
        JTextField jTextField = new JTextField();
        jTextField.setBounds(x,y,width,height);
        container.add(jTextField);
        jTextFieldList.add(jTextField);
    }

    public void setController() throws IOException {
        m_nodeList.get(m_nodeList.size()-1).sendRoutingTableTo(m_nodeList.get(0),getRoutingTable());
    }

    public void setNodes(){
        for(int i=0;i<4;i++){
            int targetNodeIndex = Integer.parseInt(jTextFieldList.get(i).getText().substring(1))-1;
            System.out.println("Local address for current node:::::"+m_nodeList.get(i).getM_localAddress());
            System.out.println("Local address for target node:::::"+m_nodeList.get(targetNodeIndex).getM_localAddress());
            m_nodeList.get(i).setM_targetAddress(m_nodeList.get(targetNodeIndex).getM_localAddress());
            m_nodeList.get(i).setM_targetPort(m_nodeList.get(targetNodeIndex).getM_localPort());
        }
        setM_startThreads(true);
    }

    public void sendData(){
        try {
            System.out.println("NODUL START ALES:"+startNodeCB.getSelectedIndex());
            System.out.println("LOCAL ADR START:"+m_nodeList.get(startNodeCB.getSelectedIndex()).getM_localAddress());
            System.out.println("NODUL END ALES:"+endNodeCB.getSelectedIndex());
            System.out.println("LOCAL ADR END:"+m_nodeList.get(endNodeCB.getSelectedIndex()).getM_localAddress());
            m_nodeList.get(startNodeCB.getSelectedIndex()).sendPackageTo(m_nodeList.get(endNodeCB.getSelectedIndex()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public Map<String, Map<String, String>> getRoutingTable(){
        Map<String, Map<String, String>> routing_table_map = new HashMap<>();
        for(int i=0;i<m_nodeList.size()-1;i++){
            Map<String,String> nodeRoutingTable = new HashMap<>();
            for (int j=i+1;j<m_nodeList.size()-1;j++){
                nodeRoutingTable.put(m_nodeList.get(j).getM_localAddress(),m_nodeList.get(i).getM_targetAddress());
            }
            int indexNode = i+1;
            routing_table_map.put("N"+indexNode,nodeRoutingTable);
        }
        return routing_table_map;
    }

    public boolean isM_startThreads() {
        return m_startThreads;
    }

    public void setM_startThreads(boolean m_startThreads) {
        this.m_startThreads = m_startThreads;
        System.out.println("Start threads variable was set:"+ m_startThreads);
    }

}
