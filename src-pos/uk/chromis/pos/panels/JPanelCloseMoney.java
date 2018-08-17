/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2016
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version V0.60.2 beta
**
**    Chromis POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    Chromis POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
**
**
 */
package uk.chromis.pos.panels;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.gui.TableRendererBasic;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppUser;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.BeanFactoryApp;
import uk.chromis.pos.forms.BeanFactoryException;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.JPanelView;
import uk.chromis.pos.printer.TicketParser;
import uk.chromis.pos.printer.TicketPrinterException;
import uk.chromis.pos.scripting.ScriptEngine;
import uk.chromis.pos.scripting.ScriptException;
import uk.chromis.pos.scripting.ScriptFactory;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author adrianromero
 */
public class JPanelCloseMoney extends JPanel implements JPanelView, BeanFactoryApp {

    private AppView m_App;
    private DataLogicSystem m_dlSystem;
    private DataLogicSales m_dlSales;
    private DataLogicSync m_dlSync;
    private PaymentsModel m_PaymentsToClose = null;
    private TicketParser m_TTP;
    private final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private Session s;
    private Connection con;
    private Statement stmt;
    private Integer result;
    private String SQL;
    private ResultSet rs;
    private List<HourlySalesInfo> hSales;

    private AppUser m_User;

    /**
     * Creates new form JPanelCloseMoney
     */
    public JPanelCloseMoney() {

        initComponents();
    }

    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {

        m_App = app;
        m_dlSystem = (DataLogicSystem) m_App.getBean("uk.chromis.pos.forms.DataLogicSystem");
        m_dlSales = (DataLogicSales) m_App.getBean("uk.chromis.pos.forms.DataLogicSales");
        m_dlSync = (DataLogicSync) m_App.getBean("uk.chromis.pos.sync.DataLogicSync");
        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);

