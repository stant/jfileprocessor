/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.ResultsData;
import java.text.NumberFormat;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class FillTableModelSwingWorker extends SwingWorker<ResultsData, Object> {

    static JFileFinderWin jFileFinderWin = null;
    static String startingPath = null;
    static String patternType = null;
    static String filePattern = null;
    JFileFinder jfilefinder = null;

    public FillTableModelSwingWorker( JFileFinderWin jFileFinderWinArg, JFileFinder jfilefinderArg )
        {
        jFileFinderWin = jFileFinderWinArg;
        jfilefinder = jfilefinderArg;
        }

    @Override
    public ResultsData doInBackground() {
        System.out.println( "FillTableModelSwingWorker.doInBackground() before fillInFilesTable.run()" );
        jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_FILL_STARTED );
        jFileFinderWin.fillInFilesTable( null );
        
        System.out.println( "after FillTableModelSwingWorker.doInBackground()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                jFileFinderWin.replaceDirWatcher();
//                System.out.println( "entered FillTableModelSwingWorker.doInBackground() set my own DoneFlag" );
//            }
//        });
        return JFileFinder.getResultsData();
    }

    @Override
    public void done() {
        try {
            System.out.println( "entered FillTableModelSwingWorker.done()" );
            System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            ResultsData resultsData = get();
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            if ( resultsData.getFillWasCanceled() )
                {
                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_FILL_CANCELED );
                }
            else
                {
                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_FILL_COMPLETED );
                }
            jFileFinderWin.setNumFilesInTable();
            //SwingUtilities.invokeLater( jFileFinderWin.fillInFilesTable( resultsData ) );
            //jFileFinderWin.fillInFilesTable( resultsData );
            //jFileFinderWin.setResultsData( resultsData );

            jFileFinderWin.stopDirWatcher();
            jFileFinderWin.startDirWatcher();
            
            System.out.println( "FillTableModelSwingWorker() jFileFinderWin.afterFillSwingWorker =" + jFileFinderWin.afterFillSwingWorker+ "=" );
            if ( jFileFinderWin.afterFillSwingWorker != null )
                {
                jFileFinderWin.takeAfterFillSwingWorker().execute();    
                }

            System.out.println( "exiting FillTableModelSwingWorker.done()" );
            } 
        catch (InterruptedException ignore) {}
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
            System.out.println("Error FillTableModelSwingWorker() retrieving file: " + why);
            e.printStackTrace();
            }
        }    
}
