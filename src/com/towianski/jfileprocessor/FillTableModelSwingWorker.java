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
        jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_FILL_STARTED );
        jFileFinderWin.fillInFilesTable();
        return JFileFinder.getResultsData();
    }

    @Override
    public void done() {
        try {
            System.out.println( "entered FillTableModelSwingWorker.done()" );
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
            System.out.println( "exiting FillTableModelSwingWorker.done()" );
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
