/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocessor.services.CallGroovy;
import groovy.lang.Binding;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author stan
 */
public class CodeProcessorPanel extends javax.swing.JFrame {

        LinkedHashMap<String,String> savedPathsHm = new LinkedHashMap<String,String>();
        JFileFinderWin jFileFinderWin = null;
        DefaultComboBoxModel listOfFilesPanelModel = null;
        String currentDirectory = "";
        String currentFile = "";
        static Color buttonBgColor = null;

    /**
     * Creates new form SavedPathsPanel
     */
    public CodeProcessorPanel( JFileFinderWin jFileFinderWin, String selectedPath, String listOfFilesPanelName ) {
        this.jFileFinderWin = jFileFinderWin;
        this.listOfFilesPanelModel = jFileFinderWin.getListOfFilesPanelsModel();
        initComponents();
        
        buttonBgColor = goButton.getBackground();

        if ( selectedPath != null )
            {
            readFile( new File( selectedPath ) );
            }
        else if ( jFileFinderWin.getStartingFolder().equals( "" ) )
            {
            currentDirectory = ".";
            }
        else
            {
            currentDirectory = jFileFinderWin.getStartingFolder();
            }
        listOfLists.setSelectedItem( listOfFilesPanelName );
        this.setLocationRelativeTo( getRootPane() );
        this.validate();
        this.addEscapeListener( this );
    }

    public void setJFileFinderWin( JFileFinderWin jFileFinderWin ) {
        this.jFileFinderWin = jFileFinderWin;
    }

    public LinkedHashMap<String, String> getSavedPathsHm() {
        return savedPathsHm;
    }

    public void setSavedPathsHm(LinkedHashMap<String, String> savedPathsHm) {
        this.savedPathsHm = savedPathsHm;
    }

    public DefaultComboBoxModel getModel() {
        return (DefaultComboBoxModel) PathsList.getModel();
    }

    public void setSavedPathsList(JList<String> savedPathsList) {
        this.PathsList = savedPathsList;
    }

    public void setGoButton( Color toColor ) {
        goButton.setBackground( toColor );
    }
    
    public void resetGoButton() {
        goButton.setBackground( buttonBgColor );
    }
    
    public void readFile( File selectedFile )
        {
        System.out.println( "File to read =" + selectedFile + "=" );
        currentDirectory = selectedFile.getParent();
        currentFile = selectedFile.getAbsolutePath();
        
        try
            {
            if ( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileReader fr = new FileReader( selectedFile.getAbsoluteFile() );
            BufferedReader br = new BufferedReader(fr);

//            codePane.read( br, evt );
            codePane.read( br, null );
            //close BufferedWriter
            br.close();
            //close FileWriter 
            fr.close();
            this.setTitle( "code - " + currentFile );
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        }
    
    public void saveToFile( File selectedFile )
        {
        System.out.println( "File to save to =" + selectedFile + "=" );
        currentDirectory = selectedFile.getParent();
        currentFile = selectedFile.getAbsolutePath();
        
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileWriter fw = new FileWriter( selectedFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            codePane.write(bw);
//            int numItems = thisListModel.getSize();
//            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
//            
//            //loop for jtable rows
//            for( int i = 0; i < numItems; i++ )
//                {
//                bw.write( (String) thisListModel.getElementAt( i ) );
//                bw.write( "\n" );
//            }
            //close BufferedWriter
            bw.close();
            //close FileWriter 
            fw.close();
//            JOptionPane.showMessageDialog(null, "Saved to File");        
            this.setTitle( "code - " + currentFile );
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        }
    
    public void executeScript()
    {
        File currentDir = new File( currentDirectory );
//        this.pathRoots = new String[] { currentDir.getAbsolutePath() };
        CallGroovy callGroovy = new CallGroovy( new String[] { currentDir.getAbsolutePath() } );
        System.out.println( "before call: callGroovy.testGroovyScriptEngineVsGroovyShell();" );
        Binding binding = new Binding();
        System.out.println( "start codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + this.jFileFinderWin.getStartingFolder() + "=" );

        DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) jFileFinderWin.getListPanelModel( (String) listOfLists.getSelectedItem() );
        
        binding.setProperty( "codeProcessorPanel", this );
        binding.setProperty( "defaultComboBoxModel", defaultComboBoxModel );
        File tmpFile = new File( currentFile );
        callGroovy.testGroovyScriptEngineVsGroovyShell( tmpFile.getName(), binding );        
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

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        cmdCb = new javax.swing.JComboBox<>();
        listOfLists = new javax.swing.JComboBox<>();
        goButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        PathsList = new javax.swing.JList<>();
        saveToFile = new javax.swing.JButton();
        readFile = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        codePane = new javax.swing.JEditorPane();
        jButton1 = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(500, 500));
        setPreferredSize(new java.awt.Dimension(800, 700));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cmdCb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Run on List" }));
        cmdCb.setMinimumSize(new java.awt.Dimension(150, 25));
        cmdCb.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(cmdCb, gridBagConstraints);

        listOfLists.setModel( listOfFilesPanelModel );
        listOfLists.setMinimumSize(new java.awt.Dimension(150, 25));
        listOfLists.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(listOfLists, gridBagConstraints);

        goButton.setText("Go");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(goButton, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(10, 22));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(10, 10));

        PathsList.setModel(new DefaultComboBoxModel() );
        PathsList.setPreferredSize(new java.awt.Dimension(150, 200));
        jScrollPane1.setViewportView(PathsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        saveToFile.setText("Save to File");
        saveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(saveToFile, gridBagConstraints);

        readFile.setText("Read File");
        readFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(readFile, gridBagConstraints);

        jScrollPane2.setViewportView(codePane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane2, gridBagConstraints);

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        getContentPane().add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
        System.out.println( "before call: new callGroovy();" );
        executeScript();
    }//GEN-LAST:event_goButtonActionPerformed

    private void saveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "File to Save To" );
        chooser.setCurrentDirectory( new File( currentDirectory ) );
        chooser.setSelectedFile( new File( currentFile ) );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        File selectedFile = chooser.getSelectedFile();
        saveToFile( selectedFile );
        }

    }//GEN-LAST:event_saveToFileActionPerformed

    private void readFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "File to Save To" );
        chooser.setCurrentDirectory( new File( currentDirectory ) );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        readFile( chooser.getSelectedFile() );
        }
    }//GEN-LAST:event_readFileActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        saveToFile( new File( currentFile )  );
    }//GEN-LAST:event_jButton1ActionPerformed

        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                           String[] data = {"Fe", "Fi", "Fo", "Fum"};

           final DefaultComboBoxModel listOfFilesPanelModel = new DefaultComboBoxModel(data);
                CodeProcessorPanel dialog = new CodeProcessorPanel( null, null, null );
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });

                dialog.setSize( 800, 600 );
                dialog.setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> PathsList;
    private javax.swing.JComboBox<String> cmdCb;
    private javax.swing.JEditorPane codePane;
    private javax.swing.JButton goButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox<String> listOfLists;
    private javax.swing.JButton readFile;
    private javax.swing.JButton saveToFile;
    // End of variables declaration//GEN-END:variables
}
