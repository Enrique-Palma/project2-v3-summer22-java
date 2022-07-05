/*
Name: Enrique Palma
Course: CNT 4714 Summer 2022
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 4, 2022
Class: DisplayQueryResults
*/

// Display the results of queries against the bikes table in the bikedb database.
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DisplayQueryResults extends JFrame {

    private ResultSetTableModel tableModel;
    private JTextArea queryArea;
    private JComboBox<String> driverComboBox;
    private JComboBox<String> schemaComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private MysqlDataSource dataSource;
    private Connection connection;

    // create ResultSetTableModel and GUI
    public DisplayQueryResults() {

        super("SQL Client App (CNT 4714 - Project 2)");
        String[] PropertiesItems = {"root.properties", "client.properties"};

        dataSource = new MysqlDataSource();
        connection = null;
        tableModel = new ResultSetTableModel();

        JLabel schemaLabel = new JLabel("Properties File", SwingConstants.RIGHT);
        schemaLabel.setOpaque(true);
        schemaLabel.setPreferredSize(new Dimension(84, 30));
        schemaLabel.setBackground(Color.lightGray);
        //schemaComboBox = new JComboBox<String>(new String[] { "root.properties", "client.properties" ,"test.properties"});
        schemaComboBox = new JComboBox(PropertiesItems);
        schemaComboBox.setMinimumSize(new Dimension(200, 30));
        schemaComboBox.setPreferredSize(new Dimension(200, 30));

        JLabel usernameLabel = new JLabel("Username", SwingConstants.CENTER);
        usernameLabel.setOpaque(true);
        usernameLabel.setPreferredSize(new Dimension(84, 30));
        usernameLabel.setBackground(Color.lightGray);
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password", SwingConstants.CENTER);
        passwordLabel.setOpaque(true);
        passwordLabel.setPreferredSize(new Dimension(84, 30));
        passwordLabel.setBackground(Color.lightGray);
        passwordField = new JPasswordField();

        queryArea = new JTextArea(3, 100);
        queryArea.setWrapStyleWord(true);
        queryArea.setLineWrap(true);

        // set up JButton for submitting queries
        JButton submitButton = new JButton("Execute SQL Command");
        submitButton.setBackground(Color.black);
        submitButton.setForeground(Color.white);

        JButton connectButton = new JButton("Connect To Database");
        connectButton.setBackground(Color.BLUE);
        connectButton.setForeground(Color.YELLOW);

        JButton clearSqlButton = new JButton("Clear SQL Command");
        clearSqlButton.setBackground(Color.red);
        clearSqlButton.setForeground(Color.white);

        JButton clearResultsButton = new JButton("Clear Result Window");
        clearResultsButton.setBackground(Color.red);
        clearResultsButton.setForeground(Color.white);

        JLabel label1 = new JLabel("Enter Database Information", SwingConstants.LEFT);
        label1.setForeground(Color.black);

        JLabel label2 = new JLabel("Enter a SQL Command", SwingConstants.CENTER);
        label2.setForeground(Color.black);

        JLabel label3 = new JLabel("SQL Execution Result Window", SwingConstants.LEFT);
        label3.setForeground(Color.black);

        JLabel connectionLabel = new JLabel("No connection now");
        connectionLabel.setOpaque(true);
        connectionLabel.setBackground(Color.blue);
        connectionLabel.setForeground(Color.white);
        connectionLabel.setPreferredSize(new Dimension(400, 40));

        Box schemaBox = Box.createHorizontalBox();
        schemaBox.add(schemaLabel);
        schemaBox.add(schemaComboBox);

        Box usernameBox = Box.createHorizontalBox();
        usernameBox.add(usernameLabel);
        usernameBox.add(usernameField);

        Box passwordBox = Box.createHorizontalBox();
        passwordBox.add(passwordLabel);
        passwordBox.add(passwordField);

        Box dbInfoLabelBox = Box.createHorizontalBox();
        dbInfoLabelBox.add(label1);
        dbInfoLabelBox.add(Box.createGlue());

        Box dbInfoBox = Box.createVerticalBox();
        dbInfoBox.add(dbInfoLabelBox);
        dbInfoBox.add(Box.createVerticalStrut(8));
        dbInfoBox.add(Box.createVerticalStrut(8));
        dbInfoBox.add(schemaBox);
        dbInfoBox.add(Box.createVerticalStrut(8));
        dbInfoBox.add(usernameBox);
        dbInfoBox.add(Box.createVerticalStrut(8));
        dbInfoBox.add(passwordBox);

        Box sqlLabelBox = Box.createHorizontalBox();
        sqlLabelBox.add(label2);
        sqlLabelBox.add(Box.createGlue());
        Box sqlBox = Box.createVerticalBox();
        sqlBox.add(sqlLabelBox);
        sqlBox.add(queryArea);

        Box dbInfoAndSQLBox = Box.createHorizontalBox();
        dbInfoAndSQLBox.add(Box.createHorizontalStrut(10));
        dbInfoAndSQLBox.add(dbInfoBox);
        dbInfoAndSQLBox.add(Box.createHorizontalStrut(10));
        dbInfoAndSQLBox.add(sqlBox);
        dbInfoAndSQLBox.add(Box.createHorizontalStrut(10));

        Box actionBox = Box.createHorizontalBox();
        actionBox.add(Box.createHorizontalStrut(10));
        actionBox.add(connectionLabel);
        actionBox.add(Box.createHorizontalStrut(10));
        actionBox.add(connectButton);
        actionBox.add(Box.createHorizontalStrut(10));
        actionBox.add(clearSqlButton);
        actionBox.add(Box.createHorizontalStrut(10));
        actionBox.add(submitButton);
        actionBox.add(Box.createGlue());

        Box resultsLabelBox = Box.createHorizontalBox();
        resultsLabelBox.createHorizontalStrut(10);
        resultsLabelBox.add(label3);
        resultsLabelBox.add(Box.createGlue());

        Box northBox = Box.createVerticalBox();
        northBox.add(Box.createVerticalStrut(10));
        northBox.add(dbInfoAndSQLBox);
        northBox.add(Box.createVerticalStrut(10));
        northBox.add(actionBox);
        northBox.add(Box.createVerticalStrut(10));
        northBox.add(resultsLabelBox);
        northBox.add(Box.createVerticalStrut(10));

        // create JTable delegate for tableModel
        JTable resultTable = new JTable(tableModel);
        resultTable.setMinimumSize(new Dimension(500, 400));
        resultTable.setPreferredSize(new Dimension(500, 400));
        resultTable.setMaximumSize(new Dimension(500, 400));

        Box resultsBox = Box.createHorizontalBox();
        resultsBox.add(Box.createHorizontalStrut(10));
        resultsBox.add(new JScrollPane(resultTable));
        resultsBox.add(Box.createHorizontalStrut(10));

        Box southBox = Box.createHorizontalBox();
        southBox.add(Box.createHorizontalStrut(15));
        southBox.add(clearResultsButton);
        southBox.add(Box.createGlue());

        // place GUI components on content pane
        add(northBox, BorderLayout.NORTH);
        add(resultsBox, BorderLayout.CENTER);
        add(southBox, BorderLayout.SOUTH);

        // create event listener for submitButton
        submitButton.addActionListener(

                new ActionListener() {
                    // pass query to table model
                    public void actionPerformed(ActionEvent event) {
                        // perform a new query
                        try {
                            String sql = queryArea.getText().trim();
                            tableModel.setSQL(sql);
                        } // end try
                        catch (SQLException sqlException) {
                            JOptionPane.showMessageDialog(null, sqlException.getMessage(), "Database error",
                                    JOptionPane.ERROR_MESSAGE);
                        } // end catch
                    } // end actionPerformed
                } // end ActionListener inner class
        ); // end call to addActionListener

        // create event listener for connectButton
        connectButton.addActionListener(

                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        Properties properties = new Properties();
                        FileInputStream filein = null;
                        FileInputStream filein2 = null;

                        //connect to the database
                        try {

                            dataSource.setURL((String)(schemaComboBox.getSelectedItem()));

                            filein = new FileInputStream((String) schemaComboBox.getSelectedItem());
                            properties.load(filein);
                            dataSource = new MysqlDataSource();
                            String usernameinFile = properties.getProperty("MYSQL_DB_USERNAME");
                            String passwordinFile = properties.getProperty("MYSQL_DB_PASSWORD");
                            String usernameFieldinput = usernameField.getText();
                            String passwordFieldInput = passwordField.getText();

                            if(usernameFieldinput.equals(usernameinFile) && passwordFieldInput.equals(passwordinFile)) {

                                dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
                                dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
                                dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
                                connection = dataSource.getConnection();
                                connectionLabel.setText("jdbc:mysql://127.0.0.1:3306/project2?useTimezone=true&serverTimezone=UTC");
                                tableModel.setConnection(connection);
                            }
                            else {
                                connectionLabel.setText("Wrong username or password");
                            }
                        } // end try
                        catch (SQLException | IOException e) {
                            connectionLabel.setText("No connection");
                            JOptionPane.showMessageDialog(null, e.getMessage(), "Database connection error", JOptionPane.ERROR_MESSAGE);
                        }// end catch
                    }// end actionPerformed
                } // end ActionListener inner class );
        ); // end call to addActionListener

        // create event listener for clearSqlButton
        clearSqlButton.addActionListener(

                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        queryArea.setText("");
                    } // end actionPerformed
                } // end ActionListener inner class
        ); // end call to addActionListener

        // create event listener for clearResultsButton
        clearResultsButton.addActionListener(

                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        tableModel.clearData();
                    } // end actionPerformed
                } // end ActionListener inner class
        ); // end call to addActionListener

        setSize(1000, 600);
        setVisible(true); // display window

        // dispose of window when user quits application (this overrides
        // the default of HIDE_ON_CLOSE)
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ensure database connection is closed when user quits application
        addWindowListener(

                new WindowAdapter() {
                    // disconnect from database and exit when window has closed
                    public void windowClosed(WindowEvent event) {
                        tableModel.disconnectFromDatabase();
                        System.exit(0);
                    } // end method windowClosed
                } // end WindowAdapter inner class
        ); // end call to addWindowListener
    } // end DisplayQueryResults constructor

    // execute application
    public static void main(String args[]) {
        new DisplayQueryResults();
    } // end main
}// end class DisplayQueryResults