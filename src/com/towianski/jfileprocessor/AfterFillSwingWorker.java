/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class AfterFillSwingWorker extends SwingWorker<String, Object> {

    static JFileFinderWin jFileFinderWin = null;
    static CodeProcessorPanel codeProcessorPanel = null;
    static String startingPath = null;
    static String patternType = null;
    static String filePattern = null;
    JFileFinder jfilefinder = null;

    public AfterFillSwingWorker( JFileFinderWin jFileFinderWinArg, JFileFinder jfilefinderArg )
        {
        System.out.println( "AfterFillSwingWorker constructor() with 2 args" );
        jFileFinderWin = jFileFinderWinArg;
        jfilefinder = jfilefinderArg;
        }

    
    public AfterFillSwingWorker( CodeProcessorPanel codeProcessorPanelArg )
        {
        System.out.println( "AfterFillSwingWorker constructor() with args" );
        codeProcessorPanel = codeProcessorPanelArg;
        }

    public AfterFillSwingWorker( JFileFinderWin jFileFinderWinArg )
        {
        System.out.println( "AfterFillSwingWorker constructor() with args" );
        jFileFinderWin = jFileFinderWinArg;
        }

    public AfterFillSwingWorker()
        {
        System.out.println( "AfterFillSwingWorker constructor()" );
        }

    @Override
    public String doInBackground() {
        System.out.println( "entered AfterFillSwingWorker.doInBackground()" );
        return "";  //JFileFinder.getResultsData();
    }

    @Override
    public void done() {
        try {
            System.out.println( "entered AfterFillSwingWorker.done()" );
//            ResultsData resultsData = get();
            String ans = get();
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();

            System.out.println( "exiting AfterFillSwingWorker.done()" );
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
            System.out.println("Error AfterFillSwingWorker() retrieving file: " + why);
            e.printStackTrace();
            }
        }    
}
