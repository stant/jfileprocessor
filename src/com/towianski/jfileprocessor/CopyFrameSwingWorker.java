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
public class CopyFrameSwingWorker extends SwingWorker<ResultsData, Object> {

    JFileFinderWin jFileFinderWin = null;
    CopyFrame copyFrame = null;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    String toPath = null;
    JFileCopy jfilecopy = null;

    public CopyFrameSwingWorker( JFileFinderWin jFileFinderWin, CopyFrame copyFrame, JFileCopy jfilecopy, ArrayList<Path> copyPaths, String toPath )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.copyFrame = copyFrame;
        this.jfilecopy = jfilecopy;
        this.copyPaths = copyPaths;
        this.toPath = toPath;
        }

    @Override
    public ResultsData doInBackground() {
        copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_STARTED );
        jfilecopy.run();
        return jfilecopy.getResultsData();
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
            copyFrame.setMessage( "Copied " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() ) );
            if ( resultsData.getSearchWasCanceled() )
                {
                copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_CANCELED );
//                else
                    {
                    copyFrame.setMessage( copyFrame.getMessage() + " partial files list." );
                    }
                }
            else
                {
                copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_COMPLETED );
                }

            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
                {
                copyFrame.setProcessStatus( resultsData.getProcessStatus() );
                }
            if ( ! resultsData.getMessage().trim().equals( "" ) )
                {
                copyFrame.setMessage( resultsData.getMessage() );
                }

            copyFrame.setResultsData( resultsData );
            
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
            System.out.println( "Error in CopyFrameSwingWorker(): " + why);
            e.printStackTrace();
            }
    }    
}
