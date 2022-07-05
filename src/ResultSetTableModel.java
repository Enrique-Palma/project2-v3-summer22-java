/*
Name: Enrique Palma
Course: CNT 4714 Summer 2022
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 4, 2022
Class: ResultSetTableModel
*/

// A TableModel that supplies ResultSet data to a JTable.
import java.sql.Connection;
import java.sql.Statement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel
{
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;

    // keep track of database connection status
    private boolean connectedToDatabase;

    public ResultSetTableModel( )
    {
        connection = null;
        statement = null;
        resultSet = null;
        metaData = null;
        numberOfRows = 0;
        connectedToDatabase = false;

    } // end constructor ResultSetTableModel

    // get class that represents column type
    public Class getColumnClass( int column ) throws IllegalStateException
    {
        // ensure database connection is available
        if ( !connectedToDatabase )
            throw new IllegalStateException( "Not Connected to Database" );

        // determine Java class of column
        try
        {
            String className = metaData.getColumnClassName( column + 1 );

            // return Class object that represents className
            return Class.forName( className );
        } // end try
        catch ( Exception exception )
        {
            exception.printStackTrace();
        } // end catch

        return Object.class; // if problems occur above, assume type Object
    } // end method getColumnClass

    // get number of columns in ResultSet
    public int getColumnCount() throws IllegalStateException
    {
        // ensure database connection is available
        if ( !connectedToDatabase )
            return 0;

        if ( metaData == null)
            return 0;

        // determine number of columns
        try
        {
            return metaData.getColumnCount();
        } // end try
        catch ( SQLException sqlException )
        {
            sqlException.printStackTrace();
        } // end catch

        return 0; // if problems occur above, return 0 for number of columns
    } // end method getColumnCount

    // get name of a particular column in ResultSet
    public String getColumnName( int column ) throws IllegalStateException
    {
        // ensure database connection is available
        if ( !connectedToDatabase )
            throw new IllegalStateException( "Not Connected to Database" );

        // determine column name
        try
        {
            return metaData.getColumnName( column + 1 );
        } // end try
        catch ( SQLException sqlException )
        {
            sqlException.printStackTrace();
        } // end catch

        return ""; // if problems, return empty string for column name
    } // end method getColumnName

    // return number of rows in ResultSet
    public int getRowCount() throws IllegalStateException
    {
        // ensure database connection is available
        if ( !connectedToDatabase )
            return 0;

        if ( metaData == null)
            return 0;

        return numberOfRows;
    } // end method getRowCount

    // obtain value in particular row and column
    public Object getValueAt( int row, int column )
            throws IllegalStateException
    {
        // ensure database connection is available
        if ( !connectedToDatabase )
            throw new IllegalStateException( "Not Connected to Database" );

        // obtain a value at specified ResultSet row and column
        try
        {
            resultSet.next();
            resultSet.absolute( row + 1 );
            return resultSet.getObject( column + 1 );
        } // end try
        catch ( SQLException sqlException )
        {
            sqlException.printStackTrace();
        } // end catch

        return ""; // if problems, return empty string object
    } // end method getValueAt


    public void setConnection(Connection conn) {
        if (connectedToDatabase) {
            disconnectFromDatabase();
        }

        try {
            this.connection = conn;
            statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            connectedToDatabase = true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void clearData() {
        metaData = null;
        fireTableStructureChanged();
    }

    public void setSQL(String sql)
            throws SQLException
    {

        // ensure database connection is available
        if ( !connectedToDatabase )
            throw new IllegalStateException( "Not Connected to Database" );

        // specify query and execute it
        if (statement.execute( sql )) {
            // It's a SELECT. Get the metadata and set the result set.
            resultSet = statement.getResultSet();
            metaData = resultSet.getMetaData();
            resultSet.last();
            numberOfRows = resultSet.getRow();

        } else {

            // It's not a SELECT. Clear the metadata.
            metaData = null;
            numberOfRows = 0;

        }

        // notify JTable that model has changed
        fireTableStructureChanged();
    }

    // close Statement and Connection
    public void disconnectFromDatabase()
    {
        if ( !connectedToDatabase )
            return;

        // close Statement and Connection
        try
        {
            statement.close();
            connection.close();
        } // end try
        catch ( SQLException sqlException )
        {
            sqlException.printStackTrace();
        } // end catch
        finally  // update database connection status
        {
            connectedToDatabase = false;
        } // end finally
    } // end method disconnectFromDatabase

}  // end class ResultSetTableModel