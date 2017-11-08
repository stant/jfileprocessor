/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.WatchDir;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class WatchDirSw {
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    JFileFinderWin jFileFinderWin = null;
    WatchDirSwingWorker watchDirSwingWorker = null;
//    AddWatchSwingWorker awsw = null;
    Thread watchThread = null;
    WatchDir watchDir = null;
    
    boolean cancelFlag = false;
    boolean doneFlag = false;
    Path startingPath = null;

    public WatchDirSw( JFileFinderWin jFileFinderWin, Path startingPath )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.startingPath = startingPath;
        }

    public void setIsDone( boolean flag )
        {
        doneFlag = flag;
        }

//    public void addWatch( Path folder )
//        {
//        if ( watchDir != null )
//            {
//            awsw = new AddWatchSwingWorker( watchDir, folder );
//            awsw.execute();
//            }
//        }

    public void clearSearch( boolean flag )
        {
        doneFlag = flag;
        }
            
    public void stopWatch() {
        System.out.println("watchDirSw.stopSearch()");

        watchDir.cancelWatch();
//        watchThread.interrupt();
//        try {
//            watchThread.join();
            
//        if ( awsw != null )
//            {
//            watchDir.cancelRegister();
//            awsw.cancel( true );
//            }
//        
//        if ( watchDir != null )
//            {
////            watchDir.cancelSearch();
//            watchDir.unRegisterExisting();
//            }
        
//        if ( watchDirSwingWorker != null )
//            {
//            watchDirSwingWorker.cancel( true );
//            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(WatchDirSw.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }

    public void actionPerformed(java.awt.event.ActionEvent evt) {                                         
        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( jFileFinderWin.PROCESS_STATUS_SEARCH_CANCELED ) )
            {
            this.stopWatch();
            }
        else
            {
            try {
                System.out.println( "WatchDirSw doCmdBtnActionPerformed start" );
                System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//                watchDirSwingWorker = new WatchDirSwingWorker( jFileFinderWin, this, startingPath );
//                watchDir = watchDirSwingWorker.getWatchDir();
//                watchDirSwingWorker.execute();   //doInBackground();
                watchDir = new WatchDir( jFileFinderWin, Paths.get( jFileFinderWin.getStartingFolder() ), false );
                watchThread = newThread( watchDir );
                watchThread.start();
                System.out.println( "WatchDirSw after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) {
                Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
    }                                        

    public Thread newThread(final Runnable r) {
            Thread thread = new Thread( r );
            thread.setName( "watchDir" + thread.getName());
            thread.setDaemon(true);
            return thread;
        }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new WatchDirSwFrame().setVisible(true);
//            }
//        });
    }
}
