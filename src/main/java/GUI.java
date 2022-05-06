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
        addLabel("Start Node",200,60,100,20);
        addLabel("End Node",200,100,100,20);
        addLabel("Remove Node from network:",200,200,180,20);

        addTextField(50,20,100,20);
        addTextField(50,60,100,20);
        addTextField(50,100,100,20);
        addTextField(50,140,100,20);

        String[] nodeStrings = {"N1", "N2", "N3", "N4", "N5"};
//        String[] nodeStrings = new String[]{};
//        int length = 0;
//        for(int i = 0; i<m_nodeList.size();i++){
//            nodeStrings[length++] = "N"+i;
//        }
        startNodeCB = new JComboBox(nodeStrings);
        startNodeCB.setBounds(280,60,60,20);
        container.add(startNodeCB);
        endNodeCB = new JComboBox(nodeStrings);
        endNodeCB.setBounds(280,100,60,20);
        container.add(endNodeCB);
        deleteNodeCB = new JComboBox(nodeStrings);
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
                setController();
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

    public void deleteNode(){
        System.out.println("Node to delete: "+deleteNodeCB.getSelectedIndex());
        Node deletedNode = m_nodeList.get(deleteNodeCB.getSelectedIndex());
        Map<String, Map<String,String>> newRoutingTableMap = new HashMap<>();
        for(int i=0;i<4;i++){
            if (m_nodeList.get(i).getM_targetAddress().equalsIgnoreCase(deletedNode.getM_localAddress())){
                Node prevNode = m_nodeList.get(i);
                String prevN = "N"+(i+1);
                String nodeDel = "N"+(deleteNodeCB.getSelectedIndex()+1);
                Set<String> keys = prevNode.getM_routing_table_map().get(prevN).keySet();
                Map<String,String> newMap = new HashMap<>();
                for(String k : keys){
                    if(!k.equalsIgnoreCase(deletedNode.getM_localAddress())){
                        newMap.put(k,deletedNode.getM_routing_table_map().get(nodeDel).get(k));
                    }
                }
                newRoutingTableMap.put(prevN,newMap);
                m_nodeList.get(i).setM_targetAddress(deletedNode.getM_targetAddress());
                m_nodeList.get(i).setM_targetPort(deletedNode.getM_targetPort());
            }else if(!m_nodeList.get(i).getM_localAddress().equalsIgnoreCase(deletedNode.getM_localAddress())){
                String otherNode = "N"+(i+1);
                System.out.println("OTHER NODE:::::"+otherNode);
                newRoutingTableMap.put(otherNode,m_nodeList.get(i).getM_routing_table_map().get(otherNode));
            }
        }
        System.out.println("GATA");
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
