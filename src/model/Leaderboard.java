package model;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import we.getconnected.Main;
import we.getconnected.gui.MainPanel;

/**
 *
 * Laat gegevens van klasgenoten zien.
 * 
 * @author lou
 */

public class Leaderboard extends JPanel{
    private ArrayList<User> leaderboard; 
    private JLabel header, nameHeader;
    private JButton showMap, backToLeader;
    private JTable table;
    private JPanel bottomBar;
    
    //Constanten voor dimensies van labels/fields/panels
    private Rectangle LABEL_NAME_BOUNDS = new Rectangle(0, 70, MainPanel.BOTTOM_BAR.width, 50);
    
    private Rectangle BUTTON_SHOWMAP_BOUNDS = new Rectangle((MainPanel.BOTTOM_BAR.width-134) /2, 10,134,53);
    private Rectangle BUTTON_BACKTOLEADER_BOUNDS = new Rectangle((MainPanel.BOTTOM_BAR.width-134) /2,10,134,53);
    
    private Rectangle PANEL_BOTTOMBAR_BOUNDS = new Rectangle(0, 0, MainPanel.BOTTOM_BAR.width, MainPanel.BOTTOM_BAR.height);
    
    
    public Leaderboard(final ArrayList<User> users){
        leaderboard = users;
        setBounds(0, 0, MainPanel.MAP_AREA.width, MainPanel.MAP_AREA.height);
        setBackground(MainPanel.BACKGROUND_COLOR);
        
        header = new JLabel();
        header.setIcon(new ImageIcon(getClass().getResource("/media/LeaderboardHeader.png")));
        //X co-ordinaat voor de Leaderboard header. (image)
        int headerX = (MainPanel.MAP_AREA.width-header.getWidth())/2;
        header.setBounds(headerX, 10, header.getWidth(), header.getHeight());
        add(header);
        
        nameHeader = new JLabel("", SwingConstants.CENTER);
        nameHeader.setFont(new Font("Rockwell",Font.PLAIN,15));
        nameHeader.setBounds(LABEL_NAME_BOUNDS);
        
        
        table = new JTable();
        table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				"Username", "Naam", "Achternaam", "Landen", "Klas"}) {

			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		});
        //X co-ordinaat voor de leaderboard table;
        int tableX = (MainPanel.MAP_AREA.width-header.getWidth())/2;
        table.setBounds(tableX, 10, header.getWidth(), MainPanel.MAP_AREA.height-header.getHeight());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ((DefaultTableModel)table.getModel()).addRow(new Object[] {"Username","Voornaam","Achternaam","Landen", "Klas"});
        //Voeg user toe aan de table.
        for(User user:users){
            //User gegevens
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String userName = user.getUserName();
            String groupName = user.getGroupName();
            
            //Tel voltooide landen.
            int completedCountries = 0;
            int availableCountries  = user.getEurope().getLanden().size();
            for(Country land:user.getEurope().getLanden()){
                if(land.isCompleted()){
                   completedCountries++; 
                }
            }
            //Voeg land gegevens toe.
            String progress = completedCountries +" / "+availableCountries;
            ((DefaultTableModel)table.getModel()).addRow(new Object[] {userName,firstName,lastName,progress, groupName});
        }
        
        //Set table column grote.
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        table.setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        
        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener(){

            // Selection listener die showMap disabled als de bovenste column
            // geselecteerd is.
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (table.getSelectedRow() == 0){
                    showMap.setEnabled(false);
                }
                else{
                    showMap.setEnabled(true);
                }
            }
        });
        
        add(table);
        
        bottomBar = new JPanel();
        bottomBar.setLayout(null);
        bottomBar.setBackground(MainPanel.BACKGROUND_COLOR);
        bottomBar.setBounds(PANEL_BOTTOMBAR_BOUNDS);
        
        showMap = new JButton();
        showMap.setEnabled(false);
        showMap.setIcon(new ImageIcon(getClass().getResource("/media/ZieKaart.png")));
        showMap.setBounds(BUTTON_SHOWMAP_BOUNDS);
        showMap.addActionListener(new ActionListener(){

            //Action performed (button press) voor showMap. Laat de kaart van de geselecteerde
            // speler zien. De kaart kan niet gespeeld worden.
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table.getSelectedRow()!=-1 && table.getSelectedRow()!=0){
                    User user = getUser(table.getModel().getValueAt(table.getSelectedRow(), 0).toString());
                    Continent continent = user.getEurope();
                    
                    //Als het niet de kaart is van de ingelogte speler, kan hij
                    //de kaart niet spelen.
                    if(user.getUser_id()!=Main.getCurrentUser().getUser_id()){
                       continent.setPlayable(false);
                    }
                    
                    //Laat zien welke kaart je bekijkt.
                    nameHeader.setText("Je bekijkt " + user.getUserName() + " zijn kaart");
                    nameHeader.setVisible(true);
                    
                    
                    showMap.setVisible(false);
                    backToLeader.setVisible(true);
                    
                    
                    Main.getMainPanel().clearPanelMapArea();
                    Main.getMainPanel().showPanelMapArea(continent);
                    continent.updateWorldMap();
                }
            }
            
        });
        
        backToLeader = new JButton();
        backToLeader.setIcon(new ImageIcon(getClass().getResource("/media/Terug.png")));
        backToLeader.setBounds(BUTTON_BACKTOLEADER_BOUNDS);
        backToLeader.addActionListener(new ActionListener(){

            
            //Action performed (button press) event voor terug gaan naar het 
            //leaderboard. Maak oude buttons weer onzichtbaar.
            @Override
            public void actionPerformed(ActionEvent e) {
               Main.getMainPanel().showPanelMapArea(Main.getLeaderboard());
               nameHeader.setVisible(false);
               backToLeader.setVisible(false);
               showMap.setVisible(true);
            }
            
        });
        backToLeader.setVisible(false);
       
        bottomBar.add(nameHeader);
        bottomBar.add(backToLeader);
        bottomBar.add(showMap);
    }
    
    // Get en Set methode om de ArrayList leaderboard aan te roepen.
    public ArrayList<User> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(ArrayList<User> leaderboard) {
        this.leaderboard = leaderboard;
    }
    
    public JPanel getBottomBar(){
        return bottomBar;
    }
    
    /**
     * Vraag een user op uit de Arraylist leaderboard.
     * @param username
     * @return User with the username <username>
     */
    public User getUser(String username){
        for(User user:leaderboard){
            if(user.getUserName().equals(username)){
                return user;
            }
        }
        return null;
    }
}
