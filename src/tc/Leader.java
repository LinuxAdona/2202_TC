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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
/**
 *
 * @author ADMIN
 */
public class Leader extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    public Leader() {
        initComponents();
        loadTypes();
        loadTrash(null);
        loadBags();
        loadCards();
        loadTrashChart();
        loadStudents();
        loadAnimals();
    }
    
    private int getLoggedInUserID() {
        return Login.loggedInUserID;
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void loadStudents() {
        String sql = "SELECT u.first_name, u.last_name, "
                + "FLOOR(DATEDIFF(NOW(), u.birth_date) / 365.25) AS age, "
                + "UCASE(LEFT(u.gender, 1)) AS gender, "
                + "COALESCE(SUM(cw.quantity), 0) AS total_items "
                + "FROM users u "
                + "LEFT JOIN collected_waste cw ON u.user_id = cw.user_id "
                + "WHERE u.role = 'collector' "
                + "GROUP BY u.user_id";

        DefaultTableModel model = (DefaultTableModel) tbCollectors.getModel();
        model.setRowCount(0); 
        
        tbCollectors.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbCollectors.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbCollectors.getColumnModel().getColumnCount(); i++) {
            tbCollectors.getColumnModel().getColumn(i).setResizable(false);
        }
        
        tbCollectors.getColumnModel().getColumn(0).setPreferredWidth(204);
        tbCollectors.getColumnModel().getColumn(1).setPreferredWidth(40);
        tbCollectors.getColumnModel().getColumn(2).setPreferredWidth(45);
        tbCollectors.getColumnModel().getColumn(3).setPreferredWidth(40);

        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int age = rs.getInt("age");
                    String gender = rs.getString("gender");
                    int totalItems = rs.getInt("total_items");

                    model.addRow(new Object[]{firstName + " " + lastName, age, gender, totalItems});
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private void loadAnimals() {
        String sql = "SELECT name, CONCAT(UCASE(LEFT(status, 1)), SUBSTRING(status, 2))status, tangled, material FROM animal";

        DefaultTableModel model = (DefaultTableModel) tbAnimals.getModel();
        model.setRowCount(0);
        
        tbAnimals.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbAnimals.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbAnimals.getColumnModel().getColumnCount(); i++) {
            tbAnimals.getColumnModel().getColumn(i).setResizable(false);
        }
        
        tbAnimals.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbAnimals.getColumnModel().getColumn(1).setPreferredWidth(60);
        tbAnimals.getColumnModel().getColumn(3).setPreferredWidth(129);

        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String status = rs.getString("status");
                    boolean tangled = rs.getBoolean("tangled");
                    String material = rs.getString("material");

                    model.addRow(new Object[]{name, status, tangled ? "Yes" : "No", material});
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private void loadTrashChart() {
        String sql = "SELECT w.type, SUM(cw.quantity) AS total_quantity "
                + "FROM collected_waste cw "
                + "JOIN waste w ON cw.waste_id = w.waste_id "
                + "GROUP BY w.type";

        DefaultPieDataset dataset = new DefaultPieDataset();

        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    int totalQuantity = rs.getInt("total_quantity");
                    dataset.setValue(type, totalQuantity);
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return;
        }

        // Create the pie chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Trash Types Distribution", // Chart title
                dataset, // Dataset
                true, // Include legend
                true, // Tooltips
                false // URLs
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        // Create a ChartPanel and add it to jPanel2
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(jPanel7.getWidth(), jPanel7.getHeight()));

        // Clear existing components and add the chart panel
        jPanel7.removeAll();
        jPanel7.setLayout(new BorderLayout());
        jPanel7.add(chartPanel, BorderLayout.CENTER);
        jPanel7.revalidate();
        jPanel7.repaint();
    }
    
    private void loadCards() {
        String tSql = "SELECT SUM(quantity) AS total_collection FROM collected_waste";
        String sSql = "SELECT COUNT(*) AS total_students FROM users WHERE role = 'collector'";
        try (Connection conn = DBConnection.Connect()) {
            try (PreparedStatement psT = conn.prepareStatement(tSql); 
                    PreparedStatement psS = conn.prepareStatement(sSql);
                    ResultSet rsT = psT.executeQuery();
                    ResultSet rsS = psS.executeQuery()) {
                String totalCollection = "0";
                String totalStudents = "0";
                if (rsT.next()) { 
                    totalCollection = rsT.getString("total_collection");
                }
                if (rsS.next()) {
                    totalStudents = rsS.getString("total_students");
                }
                lblTrashCollected.setText(totalCollection);
                lblStudents.setText(totalStudents);
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
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

        jPanel3 = new javax.swing.JPanel();
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
        btnBack = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblTrashCollected = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblStudents = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        lblLandmark3 = new javax.swing.JLabel();
        lblProvince3 = new javax.swing.JLabel();
        lblCleanup3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        cbType = new javax.swing.JComboBox<>();
        spQty = new javax.swing.JSpinner();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbTrash = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnBack2 = new javax.swing.JButton();
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
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        lblLandmark2 = new javax.swing.JLabel();
        lblProvince2 = new javax.swing.JLabel();
        lblCleanup2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnBack1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbCollectors = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbAnimals = new javax.swing.JTable();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Montserrat", 1, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(240, 240, 240));
        jPanel2.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        lblLandmark.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblLandmark.setText("Batangass State University");

        lblProvince.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblProvince.setText("Batangas");

        lblCleanup.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblCleanup.setText("Beach");

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
                .addContainerGap(17, Short.MAX_VALUE))
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
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        jLabel9.setText("DASHBOARD");

        btnBack.setBackground(new java.awt.Color(255, 153, 153));
        btnBack.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-out-regular-24.png"))); // NOI18N
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/garbage.png"))); // NOI18N

        lblTrashCollected.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        lblTrashCollected.setText("0");

        jLabel19.setFont(new java.awt.Font("Montserrat", 1, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Trash Collected");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(74, 74, 74)
                                .addComponent(lblTrashCollected)))
                        .addGap(0, 17, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTrashCollected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/group.png"))); // NOI18N

        lblStudents.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        lblStudents.setText("0");

        jLabel20.setFont(new java.awt.Font("Montserrat", 1, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Students");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStudents)
                .addGap(73, 73, 73))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStudents)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 230, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel9))
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Home", new javax.swing.ImageIcon(getClass().getResource("/assets/home-solid-24-black.png")), jPanel2); // NOI18N

        jPanel10.setBackground(new java.awt.Color(240, 240, 240));
        jPanel10.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        lblLandmark3.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblLandmark3.setText("Batangas State University");

        lblProvince3.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblProvince3.setText("Batangass");

        lblCleanup3.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblCleanup3.setText("Beach");

        jLabel17.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel17.setText("Philippines");

        jLabel21.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        jLabel21.setText("Site Information");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCleanup3)
                            .addComponent(lblProvince3))
                        .addGap(85, 85, 85)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLandmark3)
                            .addComponent(jLabel17)))
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCleanup3)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProvince3)
                    .addComponent(lblLandmark3))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel22.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        jLabel22.setText("TRASH COLLECTION");

        jLabel23.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel23.setText("What type?");

        cbType.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        cbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTypeActionPerformed(evt);
            }
        });

        spQty.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        spQty.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jLabel24.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel24.setText("How Many?");

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

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/default-image.png"))); // NOI18N

        btnRefresh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/refresh-regular-24.png"))); // NOI18N
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Montserrat", 1, 14)); // NOI18N
        jLabel26.setText("MOST LIKELY TO FIND ITEMS");

        btnBack2.setBackground(new java.awt.Color(255, 153, 153));
        btnBack2.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        btnBack2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-out-regular-24.png"))); // NOI18N
        btnBack2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBack2ActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(btnBack2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(cbType, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(spQty, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnAdd))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel26))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(btnOthers)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(btnAnimal, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 8, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22)
                    .addComponent(btnBack2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(6, 6, 6)
                        .addComponent(cbType, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(6, 6, 6)
                        .addComponent(spQty, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel26)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOthers)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(btnAnimal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Trash Collection", new javax.swing.ImageIcon(getClass().getResource("/assets/collection-solid-24.png")), jPanel10); // NOI18N

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
        lblLandmark1.setText("Batangas State University");

        lblProvince1.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblProvince1.setText("Batangas");

        lblCleanup1.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblCleanup1.setText("Beach");

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
                .addContainerGap(23, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Trash Bags", new javax.swing.ImageIcon(getClass().getResource("/assets/recycle-bag.png")), jPanel6); // NOI18N

        jPanel12.setBackground(new java.awt.Color(240, 240, 240));

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        lblLandmark2.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblLandmark2.setText("Batangas State University");

        lblProvince2.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblProvince2.setText("Batangas");

        lblCleanup2.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblCleanup2.setText("Beach");

        jLabel7.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jLabel7.setText("Philippines");

        jLabel11.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        jLabel11.setText("Site Information");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCleanup2)
                            .addComponent(lblProvince2))
                        .addGap(85, 85, 85)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLandmark2)
                            .addComponent(jLabel7)))
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCleanup2)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProvince2)
                    .addComponent(lblLandmark2))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        btnBack1.setBackground(new java.awt.Color(255, 153, 153));
        btnBack1.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        btnBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-out-regular-24.png"))); // NOI18N
        btnBack1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBack1ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Montserrat Black", 1, 24)); // NOI18N
        jLabel12.setText("REPORT");

        tbCollectors.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        tbCollectors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Age", "Sex", "Total"
            }
        ));
        tbCollectors.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbCollectors.setRowHeight(30);
        tbCollectors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCollectorsMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tbCollectors);

        jLabel27.setFont(new java.awt.Font("Montserrat", 1, 14)); // NOI18N
        jLabel27.setText("Students");

        jLabel28.setFont(new java.awt.Font("Montserrat", 1, 14)); // NOI18N
        jLabel28.setText("Wildlife");

        tbAnimals.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        tbAnimals.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Animal", "Status", "Entangled", "Type"
            }
        ));
        tbAnimals.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbAnimals.setRowHeight(30);
        tbAnimals.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnimalsMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tbAnimals);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBack1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBack1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(7, 7, 7)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Report", new javax.swing.ImageIcon(getClass().getResource("/assets/report-solid-24.png")), jPanel12); // NOI18N

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
    
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Log Out?", "Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Login login = Login.getInstance();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnBackActionPerformed

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
        String addSql = "INSERT INTO collected_waste (waste_id, bag_id, quantity, collected_at, user_id) VALUES (?, ?, ?, NOW(), ?)";
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
            int user_id = getLoggedInUserID();
            try (PreparedStatement psAdd = conn.prepareStatement(addSql)) {
                psAdd.setInt(1, wasteId);
                psAdd.setInt(2, Integer.parseInt(bagId));
                psAdd.setInt(3, qty);
                psAdd.setInt(4, user_id);
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
        String sql = "SELECT bag_id FROM bags"; 
        
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
    
    private void tbBagsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbBagsMouseClicked
        int selectedRow = tbBags.getSelectedRow();
        if (selectedRow != -1) {
            int bagId = (int) tbBags.getValueAt(selectedRow, 0); // Assuming the first column is bag_id
            loadItemsForSelectedBag(bagId); // Load items for the selected bag
        }
    }//GEN-LAST:event_tbBagsMouseClicked

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

    private void cbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTypeActionPerformed
        String type = cbType.getSelectedItem().toString();
        loadTrash(type);
    }//GEN-LAST:event_cbTypeActionPerformed

    private void btnSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSearchMouseClicked
        String searchQuery = txtSearch.getText().trim();
        if (!searchQuery.isEmpty()) {
            searchTrash(searchQuery);
        } else {
            loadTrash(null);
        }
    }//GEN-LAST:event_btnSearchMouseClicked

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

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        loadTrash(null);
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void btnBack2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBack2ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Log Out?", "Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Login login = Login.getInstance();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnBack2ActionPerformed

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
        loadAnimals();
    }//GEN-LAST:event_btnAnimalActionPerformed

    private void btnBack1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBack1ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Log Out?", "Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Login login = Login.getInstance();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnBack1ActionPerformed

    private void tbCollectorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCollectorsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbCollectorsMouseClicked

    private void tbAnimalsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnimalsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnimalsMouseClicked

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
            java.util.logging.Logger.getLogger(Leader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Leader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Leader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Leader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Leader().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddBag;
    private javax.swing.JButton btnAnimal;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnBack1;
    private javax.swing.JButton btnBack2;
    private javax.swing.JButton btnDeleteBag;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnOthers;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JLabel btnSearch;
    private javax.swing.JComboBox<String> cbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCleanup;
    private javax.swing.JLabel lblCleanup1;
    private javax.swing.JLabel lblCleanup2;
    private javax.swing.JLabel lblCleanup3;
    private javax.swing.JLabel lblLandmark;
    private javax.swing.JLabel lblLandmark1;
    private javax.swing.JLabel lblLandmark2;
    private javax.swing.JLabel lblLandmark3;
    private javax.swing.JLabel lblProvince;
    private javax.swing.JLabel lblProvince1;
    private javax.swing.JLabel lblProvince2;
    private javax.swing.JLabel lblProvince3;
    private javax.swing.JLabel lblStudents;
    private javax.swing.JLabel lblTrashCollected;
    private javax.swing.JSpinner spQty;
    private javax.swing.JTable tbAnimals;
    private javax.swing.JTable tbBags;
    private javax.swing.JTable tbCollectors;
    private javax.swing.JTable tbItems;
    private javax.swing.JTable tbTrash;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
