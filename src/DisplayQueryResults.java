// Display the results of queries against the bikes table in the bikedb database.
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.*;

//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

public class DisplayQueryResults extends JFrame
{
    private ResultSetTableModel tableModel;
    private JTextArea queryArea;
    private JComboBox<String> driverComboBox;
    private JComboBox<String> schemaComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private MysqlDataSource dataSource;
    private Connection connection;

    // create ResultSetTableModel and GUI
    public DisplayQueryResults()
    {
        super( "Displaying Query Results" );

        dataSource = new MysqlDataSource();
        connection = null;
        tableModel = new ResultSetTableModel( );

        JLabel driverLabel = new JLabel("JDBC Driver", SwingConstants.RIGHT);
        driverLabel.setOpaque(true);
        driverLabel.setPreferredSize(new Dimension(84,30));
        driverLabel.setBackground(Color.GRAY);
        driverComboBox = new JComboBox(new String[]{"root.properties" , "client.properties"});
        driverComboBox.setMinimumSize(new Dimension(200, 30));
        driverComboBox.setPreferredSize(new Dimension(200, 30));

        /*JLabel schemaLabel = new JLabel("Database URL", SwingConstants.RIGHT);
        schemaLabel.setOpaque(true);
        schemaLabel.setPreferredSize(new Dimension(84,30));
        schemaLabel.setBackground(Color.GRAY);
        schemaComboBox.setMinimumSize(new Dimension(200, 30));
        schemaComboBox.setPreferredSize(new Dimension(200, 30));*/
        schemaComboBox = new JComboBox(new String[]{"jdbc:mysql://127.0.0.1:3306/project2"});


        JLabel usernameLabel = new JLabel("Username", SwingConstants.RIGHT);
        usernameLabel.setOpaque(true);
        usernameLabel.setPreferredSize(new Dimension(84,30));
        usernameLabel.setBackground(Color.GRAY);
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password", SwingConstants.RIGHT);
        passwordLabel.setOpaque(true);
        passwordLabel.setPreferredSize(new Dimension(84,30));
        passwordLabel.setBackground(Color.GRAY);
        passwordField = new JPasswordField();

        queryArea = new JTextArea( 3, 100 );
        queryArea.setWrapStyleWord( true );
        queryArea.setLineWrap( true );

        // set up JButton for submitting queries
        JButton submitButton = new JButton( "Execute SQL Command" );
        submitButton.setBackground(Color.GREEN);
        submitButton.setForeground(Color.BLACK);

        JButton connectButton = new JButton("Connect To Database");
        connectButton.setBackground(Color.BLUE);
        connectButton.setForeground(Color.YELLOW);

        JButton clearSqlButton = new JButton("Clear SQL Command");
        clearSqlButton.setBackground(Color.WHITE);
        clearSqlButton.setForeground(Color.RED);

        JButton clearResultsButton = new JButton("Clear Result Window");
        clearResultsButton.setBackground(Color.YELLOW);
        clearResultsButton.setForeground(Color.BLACK);

        JLabel label1 = new JLabel("Enter Database Information", SwingConstants.LEFT);
        label1.setForeground(Color.BLUE);

        JLabel label2 = new JLabel("Enter An SQL Command", SwingConstants.LEFT);
        label2.setForeground(Color.BLUE);

        JLabel label3 = new JLabel("SQL Execution Result Window", SwingConstants.LEFT);
        label3.setForeground(Color.BLUE);

        JLabel connectionLabel = new JLabel("No connection now");
        connectionLabel.setOpaque(true);
        connectionLabel.setBackground(Color.BLACK);
        connectionLabel.setForeground(Color.RED);
        connectionLabel.setPreferredSize(new Dimension(200, 40));

        // Layout boxes.
        Box driverBox = Box.createHorizontalBox();
        driverBox.add(driverLabel);
        driverBox.add(Box.createHorizontalStrut(5));
        driverBox.add(driverComboBox);

        Box schemaBox = Box.createHorizontalBox();
        //schemaBox.add(schemaLabel);
        driverBox.add(Box.createHorizontalStrut(5));
        //schemaBox.add(schemaComboBox);

        Box usernameBox = Box.createHorizontalBox();
        usernameBox.add(usernameLabel);
        driverBox.add(Box.createHorizontalStrut(5));
        usernameBox.add(usernameField);

        Box passwordBox = Box.createHorizontalBox();
        passwordBox.add(passwordLabel);
        driverBox.add(Box.createHorizontalStrut(5));
        passwordBox.add(passwordField);

        Box dbInfoLabelBox = Box.createHorizontalBox();
        dbInfoLabelBox.add(label1);
        dbInfoLabelBox.add(Box.createGlue());

        Box dbInfoBox = Box.createVerticalBox();
        dbInfoBox.add(dbInfoLabelBox);
        dbInfoBox.add(Box.createVerticalStrut(8));
        dbInfoBox.add(driverBox);
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
        northBox.add( dbInfoAndSQLBox );
        northBox.add(Box.createVerticalStrut(10));
        northBox.add( actionBox );
        northBox.add(Box.createVerticalStrut(10));
        northBox.add( resultsLabelBox );
        northBox.add(Box.createVerticalStrut(10));

        // create JTable delegate for tableModel
        JTable resultTable = new JTable( tableModel );
        resultTable.setMinimumSize(new Dimension(500,400));
        resultTable.setPreferredSize(new Dimension(500,400));
        resultTable.setMaximumSize(new Dimension(500,400));

        Box resultsBox = Box.createHorizontalBox();
        resultsBox.add(Box.createHorizontalStrut(10));
        resultsBox.add(new JScrollPane(resultTable));
        resultsBox.add(Box.createHorizontalStrut(10));

        Box southBox = Box.createHorizontalBox();
        southBox.add(Box.createHorizontalStrut(15));
        southBox.add(clearResultsButton);
        southBox.add(Box.createGlue());

        // place GUI components on content panel
        add( northBox, BorderLayout.NORTH );
        add( resultsBox, BorderLayout.CENTER );
        add( southBox, BorderLayout.SOUTH);

        // create event listener for submitButton
        submitButton.addActionListener(new ActionListener()
                {
                    // pass query to table model
                    public void actionPerformed( ActionEvent event )
                    {
                        // perform a new query
                        try
                        {
                            String sql = queryArea.getText().trim();
                            tableModel.setSQL( sql );
                        } // end try
                        catch ( SQLException sqlException )
                        {
                            JOptionPane.showMessageDialog( null, sqlException.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE );
                        } // end catch
                    } // end actionPerformed
                }  // end ActionListener inner class
        ); // end call to addActionListener

        // create event listener for connectButton
        connectButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed( ActionEvent event )
                    {
                        // connect to the database
                        try
                        {
                            dataSource.setUser(usernameField.getText());
                            dataSource.setPassword(passwordField.getText());
                            connection = dataSource.getConnection();
                            connectionLabel.setText((String)(schemaComboBox.getSelectedItem()));
                            tableModel.setConnection(connection);
                        } // end try
                        catch ( SQLException sqlException )
                        {
                            connectionLabel.setText("No connection");
                            JOptionPane.showMessageDialog( null, sqlException.getMessage(), "Database connection error", JOptionPane.ERROR_MESSAGE );
                        } // end catch
                    } // end actionPerformed
                }  // end ActionListener inner class
        ); // end call to addActionListener


        // create event listener for clearSqlButton
        clearSqlButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed( ActionEvent event )
                    {
                        queryArea.setText("");
                    } // end actionPerformed
                }  // end ActionListener inner class
        ); // end call to addActionListener

        // create event listener for clearResultsButton
        clearResultsButton.addActionListener(

                new ActionListener()
                {
                    public void actionPerformed( ActionEvent event )
                    {
                        tableModel.clearData();
                    } // end actionPerformed
                }  // end ActionListener inner class
        ); // end call to addActionListener

        setSize(1000,600);
        setVisible( true ); // display window

        // dispose of window when user quits application (this overrides
        // the default of HIDE_ON_CLOSE)
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        // ensure database connection is closed when user quits application
        addWindowListener(

                new WindowAdapter()
                {
                    // disconnect from database and exit when window has closed
                    public void windowClosed( WindowEvent event )
                    {
                        tableModel.disconnectFromDatabase();
                        System.exit( 0 );
                    } // end method windowClosed
                } // end WindowAdapter inner class
        ); // end call to addWindowListener
    } // end DisplayQueryResults constructor

    // execute application
    public static void main( String args[] )
    {
        new DisplayQueryResults();
    } // end main
} // end class DisplayQueryResults


