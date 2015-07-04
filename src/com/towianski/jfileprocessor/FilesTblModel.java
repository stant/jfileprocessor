package com.towianski.jfileprocessor;

import java.util.ArrayList;
import java.util.Date;
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
    public int getColumnCount() { return data.get(0).size(); }
    
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
    
    public void refresh()
    {
        fireTableChanged(null);
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
