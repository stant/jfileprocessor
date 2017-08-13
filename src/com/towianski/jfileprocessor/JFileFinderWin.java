/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.chainfilters.ChainFilterOfBoolean;
import com.towianski.models.ResultsData;
import com.towianski.renderers.NumberRenderer;
import com.towianski.renderers.FormatRenderer;
import com.towianski.models.CyclingSpinnerListModel;
import com.towianski.models.FilesTblModel;
import com.towianski.listeners.MyMouseAdapter;
import com.towianski.chainfilters.ChainFilterOfNames;
import com.towianski.chainfilters.ChainFilterOfMaxDepth;
import com.towianski.chainfilters.ChainFilterOfSizes;
import com.towianski.chainfilters.ChainFilterOfDates;
import com.towianski.chainfilters.ChainFilterOfMinDepth;
import com.towianski.chainfilters.ChainFilterOfPreVisitMaxDepth;
import com.towianski.chainfilters.ChainFilterOfPreVisitMinDepth;
import com.towianski.chainfilters.FilterChain;
import com.towianski.jfileprocess.actions.BackwardFolderAction;
import com.towianski.jfileprocess.actions.CopyAction;
import com.towianski.jfileprocess.actions.CutAction;
import com.towianski.jfileprocess.actions.DeleteAction;
import com.towianski.jfileprocess.actions.EnterAction;
import com.towianski.jfileprocess.actions.ForwardFolderAction;
import com.towianski.jfileprocess.actions.PasteAction;
import com.towianski.jfileprocess.actions.RenameAction;
import com.towianski.jfileprocess.actions.UpFolderAction;
import com.towianski.jfileprocess.actions.JavaProcess;
import com.towianski.jfileprocess.actions.NewFolderAction;
import com.towianski.listeners.MyFocusAdapter;
import com.towianski.models.CircularArrayList;
import com.towianski.renderers.FiletypeCBCellRenderer;
import com.towianski.renderers.LinktypeCBCellRenderer;
import com.towianski.renderers.PathRenderer;
import com.towianski.renderers.TableCellListener;
import static com.towianski.utils.ClipboardUtils.getClipboardStringsList;
import static com.towianski.utils.ClipboardUtils.setClipboardContents;
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.MyLogger;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerListModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class JFileFinderWin extends javax.swing.JFrame {

    MyLogger logger = MyLogger.getLogger( JFileFinderWin.class.getName() );

    Thread jfinderThread = null;
    JFileFinderSwingWorker jFileFinderSwingWorker = null;
    ResultsData resultsData = null;
    JFileFinder jfilefinder = null;
    Color saveColor = null;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    String copyPathStartPath = null;
    private TableCellListener filesTblCellListener = null;
    Boolean isDoingCutFlag = false;
    Boolean countOnlyFlag = false;
    
    public static final String PROCESS_STATUS_SEARCH_STARTED = "Search Started . . .";
    public static final String PROCESS_STATUS_FILL_STARTED = "Fill Started . . .";
    public static final String PROCESS_STATUS_SEARCH_CANCELED = "Search canceled";
    public static final String PROCESS_STATUS_SEARCH_COMPLETED = "Search completed";
    public static final String PROCESS_STATUS_FILL_CANCELED = "Fill canceled";
    public static final String PROCESS_STATUS_FILL_COMPLETED = "Fill completed";
    public static final String PROCESS_STATUS_CANCEL_SEARCH = "Cancel Search";
    public static final String PROCESS_STATUS_CANCEL_FILL = "Cancel Fill";
    public static final String PROCESS_STATUS_SEARCH_READY = "Search";
    public static final String PROCESS_STATUS_ERROR = "Error";

    public static final String SHOWFILESFOLDERSCB_BOTH = "Files & Folders";
    public static final String SHOWFILESFOLDERSCB_FILES_ONLY = "Files Only";
    public static final String SHOWFILESFOLDERSCB_FOLDERS_ONLY = "Folders Only";
    public static final String SHOWFILESFOLDERSCB_NEITHER = "Neither";

    CircularArrayList pathsHistoryList = new CircularArrayList(50 );
    SavedPathsPanel savedPathReplacablePanel = new SavedPathsPanel( this );
//    HashMap<String,String> savedPathsHm = new HashMap<String,String>();
    HashMap<String,ListOfFilesPanel> listOfFilesPanelHm = new HashMap<String,ListOfFilesPanel>();
    DefaultComboBoxModel listOfFilesPanelsModel = new DefaultComboBoxModel();

    PrintStream console = System.out;
    
//    JDatePickerImpl date1 = null;
//    JDatePickerImpl date2 = null;
    
    /**
     * Creates new form JFileFinder
     */
    public JFileFinderWin() {

        initComponents();

        start();       
    }

    public void start() 
    {
        jSplitPane2.setLeftComponent( savedPathReplacablePanel );
        this.validate();

        date2.setMyEnabled( false );
        date2Op.setEnabled( false );
        jTabbedPane1.setSelectedIndex( 2 );

        fileMgrMode.setSelected( true );
        fileMgrModeActionPerformed( null );
        useGlobPattern.setSelected( true );
        tabsLogicAndBtn.setSelected( true );
        saveColor = searchBtn.getBackground();
        this.addEscapeListener( this );
        filesTbl.addMouseListener( new MyMouseAdapter( jPopupMenu1, this, jScrollPane1 ) );
        jScrollPane1.addMouseListener( new MyMouseAdapter( jPopupMenu2, this, jScrollPane1 ) );
        this.setLocationRelativeTo( getRootPane() );

        filesTblCellListener = new TableCellListener( filesTbl, filesTblCellChangedAction );
//        filesTbl.putClientProperty( "terminateEditOnFocusLost", Boolean.TRUE );

        filesTbl.addFocusListener( new MyFocusAdapter( filesTblCellListener, this ) );
        
        addkeymapstuff();

        startingFolder.requestFocus();
        
//        filesTbl.getInputMap().put(
//            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "startEditing");

//        System.out.println( "create spinner");
//        String[] andOrSpinModelList = { "", "And", "Or" };
//        SpinnerListModel andOrSpinModel = new CyclingSpinnerListModel( andOrSpinModelList );
//        jSpinner1 = new javax.swing.JSpinner( andOrSpinModel );        

    DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
    listModel.addElement( "New Window" );
    listModel.addElement( "Trash" );
    savedPathReplacablePanel.getSavedPathsHm().put( "New Window", "New Window" );
    savedPathReplacablePanel.getSavedPathsHm().put( "Trash", DesktopUtils.getTrashFolder().toString() );
    readInBookmarks();
    jSplitPane2.setDividerLocation( 150 );

    }
        
    public void addkeymapstuff()
    {
       EnterAction enterAction = new EnterAction( this );
       RenameAction renameAction = new RenameAction( this );
       DeleteAction deleteAction = new DeleteAction( this );
       UpFolderAction upFolderAction = new UpFolderAction( this );
       BackwardFolderAction backwardFolderAction = new BackwardFolderAction( this );
       ForwardFolderAction forwardFolderAction = new ForwardFolderAction( this );
       NewFolderAction newFolderAction = new NewFolderAction( this );
       CopyAction copyAction = new CopyAction( this );
       CutAction cutAction = new CutAction( this );
       PasteAction pasteAction = new PasteAction( this );
       
//       InputEvent.CTRL_MASK   works on linux and windows but not Mac.
//       using Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() works on mac and linux.
               
        InputMap inputMap = null;
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            inputMap = filesTbl.getInputMap( JPanel.WHEN_IN_FOCUSED_WINDOW );
            }
        else
            {
            inputMap = filesTbl.getInputMap();
            }
            
        ActionMap actionMap = filesTbl.getActionMap();
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "enterAction" );
        actionMap.put( "enterAction", enterAction );
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ), "renameAction" );
        actionMap.put( "renameAction", renameAction );
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ), "deleteAction" );
        actionMap.put( "deleteAction", deleteAction );
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_BACK_SPACE, 0 ), "upFolderAction" );
        actionMap.put( "upFolderAction", upFolderAction );
 
        System.out.println( "System.getProperty( \"os.name\" ) =" + System.getProperty( "os.name" ) + "=" );
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            System.out.println( "Mac specific keys !" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "newFolderAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "copyAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "cutAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "pasteAction" );
            }
        else
            {
            System.out.println( "non Mac specific keys" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK ), "newFolderAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK ), "copyAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_X, InputEvent.CTRL_MASK ), "cutAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK ), "pasteAction" );
            }
        
        actionMap.put( "newFolderAction", newFolderAction );
 
        actionMap.put( "copyAction", copyAction );
 
        actionMap.put( "cutAction", cutAction );
 
        actionMap.put( "pasteAction", pasteAction );


        //------- Add actions to Starting Folder to go thru history  -------
        InputMap pathInputMap = startingFolder.getInputMap();
        ActionMap pathActionMap = startingFolder.getActionMap();
 
        pathInputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, InputEvent.ALT_MASK ), "backwardFolderAction" );
        pathActionMap.put( "backwardFolderAction", backwardFolderAction );
 
        pathInputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, InputEvent.ALT_MASK ), "forwardFolderAction" );
        pathActionMap.put( "forwardFolderAction", forwardFolderAction );
 
    }

    public DefaultComboBoxModel getListPanelModel( String listname ) {
        if ( listOfFilesPanelHm.containsKey( listname ) )
            {
            return listOfFilesPanelHm.get( listname ).getModel();
            }
        return null;
    }

    public void readInBookmarks() {
        File selectedFile = new File( DesktopUtils.getBookmarks().toString() );
        System.out.println( "Bookmarks File =" + selectedFile + "=" );
            
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileReader fr = new FileReader( selectedFile.getAbsoluteFile() );
            BufferedReader br = new BufferedReader(fr);

//            DefaultComboBoxModel thisListModel = (DefaultComboBoxModel) savedPathsList.getModel();
            DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
            int numItems = listModel.getSize();
            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
            
            String line = "";
            while ( ( line = br.readLine() ) != null )
                {
                System.out.println( "read line =" + line + "=" );
                String[] pieces = line.split( "," );
                listModel.addElement( pieces[0] );
                savedPathReplacablePanel.getSavedPathsHm().put( pieces[0], pieces[1] );
                }
            //close BufferedWriter
            br.close();
            //close FileWriter 
            fr.close();
            }
        catch( Exception ex )
            {

            }
    }

    public void removeListPanel( String listname ) {
        listOfFilesPanelsModel.removeElement( listname );
        listOfFilesPanelHm.remove( listname );
    }

    public JRadioButton getUseGlobPattern() {
        return useGlobPattern;
    }

    public void setUseGlobPattern(JRadioButton useGlobPattern) {
        this.useGlobPattern = useGlobPattern;
    }

    public JRadioButton getUseRegexPattern() {
        return useRegexPattern;
    }

    public void setUseRegexPattern(JRadioButton useRegexPattern) {
        this.useRegexPattern = useRegexPattern;
    }

    public String getStartingFolder() {
        return startingFolder.getText().trim();
    }

    public void setStartingFolder(String startingFolder) {
        this.startingFolder.setText( startingFolder );
    }

    public Level getLogLevel() {
        return logLevelsLhm.get( logLevel.getSelectedItem() );
    }

    public void callSearchBtnActionPerformed(java.awt.event.ActionEvent evt)
        {   // FIXXX  I can get rid of this method !
        searchBtnAction( evt );
        }
            
    public void callDeleteActionPerformed(java.awt.event.ActionEvent evt)
        {
        DeleteActionPerformed( evt );
        }
            
    public void callEnterActionPerformed(java.awt.event.ActionEvent evt)
        {
        EnterActionPerformed( evt );
        }
                        
    public void callRenameActionPerformed(java.awt.event.ActionEvent evt)
        {
        RenameActionPerformed( evt );
        }
                        
    public void callUpFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        upFolderActionPerformed( evt );
        }
                        
    public void callBackwardFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        backwardFolderActionPerformed( evt );
        }
                        
    public void callForwardFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        forwardFolderActionPerformed( evt );
        }
                        
    public void callNewFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        NewFolderActionPerformed( evt );  // note capital N for what netbeans created
        }
                        
    public void callCopyActionPerformed(java.awt.event.ActionEvent evt)
        {
        CopyActionPerformed( evt );
        }
                        
    public void callCutActionPerformed(java.awt.event.ActionEvent evt)
        {
        CutActionPerformed( evt );
        }
                        
    public void callPasteActionPerformed(java.awt.event.ActionEvent evt)
        {
        PasteActionPerformed( evt );
        }
                        
    public void stopSearch() {
        jfilefinder.cancelSearch();
//        jFileFinderSwingWorker.cancel(rootPaneCheckingEnabled);
    }

    public void stopFill() {
        jfilefinder.cancelFill();
//        jFileFinderSwingWorker.cancel(rootPaneCheckingEnabled);
    }

    public void setSearchBtn( String text, Color setColor )
        {
        searchBtn.setText( text );
        searchBtn.setBackground( setColor );
        searchBtn.setOpaque(true);
        }

