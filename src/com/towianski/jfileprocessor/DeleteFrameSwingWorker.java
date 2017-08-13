/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.ResultsData;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class DeleteFrameSwingWorker extends SwingWorker<ResultsData, Object> {

    JFileFinderWin jFileFinderWin = null;
    DeleteFrame deleteFrame = null;
    ArrayList<Path> deletePaths = new ArrayList<Path>();
    JFileDelete jfiledelete = null;

    public DeleteFrameSwingWorker( JFileFinderWin jFileFinderWin, DeleteFrame deleteFrame, JFileDelete jfiledelete, ArrayList<Path> deletePaths )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.deleteFrame = deleteFrame;
        this.jfiledelete = jfiledelete;
        this.deletePaths = deletePaths;
        }

    @Override
    public ResultsData doInBackground() {
        deleteFrame.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_STARTED );
        jfiledelete.run();
        return jfiledelete.getResultsData();
    }

    @Override
    public void done() {
        try {
            //System.out.println( "entered SwingWork.done()" );
            ResultsData resultsData = get();
            //Integer ii = get();
            //System.out.println( "SwingWork.done() at 2  ii = " + ii );
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            if ( ! resultsData.getMessage().equals( "" ) )
                {
                deleteFrame.setMessage( resultsData.getMessage() );
                }
            else
                {
                deleteFrame.setMessage( "Deleted " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() ) );
                }
            if ( ! resultsData.getProcessStatus().equals( "" ) )
                {
                deleteFrame.setProcessStatus( resultsData.getProcessStatus() );
                }
            else if ( resultsData.getSearchWasCanceled() )
                {
                deleteFrame.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_CANCELED );
                deleteFrame.setMessage( deleteFrame.getMessage() + " partial files list." );
                }
            else
                {
                deleteFrame.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_COMPLETED );
                }
            deleteFrame.setResultsData( resultsData );
            
            jFileFinderWin.callSearchBtnActionPerformed( null );
                    
            //System.out.println( "exiting SwingWork.done()" );
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
            System.out.println( "Error in DeleteFrameSwingWorker(): " + why);
            }
    }    
}
