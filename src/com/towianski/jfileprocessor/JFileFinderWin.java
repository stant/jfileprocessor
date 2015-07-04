/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

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
import static com.towianski.models.FilesTblModel.FILESTBLMODEL_ISLINK;
import com.towianski.chainfilters.FilterChain;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerListModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class JFileFinderWin extends javax.swing.JFrame {

    Thread jfinderThread = null;
    JFileFinderSwingWorker jFileFinderSwingWorker = null;
    ResultsData resultsData = null;
    JFileFinder jfilefinder = null;
    Color saveColor = null;

    public static final String PROCESS_STATUS_SEARCH_STARTED = "Search Started . . .";
    public static final String PROCESS_STATUS_FILL_STARTED = "Fill Started . . .";
    public static final String PROCESS_STATUS_SEARCH_CANCELED = "Search canceled";
    public static final String PROCESS_STATUS_SEARCH_COMPLETED = "Search completed";
    public static final String PROCESS_STATUS_FILL_CANCELED = "Fill canceled";
    public static final String PROCESS_STATUS_FILL_COMPLETED = "Fill completed";
    public static final String PROCESS_STATUS_CANCEL_SEARCH = "Cancel Search";
    public static final String PROCESS_STATUS_CANCEL_FILL = "Cancel Fill";
    public static final String PROCESS_STATUS_SEARCH_READY = "Search";

//    JDatePickerImpl date1 = null;
//    JDatePickerImpl date2 = null;
    
    /**
     * Creates new form JFileFinder
     */
    public JFileFinderWin() {

        initComponents();

        date2.setMyEnabled( false );
        date2Op.setEnabled( false );
        jTabbedPane1.setSelectedIndex( 1 );
                
        useGlobPattern.setSelected( true );
        tabsLogicAndBtn.setSelected( true );
        saveColor = searchBtn.getBackground();
        this.addEscapeListener( this );
        filesTbl.addMouseListener( new MyMouseAdapter( jPopupMenu1, this ) );
        this.setLocationRelativeTo( getRootPane() );
        
//        System.out.println( "create spinner");
//        String[] andOrSpinModelList = { "", "And", "Or" };
//        SpinnerListModel andOrSpinModel = new CyclingSpinnerListModel( andOrSpinModelList );
//        jSpinner1 = new javax.swing.JSpinner( andOrSpinModel );        
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

    public void callSearchBtnActionPerformed(java.awt.event.ActionEvent evt)
    {
        searchBtnActionPerformed( evt );
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

    public void setMessage( String text )
        {
        message.setText(text);
        }

        ActionListener menuActionListener = new ActionListener(){
  
        @Override
        public void actionPerformed(ActionEvent e) {
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
    
    public void emptyFilesTable()
        {
        System.out.println( "entered JFileFinderWin.emptyFilesTable()" );
        filesTbl.setModel( JFileFinder.emptyFilesTableModel() );
        setNumFilesInTable();
        }
    
    public void fillInFilesTable()
        {
        System.out.println( "entered JFileFinderWin.fillInFilesTable()" );
        filesTbl.setModel( JFileFinder.getFilesTableModel() );
        
        System.out.println( "resultsData.getFilesMatched() =" + resultsData.getFilesMatched() );
        if ( resultsData.getFilesMatched() > 0 )  // if we found files
            {
            TableColumnModel tblColModel = filesTbl.getColumnModel();
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_MODIFIEDDATE ).setCellRenderer( FormatRenderer.getDateTimeRenderer() );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_SIZE ).setCellRenderer( NumberRenderer.getIntegerRenderer() );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_ISLINK ).setMaxWidth( 40 );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_ISDIR ).setMaxWidth( 40 );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setPreferredWidth( 600 );
            }

        // set up sorting
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>( filesTbl.getModel() );
        sorter.setSortsOnUpdates( false );
        //TableSorter<TableModel> sorter = new TableSorter<TableModel>( filesTblModel );
        filesTbl.setRowSorter( sorter );
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
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog( this, "Edit not supported in this desktop.\nWill try Open.", "Error", JOptionPane.ERROR_MESSAGE );
            desktopOpen( file );
            }
        }
        
    
    public static void addEscapeListener(final JFrame win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.err.println( "previewImportWin formWindow dispose()" );
                win.dispatchEvent( new WindowEvent( win, WindowEvent.WINDOW_CLOSING )); 
                win.dispose();
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
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
        Edit = new javax.swing.JMenuItem();
        openFile = new javax.swing.JMenuItem();
        copyFilename = new javax.swing.JMenuItem();
        saveAllAttrsToFile = new javax.swing.JMenuItem();
        savePathsToFile = new javax.swing.JMenuItem();
        buttonGroup2 = new javax.swing.ButtonGroup();
        size3 = new javax.swing.JFormattedTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        startingFolder = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        fileMgrMode = new javax.swing.JCheckBox();
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
        upFolder = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        searchBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        filesTbl = new javax.swing.JTable();
        processStatus = new javax.swing.JLabel();
        message = new javax.swing.JLabel();
        numFilesInTable = new javax.swing.JLabel();

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

        copyFilename.setText("Copy Filename");
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

        size3.setMinimumSize(new java.awt.Dimension(6, 23));
        size3.setPreferredSize(new java.awt.Dimension(110, 23));
        size3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size3ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JFileProcessor v1.3.1 - Stan Towianski  (c) 2015");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel6.setMinimumSize(new java.awt.Dimension(400, 70));
        jPanel6.setPreferredSize(new java.awt.Dimension(400, 130));
        jPanel6.setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 282;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel6.add(startingFolder, gridBagConstraints);

        jLabel1.setText("Starting Folder: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel6.add(jLabel1, gridBagConstraints);

        jButton1.setText(". . .");
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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel6.add(jButton1, gridBagConstraints);

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(390, 80));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(600, 400));

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
        gridBagConstraints.gridwidth = 3;
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel1.add(maxDepth, gridBagConstraints);

        jLabel2.setText("Max Depth:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel4.setText("Min Depth:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        minDepth.setMinimumSize(new java.awt.Dimension(40, 23));
        minDepth.setPreferredSize(new java.awt.Dimension(40, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel1.add(minDepth, gridBagConstraints);

        fileMgrMode.setText("File Mgr Mode");
        fileMgrMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMgrModeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(fileMgrMode, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(jTabbedPane1, gridBagConstraints);
        jTabbedPane1.getAccessibleContext().setAccessibleName("Name");

        upFolder.setText("Up Folder");
        upFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upFolderActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 0);
        jPanel6.add(upFolder, gridBagConstraints);

        jSplitPane1.setLeftComponent(jPanel6);

        jPanel7.setMinimumSize(new java.awt.Dimension(300, 90));
        jPanel7.setPreferredSize(new java.awt.Dimension(1080, 400));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        searchBtn.setText("Search");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel7.add(searchBtn, gridBagConstraints);

        jLabel3.setText("Files in Table:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(jScrollPane1, gridBagConstraints);

        processStatus.setText(" ");
        processStatus.setMaximumSize(new java.awt.Dimension(100, 26));
        processStatus.setMinimumSize(new java.awt.Dimension(100, 26));
        processStatus.setPreferredSize(new java.awt.Dimension(130, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel7.add(processStatus, gridBagConstraints);

        message.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 636;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel7.add(message, gridBagConstraints);

        numFilesInTable.setText(" ");
        numFilesInTable.setMaximumSize(new java.awt.Dimension(100, 26));
        numFilesInTable.setMinimumSize(new java.awt.Dimension(100, 26));
        numFilesInTable.setPreferredSize(new java.awt.Dimension(100, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(numFilesInTable, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel7);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startingFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startingFolderActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_startingFolderActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        if ( searchBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_SEARCH ) )
            {
            System.err.println( "hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
            this.stopSearch();
            //JOptionPane.showConfirmDialog( null, "at call stop search" );
            }
        else if ( searchBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_FILL ) )
            {
            System.err.println( "hit stop fill button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
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
                
                //public ChainFilterA( ChainFilterA nextChainFilter )
                //Long size1Long = Long.parseLong( size1.getText().trim() );
                System.err.println( "tabsLogic button.getText() =" + (tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText()) + "=" );
                FilterChain chainFilterlist = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
                FilterChain chainFilterFolderlist = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );

                try {
                    if ( ! filePattern.getText().trim().equals( "" ) )
                        {
                        System.err.println( "add filter of names!" );
                        ChainFilterOfNames chainFilterOfNames = new ChainFilterOfNames( args[1], (args[0] + args[2]).replace( "\\", "\\\\" ) );
                        chainFilterlist.addFilter( chainFilterOfNames );
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
                        System.err.println( "add filter of sizes!" );
                        ChainFilterOfSizes chainFilterOfSizes = new ChainFilterOfSizes( (String)size1Op.getSelectedItem(), size1.getText().trim(), ((String) sizeLogicOp.getValue()).trim(), (String)size2Op.getSelectedItem(), size2.getText().trim() );
                        chainFilterlist.addFilter( chainFilterOfSizes );
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
                        System.err.println( "add filter of dates!" );
                        System.err.println( "selected date =" + (Date) date1.getModel().getValue() + "=" );
                        ChainFilterOfDates chainFilterOfDates = new ChainFilterOfDates( (String)date1Op.getSelectedItem(), (Date) date1.getModel().getValue(), ((String) dateLogicOp.getValue()).trim(), (String)date2Op.getSelectedItem(), (Date) date2.getModel().getValue() );
                        chainFilterlist.addFilter( chainFilterOfDates );
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
                        System.err.println( "add filter of maxdepth!" );
                        System.err.println( "selected maxdepth =" + maxDepth.getText().trim() + "=" );
                        ChainFilterOfMaxDepth chainFilterOfMaxDepth = new ChainFilterOfMaxDepth( args[0], maxDepth.getText().trim() );
                        chainFilterFolderlist.addFilter( chainFilterOfMaxDepth );
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
                        System.err.println( "add filter of minDepth!" );
                        System.err.println( "selected minDepth =" + minDepth.getText().trim() + "=" );
                        ChainFilterOfMinDepth chainFilterOfMinDepth = new ChainFilterOfMinDepth( args[0], minDepth.getText().trim() );
                        chainFilterFolderlist.addFilter( chainFilterOfMinDepth );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }
                
                //jfilefinder = new JFileFinder( args[0], args[1], args[2], filterOfSizes );
                jfilefinder = new JFileFinder( args[0], args[1], args[2], chainFilterlist, chainFilterFolderlist );
                jFileFinderSwingWorker = new JFileFinderSwingWorker( this, jfilefinder, args[0], args[1], args[2] );
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
                Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
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
        System.err.println( "File to save to =" + selectedFile + "=" );
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
        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
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
        // TODO add your handling code here:
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

    private void size3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_size3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_size3ActionPerformed

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

    private void fileMgrModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMgrModeActionPerformed
        if ( fileMgrMode.isSelected() )
            {
            minDepth.setText( "1" );
            maxDepth.setText( "1" );
            }
        else
            {
            minDepth.setText( "" );
            maxDepth.setText( "" );
            }
    }//GEN-LAST:event_fileMgrModeActionPerformed

    private void startingFolderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startingFolderKeyTyped
                // TODO add your handling code here:
    }//GEN-LAST:event_startingFolderKeyTyped

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
            java.util.logging.Logger.getLogger(JFileFinderWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFileFinderWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFileFinderWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFileFinderWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFileFinderWin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Edit;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JMenuItem copyFilename;
    private org.jdatepicker.impl.JDatePickerImpl date1;
    private javax.swing.JComboBox date1Op;
    private org.jdatepicker.impl.JDatePickerImpl date2;
    private javax.swing.JComboBox date2Op;
    private javax.swing.JSpinner dateLogicOp;
    private javax.swing.JCheckBox fileMgrMode;
    private javax.swing.JTextField filePattern;
    private javax.swing.JTable filesTbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField maxDepth;
    private javax.swing.JLabel message;
    private javax.swing.JTextField minDepth;
    private javax.swing.JLabel numFilesInTable;
    private javax.swing.JMenuItem openFile;
    private javax.swing.JLabel processStatus;
    private javax.swing.JMenuItem saveAllAttrsToFile;
    private javax.swing.JMenuItem savePathsToFile;
    javax.swing.JButton searchBtn;
    private javax.swing.JFormattedTextField size1;
    private javax.swing.JComboBox size1Op;
    private javax.swing.JTextField size2;
    private javax.swing.JComboBox size2Op;
    private javax.swing.JFormattedTextField size3;
    private javax.swing.JSpinner sizeLogicOp;
    private javax.swing.JTextField startingFolder;
    private javax.swing.JRadioButton tabsLogicAndBtn;
    private javax.swing.JRadioButton tabsLogicOrBtn;
    private javax.swing.JButton upFolder;
    private javax.swing.JRadioButton useGlobPattern;
    private javax.swing.JRadioButton useRegexPattern;
    // End of variables declaration//GEN-END:variables
}
