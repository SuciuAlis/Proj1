import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GUI extends JFrame{
    private final List<Node> m_nodeList;
    private final List<Node> m_targetNodesList;
    private final List<JTextField> jTextFieldList = new ArrayList<>();
    private final JComboBox startNodeCB;
    private final JComboBox endNodeCB;
    private final JComboBox deleteNodeCB;
    Container container;
    public GUI(List<Node> nodeList){
        m_nodeList = nodeList;
        m_targetNodesList = new ArrayList<>();
        this.setBounds(10,10,400,400);
        this.setTitle("SDN simulation app");
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container = getContentPane();
        container.setLayout(null);
        addLabel("Set the path for nodes",20,20,160,20);
        addLabel("N1",20,60,20,20);
        addLabel("N2",20,100,20,20);
        addLabel("N3",20,140,20,20);
        addLabel("N4",20,180,20,20);
        addLabel("Start node",200,60,100,20);
        addLabel("End node",200,100,100,20);
        addLabel("Remove node from network:",200,200,180,20);
        addTextField(50,60,100,20,"N2");
        addTextField(50,100,100,20,"N3");
        addTextField(50,140,100,20,"N4");
        addTextField(50,180,100,20,"N5");
        String[] nodeStrings = {"N1", "N2", "N3", "N4", "N5"};
        String[] nodesDeleteStrings = {"N2", "N3", "N4"};
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
        saveButton.setBounds(50,280,100,20);
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

    public void addTextField(int x, int y, int width, int height, String value){
        JTextField jTextField = new JTextField(value);
        jTextField.setBounds(x,y,width,height);
        container.add(jTextField);
        jTextFieldList.add(jTextField);
    }

    public Node getNodeWithAddress(String address){
        for(Node node: m_nodeList){
            if(node.getM_localAddress().equalsIgnoreCase(address)){
                return node;
            }
        }
        System.out.println("[INFO - getNodeWithAddress(address)] - Node was not found");
        return null;
    }

    public void setNodes(){
        for(int i=0;i<4;i++){
            int targetNodeIndex = Integer.parseInt(jTextFieldList.get(i).getText().substring(1))-1;
            System.out.println("[INFO - setNodes()] - For node N"+(i+1)+" target index from text field is: "+(targetNodeIndex+1));
            m_nodeList.get(i).setM_targetAddress(m_nodeList.get(targetNodeIndex).getM_localAddress());
            System.out.println("[INFO - setNodes()] - For node N"+(i+1)+" target address was set: "+m_nodeList.get(targetNodeIndex).getM_localAddress());
            m_nodeList.get(i).setM_targetPort(m_nodeList.get(targetNodeIndex).getM_localPort());
            System.out.println("[INFO - setNodes()] - For node N"+(i+1)+" target port was set: "+m_nodeList.get(targetNodeIndex).getM_localPort());
        }
        m_targetNodesList.add(m_nodeList.get(0));
        for(int node=0;node<m_nodeList.size()-2;node++){
            m_targetNodesList.add(getNodeWithAddress(m_targetNodesList.get(node).getM_targetAddress()));
        }
        System.out.println("[INFO - setNodes()] - The list with the target nodes in the user's input order:");
        for (Node node: m_targetNodesList){
            System.out.println("****Node with address****"+node.getM_localAddress());
        }
        for(Node node:m_nodeList){
            node.setM_routing_table_map(getRoutingTable());
        }
        for(Node node:m_targetNodesList){
            node.setM_routing_table_map(getRoutingTable());
        }
    }

    public void setController() throws IOException {
        m_nodeList.get(m_nodeList.size()-1).sendRoutingTable(getRoutingTable());
        System.out.println("[INFO - setController()] - The routing table was sent from controller: "+(m_nodeList.get(m_nodeList.size()-1).getM_localAddress())+" to the first node in the network: "+m_nodeList.get(0).getM_localAddress());
    }

    public Map<String, Map<String, String>> getRoutingTable(){
        Map<String, Map<String, String>> routing_table_map = new HashMap<>();
        for(int i=0;i<m_nodeList.size()-2;i++){
            Map<String,String> nodeRoutingTable = new HashMap<>();
            for (int j=i+1;j<m_nodeList.size()-1;j++){
                nodeRoutingTable.put(m_targetNodesList.get(j).getM_localAddress(),m_targetNodesList.get(i).getM_targetAddress()+":"+m_targetNodesList.get(i).getM_targetPort());
            }
            routing_table_map.put(m_nodeList.get(i).getM_localAddress(),nodeRoutingTable);
        }
        return routing_table_map;
    }

    public void sendData(){
        try {
            System.out.println("[INFO - sendData()] - Start node: " + m_nodeList.get(startNodeCB.getSelectedIndex()).getM_localAddress());
            System.out.println("[INFO - sendData()] - End node: " + m_nodeList.get(endNodeCB.getSelectedIndex()).getM_localAddress());
            m_nodeList.get(m_nodeList.size()-1).setM_destinationAddress(m_nodeList.get(endNodeCB.getSelectedIndex()).getM_localAddress());
            m_nodeList.get(m_nodeList.size()-1).sendPackage();
            System.out.println("[INFO - sendData()] - Data packet was sent from controller:" + (m_nodeList.get(m_nodeList.size()-1).getM_localAddress())+" to the first node in the network: "+m_nodeList.get(0).getM_localAddress());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void deleteNode() throws IOException {
        System.out.println("Node to delete: "+deleteNodeCB.getSelectedIndex());
        Node deleteNode = m_nodeList.get(deleteNodeCB.getSelectedIndex()+1);
        System.out.println("Node address: "+deleteNode.getM_localAddress());
        Map<String, Map<String,String>> newRoutingTableMap = new HashMap<>();
        for(int i=0;i<m_nodeList.size()-2;i++){
            if (m_nodeList.get(i).getM_targetAddress().equalsIgnoreCase(deleteNode.getM_localAddress())) {
                Node previousNode = m_nodeList.get(i);
                String previousNodeAddress = previousNode.getM_localAddress();
                Map<String, String> oldMap = previousNode.getM_routing_table_map().get(previousNodeAddress);
                Map<String, String> newMap = new HashMap<>();
                for (String key : oldMap.keySet()) {
                    if (!key.equalsIgnoreCase(deleteNode.getM_localAddress())) {
                        if(oldMap.get(key).contains(deleteNode.getM_localAddress())){
                            newMap.put(key, deleteNode.getM_targetAddress()+":"+deleteNode.getM_targetPort());
                        }
                    }
                }
                newRoutingTableMap.put(previousNodeAddress, newMap);
                m_nodeList.get(i).setM_targetAddress(deleteNode.getM_targetAddress());
                m_nodeList.get(i).setM_targetPort(deleteNode.getM_targetPort());
            }else if(!m_nodeList.get(i).getM_localAddress().equalsIgnoreCase(deleteNode.getM_localAddress())){
                String otherNode = m_nodeList.get(i).getM_localAddress();
                Map<String,String> oldMap = m_nodeList.get(i).getM_routing_table_map().get(otherNode);
                Map<String,String> newMap = new HashMap<>();
                for(String key: oldMap.keySet()){
                    if (!key.equalsIgnoreCase(deleteNode.getM_localAddress())){
                        newMap.put(key,oldMap.get(key));
                    }
                }
                newRoutingTableMap.put(otherNode,newMap);
            }
        }
        System.out.println("Delete node index::"+deleteNodeCB.getSelectedIndex());
        for(Node node:m_nodeList){
            node.setM_routing_table_map(newRoutingTableMap);
        }
        m_nodeList.get(m_nodeList.size()-1).sendRoutingTable(newRoutingTableMap);
        System.out.println("[INFO - sendNewRoutingTable()] - The routing table was sent from controller: "+(m_nodeList.get(m_nodeList.size()-1).getM_localAddress())+" to the first node in the network: "+m_nodeList.get(0).getM_localAddress());
    }
}