//    public void resetSearchBtn() {
//        searchBtn.setText( "Search" );
//        searchBtn.setBackground( saveColor );
//        searchBtn.setOpaque(true);
//    }

    public void setResultsData( ResultsData resultsData )
        {
        this.resultsData = resultsData;
        }

    public void setNumFilesInTable()
        {
        NumberFormat numFormat = NumberFormat.getIntegerInstance();
        numFilesInTable.setText( numFormat.format( filesTbl.getModel().getRowCount() ) );
        }
    
    public void setProcessStatus( String text )
        {
        processStatus.setText(text);
        switch( text )
            {
            case PROCESS_STATUS_SEARCH_STARTED:  
                processStatus.setBackground( Color.GREEN );
                setSearchBtn( this.PROCESS_STATUS_CANCEL_SEARCH, Color.RED );
                setMessage( "" );
                break;
            case PROCESS_STATUS_FILL_STARTED:
                processStatus.setBackground( Color.GREEN );
                setSearchBtn( this.PROCESS_STATUS_CANCEL_FILL, Color.RED );
                break;
//            case PROCESS_STATUS_CANCEL_SEARCH:
//                processStatus.setBackground( Color.RED );
//                setSearchBtn( this.SEARCH_BTN_STOP_SEARCH, Color.RED );
//                break;
            case PROCESS_STATUS_SEARCH_CANCELED:
                processStatus.setBackground( Color.YELLOW );
                setSearchBtn( this.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            case PROCESS_STATUS_SEARCH_COMPLETED:
                processStatus.setBackground( saveColor );
                setSearchBtn( this.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
//            case PROCESS_STATUS_CANCEL_FILL:
//                processStatus.setBackground( Color.RED );
//                setSearchBtn( this.SEARCH_BTN_STOP_SEARCH, Color.RED );
//                break;
            case PROCESS_STATUS_FILL_CANCELED:
                processStatus.setBackground( Color.YELLOW );
                setSearchBtn( this.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            case PROCESS_STATUS_FILL_COMPLETED:
                processStatus.setBackground( saveColor );
                setSearchBtn( this.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            case PROCESS_STATUS_ERROR:
                processStatus.setBackground( Color.RED );
            System.out.println( "process status error !" );
                break;
            default:
                processStatus.setBackground( saveColor );
                setSearchBtn( this.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            }
        }

    public String getProcessStatus()
        {
        return processStatus.getText();
        }

    public String getMessage()
        {
        return message.getText();
        }

    public void setMessage( String text )
        {
        message.setText(text);
        }

    ActionListener menuActionListener = new ActionListener()
        {
        @Override
        public void actionPerformed(ActionEvent e) 
            {
            message.setText(e.getActionCommand());
            } 
        };
        
//            System.out.println( " am i running in EDT? =" + SwingUtilities.isEventDispatchThread() );
//            if (SwingUtilities.isEventDispatchThread())
//{
//    code.run();
//}
//else
//{
//    SwingUtilities.invokeLater(code);
//}

    Action filesTblCellChangedAction = new AbstractAction()
        {
        public void actionPerformed(ActionEvent e)
            {
            System.out.println( "doing filesTblCellChangedAction()" );
//            TableColumnModel tblColModel = filesTbl.getColumnModel();
//            System.out.println( "filesTblCellChangedAction() table col count 1=" + tblColModel.getColumnCount() );

            TableCellListener tcl = (TableCellListener)e.getSource();
            System.out.println("Row   : " + tcl.getRow());
            System.out.println("Column: " + tcl.getColumn());
            System.out.println("Old   : " + tcl.getOldValue());
            System.out.println("New   : " + tcl.getNewValue());
            Path targetPath = Paths.get( tcl.getNewValue().toString().trim() );
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();                
            if ( Files.exists( targetPath ) )
                {
                filesTblModel.setValueAt( tcl.getOldValue(), tcl.getRow(), tcl.getColumn() );
                JOptionPane.showMessageDialog( null, "That Folder name already exists!", "Error", JOptionPane.ERROR_MESSAGE );
                System.out.println( "That Folder name already exists! ( " + targetPath + ")" );
                setProcessStatus( PROCESS_STATUS_ERROR );
                processStatus.setText( "Error" );
                filesTblModel.deleteRowAt( 0 );
                }
            else
                {
                try {
                    if ( tcl.getOldValue() == null )
                        {
                        System.out.println( "try to create dir target =" + targetPath + "=" );
                        Files.createDirectory( targetPath );

//                        RenameActionPerformed( null );
                        }
                    else
                        {
                        Path sourcePath = Paths.get( tcl.getOldValue().toString().trim() );
                        if ( Files.exists( sourcePath ) )
                            {
                            System.out.println( "try to move dir source =" + sourcePath + "=   target =" + targetPath + "=" );
                            Files.move( sourcePath, targetPath );
                            }
                        }
                    }
                catch( AccessDeniedException ae )
                    {
                    setProcessStatus( PROCESS_STATUS_ERROR );
                    processStatus.setText( "Error" );
                    message.setText( "Access Denied Exception" );
                    filesTblModel.deleteRowAt( 0 );
                    }
                catch (Exception ex) 
                    {
                    System.out.println( "ex.getMessage() =" + ex.getMessage()+ "=" );
                    processStatus.setText( "Error" );
                    ex.printStackTrace();
                    message.setText( ex.getMessage() );
                    logger.log(Level.SEVERE, null, ex);
                    }
                }
            //filesTbl.setCellSelectionEnabled( false );
            setColumnSizes();
            }
        };

    public void searchBtnAction( java.awt.event.ActionEvent evt )
    {
        if ( searchBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_SEARCH ) )
            {
            System.out.println( "hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
            this.stopSearch();
            //JOptionPane.showConfirmDialog( null, "at call stop search" );
            }
        else if ( searchBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_FILL ) )
            {
            System.out.println( "hit stop fill button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus( PROCESS_STATUS_FILL_CANCELED );
            this.stopFill();
            //JOptionPane.showConfirmDialog( null, "at call stop fill" );
            }
        else
            {
            //JOptionPane.showConfirmDialog( null, "at call do search" );
            try {
                String[] args = new String[3];
                args[0] = startingFolder.getText().trim();
                //int startingPathLength = (args[0].endsWith( System.getProperty( "file.separator" ) ) || args[0].endsWith( "/" ) ) ? args[0].length() - 1 : args[0].length();
                //args[0] = args[0].substring( 0, startingPathLength );
                if ( ! ( args[0].endsWith(System.getProperty( "file.separator" ) )
                         || args[0].endsWith( "/" ) ) ) 
                    {
                    args[0] += System.getProperty( "file.separator" );
                    }
                startingFolder.setText( args[0] );

                args[1] = useRegexPattern.isSelected() ? "-regex" : "-glob";
                args[2] = filePattern.getText().trim();
                
                if ( useGlobPattern.isSelected()
                        && ! ( args[0].endsWith(System.getProperty( "file.separator" ) )
                         || args[0].endsWith( "/" ) )
                        && ! args[2].startsWith( "**" )
                        && ! (args[2].startsWith( System.getProperty( "file.separator" ) ) || args[2].startsWith( "/" )) )
                    {
                    int result = JOptionPane.showConfirmDialog( null, 
                       "There is no file separator (/ or \\ or **) between starting folder and pattern. Do you want to insert one?"
                            ,null, JOptionPane.YES_NO_OPTION );
                    if ( result == JOptionPane.YES_OPTION )
                        {
                        filePattern.setText( System.getProperty( "file.separator" ) + args[2] );
                        args[2] = filePattern.getText();
                        }
                    }
                
                //------- save history of paths  -------
                pathsHistoryList.add( args[0] );
                System.out.println( "after pathsHistoryList()" );

                //public ChainFilterA( ChainFilterA nextChainFilter )
                //Long size1Long = Long.parseLong( size1.getText().trim() );
                System.out.println( "tabsLogic button.getText() =" + (tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText()) + "=" );
                FilterChain chainFilterList = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
                FilterChain chainFilterFolderList = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
                FilterChain chainFilterPreVisitFolderList = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );

                try {
                    if ( ! filePattern.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of names!" );
                        ChainFilterOfNames chainFilterOfNames = new ChainFilterOfNames( args[1], (args[0] + args[2]).replace( "\\", "\\\\" ) );
                        chainFilterList.addFilter( chainFilterOfNames );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Name filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! size1.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of sizes!" );
                        ChainFilterOfSizes chainFilterOfSizes = new ChainFilterOfSizes( (String)size1Op.getSelectedItem(), size1.getText().trim(), ((String) sizeLogicOp.getValue()).trim(), (String)size2Op.getSelectedItem(), size2.getText().trim() );
                        chainFilterList.addFilter( chainFilterOfSizes );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Size filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( (Date) date1.getModel().getValue() != null )
                        {
                        System.out.println( "add filter of dates!" );
                        System.out.println( "selected date =" + (Date) date1.getModel().getValue() + "=" );
                        ChainFilterOfDates chainFilterOfDates = new ChainFilterOfDates( (String)date1Op.getSelectedItem(), (Date) date1.getModel().getValue(), ((String) dateLogicOp.getValue()).trim(), (String)date2Op.getSelectedItem(), (Date) date2.getModel().getValue() );
                        chainFilterList.addFilter( chainFilterOfDates );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Date filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! maxDepth.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of maxdepth!" );
                        System.out.println( "selected maxdepth =" + maxDepth.getText().trim() + "=" );
                        ChainFilterOfMaxDepth chainFilterOfMaxDepth = new ChainFilterOfMaxDepth( args[0], maxDepth.getText().trim() );
                        chainFilterFolderList.addFilter( chainFilterOfMaxDepth );
                        chainFilterList.addFilter( chainFilterOfMaxDepth );
                        ChainFilterOfPreVisitMaxDepth chainFilterOfPreVisitMaxDepth = new ChainFilterOfPreVisitMaxDepth( args[0], maxDepth.getText().trim() );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMaxDepth );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! minDepth.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of minDepth!" );
                        System.out.println( "selected minDepth =" + minDepth.getText().trim() + "=" );
                        ChainFilterOfMinDepth chainFilterOfMinDepth = new ChainFilterOfMinDepth( args[0], minDepth.getText().trim() );
                        chainFilterFolderList.addFilter( chainFilterOfMinDepth );
                        chainFilterList.addFilter( chainFilterOfMinDepth );
                        ChainFilterOfPreVisitMinDepth chainFilterOfPreVisitMinDepth = new ChainFilterOfPreVisitMinDepth( args[0], minDepth.getText().trim() );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMinDepth );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }
                
                try {
                    System.out.println( "showFilesFoldersCb.getSelectedItem() =" + showFilesFoldersCb.getSelectedItem() + "=" );
                    if ( showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FILES_ONLY ) 
                         || showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        System.out.println( "add filter Boolean False for folders" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterFolderList.addFilter( chainFilterOfBoolean );
                        }
                    if ( showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FOLDERS_ONLY ) 
                         || showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        System.out.println( "add filter Boolean False for files" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterList.addFilter( chainFilterOfBoolean );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Boolean filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                // if it matters for speed I could pass countOnlyFlag to jFileFinder too and not create the arrayList of paths !
                jfilefinder = new JFileFinder( args[0], args[1], args[2], chainFilterList, chainFilterFolderList, chainFilterPreVisitFolderList );
                jFileFinderSwingWorker = new JFileFinderSwingWorker( this, jfilefinder, args[0], args[1], args[2], countOnlyFlag );
//                searchBtn.setText( "Stop" );
//                searchBtn.setBackground(Color.RED);
//                searchBtn.setOpaque(true);
//                searchBtn.setBorderPainted(false);
//                message.setText( "Search started . . ." );
                //setProcessStatus( PROCESS_STATUS_SEARCH_STARTED );
                jFileFinderSwingWorker.execute();   //doInBackground();
                //jfinderThread = new Thread( jfilefinder );
//                        jfinderThread.start();
//                        jfinderThread.join();
//                        searchBtn.setText( "Search" );
//                        searchBtn.setBackground( saveColor );
//                        searchBtn.setOpaque(true);
            } 
            catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    public void emptyFilesTable()
        {
        System.out.println( "entered JFileFinderWin.emptyFilesTable()" );
        filesTbl.setModel( JFileFinder.emptyFilesTableModel( countOnlyFlag ) );
        setNumFilesInTable();
        }
    
    public void setColumnSizes()
        {
        TableColumnModel tblColModel = filesTbl.getColumnModel();
        if ( tblColModel.getColumnCount() < 2 )
            {
            return;
            }
        System.out.println( "setColumnSizes() table col count =" + tblColModel.getColumnCount() );
        tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_ISLINK ).setMaxWidth( 25 );
        tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_ISLINK ).setCellRenderer( new LinktypeCBCellRenderer() );
        tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_ISDIR ).setMaxWidth( 25 );
        tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_ISDIR ).setCellRenderer( new FiletypeCBCellRenderer() );
        if ( showJustFilenameFlag.isSelected() )
            {
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setPreferredWidth( 300 );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setCellRenderer( new PathRenderer() );
            }
        else
            {
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setPreferredWidth( 600 );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setCellRenderer( new DefaultTableCellRenderer() );
            }
        tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_MODIFIEDDATE ).setCellRenderer( FormatRenderer.getDateTimeRenderer() );
        tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_SIZE ).setCellRenderer( NumberRenderer.getIntegerRenderer() );
        }
    
    public void fillInFilesTable()
        {
        System.out.println( "entered JFileFinderWin.fillInFilesTable()" );
        filesTbl.getSelectionModel().clearSelection();
        filesTbl.setModel( JFileFinder.getFilesTableModel() );
        
        System.out.println( "resultsData.getFilesMatched() =" + resultsData.getFilesMatched() );
        if ( resultsData.getFilesMatched() > 0 || resultsData.getFoldersMatched() > 0 )  // if we found files
            {
            setColumnSizes();
            }

        // set up sorting
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>( filesTbl.getModel() );
        sorter.setSortsOnUpdates( false );
        //TableSorter<TableModel> sorter = new TableSorter<TableModel>( filesTblModel );
        filesTbl.setRowSorter( sorter );
        
        //int dirRowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );

        if ( filesTbl.getModel().getColumnCount() > 1 )
            {
            List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
            sortKeys.add( new RowSorter.SortKey( FilesTblModel.FILESTBLMODEL_ISDIR, SortOrder.DESCENDING ) );
            sortKeys.add( new RowSorter.SortKey( FilesTblModel.FILESTBLMODEL_PATH, SortOrder.ASCENDING ) );
            sorter.setSortKeys( sortKeys );
            }
        }

    public void desktopOpen( File file )
        {
        //File file = fpath.toFile();
        //first check if Desktop is supported by Platform or not
        if ( ! Desktop.isDesktopSupported() )
            {
            System.out.println("Desktop is not supported");
            return;
            }
         
        Desktop desktop = Desktop.getDesktop();
        try {
            if ( file.exists() )
                {
                desktop.open( file );
                }
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog( this, "Open not supported in this desktop", "Error", JOptionPane.ERROR_MESSAGE );
            }
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        }
        
    public void desktopEdit( File file )
        {
        //File file = fpath.toFile();
        //first check if Desktop is supported by Platform or not
        if ( ! Desktop.isDesktopSupported() )
            {
            System.out.println("Desktop is not supported");
            return;
            }
         
        Desktop desktop = Desktop.getDesktop();
        try {
            if ( file.exists() )
                {
                desktop.edit( file );
                }
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
//            JOptionPane.showMessageDialog( this, "Edit not supported in this desktop.\nWill try Open.", "Error", JOptionPane.ERROR_MESSAGE );
            desktopOpen( file );
            }
        }
        
    
    public static void addEscapeListener(final JFrame win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println( "previewImportWin formWindow dispose()" );
                win.dispatchEvent( new WindowEvent( win, WindowEvent.WINDOW_CLOSING )); 
                win.dispose();
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, KeyEvent.SHIFT_DOWN_MASK ),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }    
    
//    public static void addHotKeysListener(final JTable table) {
//        ActionListener hotkeysListener = new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println( "addHotKeysListener hotkeys()" );
//                System.out.println( "addHotKeysListener hotkeys()" + e.get );
//                win.dispatchEvent( new WindowEvent( table, WindowEvent.WINDOW_CLOSING )); 
//                win.dispose();
//            }
//        };
//
//        table.registerKeyboardAction(hotkeysListener,
//                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
//                JComponent.WHEN_IN_FOCUSED_WINDOW);
//    }    

    public void copyOrCut()
    {
        if ( filesTbl.getSelectedRow() < 0 )
            {
            JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
 
        //copyPaths = new ArrayList<Path>();
        //ArrayList<String> filesList = new ArrayList<String>();
        
        // DESIGN NOTE:  first file on clipboard is starting/from path ! - 2nd is word cut or copy
        StringBuffer stringBuf = new StringBuffer();
        copyPathStartPath = startingFolder.getText().trim();
        //filesList.add( new File( copyPathStartPath ) );
        //filesList.add( copyPathStartPath );
        //filesList.add( ( isDoingCutFlag ? "CUT" : "COPY" ) );
        stringBuf.append( copyPathStartPath ).append( "?" );
        stringBuf.append( ( isDoingCutFlag ? "CUT" : "COPY" ) ).append( "?" );
        
        for( int row : filesTbl.getSelectedRows() )
            {
            int rowIndex = filesTbl.convertRowIndexToModel( row );
            //System.out.println( "add copy path  row =" + row + "   rowIndex = " + rowIndex );
            //System.out.println( "copy path  =" + ((String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) + "=" );
            //copyPaths.add( Paths.get( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
            //filesList.add( new File( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
            stringBuf.append( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ).append( "?" );
            System.out.println( "add fpath =" + (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
            }   

//        ClipboardFiles clipboardFiles = new ClipboardFiles( filesList );
//        clipboard.setContents( clipboardFiles, clipboardFiles );  );        

        setClipboardContents( stringBuf.toString() );
        filesTbl.clearSelection();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        Copy = new javax.swing.JMenuItem();
        Cut = new javax.swing.JMenuItem();
        Paste = new javax.swing.JMenuItem();
        Delete = new javax.swing.JMenuItem();
        NewFolder = new javax.swing.JMenuItem();
        Rename = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        startCmdWin = new javax.swing.JMenuItem();
        Edit = new javax.swing.JMenuItem();
        openFile = new javax.swing.JMenuItem();
        copyFilename = new javax.swing.JMenuItem();
        saveAllAttrsToFile = new javax.swing.JMenuItem();
        savePathsToFile = new javax.swing.JMenuItem();
        openListWindow = new javax.swing.JMenuItem();
        openCodeWin = new javax.swing.JMenuItem();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        Paste1 = new javax.swing.JMenuItem();
        NewFolder1 = new javax.swing.JMenuItem();
        startCmdWin1 = new javax.swing.JMenuItem();
        saveAllAttrsToFile1 = new javax.swing.JMenuItem();
        savePathsToFile1 = new javax.swing.JMenuItem();
        jSplitPane2 = new javax.swing.JSplitPane();
        replaceSavePathPanel = new javax.swing.JPanel();
        savedPathsListScrollPane = new javax.swing.JScrollPane();
        savedPathsList = new javax.swing.JList<>();
        addPath = new javax.swing.JButton();
        deletePath = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        fileMgrMode = new javax.swing.JCheckBox();
        startingFolder = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        showJustFilenameFlag = new javax.swing.JCheckBox();
        logLevel = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        tabsLogicAndBtn = new javax.swing.JRadioButton();
        tabsLogicOrBtn = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        useGlobPattern = new javax.swing.JRadioButton();
        useRegexPattern = new javax.swing.JRadioButton();
        filePattern = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        maxDepth = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        minDepth = new javax.swing.JTextField();
        showFilesFoldersCb = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        date1Op = new javax.swing.JComboBox();
        String[] andOrSpinModelList = { "", "And", "Or" };
        SpinnerListModel dateAndOrSpinModel = new CyclingSpinnerListModel( andOrSpinModelList );
        dateLogicOp = new javax.swing.JSpinner( dateAndOrSpinModel );
        date2Op = new javax.swing.JComboBox();
        date1 = new org.jdatepicker.impl.JDatePickerImpl();
        date2 = new org.jdatepicker.impl.JDatePickerImpl();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        size2 = new javax.swing.JTextField();
        size1Op = new javax.swing.JComboBox();
        size2Op = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        SpinnerListModel sizeAndOrSpinModel = new CyclingSpinnerListModel( andOrSpinModelList );
        sizeLogicOp = new javax.swing.JSpinner( sizeAndOrSpinModel );
        size1 = new javax.swing.JFormattedTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        startConsoleCmd = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        stdOutFile = new javax.swing.JFormattedTextField();
        stdErrFile = new javax.swing.JFormattedTextField();
        jPanel7 = new javax.swing.JPanel();
        searchBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        filesTbl = new javax.swing.JTable() {
            public void changeSelection(    int row, int column, boolean toggle, boolean extend)
            {
                super.changeSelection(row, column, toggle, extend);

                if (editCellAt(row, column))
                {
                    Component editor = getEditorComponent();
                    editor.requestFocusInWindow();
                }
            }};
            processStatus = new javax.swing.JLabel();
            message = new javax.swing.JLabel();
            numFilesInTable = new javax.swing.JLabel();
            upFolder = new javax.swing.JButton();
            countBtn = new javax.swing.JButton();

            Copy.setText("Copy   (Ctrl-C)");
            Copy.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    CopyActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Copy);

            Cut.setText("Cut   (Ctrl-X)");
            Cut.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    CutActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Cut);

            Paste.setText("Paste   (Ctrl-P)");
            Paste.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    PasteActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Paste);

            Delete.setText("Delete   (Del or fn-Del)");
            Delete.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    DeleteActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Delete);

            NewFolder.setText("New Folder   (Ctrl-N)");
            NewFolder.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NewFolderActionPerformed(evt);
                }
            });
            jPopupMenu1.add(NewFolder);

            Rename.setText("Rename   (F2)");
            Rename.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RenameActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Rename);
            jPopupMenu1.add(jSeparator1);

            startCmdWin.setText("Open Terminal here");
            startCmdWin.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startCmdWinActionPerformed(evt);
                }
            });
            jPopupMenu1.add(startCmdWin);

            Edit.setText("Edit File");
            Edit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    EditActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Edit);

            openFile.setText("Open File");
            openFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openFileActionPerformed(evt);
                }
            });
            jPopupMenu1.add(openFile);

            copyFilename.setText("Copy Filename to Clipboard");
            copyFilename.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    copyFilenameActionPerformed(evt);
                }
            });
            jPopupMenu1.add(copyFilename);

            saveAllAttrsToFile.setText("Save All Attrs To File");
            saveAllAttrsToFile.setEnabled(false);
            saveAllAttrsToFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    saveAllAttrsToFileActionPerformed(evt);
                }
            });
            jPopupMenu1.add(saveAllAttrsToFile);

            savePathsToFile.setText("Save Paths to File");
            savePathsToFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    savePathsToFileActionPerformed(evt);
                }
            });
            jPopupMenu1.add(savePathsToFile);

            openListWindow.setText("Open List Window");
            openListWindow.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openListWindowActionPerformed(evt);
                }
            });
            jPopupMenu1.add(openListWindow);

            openCodeWin.setText("Open Code Window");
            openCodeWin.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openCodeWinActionPerformed(evt);
                }
            });
            jPopupMenu1.add(openCodeWin);

            Paste1.setText("Paste   (Ctrl-P)");
            Paste1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Paste1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(Paste1);

            NewFolder1.setText("New Folder   (Ctrl-N)");
            jPopupMenu2.add(NewFolder1);

            startCmdWin1.setText("Open Terminal here");
            startCmdWin1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startCmdWin1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(startCmdWin1);

            saveAllAttrsToFile1.setText("Save All Attrs To File");
            saveAllAttrsToFile1.setEnabled(false);
            saveAllAttrsToFile1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    saveAllAttrsToFile1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(saveAllAttrsToFile1);

            savePathsToFile1.setText("Save Paths to File");
            savePathsToFile1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    savePathsToFile1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(savePathsToFile1);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setTitle("JFileProcessor v1.4.10 - Stan Towianski  (c) 2015-2017");
            setPreferredSize(new java.awt.Dimension(900, 544));
            addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowOpened(java.awt.event.WindowEvent evt) {
                    formWindowOpened(evt);
                }
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

            jSplitPane2.setDividerSize(5);

            replaceSavePathPanel.setMinimumSize(new java.awt.Dimension(0, 0));
            replaceSavePathPanel.setPreferredSize(new java.awt.Dimension(100, 500));
            replaceSavePathPanel.setLayout(new java.awt.GridBagLayout());

            savedPathsListScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
            savedPathsListScrollPane.setPreferredSize(new java.awt.Dimension(100, 500));

            savedPathsList.setModel(new DefaultListModel() );
            savedPathsList.setPreferredSize(new java.awt.Dimension(100, 500));
            savedPathsList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    savedPathsListMouseClicked(evt);
                }
            });
            savedPathsListScrollPane.setViewportView(savedPathsList);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.5;
            replaceSavePathPanel.add(savedPathsListScrollPane, gridBagConstraints);

            addPath.setText("Add");
            addPath.setMargin(new java.awt.Insets(0, 0, 0, 0));
            addPath.setMaximumSize(new java.awt.Dimension(31, 23));
            addPath.setMinimumSize(new java.awt.Dimension(0, 0));
            addPath.setPreferredSize(new java.awt.Dimension(31, 23));
            addPath.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    addPathActionPerformed(evt);
                }
            });
            replaceSavePathPanel.add(addPath, new java.awt.GridBagConstraints());

            deletePath.setText("Delete");
            deletePath.setMargin(new java.awt.Insets(0, 0, 0, 0));
            deletePath.setMaximumSize(new java.awt.Dimension(75, 23));
            deletePath.setMinimumSize(new java.awt.Dimension(0, 0));
            deletePath.setPreferredSize(new java.awt.Dimension(75, 23));
            deletePath.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    deletePathActionPerformed(evt);
                }
            });
            replaceSavePathPanel.add(deletePath, new java.awt.GridBagConstraints());

            jSplitPane2.setLeftComponent(replaceSavePathPanel);

            jPanel10.setPreferredSize(new java.awt.Dimension(800, 500));
            jPanel10.setLayout(new java.awt.BorderLayout());

            jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

            jPanel6.setMinimumSize(new java.awt.Dimension(400, 35));
            jPanel6.setPreferredSize(new java.awt.Dimension(400, 130));
            jPanel6.setLayout(new java.awt.GridBagLayout());

            fileMgrMode.setText("File Mgr Mode");
            fileMgrMode.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fileMgrModeActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            jPanel6.add(fileMgrMode, gridBagConstraints);

            startingFolder.setToolTipText("History: Alt-Left, Alt-Right (Mac: option-Left, option-Right)");
            startingFolder.setMinimumSize(new java.awt.Dimension(200, 26));
            startingFolder.setPreferredSize(new java.awt.Dimension(200, 26));
            startingFolder.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startingFolderActionPerformed(evt);
                }
            });
            startingFolder.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent evt) {
                    startingFolderKeyTyped(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 0.5;
            gridBagConstraints.insets = new java.awt.Insets(2, 0, 4, 0);
            jPanel6.add(startingFolder, gridBagConstraints);

            jLabel1.setText("Starting Folder: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 0);
            jPanel6.add(jLabel1, gridBagConstraints);

            jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow/Search-icon-16.png"))); // NOI18N
            jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            jButton1.setMargin(new java.awt.Insets(2, 14, 2, 0));
            jButton1.setMaximumSize(new java.awt.Dimension(40, 23));
            jButton1.setMinimumSize(new java.awt.Dimension(40, 23));
            jButton1.setPreferredSize(new java.awt.Dimension(40, 23));
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 7;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
            jPanel6.add(jButton1, gridBagConstraints);

            jTabbedPane1.setMinimumSize(new java.awt.Dimension(390, 80));
            jTabbedPane1.setPreferredSize(new java.awt.Dimension(500, 400));

            jPanel5.setLayout(new java.awt.GridBagLayout());

            showJustFilenameFlag.setText("Show Just Filename");
            showJustFilenameFlag.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    showJustFilenameFlagActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
            jPanel5.add(showJustFilenameFlag, gridBagConstraints);

            logLevel.setModel(new javax.swing.DefaultComboBoxModel(logLevelsArrStr));
            logLevelsLhm.put( Level.OFF.toString(), Level.OFF );
            logLevelsLhm.put( Level.INFO.toString(), Level.INFO );
            logLevelsLhm.put( Level.WARNING.toString(), Level.WARNING );
            logLevelsLhm.put( Level.FINE.toString(), Level.FINE );
            logLevelsLhm.put( Level.FINER.toString(), Level.FINER );
            logLevelsLhm.put( Level.FINEST.toString(), Level.FINEST );
            logLevelsLhm.put( Level.SEVERE.toString(), Level.SEVERE );
            logLevelsLhm.put( Level.ALL.toString(), Level.ALL );
            logLevel.setSelectedItem( Level.OFF.toString() );
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.2;
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel5.add(logLevel, gridBagConstraints);

            jLabel10.setText("Log Level: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel5.add(jLabel10, gridBagConstraints);

            jButton3.setText("New Window");
            jButton3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
            jPanel5.add(jButton3, gridBagConstraints);

            jButton4.setText("Trash");
            jButton4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
            jPanel5.add(jButton4, gridBagConstraints);

            jTabbedPane1.addTab("View", jPanel5);

            jPanel4.setLayout(new java.awt.GridBagLayout());

            buttonGroup2.add(tabsLogicAndBtn);
            tabsLogicAndBtn.setSelected(true);
            tabsLogicAndBtn.setText("And");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel4.add(tabsLogicAndBtn, gridBagConstraints);

            buttonGroup2.add(tabsLogicOrBtn);
            tabsLogicOrBtn.setText("Or");
            tabsLogicOrBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    tabsLogicOrBtnActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel4.add(tabsLogicOrBtn, gridBagConstraints);

            jLabel8.setText("This is the logic between the conditions on each Tab.");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.2;
            gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
            jPanel4.add(jLabel8, gridBagConstraints);

            jTabbedPane1.addTab("Logical between Tabs", jPanel4);

            jPanel1.setLayout(new java.awt.GridBagLayout());

            buttonGroup1.add(useGlobPattern);
            useGlobPattern.setText("Glob pattern");
            useGlobPattern.setMaximumSize(new java.awt.Dimension(900, 23));
            useGlobPattern.setMinimumSize(new java.awt.Dimension(90, 23));
            useGlobPattern.setPreferredSize(new java.awt.Dimension(110, 23));
            useGlobPattern.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    useGlobPatternActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            jPanel1.add(useGlobPattern, gridBagConstraints);

            buttonGroup1.add(useRegexPattern);
            useRegexPattern.setText("Regex pattern");
            useRegexPattern.setMaximumSize(new java.awt.Dimension(900, 23));
            useRegexPattern.setMinimumSize(new java.awt.Dimension(100, 23));
            useRegexPattern.setPreferredSize(new java.awt.Dimension(120, 23));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 0);
            jPanel1.add(useRegexPattern, gridBagConstraints);

            filePattern.setMinimumSize(new java.awt.Dimension(150, 26));
            filePattern.setPreferredSize(new java.awt.Dimension(300, 26));
            filePattern.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filePatternActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
            jPanel1.add(filePattern, gridBagConstraints);

            jLabel6.setText("          ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 2.0;
            jPanel1.add(jLabel6, gridBagConstraints);

            maxDepth.setMinimumSize(new java.awt.Dimension(40, 23));
            maxDepth.setPreferredSize(new java.awt.Dimension(40, 23));
            maxDepth.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    maxDepthActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel1.add(maxDepth, gridBagConstraints);

            jLabel2.setText("Max Depth:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel1.add(jLabel2, gridBagConstraints);

            jLabel4.setText("Min Depth:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel1.add(jLabel4, gridBagConstraints);

            minDepth.setMinimumSize(new java.awt.Dimension(40, 23));
            minDepth.setPreferredSize(new java.awt.Dimension(40, 23));
            minDepth.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    minDepthActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel1.add(minDepth, gridBagConstraints);

            showFilesFoldersCb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Files & Folders", "Files Only", "Folders Only", "Neither" }));
            showFilesFoldersCb.setMinimumSize(new java.awt.Dimension(120, 26));
            showFilesFoldersCb.setPreferredSize(new java.awt.Dimension(120, 26));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.2;
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel1.add(showFilesFoldersCb, gridBagConstraints);

            jLabel9.setText("Files and/or Folders: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
            jPanel1.add(jLabel9, gridBagConstraints);

            jTabbedPane1.addTab("Name", jPanel1);

            jPanel2.setAlignmentX(0.0F);
            jPanel2.setAlignmentY(0.0F);
            jPanel2.setLayout(new java.awt.GridBagLayout());

            jLabel5.setText("          ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 2.0;
            jPanel2.add(jLabel5, gridBagConstraints);

            date1Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel2.add(date1Op, gridBagConstraints);

            dateLogicOp.setFocusable(false);
            dateLogicOp.setMinimumSize(new java.awt.Dimension(29, 25));
            dateLogicOp.setPreferredSize(new java.awt.Dimension(70, 25));
            dateLogicOp.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    dateLogicOpStateChanged(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel2.add(dateLogicOp, gridBagConstraints);

            date2Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            date2Op.setEnabled(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel2.add(date2Op, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel2.add(date1, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel2.add(date2, gridBagConstraints);

            jButton2.setText("clear");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel2.add(jButton2, gridBagConstraints);

            jTabbedPane1.addTab("Dates", jPanel2);

            jPanel3.setLayout(new java.awt.GridBagLayout());

            size2.setEnabled(false);
            size2.setMinimumSize(new java.awt.Dimension(6, 23));
            size2.setPreferredSize(new java.awt.Dimension(110, 23));
            size2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    size2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel3.add(size2, gridBagConstraints);

            size1Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel3.add(size1Op, gridBagConstraints);

            size2Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            size2Op.setEnabled(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel3.add(size2Op, gridBagConstraints);

            jLabel7.setText("          ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 8;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.3;
            jPanel3.add(jLabel7, gridBagConstraints);

            sizeLogicOp.setFocusable(false);
            sizeLogicOp.setMinimumSize(new java.awt.Dimension(29, 25));
            sizeLogicOp.setPreferredSize(new java.awt.Dimension(70, 25));
            sizeLogicOp.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    sizeLogicOpStateChanged(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel3.add(sizeLogicOp, gridBagConstraints);

            size1.setMinimumSize(new java.awt.Dimension(6, 23));
            size1.setPreferredSize(new java.awt.Dimension(110, 23));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel3.add(size1, gridBagConstraints);

            jTabbedPane1.addTab("Sizes", jPanel3);
            jTabbedPane1.addTab("Process", jPanel8);

            jPanel9.setLayout(new java.awt.GridBagLayout());

            jLabel11.setText("Start Console command: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 0.3;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(jLabel11, gridBagConstraints);

            startConsoleCmd.setMinimumSize(new java.awt.Dimension(300, 24));
            startConsoleCmd.setPreferredSize(new java.awt.Dimension(300, 24));
            startConsoleCmd.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startConsoleCmdActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            jPanel9.add(startConsoleCmd, gridBagConstraints);

            jLabel12.setText("StdOut Log File: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 0.3;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(jLabel12, gridBagConstraints);

            jLabel13.setText("StdErr Log File: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(jLabel13, gridBagConstraints);

            stdOutFile.setText("/tmp/jfp-out.log");
            stdOutFile.setMinimumSize(new java.awt.Dimension(300, 24));
            stdOutFile.setPreferredSize(new java.awt.Dimension(300, 24));
            stdOutFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    stdOutFileActionPerformed(evt);
                }
            });
            stdOutFile.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    stdOutFilePropertyChange(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jPanel9.add(stdOutFile, gridBagConstraints);

            stdErrFile.setText("/tmp/jfp-err.log");
            stdErrFile.setMinimumSize(new java.awt.Dimension(300, 24));
            stdErrFile.setPreferredSize(new java.awt.Dimension(300, 24));
            stdErrFile.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    stdErrFilePropertyChange(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jPanel9.add(stdErrFile, gridBagConstraints);

            jTabbedPane1.addTab("Settings", jPanel9);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 8;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            jPanel6.add(jTabbedPane1, gridBagConstraints);
            jTabbedPane1.getAccessibleContext().setAccessibleName("Name");

            jSplitPane1.setLeftComponent(jPanel6);

            jPanel7.setMinimumSize(new java.awt.Dimension(300, 90));
            jPanel7.setPreferredSize(new java.awt.Dimension(500, 400));
            jPanel7.setLayout(new java.awt.GridBagLayout());

            searchBtn.setText("Search");
            searchBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    searchBtnActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel7.add(searchBtn, gridBagConstraints);

            jLabel3.setText("Files in Table:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            jPanel7.add(jLabel3, gridBagConstraints);

            filesTbl.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null}
                },
                new String [] {
                    "Title 1", "Title 2", "Title 3", "Title 4"
                }
            ));
            jScrollPane1.setViewportView(filesTbl);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 9;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weighty = 1.0;
            jPanel7.add(jScrollPane1, gridBagConstraints);

            processStatus.setText(" ");
            processStatus.setMaximumSize(new java.awt.Dimension(999, 23));
            processStatus.setMinimumSize(new java.awt.Dimension(115, 23));
            processStatus.setOpaque(true);
            processStatus.setPreferredSize(new java.awt.Dimension(140, 23));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            jPanel7.add(processStatus, gridBagConstraints);

            message.setText(" ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 5;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.ipadx = 636;
            gridBagConstraints.weightx = 0.5;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            jPanel7.add(message, gridBagConstraints);

            numFilesInTable.setText(" ");
            numFilesInTable.setMaximumSize(new java.awt.Dimension(100, 26));
            numFilesInTable.setMinimumSize(new java.awt.Dimension(100, 26));
            numFilesInTable.setPreferredSize(new java.awt.Dimension(100, 26));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            jPanel7.add(numFilesInTable, gridBagConstraints);

            upFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow/Folder-Upload-icon-16.png"))); // NOI18N
            upFolder.setText("Up");
            upFolder.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    upFolderActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel7.add(upFolder, gridBagConstraints);

            countBtn.setText("Count");
            countBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    countBtnActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel7.add(countBtn, gridBagConstraints);

            jSplitPane1.setRightComponent(jPanel7);

            jPanel10.add(jSplitPane1, java.awt.BorderLayout.PAGE_START);

            jSplitPane2.setRightComponent(jPanel10);

            getContentPane().add(jSplitPane2, java.awt.BorderLayout.CENTER);

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void startingFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startingFolderActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_startingFolderActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        countOnlyFlag = false;
        searchBtnAction( evt );
    }//GEN-LAST:event_searchBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "Select Starting Search Folder" );
        if ( startingFolder.getText().trim().equals( "" ) )
            {
            chooser.setCurrentDirectory( new java.io.File(".") );
            }
        else
            {
            chooser.setCurrentDirectory( new java.io.File( startingFolder.getText().trim() ) );
            }
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        File selectedFile = chooser.getSelectedFile();
        //Settings.set( "last.directory", dialog.getCurrentDirectory().getAbsolutePath() );
        //String[] tt = { selectedFile.getPath() };
        startingFolder.setText( selectedFile.getPath() );
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void savePathsToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePathsToFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "File to Save To" );
        if ( startingFolder.getText().trim().equals( "" ) )
            {
            chooser.setCurrentDirectory( new java.io.File(".") );
            }
        else
            {
            chooser.setCurrentDirectory( new java.io.File( startingFolder.getText().trim() ) );
            }
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        File selectedFile = chooser.getSelectedFile();
        System.out.println( "File to save to =" + selectedFile + "=" );
        //Settings.set( "last.directory", dialog.getCurrentDirectory().getAbsolutePath() );
        //String[] tt = { selectedFile.getPath() };
        //startingFolder.setText( selectedFile.getPath() );
        
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileWriter fw = new FileWriter( selectedFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int maxRows = filesTbl.getRowCount();
            //int maxCols =  filesTbl.getColumnCount();
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            
            //loop for jtable rows
            for( int i = 0; i < maxRows; i++ )
                {
                bw.write( (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) );
                bw.write( "\n" );
            }
            //close BufferedWriter
            bw.close();
            //close FileWriter 
            fw.close();
            JOptionPane.showMessageDialog(null, "Data Exported");        
            }
        catch( Exception ex )
            {

            }
        }

    }//GEN-LAST:event_savePathsToFileActionPerformed

    private void saveAllAttrsToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllAttrsToFileActionPerformed
        JOptionPane.showConfirmDialog( null, "saveAllAttsToFileActionPerformed" );
    }//GEN-LAST:event_saveAllAttrsToFileActionPerformed

    private void useGlobPatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useGlobPatternActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useGlobPatternActionPerformed

    private void tabsLogicOrBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabsLogicOrBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tabsLogicOrBtnActionPerformed

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        File selectedPath = new File( (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
        //System.out.println( "selected row file =" + selectedPath );
        desktopOpen( selectedPath );
    }//GEN-LAST:event_openFileActionPerformed

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        File selectedPath = new File( (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
        //System.out.println( "selected row file =" + selectedPath );
        desktopEdit( selectedPath );
    }//GEN-LAST:event_EditActionPerformed

    private void copyFilenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyFilenameActionPerformed
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        String selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
        //System.out.println( "selected row file =" + selectedPath );
        StringSelection stringSelection = new StringSelection ( selectedPath );
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents (stringSelection, null);
    }//GEN-LAST:event_copyFilenameActionPerformed

    private void sizeLogicOpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeLogicOpStateChanged
        CyclingSpinnerListModel mod = (CyclingSpinnerListModel) sizeLogicOp.getModel();
        //System.out.println( "selected spinner value =" + ((String)mod.getValue()).trim() + "=" );
        if ( ((String)mod.getValue()).trim().equals( "" ) )
            {
            size2.setEnabled( false );
            size2Op.setEnabled( false );
            }
        else
            {
            size2.setEnabled( true );
            size2Op.setEnabled( true );
            }

    }//GEN-LAST:event_sizeLogicOpStateChanged

    private void filePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filePatternActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_filePatternActionPerformed

    private void size2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_size2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_size2ActionPerformed

    private void dateLogicOpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dateLogicOpStateChanged
        CyclingSpinnerListModel mod = (CyclingSpinnerListModel) dateLogicOp.getModel();
        //System.out.println( "selected spinner value =" + ((String)mod.getValue()).trim() + "=" );
        if ( ((String)mod.getValue()).trim().equals( "" ) )
            {
            date2.setMyEnabled( false );
            date2Op.setEnabled( false );
            }
        else
            {
            date2.setMyEnabled( true );
            date2Op.setEnabled( true );
            }

    }//GEN-LAST:event_dateLogicOpStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        date1.getModel().setValue( null );
        date2.getModel().setValue( null );
        dateLogicOp.setValue( "" );
    }//GEN-LAST:event_jButton2ActionPerformed

    private void upFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upFolderActionPerformed
        Path path = Paths.get( startingFolder.getText().trim() );
        int elements = path.getNameCount();
        if ( elements > 1 )
            {
            startingFolder.setText( path.getRoot().toString() + path.subpath( 0, elements - 1).toString() );
            }
        else if ( elements > 0 )
            {
            startingFolder.setText( path.getRoot().toString() );
            }
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_upFolderActionPerformed

    private void backwardFolderActionPerformed(java.awt.event.ActionEvent evt) {                                         
        startingFolder.setText( pathsHistoryList.getBackward() );
//        searchBtnActionPerformed( null );
    }                                        

    private void forwardFolderActionPerformed(java.awt.event.ActionEvent evt) {                                         
            System.out.println( "forwardFolderActionPerformed" );
        startingFolder.setText( pathsHistoryList.getForward() );
//        searchBtnActionPerformed( null );
    }                                        

    private void fileMgrModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMgrModeActionPerformed
        if ( fileMgrMode.isSelected() )
            {
            fileMgrMode.setText( "File Mgr Mode" );
            minDepth.setText( "1" );
            maxDepth.setText( "1" );
            showJustFilenameFlag.setSelected( true );
            showFilesFoldersCb.setSelectedItem( SHOWFILESFOLDERSCB_BOTH );
            jSplitPane1.setDividerLocation( jSplitPane1.getMinimumDividerLocation() );
            filePattern.setText( "" );
            jPanel6.setBackground( Color.LIGHT_GRAY );
            }
        else
            {
            fileMgrMode.setText( "Search Mode" );
            minDepth.setText( "" );
            maxDepth.setText( "" );
            showJustFilenameFlag.setSelected( false );
            showFilesFoldersCb.setSelectedItem( SHOWFILESFOLDERSCB_FILES_ONLY );
            jSplitPane1.setDividerLocation( jSplitPane1.getLastDividerLocation() );
            jPanel6.setBackground( Color.ORANGE );
            }
    }//GEN-LAST:event_fileMgrModeActionPerformed

    private void startingFolderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startingFolderKeyTyped
                // TODO add your handling code here:
    }//GEN-LAST:event_startingFolderKeyTyped

    private void CopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopyActionPerformed

        isDoingCutFlag = false;  // copy or cut starting?
        copyOrCut();
    }//GEN-LAST:event_CopyActionPerformed

    private void PasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PasteActionPerformed
        try {
            System.out.println( "PasteActionPerformed" );
            String selectedPath = startingFolder.getText().trim();
            if ( filesTbl.getSelectedRow() >= 0 )
                {
                int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
                if ( rowIndex >= 0 )
                    {
                    selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
                    }
                if ( selectedPath.equals( "" ) || ! Files.isDirectory( Paths.get( selectedPath ) ) )
                    {
                    selectedPath = startingFolder.getText().trim();
                    }
                }
            CopyFrame copyFrame = new CopyFrame();
        // DESIGN NOTE:  first file on clipboard is starting/from path ! - 2nd is word cut or copy
            //copyPaths = getClipboardFilesList();
            ArrayList<String> StringsList = getClipboardStringsList( "\\?" );
    
            if ( StringsList == null || StringsList.size() < 3 )
                {
                JOptionPane.showMessageDialog( this, "No Files selected to Copy.", "Error", JOptionPane.ERROR_MESSAGE );
                return;
                }
            copyPathStartPath = StringsList.remove( 0 );
            String tmp = StringsList.remove( 0 );
            isDoingCutFlag = tmp.equalsIgnoreCase( "CUT" ) ? true : false;
            System.out.println( "read clipboard isDoingCutFlag =" + isDoingCutFlag );
            copyPaths.clear();
            for ( String fpath : StringsList )
                {
                copyPaths.add( Paths.get( fpath ) );
                System.out.println( "read clipboard fpath =" + Paths.get( fpath ) );
                }
            //if ( 1 == 1 ) return;
            copyFrame.setup( this, isDoingCutFlag, copyPathStartPath, copyPaths, selectedPath );
            copyFrame.pack();
            copyFrame.setVisible( true );
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            }                     
    }//GEN-LAST:event_PasteActionPerformed

    private void saveAllAttrsToFile1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllAttrsToFile1ActionPerformed
        saveAllAttrsToFileActionPerformed(evt);
    }//GEN-LAST:event_saveAllAttrsToFile1ActionPerformed

    private void Paste1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Paste1ActionPerformed
        PasteActionPerformed(evt);
    }//GEN-LAST:event_Paste1ActionPerformed

    private void savePathsToFile1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePathsToFile1ActionPerformed
        savePathsToFileActionPerformed(evt);
    }//GEN-LAST:event_savePathsToFile1ActionPerformed

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteActionPerformed

        if ( filesTbl.getSelectedRow() < 0 )
            {
            JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
        
        copyPaths = new ArrayList<Path>();
        //ArrayList<File> filesList = new ArrayList<File>();

        // Do not use clipboard for deletes. only allow per this app instance!
        copyPathStartPath = startingFolder.getText().trim();
        //filesList.add( new File( copyPathStartPath ) );

        for( int row : filesTbl.getSelectedRows() )
            {
            int rowIndex = filesTbl.convertRowIndexToModel( row );
            //System.out.println( "add copy path  row =" + row + "   rowIndex = " + rowIndex );
            //System.out.println( "copy path  =" + ((String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) + "=" );
            copyPaths.add( Paths.get( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
            //filesList.add( new File( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
            System.out.println( "add filesList =" + copyPaths.get( copyPaths.size() - 1 ) );
            }   

        try {
            DeleteFrame deleteFrame = new DeleteFrame();
            if ( copyPaths == null || copyPaths.size() < 1 )
                {
                JOptionPane.showMessageDialog( this, "No Files selected to Delete.", "Error", JOptionPane.ERROR_MESSAGE );
                return;
                }
            deleteFrame.setup( this, copyPathStartPath, copyPaths );
            deleteFrame.pack();
            deleteFrame.setVisible( true );
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            } 
    }//GEN-LAST:event_DeleteActionPerformed

    private void NewFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewFolderActionPerformed
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
        filesTblCellListener.skipFirstEditOn( startingFolder.getText().trim() + "New Folder" );
        filesTblModel.insertRowAt( 0, startingFolder.getText().trim() + "New Folder" );
        setColumnSizes();
//        filesTbl.setCellSelectionEnabled( true );
            filesTblModel.setCellEditable( 0, FilesTblModel.FILESTBLMODEL_PATH, true );
        //filesTbl.changeSelection( 0, FilesTblModel.FILESTBLMODEL_PATH, false, false );
        //filesTbl.requestFocus();
        
//        filesTbl.editCellAt( 0, FilesTblModel.FILESTBLMODEL_PATH ); 
//        filesTbl.setSurrendersFocusOnKeystroke( true );	
//        filesTbl.transferFocus();
        filesTbl.changeSelection( 0, FilesTblModel.FILESTBLMODEL_PATH, false, false );
//        filesTbl.getEditorCo‌​mponent().requestFocus();
//        filesTbl.getEditorCo‌​mponent().requestFocus();
        //filesTbl.setCellSelectionEnabled( false );        

    }//GEN-LAST:event_NewFolderActionPerformed

    private void RenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RenameActionPerformed
        //System.out.println( "RenameActionPerformed evt.getSource() =" + evt.getSource() );
        if ( filesTbl.getSelectedRow() < 0 )
            {
            JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        System.out.println( "rename filesTbl.getSelectedRow() =" + filesTbl.getSelectedRow() + "   rowIndex = " + rowIndex );
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
        String selectedPath = (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
        System.out.println( "rename selectedPath =" + selectedPath + "   rowIndex = " + rowIndex );

//        filesTbl.setCellSelectionEnabled( true );
        filesTblModel.setCellEditable( rowIndex, FilesTblModel.FILESTBLMODEL_PATH, true );
        filesTbl.changeSelection( filesTbl.getSelectedRow(), FilesTblModel.FILESTBLMODEL_PATH, false, false );        
        
    }//GEN-LAST:event_RenameActionPerformed

    private void EnterActionPerformed(java.awt.event.ActionEvent evt) {                                       
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        //System.out.println( "converted rowIndex =" + rowIndex );
        String selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
        Boolean isDir = (Boolean) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_ISDIR );
        //System.out.println( "selected row file =" + selectedPath );
        if ( isDir )
            {
            this.setStartingFolder( selectedPath );
            this.callSearchBtnActionPerformed( null );
            }
        else
            {
            this.desktopEdit( new File( selectedPath ) );
            }        
    }                                      

    private void showJustFilenameFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showJustFilenameFlagActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showJustFilenameFlagActionPerformed

    private void CutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CutActionPerformed
        
        isDoingCutFlag = true;  // copy or cut starting?
        copyOrCut();
    }//GEN-LAST:event_CutActionPerformed

    private void minDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minDepthActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_minDepthActionPerformed

    private void maxDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxDepthActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_maxDepthActionPerformed

    private void countBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countBtnActionPerformed
        countOnlyFlag = true;
        searchBtnAction( evt );
    }//GEN-LAST:event_countBtnActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            int rc = JavaProcess.execJava( com.towianski.jfileprocessor.JFileFinderWin.class );
            System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
        } catch (IOException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            int rc = JavaProcess.execJava( com.towianski.jfileprocessor.JFileFinderWin.class, DesktopUtils.getTrashFolder().toString() );
            System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
        } catch (IOException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void savedPathsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_savedPathsListMouseClicked
        DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
        int index = savedPathsList.getSelectedIndex();
        String strPath = listModel.getElementAt(index).toString();
        if ( strPath.equals( "New Window" ) )
            {
            try {
                int rc = JavaProcess.execJava( com.towianski.jfileprocessor.JFileFinderWin.class );
                System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
            } catch (IOException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
            }
        else if ( strPath.equals( "Trash" ) )
            {
            try {
                int rc = JavaProcess.execJava( com.towianski.jfileprocessor.JFileFinderWin.class, DesktopUtils.getTrashFolder().toString() );
                System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
            } catch (IOException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
            }
        startingFolder.setText( savedPathReplacablePanel.getSavedPathsHm().get( strPath ) );
        searchBtnActionPerformed( null );

    }//GEN-LAST:event_savedPathsListMouseClicked

    private void deletePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePathActionPerformed
        DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
        int index = savedPathsList.getSelectedIndex();
        String strPath = listModel.getElementAt(index).toString();
        System.out.println( "delete path() index =" + index + "   element =" + strPath + "=" );
        if ( strPath.equals( "New Window" ) || strPath.equals( "Trash" ) )
            {
            return;
            }
        savedPathReplacablePanel.getSavedPathsHm().remove( listModel.getElementAt(index).toString() );
        listModel.remove( index );
        
    }//GEN-LAST:event_deletePathActionPerformed

    private void addPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPathActionPerformed
        DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
        System.out.println( "add path() startingFolder.getText() =" + startingFolder.getText() + "=" );
        String ans = JOptionPane.showInputDialog( "Name: ", Paths.get( startingFolder.getText() ).getFileName() );
        if ( ans == null )
            {
            return;
            }
        savedPathReplacablePanel.getSavedPathsHm().put( ans, startingFolder.getText() );
        System.out.println( "savedPathReplacablePanel.getSavedPathsHm().size() =" + savedPathReplacablePanel.getSavedPathsHm().size() + "=" );
        listModel.addElement( ans );
    }//GEN-LAST:event_addPathActionPerformed

    private void openListWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openListWindowActionPerformed
        try
            {
            String ans = JOptionPane.showInputDialog( "List Name: ", "" );
            if ( ans == null )
                {
                return;
                }
            while ( listOfFilesPanelsModel.getIndexOf( ans ) >= 0 )
                {
                ans = JOptionPane.showInputDialog( "Name exists. New name: ", "" );
                if ( ans == null )
                    {
                    return;
                    }
                }
            listOfFilesPanelsModel.addElement( ans );
            ListOfFilesPanel listOfFilesPanel = new ListOfFilesPanel( this, ans, listOfFilesPanelsModel );
            listOfFilesPanel.setState ( JFrame.ICONIFIED );
            listOfFilesPanel.setTitle(ans);
            listOfFilesPanelHm.put( ans, listOfFilesPanel );
            int maxRows = filesTbl.getRowCount();
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();

            DefaultComboBoxModel listModel = (DefaultComboBoxModel) listOfFilesPanel.getModel();

            int reply = JOptionPane.showConfirmDialog( null, "Add current files into list ? ", "List", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) 
                {
                System.out.println( "add list path() num of items =" + maxRows + "=" );
                for( int i = 0; i < maxRows; i++ )
                    {
    //                System.out.println( "add list path() path =" + (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
                    listModel.addElement( (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) );
                    }
                }
            listOfFilesPanel.setCount();

            listOfFilesPanel.pack();
            listOfFilesPanel.setVisible(true);
            listOfFilesPanel.setState ( JFrame.NORMAL );
//            JOptionPane.showMessageDialog(null, "Data Exported");        
            }
        catch( Exception ex )
            {

            }

    }//GEN-LAST:event_openListWindowActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        File selectedFile = new File( DesktopUtils.getBookmarks().toString() );
        System.out.println( "Bookmarks File =" + selectedFile + "=" );
        
        try
            {
            if ( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileWriter fw = new FileWriter( selectedFile.getAbsoluteFile() );
            BufferedWriter bw = new BufferedWriter(fw);

            //DefaultListModel thisListModel = (DefaultListModel) savedPathsList.getModel();
            DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
            int numItems = listModel.getSize();
            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
            System.out.println( "savedPathReplacablePanel.getSavedPathsHm().size() =" + savedPathReplacablePanel.getSavedPathsHm().size() + "=" );
            
            //loop for jtable rows
            for( int i = 0; i < numItems; i++ )
                {
                if ( ! listModel.getElementAt( i ).toString().equals( "New Window" ) && 
                     ! listModel.getElementAt( i ).toString().equals( "Trash" ) )
                    {
                    System.out.println( "bookmark saving =" + listModel.getElementAt( i ).toString() + "=" );
                    bw.write( listModel.getElementAt( i ).toString() + "," + savedPathReplacablePanel.getSavedPathsHm().get( listModel.getElementAt( i ).toString() ) );
                    bw.write( "\n" );
                    }
                }
            //close BufferedWriter
            bw.close();
            //close FileWriter 
            fw.close();
//            JOptionPane.showMessageDialog(null, "Data Exported");        
            }
        catch( Exception ex )
            {

            }
    }//GEN-LAST:event_formWindowClosing

    private void startCmdWinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCmdWinActionPerformed
        try {
            System.out.println( "javaprocess.exec start new window getStartingFolder() =" + getStartingFolder() + "=   startConsoleCmd.getText() =" + startConsoleCmd.getText() + "=" );
            int rc = 0;
            rc = JavaProcess.exec( getStartingFolder(), startConsoleCmd.getText() );
            System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
        } catch (IOException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_startCmdWinActionPerformed

    private void startConsoleCmdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startConsoleCmdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startConsoleCmdActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if ( startConsoleCmd.getText() == null || startConsoleCmd.getText().equals( "" ) )
            {
            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
                {
                startConsoleCmd.setText( "/usr/bin/open -n -a /Applications/Utilities/Terminal.app" );
                }
            else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
                {
                startConsoleCmd.setText( "cmd.exe /C start" );
                }
            else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "linux" ) )
                {
                if ( Files.exists( Paths.get( "/bin/konsole" ) ) )
                    {
                    startConsoleCmd.setText( "/bin/konsole" );
                    }
                else if ( Files.exists( Paths.get( "/usr/bin/gnome-terminal" ) ) )
                    {
                    startConsoleCmd.setText( "/usr/bin/gnome-terminal" );
                    }
                else if ( Files.exists( Paths.get( "/usr/bin/xterm" ) ) )
                    {
                    startConsoleCmd.setText( "/usr/bin/xterm" );
                    }
                }
            }
    }//GEN-LAST:event_formWindowOpened

    private void startCmdWin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCmdWin1ActionPerformed
        startCmdWinActionPerformed( null );
    }//GEN-LAST:event_startCmdWin1ActionPerformed

    private void stdOutFilePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_stdOutFilePropertyChange
        try {
            System.out.println( "stdOutFilePropertyChange() set to file =" + stdOutFile.getText() + "=" );
            if ( stdOutFile.getText() != null && ! stdOutFile.getText().equals( "" ) )
                {
                System.setOut(new PrintStream(new File( stdOutFile.getText() ) ) );
                }
            else
                {
                    //  should go to stderr but oh well for now
                System.setOut( console );
                }
            } 
        catch (Exception e) 
            {
            e.printStackTrace();
            }

    }//GEN-LAST:event_stdOutFilePropertyChange

    private void stdErrFilePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_stdErrFilePropertyChange
        try {
            System.out.println( "stdErrFilePropertyChange() set to file =" + stdErrFile.getText() + "=" );
            if ( stdErrFile.getText() != null && ! stdErrFile.getText().equals( "" ) )
                {
                System.setErr(new PrintStream(new File( stdErrFile.getText() ) ) );
                }
            else
                {
                    //  should go to stderr but oh well for now
                System.setErr( console );
                }
            } 
        catch (Exception e) 
            {
            e.printStackTrace();
            }

    }//GEN-LAST:event_stdErrFilePropertyChange

    private void openCodeWinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCodeWinActionPerformed
        try
            {
//            String ans = JOptionPane.showInputDialog( "List Name: ", "" );
//            if ( ans == null )
//                {
//                return;
//                }
//            while ( listOfFilesPanelsModel.getIndexOf( ans ) >= 0 )
//                {
//                ans = JOptionPane.showInputDialog( "Name exists. New name: ", "" );
//                if ( ans == null )
//                    {
//                    return;
//                    }
//                }
//            listOfFilesPanelsModel.addElement( ans );
            CodeProcessorPanel codeProcessorPanel = new CodeProcessorPanel( this, listOfFilesPanelsModel );
            codeProcessorPanel.setState ( JFrame.ICONIFIED );
            codeProcessorPanel.setTitle( "code" );
//            listOfFilesPanelHm.put( ans, listOfFilesPanel );
//            int maxRows = filesTbl.getRowCount();
//            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
//
//            DefaultComboBoxModel listModel = (DefaultComboBoxModel) listOfFilesPanel.getModel();
//
//            //loop for jtable rows
//            System.out.println( "add list path() num of items =" + maxRows + "=" );
//            for( int i = 0; i < maxRows; i++ )
//                {
////                System.out.println( "add list path() path =" + (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
//                listModel.addElement( (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) );
//                }
            codeProcessorPanel.pack();
            codeProcessorPanel.setVisible(true);
            codeProcessorPanel.setState ( JFrame.NORMAL );
//            JOptionPane.showMessageDialog(null, "Data Exported");        
            }
        catch( Exception ex )
            {

            }

    }//GEN-LAST:event_openCodeWinActionPerformed

    private void stdOutFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdOutFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stdOutFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        MyLogger logger = MyLogger.getLogger( JFileFinderWin.class.getName() );

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        final JFileFinderWin jffw = new JFileFinderWin();
        if ( args.length > 0 )
            {
            jffw.startingFolder.setText( args[0] );
            jffw.searchBtnActionPerformed( null );
            }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new JFileFinderWin().setVisible(true);
                jffw.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Copy;
    private javax.swing.JMenuItem Cut;
    private javax.swing.JMenuItem Delete;
    private javax.swing.JMenuItem Edit;
    private javax.swing.JMenuItem NewFolder;
    private javax.swing.JMenuItem NewFolder1;
    private javax.swing.JMenuItem Paste;
    private javax.swing.JMenuItem Paste1;
    private javax.swing.JMenuItem Rename;
    private javax.swing.JButton addPath;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JMenuItem copyFilename;
    private javax.swing.JButton countBtn;
    private org.jdatepicker.impl.JDatePickerImpl date1;
    private javax.swing.JComboBox date1Op;
    private org.jdatepicker.impl.JDatePickerImpl date2;
    private javax.swing.JComboBox date2Op;
    private javax.swing.JSpinner dateLogicOp;
    private javax.swing.JButton deletePath;
    private javax.swing.JCheckBox fileMgrMode;
    private javax.swing.JTextField filePattern;
    private javax.swing.JTable filesTbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    //Level[] logLevelsArr = new Level [] { Level.ALL, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.INFO, Level.OFF, Level.SEVERE, Level.WARNING };
    String[] logLevelsArrStr = new String [] { Level.OFF.toString(), Level.SEVERE.toString(), Level.WARNING.toString(), Level.INFO.toString(), Level.CONFIG.toString(), Level.FINE.toString(), Level.FINER.toString(), Level.FINEST.toString(), Level.ALL.toString() };
    LinkedHashMap<String,Level> logLevelsLhm = new LinkedHashMap<String,Level>();
    private javax.swing.JComboBox logLevel;
    private javax.swing.JTextField maxDepth;
    private javax.swing.JLabel message;
    private javax.swing.JTextField minDepth;
    private javax.swing.JLabel numFilesInTable;
    private javax.swing.JMenuItem openCodeWin;
    private javax.swing.JMenuItem openFile;
    private javax.swing.JMenuItem openListWindow;
    private javax.swing.JLabel processStatus;
    private javax.swing.JPanel replaceSavePathPanel;
    private javax.swing.JMenuItem saveAllAttrsToFile;
    private javax.swing.JMenuItem saveAllAttrsToFile1;
    private javax.swing.JMenuItem savePathsToFile;
    private javax.swing.JMenuItem savePathsToFile1;
    private javax.swing.JList<String> savedPathsList;
    private javax.swing.JScrollPane savedPathsListScrollPane;
    protected javax.swing.JButton searchBtn;
    private javax.swing.JComboBox showFilesFoldersCb;
    private javax.swing.JCheckBox showJustFilenameFlag;
    private javax.swing.JFormattedTextField size1;
    private javax.swing.JComboBox size1Op;
    private javax.swing.JTextField size2;
    private javax.swing.JComboBox size2Op;
    private javax.swing.JSpinner sizeLogicOp;
    private javax.swing.JMenuItem startCmdWin;
    private javax.swing.JMenuItem startCmdWin1;
    private javax.swing.JTextField startConsoleCmd;
    private javax.swing.JTextField startingFolder;
    private javax.swing.JFormattedTextField stdErrFile;
    private javax.swing.JFormattedTextField stdOutFile;
    private javax.swing.JRadioButton tabsLogicAndBtn;
    private javax.swing.JRadioButton tabsLogicOrBtn;
    private javax.swing.JButton upFolder;
    private javax.swing.JRadioButton useGlobPattern;
    private javax.swing.JRadioButton useRegexPattern;
    // End of variables declaration//GEN-END:variables
}
