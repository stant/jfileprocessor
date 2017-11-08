/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.ResultsData;
import java.text.NumberFormat;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class JFileFinderSwingWorker extends SwingWorker<ResultsData, String> {

    static JFileFinderWin jFileFinderWin = null;
    static String startingPath = null;
    static String patternType = null;
    static String filePattern = null;
    static Boolean countOnlyFlag = false;
    JFileFinder jfilefinder = null;

    public JFileFinderSwingWorker( JFileFinderWin jFileFinderWinArg, JFileFinder jfilefinderArg, String startingPathArg, String patternTypeArg, String filePatternArg, Boolean countOnlyFlag )
        {
        jFileFinderWin = jFileFinderWinArg;
        startingPath = startingPathArg;
        patternType = patternTypeArg;
        filePattern = filePatternArg;
        jfilefinder = jfilefinderArg;
        this.countOnlyFlag = countOnlyFlag;
        }

    /** Note: 
     * Completely refrain from using 'protected void done()' and explicitly using SwingUtilities.invokeLater(Runnable) 
     * just before returning from doInBackground(), to call a method on the EDT to notify the completion of the SwingWorker.
     * 
     * There is a mistake in the test.
     * The done() method (as specified) is executed on the EDTafter the doInBackground method is finished. It should not wait 
     * other tasks started from the doInBackground method. 
     * You must replace SwingUtilities.invokeLater with SwingUtilities.invokeAndWait in the doInBackground method to ensure 
     * that the custom runnable is finished.
     * 
     * @return 
     */
    @Override
    public ResultsData doInBackground() {
        System.out.println( "JFileFinderSwingWorker.doInBackground() before jfilefinder.run()" );
        jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_STARTED );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        jfilefinder.run( this );
        //publish("Listing all text files under the directory: ");
        return JFileFinder.getResultsData();
    }

//            @Override
//            // This will be called if you call publish() from doInBackground()
//            // Can safely update the GUI here.
//            protected void process(List<String> chunks) {
//                String value = chunks.get(chunks.size() - 1);
//                
//                jFileFinderWin.setMessage( value );
//            }
            
    @Override
    public void done() {
        try {
            System.out.println( "entered JFileFinderSwingWorker.done()" );
            System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            ResultsData resultsData = get();
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            String partialMsg = "";
            if ( resultsData.getSearchWasCanceled() )
                {
                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_CANCELED );
                partialMsg = " PARTIAL files list.";
                }
            else
                {
                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_COMPLETED );
                }
            jFileFinderWin.setMessage( "Matched " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) 
                    + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.  Total "
                    + numFormat.format( resultsData.getFilesVisited() ) + partialMsg );
            //SwingUtilities.invokeLater( jFileFinderWin.fillInFilesTable( resultsData ) );
            //jFileFinderWin.fillInFilesTable( resultsData );
            jFileFinderWin.setResultsData( resultsData );

            jFileFinderWin.emptyFilesTable();
            
            if ( ! countOnlyFlag )
                {
            System.out.println( "call JFileFinderSwingWorker.fillTableModelSwingWorker.execute()" );
            System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                FillTableModelSwingWorker fillTableModelSwingWorker = new FillTableModelSwingWorker( jFileFinderWin, jfilefinder );
                fillTableModelSwingWorker.execute();   //doInBackground();
                }
            
//            jFileFinderWin.releaseSearchLock();
            System.out.println( "exiting JFileFinderSwingWorker.done()" );
            } 
        catch (InterruptedException ignore) {}
        catch (java.util.concurrent.ExecutionException e) {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            System.out.println("Error retrieving file: " + why);
            e.printStackTrace();
        }
    }    
}
