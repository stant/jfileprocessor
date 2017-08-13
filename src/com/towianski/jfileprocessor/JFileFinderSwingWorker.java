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
public class JFileFinderSwingWorker extends SwingWorker<ResultsData, Object> {

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

    @Override
    public ResultsData doInBackground() {
        jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_STARTED );
        jfilefinder.run();
        return JFileFinder.getResultsData();
    }

    @Override
    public void done() {
        try {
            System.out.println( "entered SwingWork.done()" );
            ResultsData resultsData = get();
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            jFileFinderWin.setMessage( "Matched " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) 
                    + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.  Total "
                    + numFormat.format( resultsData.getFilesVisited() ) );
            if ( resultsData.getSearchWasCanceled() )
                {
                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_CANCELED );
                jFileFinderWin.setMessage( jFileFinderWin.getMessage() + " partial files list." );
                }
            else
                {
                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_COMPLETED );
                }
            //SwingUtilities.invokeLater( jFileFinderWin.fillInFilesTable( resultsData ) );
            //jFileFinderWin.fillInFilesTable( resultsData );
            jFileFinderWin.setResultsData( resultsData );

            jFileFinderWin.emptyFilesTable();
            
            if ( ! countOnlyFlag )
                {
                FillTableModelSwingWorker fillTableModelSwingWorker = new FillTableModelSwingWorker( jFileFinderWin, jfilefinder );
                fillTableModelSwingWorker.execute();   //doInBackground();
                }
            
            System.out.println( "exiting SwingWork.done()" );
        } catch (InterruptedException ignore) {}
        catch (java.util.concurrent.ExecutionException e) {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            System.out.println("Error retrieving file: " + why);
        }
    }    
}
