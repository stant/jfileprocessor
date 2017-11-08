/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import java.nio.file.Path;
import java.util.logging.Level;
import javax.swing.SwingUtilities;

/**
 *
 * @author stan
 */
public class AddWatchRunnable implements Runnable {
    WatchDir watchDir = null;
    Path folder = null;

  public AddWatchRunnable( WatchDir watchDir, Path folder ) {
    this.watchDir = watchDir;
    this.folder = folder;
  }

  @Override
  public void run() 
    {
    System.out.println( "entered AddWatchRunnable.addWatch()" );
    System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
    watchDir.register( folder );
    
//    boolean noChangeFlag = watchDir.run();
    }

  public void runSwing() 
    {
    SwingUtilities.invokeLater(new Runnable() 
        {
        public void run() 
            {
            System.out.println( "entered AddWatchRunnable.addWatch()" );
            System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            //                watchDirSw.setIsDone(true);
            watchDir.register( folder );
            }
        });
    }
}
