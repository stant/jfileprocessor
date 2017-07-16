/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author stan
 */
public class LogStringHandler extends Handler {

    private StringBuffer outBuf = new StringBuffer();

    public LogStringHandler()
        {
        System.out.println( "new LogStringHandler()" );
        }
    
    public void publish( LogRecord logRecord )
        {
        outBuf.append( logRecord.getLevel() + ": ");
        outBuf.append( logRecord.getSourceClassName() + ": ");
        outBuf.append( logRecord.getSourceMethodName() + ": ");
        outBuf.append( logRecord.getMessage() );
        outBuf.append( "\n");
        //System.out.println( "logRecord.getLevel() =" + logRecord.getLevel() + "=" );
        //System.out.println( "logRecord.getMessage() =" + logRecord.getMessage() + "=" );
        }

    public void flush() {
        }

    public void close() {
        }
  
public String getLogString() 
    {
    return outBuf.toString();
    }
  
public void clearLog()
    {
    System.out.println( "log outBuf before clear =\n" + outBuf.toString() + "\n=" );
    outBuf.setLength( 0 );
    System.out.println( "log outBuf after  clear =\n" + outBuf.toString() + "\n=" );
    }

}
