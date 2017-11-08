/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.models.ResultsData;
import java.nio.file.Path;
import javax.swing.SwingWorker;

/**
 *
 * @author stan
 */
public class AddWatchSwingWorker extends SwingWorker<ResultsData, Object>  {
    WatchDir watchDir = null;
    Path folder = null;

  public AddWatchSwingWorker( WatchDir watchDir, Path folder ) {
    this.watchDir = watchDir;
    this.folder = folder;
  }

    @Override
    public ResultsData doInBackground() 
        {
        System.out.println( "entered AddWatchSwingWorker.addWatch()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        //                watchDirSw.setIsDone(true);
        watchDir.register( folder );
        return null;
        }
    
    @Override
    public void done() 
        {
        try {
            ResultsData resultsData = get();
            System.out.println( "entered AddWatchSwingWorker.addWatch()  DONE" );
            System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            }
        catch (InterruptedException ignore) 
            {}
        catch (java.util.concurrent.ExecutionException e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            System.out.println( "Error in AddWatchSwingWorker(): " + why);
            }
        }
  
  }
