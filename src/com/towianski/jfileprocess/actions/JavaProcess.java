/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author stan
 */
public final class JavaProcess {

    private JavaProcess() {}        

    public static int exec(Class klass, String... passArgs) throws IOException,
                                               InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = klass.getCanonicalName();

        String[] runArgs = { javaBin, "-cp", classpath, className };
        
        String[] allArgs = Arrays.copyOf( runArgs, runArgs.length + passArgs.length);
        System.arraycopy( passArgs, 0, allArgs, runArgs.length, passArgs.length );
  
        for ( String tmp : allArgs )
            {
            System.err.println( "(" + tmp + ") " );
            }
        ProcessBuilder builder = new ProcessBuilder( allArgs );

        Process process = builder.start();
//        process.waitFor();
        return process.exitValue();
    }

}