        m_jTicketTable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{new FormatsPayment(), Formats.CURRENCY}));
        m_jTicketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollTableTicket.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jTicketTable.getTableHeader().setReorderingAllowed(false);
        m_jTicketTable.setRowHeight(25);
        m_jTicketTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        m_jsalestable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{Formats.STRING, Formats.CURRENCY, Formats.CURRENCY, Formats.CURRENCY}));
        m_jsalestable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollSales.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jsalestable.getTableHeader().setReorderingAllowed(false);
        m_jsalestable.setRowHeight(25);
        m_jsalestable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        m_jCloseCash.setVisible(!m_dlSync.isCentral());

        jPanelTop.setVisible(false);
        if (AppConfig.getInstance().getBoolean("screen.600800")) {
            jPanelTop.setVisible(true);
            jPanelBottom.setVisible(false);
        } else {
            jPanelTop.setVisible(false);
            jPanelBottom.setVisible(true);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CloseTPV");
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        loadData();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        // se me debe permitir cancelar el deactivate   
        return true;
    }

    private void loadData() throws BasicException {

        // Reset
        m_jSequence.setText(null);
        m_jMinDate.setText(null);
        m_jMaxDate.setText(null);
        m_jPrintCash.setEnabled(false);
        m_jCloseCash.setEnabled(false);

        m_jPrintCashTop.setEnabled(false);
        m_jCloseCashTop.setEnabled(false);

        m_jCount.setText(null);
        m_jCash.setText(null);

        m_jSales.setText(null);
        m_jSalesSubtotal.setText(null);
        m_jSalesTaxes.setText(null);
        m_jSalesTotal.setText(null);

        m_jTicketTable.setModel(new DefaultTableModel());
        m_jsalestable.setModel(new DefaultTableModel());

        // LoadData
        m_PaymentsToClose = PaymentsModel.loadInstance(m_App);

        // Populate Data
        m_jSequence.setText(m_PaymentsToClose.printSequence());
        m_jMinDate.setText(m_PaymentsToClose.printDateStart());
        m_jMaxDate.setText(m_PaymentsToClose.printDateEnd());

        if (m_PaymentsToClose.getPayments() != 0 || m_PaymentsToClose.getSales() != 0) {

            m_jPrintCash.setEnabled(true);
            m_jCloseCash.setEnabled(true);

            m_jPrintCashTop.setEnabled(true);
            m_jCloseCashTop.setEnabled(true);

            m_jCount.setText(m_PaymentsToClose.printPayments());
            m_jCash.setText(m_PaymentsToClose.printPaymentsTotal());

            m_jSales.setText(m_PaymentsToClose.printSales());
            m_jSalesSubtotal.setText(m_PaymentsToClose.printSalesBase());
            m_jSalesTaxes.setText(m_PaymentsToClose.printSalesTaxes());
            m_jSalesTotal.setText(m_PaymentsToClose.printSalesTotal());
        }

        m_jTicketTable.setModel(m_PaymentsToClose.getPaymentsModel());

        TableColumnModel jColumns = m_jTicketTable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(178);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(80);
        jColumns.getColumn(1).setResizable(false);

        m_jsalestable.setModel(m_PaymentsToClose.getSalesModel());

        jColumns = m_jsalestable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(108);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(75);
        jColumns.getColumn(1).setResizable(false);
        jColumns.getColumn(2).setPreferredWidth(75);
        jColumns.getColumn(2).setResizable(false);

// read number of no cash drawer activations
        s = m_App.getSession();

        String sdbmanager = m_dlSystem.getDBVersion();

        try {
            result = 0;
            con = s.getConnection();
            if ("PostgreSQL".equals(sdbmanager)) {
                SQL = "SELECT * FROM DRAWEROPENED WHERE TICKETID = 'No Sale' AND OPENDATE > " + "'" + m_PaymentsToClose.printDateStart() + "'";
            } else {
                SQL = "SELECT * FROM DRAWEROPENED WHERE TICKETID = 'No Sale' AND OPENDATE > {fn TIMESTAMP('" + m_PaymentsToClose.getDateStartDerby() + "')}";
            }

            stmt = (Statement) con.createStatement();
            rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                result++;
            }
            rs = null;
            con = null;
            s = null;
        } catch (SQLException e) {
            System.out.println("error = " + e);
        }
        m_jNoCashSales.setText(result.toString());

        // read the hourly sales
        if ("PostgreSQL".equals(sdbmanager)) {
            hSales = m_dlSystem.getHourlySalesPostgreSQL(m_App.getActiveCashIndex());
        } else if ("Derby".equals(sdbmanager)) {
            hSales = m_dlSystem.getHourlySalesDerby(m_App.getActiveCashIndex());
        } else {
            hSales = m_dlSystem.getHourlySalesMySQL(m_App.getActiveCashIndex());
        }

    }

    private void printPayments(String report) {

        String sresource = m_dlSystem.getResourceAsXML(report);
        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("payments", m_PaymentsToClose);
                script.put("nosales", result.toString());
                script.put("hourlysales", hSales);
                script.put("ticket",m_dlSales);
                m_TTP.printTicket(script.eval(sresource).toString());
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    private class FormatsPayment extends Formats {

        @Override
        protected String formatValueInt(Object value) {
            return AppLocal.getIntString("transpayment." + (String) value);
        }

        @Override
        protected Object parseValueInt(String value) throws ParseException {
            return value;
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.LEFT;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        m_jSequence = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        m_jMinDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jMaxDate = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        m_jScrollTableTicket = new javax.swing.JScrollPane();
        m_jTicketTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        m_jCount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jCash = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jSales = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jSalesSubtotal = new javax.swing.JTextField();
        m_jScrollSales = new javax.swing.JScrollPane();
        m_jsalestable = new javax.swing.JTable();
        m_jSalesTaxes = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_jSalesTotal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        m_jNoCashSales = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelTop = new javax.swing.JPanel();
        m_jCloseCashTop = new javax.swing.JButton();
        m_jPrintCashTop = new javax.swing.JButton();
        jPanelBottom = new javax.swing.JPanel();
        m_jCloseCash = new javax.swing.JButton();
        m_jPrintCash = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.datestitle"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText(AppLocal.getIntString("label.sequence")); // NOI18N

        m_jSequence.setEditable(false);
        m_jSequence.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSequence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(AppLocal.getIntString("Label.StartDate")); // NOI18N

        m_jMinDate.setEditable(false);
        m_jMinDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jMinDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(AppLocal.getIntString("Label.EndDate")); // NOI18N

        m_jMaxDate.setEditable(false);
        m_jMaxDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jMaxDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jSequence, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jMinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jMaxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jSequence, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jMinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jMaxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(filler1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.paymentstitle"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        m_jScrollTableTicket.setBorder(null);
        m_jScrollTableTicket.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jScrollTableTicket.setMinimumSize(new java.awt.Dimension(350, 140));
        m_jScrollTableTicket.setPreferredSize(new java.awt.Dimension(350, 140));

        m_jTicketTable.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTicketTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        m_jTicketTable.setFocusable(false);
        m_jTicketTable.setIntercellSpacing(new java.awt.Dimension(0, 1));
        m_jTicketTable.setRequestFocusEnabled(false);
        m_jTicketTable.setShowVerticalLines(false);
        m_jScrollTableTicket.setViewportView(m_jTicketTable);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("Label.Tickets")); // NOI18N

        m_jCount.setEditable(false);
        m_jCount.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jCount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.Money")); // NOI18N

        m_jCash.setEditable(false);
        m_jCash.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jCash.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.sales")); // NOI18N

        m_jSales.setEditable(false);
        m_jSales.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSales.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.totalnet")); // NOI18N

        m_jSalesSubtotal.setEditable(false);
        m_jSalesSubtotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSalesSubtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jScrollSales.setBorder(null);
        m_jScrollSales.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        m_jsalestable.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jsalestable.setFocusable(false);
        m_jsalestable.setIntercellSpacing(new java.awt.Dimension(0, 1));
        m_jsalestable.setRequestFocusEnabled(false);
        m_jsalestable.setShowVerticalLines(false);
        m_jScrollSales.setViewportView(m_jsalestable);

        m_jSalesTaxes.setEditable(false);
        m_jSalesTaxes.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSalesTaxes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.taxes")); // NOI18N

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.total")); // NOI18N

        m_jSalesTotal.setEditable(false);
        m_jSalesTotal.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jSalesTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jSalesTotal.setPreferredSize(new java.awt.Dimension(6, 21));

        jLabel8.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel8.setText(bundle.getString("label.nocashsales")); // NOI18N

        m_jNoCashSales.setEditable(false);
        m_jNoCashSales.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jNoCashSales.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(m_jScrollTableTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jScrollSales, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(m_jCash, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(m_jNoCashSales, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(m_jSalesTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_jSalesSubtotal)
                            .addComponent(m_jCount)
                            .addComponent(m_jSalesTaxes, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(m_jSales, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jSales, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jSalesSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jSalesTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jSalesTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jCash, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jNoCashSales, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jScrollSales, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jScrollTableTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jPanelTop.setEnabled(false);

        m_jCloseCashTop.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jCloseCashTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/calculator.png"))); // NOI18N
        m_jCloseCashTop.setText(AppLocal.getIntString("Button.CloseCash")); // NOI18N
        m_jCloseCashTop.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jCloseCashTop.setIconTextGap(2);
        m_jCloseCashTop.setInheritsPopupMenu(true);
        m_jCloseCashTop.setMaximumSize(new java.awt.Dimension(85, 33));
        m_jCloseCashTop.setMinimumSize(new java.awt.Dimension(85, 33));
        m_jCloseCashTop.setPreferredSize(new java.awt.Dimension(85, 33));
        m_jCloseCashTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseCashTopActionPerformed(evt);
            }
        });

        m_jPrintCashTop.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPrintCashTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/printer.png"))); // NOI18N
        m_jPrintCashTop.setText(AppLocal.getIntString("Button.PrintCash")); // NOI18N
        m_jPrintCashTop.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jPrintCashTop.setIconTextGap(2);
        m_jPrintCashTop.setMaximumSize(new java.awt.Dimension(85, 33));
        m_jPrintCashTop.setMinimumSize(new java.awt.Dimension(85, 33));
        m_jPrintCashTop.setPreferredSize(new java.awt.Dimension(85, 33));
        m_jPrintCashTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintCashTopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTopLayout = new javax.swing.GroupLayout(jPanelTop);
        jPanelTop.setLayout(jPanelTopLayout);
        jPanelTopLayout.setHorizontalGroup(
            jPanelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTopLayout.createSequentialGroup()
                .addComponent(m_jPrintCashTop, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jCloseCashTop, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelTopLayout.setVerticalGroup(
            jPanelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTopLayout.createSequentialGroup()
                .addGroup(jPanelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jPrintCashTop, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCloseCashTop, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        m_jCloseCash.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jCloseCash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/calculator.png"))); // NOI18N
        m_jCloseCash.setText(AppLocal.getIntString("Button.CloseCash")); // NOI18N
        m_jCloseCash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jCloseCash.setIconTextGap(2);
        m_jCloseCash.setInheritsPopupMenu(true);
        m_jCloseCash.setMaximumSize(new java.awt.Dimension(85, 33));
        m_jCloseCash.setMinimumSize(new java.awt.Dimension(85, 33));
        m_jCloseCash.setPreferredSize(new java.awt.Dimension(85, 33));
        m_jCloseCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseCashActionPerformed(evt);
            }
        });

        m_jPrintCash.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPrintCash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/printer.png"))); // NOI18N
        m_jPrintCash.setText(AppLocal.getIntString("Button.PrintCash")); // NOI18N
        m_jPrintCash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jPrintCash.setIconTextGap(2);
        m_jPrintCash.setMaximumSize(new java.awt.Dimension(85, 33));
        m_jPrintCash.setMinimumSize(new java.awt.Dimension(85, 33));
        m_jPrintCash.setPreferredSize(new java.awt.Dimension(85, 33));
        m_jPrintCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintCashActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBottomLayout = new javax.swing.GroupLayout(jPanelBottom);
        jPanelBottom.setLayout(jPanelBottomLayout);
        jPanelBottomLayout.setHorizontalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jPrintCash, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jCloseCash, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanelBottomLayout.setVerticalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(m_jCloseCash, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(m_jPrintCash, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanelTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jPrintCashTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintCashTopActionPerformed

        // print report
        printPayments("Printer.PartialCash");
    }//GEN-LAST:event_m_jPrintCashTopActionPerformed

    private void m_jCloseCashTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseCashTopActionPerformed

        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannaclosecash"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {

            Date dNow = new Date();

            try {
                // Cerramos la caja si esta pendiente de cerrar.
                if (m_App.getActiveCashDateEnd() == null) {
                    new StaticSentence(m_App.getSession(), "UPDATE CLOSEDCASH SET DATEEND = ?, NOSALES = ? WHERE HOST = ? AND MONEY = ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING}))
                            .exec(new Object[]{dNow, result, m_App.getProperties().getHost(), m_App.getActiveCashIndex()});
                }
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                msg.show(this);
            }

            try {
                // Creamos una nueva caja
                m_App.setActiveCash(UUID.randomUUID().toString(), m_App.getActiveCashSequence() + 1, dNow, null);

                // creamos la caja activa
                m_dlSystem.execInsertCash(
                        new Object[]{m_App.getActiveCashIndex(), m_App.getProperties().getHost(), m_App.getActiveCashSequence(), m_App.getActiveCashDateStart(), m_App.getActiveCashDateEnd()});

                m_dlSystem.execDrawerOpened(
                        new Object[]{UUID.randomUUID().toString(), m_App.getAppUserView().getUser().getName(), "Close Cash"});

                // ponemos la fecha de fin
                m_PaymentsToClose.setDateEnd(dNow);

                // print report
                printPayments("Printer.CloseCash");

                // Mostramos el mensaje
                JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closecashok"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                msg.show(this);
            }

            try {
                loadData();
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("label.noticketstoclose"), e);
                msg.show(this);
            }
        }

    }//GEN-LAST:event_m_jCloseCashTopActionPerformed

    private void m_jCloseCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseCashActionPerformed

        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannaclosecash"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {

            Date dNow = new Date();

            try {
                // Cerramos la caja si esta pendiente de cerrar.
                if (m_App.getActiveCashDateEnd() == null) {
                    new StaticSentence(m_App.getSession(), "UPDATE CLOSEDCASH SET DATEEND = ?, NOSALES = ? WHERE HOST = ? AND MONEY = ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING}))
                            .exec(new Object[]{dNow, result, m_App.getProperties().getHost(), m_App.getActiveCashIndex()});
                }
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                msg.show(this);
            }

            try {
                // Creamos una nueva caja
                m_App.setActiveCash(UUID.randomUUID().toString(), m_App.getActiveCashSequence() + 1, dNow, null);

                // creamos la caja activa
                m_dlSystem.execInsertCash(
                        new Object[]{m_App.getActiveCashIndex(), m_App.getProperties().getHost(), m_App.getActiveCashSequence(), m_App.getActiveCashDateStart(), m_App.getActiveCashDateEnd()});

                m_dlSystem.execDrawerOpened(
                        new Object[]{UUID.randomUUID().toString(), m_App.getAppUserView().getUser().getName(), "Close Cash"});

                // ponemos la fecha de fin
                m_PaymentsToClose.setDateEnd(dNow);

                // print report
                printPayments("Printer.CloseCash");

                // Mostramos el mensaje
                JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closecashok"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                msg.show(this);
            }

            try {
                loadData();
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("label.noticketstoclose"), e);
                msg.show(this);
            }
        }
    }//GEN-LAST:event_m_jCloseCashActionPerformed

    private void m_jPrintCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintCashActionPerformed

        // print report
        printPayments("Printer.PartialCash");

    }//GEN-LAST:event_m_jPrintCashActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField m_jCash;
    private javax.swing.JButton m_jCloseCash;
    private javax.swing.JButton m_jCloseCashTop;
    private javax.swing.JTextField m_jCount;
    private javax.swing.JTextField m_jMaxDate;
    private javax.swing.JTextField m_jMinDate;
    private javax.swing.JTextField m_jNoCashSales;
    private javax.swing.JButton m_jPrintCash;
    private javax.swing.JButton m_jPrintCashTop;
    private javax.swing.JTextField m_jSales;
    private javax.swing.JTextField m_jSalesSubtotal;
    private javax.swing.JTextField m_jSalesTaxes;
    private javax.swing.JTextField m_jSalesTotal;
    private javax.swing.JScrollPane m_jScrollSales;
    private javax.swing.JScrollPane m_jScrollTableTicket;
    private javax.swing.JTextField m_jSequence;
    private javax.swing.JTable m_jTicketTable;
    private javax.swing.JTable m_jsalestable;
    // End of variables declaration//GEN-END:variables

}
