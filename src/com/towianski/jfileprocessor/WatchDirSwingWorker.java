package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.WatchDir;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import java.nio.file.Path;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - Oct 2017
 */
public class WatchDirSwingWorker extends SwingWorker<ResultsData, Object> {

    MyLogger logger = MyLogger.getLogger( WatchDirSwingWorker.class.getName() );
    JFileFinderWin jFileFinderWin = null;
    WatchDirSw watchDirSw = null;
    Path startingPath = null;
    WatchDir watchDir = null;

    public WatchDirSwingWorker( JFileFinderWin jFileFinderWin, WatchDirSw watchDirSw, Path startingPath )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.watchDirSw = watchDirSw;
        this.startingPath = startingPath;
//        watchDir = new WatchDir( jFileFinderWin );
        }

    public WatchDir getWatchDir() {
        return watchDir;
    }

    @Override
    public ResultsData doInBackground() {
        logger.log( Level.INFO, "WatchDirSwingWorker.doInBackground() before watchDir.run()" );
        logger.log( Level.INFO, "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        //----  loop because if dir we are in gets deleted it causes an error, so we wait to see if it comes back
        watchDir.register( startingPath );

//            if ( ! isCancelled() )
        while ( ! isCancelled() )
            {
            boolean noChangeFlag = false;   //watchDir.run();
            logger.log( Level.INFO, "WatchDirSwingWorker.doInBackground() after watchDir.run()" );
            logger.log( Level.INFO, "WatchDirSwingWorker.doInBackground() noChangeFlag = " + noChangeFlag );
            if ( ! noChangeFlag )
                {
                SwingUtilities.invokeLater(new Runnable() 
                {
                    public void run() {
                        logger.log( Level.INFO, "entered WatchDirSwingWorker.doInBackground().invokeLater()" );
                        logger.log( Level.INFO, "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                        //                watchDirSw.setIsDone(true);
                        jFileFinderWin.callSearchBtnActionPerformed( null );
                    }
                });
                }

            // pause 1 second to keep from too frequently refreshing as files change
            try {
                Thread.sleep(1000);
                } 
            catch (InterruptedException ex2) 
                {
                logger.log( Level.SEVERE, "Background interrupted" );
                }
            logger.log( Level.INFO, "entered WatchDirSwingWorker.doInBackground() set my own DoneFlag" );
            }
        return new ResultsData();
    }

//    @Override
//    protected void process(List<Integer> chunks) {
//        // Get Info
//        for (int number : chunks) {
//            System.out.println("Found even number: " + number);
//        }
//    }

    @Override
    public void done() {
        try {
            logger.log( Level.INFO, "entered WatchDirSwingWorker.done()" );
            logger.log( Level.INFO, "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            if ( ! isCancelled() )
                {
                ResultsData resultsData = get();
                }
            if ( watchDir != null )
                {
                logger.log( Level.INFO, "WatchDirSwingWorker.done() watchDir not null so call cancel()" );
                watchDir.cancelWatch();
                }
            }
        catch (InterruptedException ignore) 
            {
            logger.log( Level.INFO, "entered WatchDirSwingWorker.done()  Interrupted" );
            }
        catch (java.util.concurrent.ExecutionException e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) 
                {
                why = cause.getMessage();
                } 
            else 
                {
                why = e.getMessage();
                }
            logger.log( Level.INFO, "Error in WatchDirSwingWorker(): " + why);
            }
    }    
}
