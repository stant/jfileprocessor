/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author stan
 */
public final class JavaProcess {

    private JavaProcess() {}        

    public static int execJava(Class klass, String... passArgs)
            throws IOException, InterruptedException 
        {
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
            System.out.println( "(" + tmp + ") " );
            }
        ProcessBuilder builder = new ProcessBuilder( allArgs );

        Process process = builder.start();
//        process.waitFor();
        return process.exitValue();
    }

    public static int exec( String startDir, String... passArgs ) 
            throws IOException, InterruptedException 
        {
        String[] myargs =  { "" };
        if ( passArgs[0].indexOf( " " ) >=0 )
            {
            myargs = passArgs[0].split( " " );
            }
        else
            {
            myargs = passArgs;
            }

        ProcessBuilder builder = null;
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            String[] dirArg =  { startDir };
            String[] allArgs = Arrays.copyOf( myargs, myargs.length + 1 );
            System.arraycopy( dirArg, 0, allArgs, myargs.length, 1 );

            for ( String tmp : allArgs )
                {
                System.out.println( "(" + tmp + ") " );
                }
            builder = new ProcessBuilder( allArgs );
            }
        else
            {
            for ( String str : myargs )
                {
                System.out.println( "run cmd: (" + str + ") " );
    //            cmd.add( str );
                }
            builder = new ProcessBuilder( myargs );

            builder.directory( new File( startDir ) );
            }

        Process process = builder.start();
//        IOThreadHandler outputHandler = new IOThreadHandler( process.getInputStream() );
//        outputHandler.start();
//        process.waitFor();
//        System.out.println(outputHandler.getOutput());
        return process.exitValue();
	}

	private static class IOThreadHandler extends Thread {
		private InputStream inputStream;
		private StringBuilder output = new StringBuilder();

		IOThreadHandler(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		public void run() {
			Scanner br = null;
			try {
				br = new Scanner(new InputStreamReader(inputStream));
				String line = null;
				while (br.hasNextLine()) {
					line = br.nextLine();
					output.append( "out-" + line + System.getProperty("line.separator"));
				}
			} finally {
				br.close();
			}
		}

		public StringBuilder getOutput() {
			return output;
		}
	}

}