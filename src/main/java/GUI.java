import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GUI extends JFrame{
    private final List<Node> m_nodeList;
    private final List<JTextField> jTextFieldList = new ArrayList<>();
    private boolean m_startThreads = false;
    private final JComboBox startNodeCB;
    private final JComboBox endNodeCB;
    private final JComboBox deleteNodeCB;
    Container container;
    String[] nodeStrings = {"N1", "N2", "N3", "N4", "N5"};
    String[] nodesDeleteStrings = {"N2", "N3", "N4"};

    public GUI(List<Node> nodeList){
        m_nodeList = nodeList;
        this.setBounds(10,10,400,400);
        this.setTitle("SDN simulation app");
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container = getContentPane();
        container.setLayout(null);

        addLabel("N1",20,20,20,20);
        addLabel("N2",20,60,20,20);
        addLabel("N3",20,100,20,20);
        addLabel("N4",20,140,20,20);
        addLabel("Start Node",200,60,100,20);
        addLabel("End Node",200,100,100,20);
        addLabel("Remove Node from network:",200,200,180,20);

        addTextField("N2",50,20,100,20);
        addTextField("N3",50,60,100,20);
        addTextField("N4",50,100,100,20);
        addTextField("N5",50,140,100,20);

        startNodeCB = new JComboBox(nodeStrings);
        startNodeCB.setBounds(280,60,60,20);
        container.add(startNodeCB);
        endNodeCB = new JComboBox(nodeStrings);
        endNodeCB.setBounds(280,100,60,20);
        container.add(endNodeCB);
        deleteNodeCB = new JComboBox(nodesDeleteStrings);
        deleteNodeCB.setBounds(200,230,60,20);
        container.add(deleteNodeCB);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50,200,100,20);
        container.add(saveButton);

        JButton sendDataButton = new JButton("Send Data");
        sendDataButton.setBounds(220,150,100,20);
        container.add(sendDataButton);

        JButton deleteNodeButton = new JButton("Delete");
        deleteNodeButton.setBounds(220,280,100,20);
        container.add(deleteNodeButton);

        saveButton.addActionListener(e -> {
            try {
                setNodes();
                setController();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        sendDataButton.addActionListener(e -> {
            sendData();
        });

        deleteNodeButton.addActionListener(e -> {
            try {
                deleteNode();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void addLabel(String text, int x, int y, int width, int height){
        JLabel label = new JLabel(text);
        label.setBounds(x,y,width,height);
        container.add(label);
    }

    public void addTextField(String value, int x, int y, int width, int height){
        JTextField jTextField = new JTextField(value);
        jTextField.setBounds(x,y,width,height);
        container.add(jTextField);
        jTextFieldList.add(jTextField);
    }

    public void setController() throws IOException {
        m_nodeList.get(m_nodeList.size()-1).sendRoutingTable(getRoutingTable());
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

    public void deleteNode() throws IOException {
        System.out.println("Node to delete: "+deleteNodeCB.getSelectedIndex());
        Node deletedNode = m_nodeList.get(deleteNodeCB.getSelectedIndex()+1);
        System.out.println("Node address: "+deletedNode.getM_localAddress());
        Map<String, Map<String,String>> newRoutingTableMap = new HashMap<>();
        for(int i=0;i<m_nodeList.size()-2;i++){
            if (m_nodeList.get(i).getM_targetAddress().equalsIgnoreCase(deletedNode.getM_localAddress())) {
                Node previousNode = m_nodeList.get(i);
                String prevN = previousNode.getM_localAddress();
                String nodeToDelete = m_nodeList.get(deleteNodeCB.getSelectedIndex()).getM_localAddress();
                Map<String, Map<String, String>> theRoutingTableMap = previousNode.getM_routing_table_map();
                Map<String, String> t2 = theRoutingTableMap.get(prevN);
                Set<String> keys = t2.keySet();
                Map<String, String> newMap = new HashMap<>();
                for (String k : keys) {
                    if (!k.equalsIgnoreCase(deletedNode.getM_localAddress())) {
                        if(t2.get(k).equalsIgnoreCase(deletedNode.getM_localAddress())){
                            newMap.put(k, deletedNode.getM_targetAddress());
                        }else{
                            newMap.put(k, deletedNode.getM_routing_table_map().get(nodeToDelete).get(k));
                        }
                    }
                }
                newRoutingTableMap.put(prevN, newMap);
                m_nodeList.get(i).setM_targetAddress(deletedNode.getM_targetAddress());
                m_nodeList.get(i).setM_targetPort(deletedNode.getM_targetPort());
            }else if(!m_nodeList.get(i).getM_localAddress().equalsIgnoreCase(deletedNode.getM_localAddress())){
                String otherNode = m_nodeList.get(i).getM_localAddress();
                newRoutingTableMap.put(otherNode,m_nodeList.get(i).getM_routing_table_map().get(otherNode));
            }
        }
        System.out.println("Delete node index::"+deleteNodeCB.getSelectedIndex());
        m_nodeList.get(m_nodeList.size()-1).sendRoutingTable(newRoutingTableMap);
    }
    public void sendData(){
        try {
            System.out.println("Start Node: "+startNodeCB.getSelectedIndex());
            System.out.println("Local address of the start node: "+m_nodeList.get(startNodeCB.getSelectedIndex()).getM_localAddress());
            System.out.println("End Node: "+endNodeCB.getSelectedIndex());
            System.out.println("Local address of the end node: "+m_nodeList.get(endNodeCB.getSelectedIndex()).getM_localAddress());
            if (startNodeCB.getSelectedIndex() == endNodeCB.getSelectedIndex()){
                JOptionPane.showMessageDialog(this, "Nodes must be different!");
            }else{
                m_nodeList.get(startNodeCB.getSelectedIndex()).sendPackageTo(m_nodeList.get(endNodeCB.getSelectedIndex()));
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public Map<String, Map<String, String>> getRoutingTable(){
        Map<String, Map<String, String>> routing_table_map = new HashMap<>();
        for(int i=0;i<m_nodeList.size()-2;i++){
            Map<String,String> nodeRoutingTable = new HashMap<>();
            for (int j=i+1;j<m_nodeList.size()-1;j++){
                nodeRoutingTable.put(m_nodeList.get(j).getM_localAddress(),m_nodeList.get(i).getM_targetAddress());
            }
            routing_table_map.put(m_nodeList.get(i).getM_localAddress(),nodeRoutingTable);
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
