/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tc;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Databases.DBConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import strt.Login;

/**
 *
 * @author ADMIN
 */
public class Collector extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    public Collector() {
        initComponents();
        loadTypes();
        loadTrash(null);
        loadBags();
    }
    
    private int getLoggedInUserID() {
        return Login.loggedInUserID;
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void loadItemsForSelectedBag(int bagId) {
        DefaultTableModel model = (DefaultTableModel) tbItems.getModel();
        model.setRowCount(0);
        
        tbItems.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbItems.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbItems.getColumnModel().getColumnCount(); i++) {
            tbItems.getColumnModel().getColumn(i).setResizable(false);
        }
        
        tbItems.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbItems.getColumnModel().getColumn(2).setPreferredWidth(80);
        tbItems.getColumnModel().getColumn(3).setPreferredWidth(37);

        String sql = "SELECT w.name, w.weight, w.type, cw.quantity "
                + "FROM collected_waste cw "
                + "JOIN waste w ON cw.waste_id = w.waste_id "
                + "WHERE cw.bag_id = ?";

        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, bagId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String weight = rs.getDouble("weight") + " kg";
                    String type = rs.getString("type");
                    int quantity = rs.getInt("quantity");

                    model.addRow(new Object[]{name, weight, type, quantity});
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
        
        
    }
    
    private void loadTypes() {
        String sql = "SELECT DISTINCT type FROM waste";
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cbType.addItem(rs.getString("type"));
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private void loadBags() {
        DefaultTableModel model = (DefaultTableModel) tbBags.getModel();
        model.setRowCount(0);

        tbBags.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbBags.getColumnModel().getColumnCount(); i++) {
            tbBags.getColumnModel().getColumn(i).setResizable(false);
        }
        
        tbBags.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbBags.getColumnModel().getColumn(2).setPreferredWidth(102);

        // Updated SQL query to sum the quantity of waste per bag
        String sql = "SELECT b.bag_id, b.total_weight, COALESCE(SUM(cw.quantity), 0) AS total_quantity "
                + "FROM bags b "
                + "LEFT JOIN collected_waste cw ON b.bag_id = cw.bag_id "
                + "GROUP BY b.bag_id, b.total_weight";

        try (Connection conn = DBConnection.Connect()) {
            int user_id = getLoggedInUserID();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int bag_id = rs.getInt("bag_id");
                    String totalWeight = rs.getDouble("total_weight") + " kg";
                    int totalQuantity = rs.getInt("total_quantity");

                    model.addRow(new Object[]{bag_id, totalWeight, totalQuantity});
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private void loadTrash(String cb_type) {
        DefaultTableModel model = (DefaultTableModel) tbTrash.getModel();
        model.setRowCount(0);

        tbTrash.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTrash.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbTrash.getColumnModel().getColumnCount(); i++) {
            tbTrash.getColumnModel().getColumn(i).setResizable(false);
        }
        
        tbTrash.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbTrash.getColumnModel().getColumn(2).setPreferredWidth(102);
        
        String sql;
        if (cb_type == null || cb_type.isEmpty()) {
            sql = "SELECT * FROM waste";
        } else {
            sql = "SELECT * FROM waste WHERE type = ?";
        }
        
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (cb_type != null && !cb_type.isEmpty()) {
                    ps.setString(1, cb_type);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String weight = rs.getDouble("weight") + " kg";
                        String type = rs.getString("type");

                        model.addRow(new Object[]{name, weight, type});
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblLandmark = new javax.swing.JLabel();
        lblProvince = new javax.swing.JLabel();
        lblCleanup = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbType = new javax.swing.JComboBox<>();
        spQty = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbTrash = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        btnOthers = new javax.swing.JButton();
        btnAnimal = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbBags = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        btnAddBag = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblLandmark1 = new javax.swing.JLabel();
        lblProvince1 = new javax.swing.JLabel();
        lblCleanup1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnDeleteBag = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbItems = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Montserrat", 1, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(240, 240, 240));
        jPanel2.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        lblLandmark.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblLandmark.setText("Landmark");

        lblProvince.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblProvince.setText("Province");

        lblCleanup.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblCleanup.setText("Cleanup Site");

        jLabel4.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel4.setText("Philippines");

        jLabel1.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        jLabel1.setText("Site Information");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCleanup)
                            .addComponent(lblProvince))
                        .addGap(85, 85, 85)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLandmark)
                            .addComponent(jLabel4)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCleanup)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProvince)
                    .addComponent(lblLandmark))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        jLabel9.setText("TRASH COLLECTION");

        jLabel6.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel6.setText("What type?");

        cbType.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        cbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTypeActionPerformed(evt);
            }
        });

        spQty.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        spQty.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jLabel7.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel7.setText("How Many?");

        tbTrash.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        tbTrash.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Trash", "Weight", "Type"
            }
        ));
        tbTrash.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbTrash.setRowHeight(30);
        jScrollPane1.setViewportView(tbTrash);

        txtSearch.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/search-regular-24 (1).png"))); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSearchMouseClicked(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(153, 255, 153));
        btnAdd.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/plus-regular-24.png"))); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/default-image.png"))); // NOI18N

        btnRefresh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/refresh-regular-24.png"))); // NOI18N
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Montserrat", 1, 14)); // NOI18N
        jLabel8.setText("MOST LIKELY TO FIND ITEMS");

        btnBack.setBackground(new java.awt.Color(255, 153, 153));
        btnBack.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-out-regular-24.png"))); // NOI18N
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnOthers.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        btnOthers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/customize-solid-24.png"))); // NOI18N
        btnOthers.setText("Others");
        btnOthers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOthers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOthersActionPerformed(evt);
            }
        });

        btnAnimal.setBackground(new java.awt.Color(239, 167, 132));
        btnAnimal.setFont(new java.awt.Font("Montserrat", 1, 14)); // NOI18N
        btnAnimal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/injured-dog.png"))); // NOI18N
        btnAnimal.setText(" Animal");
        btnAnimal.setToolTipText("Did you find a dead/injured animal?");
        btnAnimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAnimal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnimalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(cbType, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(spQty, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnAdd))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnOthers)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnAnimal, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 6, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(6, 6, 6)
                        .addComponent(cbType, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(6, 6, 6)
                        .addComponent(spQty, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOthers)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAnimal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Home", new javax.swing.ImageIcon(getClass().getResource("/assets/home-solid-24-black.png")), jPanel2); // NOI18N

        jPanel6.setBackground(new java.awt.Color(240, 240, 240));

        tbBags.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        tbBags.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Total Weight", "No. of Items"
            }
        ));
        tbBags.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbBags.setRowHeight(30);
        tbBags.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBagsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbBags);

        jLabel2.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        jLabel2.setText("TRASH BAGS");

        btnAddBag.setBackground(new java.awt.Color(153, 255, 153));
        btnAddBag.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        btnAddBag.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/plus-regular-24.png"))); // NOI18N
        btnAddBag.setText("Add");
        btnAddBag.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddBag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBagActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        lblLandmark1.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblLandmark1.setText("Landmark");

        lblProvince1.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblProvince1.setText("Province");

        lblCleanup1.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblCleanup1.setText("Cleanup Site");

        jLabel5.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel5.setText("Philippines");

        jLabel3.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        jLabel3.setText("Site Information");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCleanup1)
                            .addComponent(lblProvince1))
                        .addGap(85, 85, 85)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLandmark1)
                            .addComponent(jLabel5)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCleanup1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProvince1)
                    .addComponent(lblLandmark1))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        btnLogout.setBackground(new java.awt.Color(255, 153, 153));
        btnLogout.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-out-regular-24.png"))); // NOI18N
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnDeleteBag.setBackground(new java.awt.Color(255, 153, 153));
        btnDeleteBag.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        btnDeleteBag.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/trash-regular-24.png"))); // NOI18N
        btnDeleteBag.setText("Delete");
        btnDeleteBag.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteBag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteBagActionPerformed(evt);
            }
        });

        tbItems.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        tbItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Weight", "Type", "Qty"
            }
        ));
        tbItems.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbItems.setRowHeight(30);
        jScrollPane3.setViewportView(tbItems);

        jLabel10.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        jLabel10.setText("ITEMS");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(btnDeleteBag)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddBag))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddBag, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteBag, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Trash Bags", new javax.swing.ImageIcon(getClass().getResource("/assets/recycle-bag.png")), jPanel6); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTypeActionPerformed
        String type = cbType.getSelectedItem().toString();
        loadTrash(type);
    }//GEN-LAST:event_cbTypeActionPerformed

    private void searchTrash(String query) {
        DefaultTableModel model = (DefaultTableModel) tbTrash.getModel();
        model.setRowCount(0); // Clear the existing rows

        String sql = "SELECT * FROM waste WHERE name LIKE ?";

        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + query + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String weight = rs.getDouble("weight") + " kg";
                        String type = rs.getString("type");

                        model.addRow(new Object[]{name, weight, type});
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private void btnSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSearchMouseClicked
        String searchQuery = txtSearch.getText().trim();
        if (!searchQuery.isEmpty()) {
            searchTrash(searchQuery);
        } else {
            loadTrash(null);
        }
    }//GEN-LAST:event_btnSearchMouseClicked

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        loadTrash(null);
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Log Out?", "Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Login login = Login.getInstance();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnOthersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOthersActionPerformed
        String name = JOptionPane.showInputDialog(this, "Enter the name of the trash:");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Trash name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String type = JOptionPane.showInputDialog(this, "Enter the type of the trash:");
        if (type == null || type.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Trash type cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String weightStr = JOptionPane.showInputDialog(this, "Enter the weight of the trash (in kg):");
        if (weightStr == null || weightStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Trash weight cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid weight format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt for quantity
        String qtyStr = JOptionPane.showInputDialog(this, "Enter the quantity of the trash:");
        if (qtyStr == null || qtyStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quantity cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                throw new NumberFormatException(); // Ensure quantity is greater than zero
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format. Please enter a positive integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return; // User canceled the file selection
        }

        File fileToUpload = fileChooser.getSelectedFile();
        byte[] imageBytes = null;

        // Read the image file into a byte array
        try (FileInputStream fis = new FileInputStream(fileToUpload)) {
            imageBytes = new byte[(int) fileToUpload.length()];
            fis.read(imageBytes);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading image file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert the new trash into the waste table
        String insertWasteSql = "INSERT INTO waste (name, type, weight, picture) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement psInsertWaste = conn.prepareStatement(insertWasteSql)) {
                psInsertWaste.setString(1, name);
                psInsertWaste.setString(2, type);
                psInsertWaste.setDouble(3, weight);
                psInsertWaste.setBytes(4, imageBytes); // Set the image bytes
                psInsertWaste.executeUpdate();
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return;
        }

        // Now, prompt the user to select a bag
        List<String> bags = getBags();
        if (bags.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bags available. Please create a bag first.", "No Bags", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bagId = (String) JOptionPane.showInputDialog(this, "Select a bag:", "Select Bag",
                JOptionPane.QUESTION_MESSAGE, null, bags.toArray(), bags.get(0));

        if (bagId != null) {
            // Add the new trash to the selected bag
            addCollectedWaste(name, type, bagId, qty); // Pass the quantity to the method
        }
    }//GEN-LAST:event_btnOthersActionPerformed

    private int getWasteID(String name, String type) {
        String sql = "SELECT waste_id FROM waste WHERE name = ? AND type = ?";
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, type);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int waste_id = rs.getInt("waste_id");
                    return waste_id;
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
        return 0;
    }
    
    private void addCollectedWaste(String name, String type, String bagId, int qty) {
        String addSql = "INSERT INTO collected_waste (waste_id, bag_id, quantity, collected_at) VALUES (?, ?, ?, NOW())";
        String weightSql = "SELECT weight FROM waste WHERE name = ? AND type = ?";
        String updateBagSql = "UPDATE bags SET total_weight = total_weight + ? WHERE bag_id = ?";
        String getBagWeightSql = "SELECT total_weight FROM bags WHERE bag_id = ?";

        try (Connection conn = DBConnection.Connect()) {
            // Get the weight of the waste
            double wasteWeight = 0.0;
            try (PreparedStatement psWeight = conn.prepareStatement(weightSql)) {
                psWeight.setString(1, name);
                psWeight.setString(2, type);
                ResultSet rsWeight = psWeight.executeQuery();

                if (rsWeight.next()) {
                    wasteWeight = rsWeight.getDouble("weight");
                } else {
                    JOptionPane.showMessageDialog(this, "Waste not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Get the current total weight of the bag
            double currentBagWeight = 0.0;
            try (PreparedStatement psGetBagWeight = conn.prepareStatement(getBagWeightSql)) {
                psGetBagWeight.setInt(1, Integer.parseInt(bagId));
                ResultSet rsBagWeight = psGetBagWeight.executeQuery();

                if (rsBagWeight.next()) {
                    currentBagWeight = rsBagWeight.getDouble("total_weight");
                }
            }

            // Calculate the new total weight
            double totalWeightToAdd = wasteWeight * qty; // Calculate the total weight to add
            double newTotalWeight = currentBagWeight + totalWeightToAdd;

            // Check if the new total weight exceeds the limit
            if (newTotalWeight > 10.0) {
                JOptionPane.showMessageDialog(this, "Adding this trash would exceed the bag's weight limit of 10 kg.", "Weight Limit Exceeded", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the waste ID for the newly inserted trash
            int wasteId = getWasteID(name, type);
            if (wasteId == 0) {
                JOptionPane.showMessageDialog(this, "Waste not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add the collected waste
            try (PreparedStatement psAdd = conn.prepareStatement(addSql)) {
                psAdd.setInt(1, wasteId);
                psAdd.setInt(2, Integer.parseInt(bagId));
                psAdd.setInt(3, qty);
                psAdd.executeUpdate();
            }

            // Update the total weight of the bag
            try (PreparedStatement psUpdateBag = conn.prepareStatement(updateBagSql)) {
                psUpdateBag.setDouble(1, totalWeightToAdd);
                psUpdateBag.setInt(2, Integer.parseInt(bagId));
                psUpdateBag.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Waste added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBags();
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private List<String> getBags() {
        List<String> bags = new ArrayList<>();
        String sql = "SELECT bag_id FROM bags"; // Adjust the query as needed
        int user_id = getLoggedInUserID();
        
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    bags.add(rs.getString("bag_id")); // Assuming you want to display bag_id
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
        return bags;
    }
    
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        int selectedRow = tbTrash.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trash item to add.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = tbTrash.getValueAt(selectedRow, 0).toString();
        String type = tbTrash.getValueAt(selectedRow, 2).toString();
        int qty = (int) spQty.getValue();

        // Get the list of bags
        List<String> bags = getBags();
        if (bags.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bags available. Please create a bag first.", "No Bags", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show a dialog to select a bag
        String bagId = (String) JOptionPane.showInputDialog(this, "Select a bag:", "Select Bag",
                JOptionPane.QUESTION_MESSAGE, null, bags.toArray(), bags.get(0));

        if (bagId != null) {
            // Proceed to add the collected waste
            addCollectedWaste(name, type, bagId, qty);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnAddBagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBagActionPerformed
        String sql = "INSERT INTO bags (user_id, total_weight) VALUES (?, 0)";
        try (Connection conn = DBConnection.Connect()) {
            int user_id = getLoggedInUserID();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, user_id);
                ps.executeUpdate();
            }
            loadBags();
            JOptionPane.showMessageDialog(this, "Bag added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnAddBagActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Log Out?", "Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Login login = Login.getInstance();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnDeleteBagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBagActionPerformed
        int selectedRow = tbBags.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bag to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the bag ID from the selected row
        int bagId = (int) tbBags.getValueAt(selectedRow, 0); // Assuming the first column is bag_id

        // Check if there are any collected waste items in the selected bag
        String checkWasteSql = "SELECT COUNT(*) AS count FROM collected_waste WHERE bag_id = ?";
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkWasteSql)) {
                psCheck.setInt(1, bagId);
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) {
                    int count = rsCheck.getInt("count");
                    if (count > 0) {
                        // There are collected waste items in the bag
                        JOptionPane.showMessageDialog(this, "Cannot delete the bag because it contains collected waste.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return;
        }

        // Proceed to delete the bag if no collected waste is found
        String deleteBagSql = "DELETE FROM bags WHERE bag_id = ?";
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement psDelete = conn.prepareStatement(deleteBagSql)) {
                psDelete.setInt(1, bagId);
                psDelete.executeUpdate();
                loadBags(); // Refresh the bags table
                JOptionPane.showMessageDialog(this, "Bag deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnDeleteBagActionPerformed

    private void tbBagsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbBagsMouseClicked
        int selectedRow = tbBags.getSelectedRow();
        if (selectedRow != -1) {
            int bagId = (int) tbBags.getValueAt(selectedRow, 0); // Assuming the first column is bag_id
            loadItemsForSelectedBag(bagId); // Load items for the selected bag
        }
    }//GEN-LAST:event_tbBagsMouseClicked

    private void btnAnimalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnimalActionPerformed
        // Create a panel to hold the input fields
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Input for animal name
        JTextField animalNameField = new JTextField();
        panel.add(new JLabel("Enter the name of the animal:"));
        panel.add(animalNameField);

        // Radio buttons for status
        JRadioButton injuredButton = new JRadioButton("Injured");
        JRadioButton deadButton = new JRadioButton("Dead");
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(injuredButton);
        statusGroup.add(deadButton);
        panel.add(new JLabel("Status:"));
        panel.add(injuredButton);
        panel.add(deadButton);

        // Radio button for tangled
        JRadioButton tangledYesButton = new JRadioButton("Yes");
        JRadioButton tangledNoButton = new JRadioButton("No");
        ButtonGroup tangledGroup = new ButtonGroup();
        tangledGroup.add(tangledYesButton);
        tangledGroup.add(tangledNoButton);
        panel.add(new JLabel("Is it tangled?"));
        panel.add(tangledYesButton);
        panel.add(tangledNoButton);

        // Input for material if tangled
        JTextField materialField = new JTextField();
        panel.add(new JLabel("If tangled, what material was used? (Leave blank if not tangled):"));
        panel.add(materialField);

        // Initially disable the material field
        materialField.setEnabled(false);

        // Add action listeners to tangled radio buttons
        tangledYesButton.addActionListener(e -> materialField.setEnabled(true));
        tangledNoButton.addActionListener(e -> materialField.setEnabled(false));

        // Show the dialog
        int result = JOptionPane.showConfirmDialog(this, panel, "Animal Information", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return; // User canceled
        }

        // Get the input values
        String animalName = animalNameField.getText().trim();
        String status = injuredButton.isSelected() ? "injured" : "dead";
        boolean isTangled = tangledYesButton.isSelected();
        String material = isTangled ? materialField.getText().trim() : null;

        // Validate inputs
        if (animalName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Animal name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert the data into the database
        String insertAnimalSql = "INSERT INTO animal (name, status, tangled, material) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement psInsertAnimal = conn.prepareStatement(insertAnimalSql)) {
                psInsertAnimal.setString(1, animalName);
                psInsertAnimal.setString(2, status);
                psInsertAnimal.setBoolean(3, isTangled);
                psInsertAnimal.setString(4, isTangled ? material : null); // Set material only if tangled
                psInsertAnimal.executeUpdate();
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Animal information added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnAnimalActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Collector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Collector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Collector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Collector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Collector().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddBag;
    private javax.swing.JButton btnAnimal;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDeleteBag;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnOthers;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JLabel btnSearch;
    private javax.swing.JComboBox<String> cbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCleanup;
    private javax.swing.JLabel lblCleanup1;
    private javax.swing.JLabel lblLandmark;
    private javax.swing.JLabel lblLandmark1;
    private javax.swing.JLabel lblProvince;
    private javax.swing.JLabel lblProvince1;
    private javax.swing.JSpinner spQty;
    private javax.swing.JTable tbBags;
    private javax.swing.JTable tbItems;
    private javax.swing.JTable tbTrash;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
