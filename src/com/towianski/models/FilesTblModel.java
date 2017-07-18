package com.towianski.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Stan Towianski - June 2015
 */

public class FilesTblModel extends AbstractTableModel 
{
    private ArrayList<String>colNames;
    private ArrayList<ArrayList> data;
    public static int FILESTBLMODEL_ISLINK = 0;
    public static int FILESTBLMODEL_ISDIR = 1;
    public static int FILESTBLMODEL_PATH = 2;
    public static int FILESTBLMODEL_MODIFIEDDATE = 3;
    public static int FILESTBLMODEL_SIZE = 4;
    private HashMap<String,Boolean> editableCells = new HashMap<String,Boolean>(); // 2d array to represent rows and columns
        
//    public PreviewImportTblModel( ArrayList<String> colNamesArg, String[][] dataArg )
//        {
//        colNames = colNamesArg;
//        data = dataArg;
//        
//        System.err.println( "row count =" + data.length );
//        System.err.println( "col count =" + data[0].length );
//        }

    public FilesTblModel( ArrayList<String> colNamesArg, ArrayList<ArrayList> dataArg )
        {
        colNames = colNamesArg;
        data = dataArg;
        
        System.err.println( "row count =" + data.size() );
        System.err.println( "col count =" + data.get(0).size() );
        }

    @Override
    public int getColumnCount() 
    {
        if ( data.size() < 1 )
            return 0;
        return data.get(0).size(); 
    }
    
    @Override
    public int getRowCount() { return data.size();}
    
    @Override
    public Object getValueAt(int row, int col) 
        {
        //System.err.println( "getValueAt row =" + row + "  col =" + col );
        try {
            if ( data.get(row).get(col) == "" ) 
                {
                //System.err.println( "NOT EXISTS getValueAt row =" + row + "  col =" + col );
                }
            } 
        catch( Exception ex )
            {
            return "";
            }
        return data.get(row).get(col); 
        }
    
    @Override
    public void setValueAt(Object aValue, int row, int col) 
        {
        //System.err.println( "setValueAt  value =" + aValue + "=  row =" + row + "  col =" + col );
        //if ( 1 == 1 ) return;
        try {
            if ( row < data.size() && col < data.get( row ).size() )
                {
                if ( col == FILESTBLMODEL_MODIFIEDDATE )
                    data.get( row ).set( col, (Date) aValue );
                else if ( col == FILESTBLMODEL_SIZE )
                    data.get( row ).set( col, (Long) aValue );
                else if ( col == FILESTBLMODEL_ISDIR || col == FILESTBLMODEL_ISLINK )
                    data.get( row ).set( col, (Boolean) aValue );
                else
                    data.get( row ).set( col, (String) aValue );
                }
            } 
        catch( Exception ex )
            {
            }
        //refresh();   NOTE:  DOING THIS CAUSED IT TO DROP A WHOLE COLUMN ! !
        }
    
    public String getColumnName(int col) {
        return colNames.get( col );
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        try {
            if ( getColumnCount() > 1 )
                {
                if ( columnIndex == FILESTBLMODEL_MODIFIEDDATE )
                    return Date.class;
                else if ( columnIndex == FILESTBLMODEL_SIZE )
                    return Long.class;
                else if ( columnIndex == FILESTBLMODEL_ISDIR || columnIndex == FILESTBLMODEL_ISLINK )
                    return Boolean.class;
                }
            }
        catch ( Exception ex )
            {
            System.out.println( "column type exc:\n" );
            ex.printStackTrace();
            System.out.println( "\n" );
            }
        return String.class;
    }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */
    
    public void insertColAt( int col ) 
        {
        //System.err.println( "getValueAt row =" + row + "  col =" + col );
        try {
            colNames.add( col, " NEW " );

            for ( ArrayList<String> colList : data )
                {
                colList.add( col, "---" );
                }
            } 
        catch( Exception ex )
            {
            return;
            }
        refresh();
        }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */    
    public void insertRowAt( int row, String Path ) 
        {
        //System.err.println( "getValueAt row =" + row + "  col =" + col );
        try {
            //System.out.println( "before add row table col count =" + this.getColumnCount() );
            ArrayList newRow = new ArrayList();
            newRow.add( false );
            newRow.add( true );
            newRow.add( Path );
            newRow.add( Calendar.getInstance().getTime() );
            newRow.add( (long) 0 );
            
            data.add( 0, newRow );
            //System.out.println( "after add row table col count =" + this.getColumnCount() );
            editableCells = new HashMap<String,Boolean>();
            } 
        catch( Exception ex )
            {
            return;
            }
        refresh();
        }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */    
    public void deleteRowAt( int row ) 
        {
        //System.err.println( "delete row =" + row + "  col =" + col );
        try {
            //System.out.println( "before add row table col count =" + this.getColumnCount() );
            data.remove( row );
            //System.out.println( "after add row table col count =" + this.getColumnCount() );
            editableCells = new HashMap<String,Boolean>();
            } 
        catch( Exception ex )
            {
            return;
            }
        refresh();
        }
    
    public void refresh()
        {
        fireTableChanged(null);
        }

    @Override
    public boolean isCellEditable( int row, int col )  // custom isCellEditable function
        {
        //if ( editableCells.containsKey( row + "-" + col ) )
            System.out.println( "cell " + row + ", " + col + " is edittable" );
        return editableCells.containsKey( row + "-" + col );
        }
    
    public void setCellEditable( int row, int col, boolean value )
        {
        if ( value )
            {   
            editableCells.put( row + "-" + col, true );
            System.out.println( "set edittable cell " + row + ", " + col );
            }
        else
            {
            editableCells.remove( row + "-" + col );
            }
        this.fireTableCellUpdated( row, col );
        }
    
//    //previewImportTbl.getTableHeader().addMouseListener(new         
//    class ColumnListener extends MouseAdapter() {
//      @Override
//      public void mouseClicked(MouseEvent mouseEvent) {
//        int index = previewImportTbl.convertColumnIndexToModel(previewImportTbl.columnAtPoint(mouseEvent.getPoint()));
//        if (index >= 0) {
//          System.out.println("Clicked on column " + index);
//        }
//      };
//    }

    
}
