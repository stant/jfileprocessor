package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.utils.FilterChain;
import java.io.File;
import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;



public class JFileFinder //  implements Runnable 
{
    static String startingPath = null;
    static String patternType = null;
    static String filePattern = null;
    static int startingPathLength = 0;
    static int basePathLen = 0;
    static boolean cancelFlag = false;
    static boolean cancelFillFlag = false;
    static ArrayList<Path> matchedPathsList = new ArrayList<Path>();
    static Finder finder = null;
    static Boolean dataSyncLock = false;
    static FilterChain chainFilterList = null;
    
    public JFileFinder( String startingPathArg, String patternTypeArg, String filePatternArg, FilterChain chainFilterList )
    {
        startingPath = startingPathArg;   //.replace( "\\\\", "/" ).replace( "\\", "/" );
        patternType = patternTypeArg;
        filePattern = filePatternArg;  //.replace( "\\\\", "/" ).replace( "\\", "/" );
        cancelFlag = false;
        matchedPathsList = new ArrayList<Path>();
        this.chainFilterList = chainFilterList;
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        }

    public void cancelFill()
        {
        cancelFillFlag = true;
        }

    public static FilesTblModel getFilesTableModel() 
        {
        synchronized( dataSyncLock ) 
            {
            cancelFillFlag = false;
            ArrayList<String> HeaderList = new ArrayList<String>();
            ArrayList<ArrayList> PathsInfoList = new ArrayList<ArrayList>();

            for ( Path fpath : matchedPathsList )
                {
                if ( cancelFillFlag )
                    {
                    break;
                    }
                ArrayList<Object> rowList = new ArrayList<Object>();
                BasicFileAttributes attr;
                try {
                    attr = Files.readAttributes( fpath, BasicFileAttributes.class );

                    rowList.add( fpath.toString() );
                    rowList.add( new Date( attr.lastModifiedTime().toMillis() ) );
                    rowList.add( attr.size() );
                    rowList.add( attr.isDirectory() );
                    rowList.add( attr.isSymbolicLink() );

    //                   // skip reading attr
    //                rowList.add( fpath.toString() );
    //                rowList.add( new Date( ) );
    //                rowList.add( 12345 );
    //                rowList.add( false );
    //                rowList.add( true );

                    PathsInfoList.add( rowList );
                } catch (Exception ex) {
                    Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                }
    //        System.out.println("creationTime     = " + attr.creationTime());
    //        System.out.println("lastAccessTime   = " + attr.lastAccessTime());
    //        System.out.println("lastModifiedTime = " + attr.lastModifiedTime());
    // 
    //        System.out.println("isDirectory      = " + attr.isDirectory());
    //        System.out.println("isOther          = " + attr.isOther());
    //        System.out.println("isRegularFile    = " + attr.isRegularFile());
    //        System.out.println("isSymbolicLink   = " + attr.isSymbolicLink());
    //        System.out.println("size             = " + attr.size());
                }

            if ( PathsInfoList.size() < 1 )
                {
                HeaderList.add( " " );
                ArrayList<Object> rowList = new ArrayList<Object>();
                rowList.add( "No Files Found" );
                PathsInfoList.add( rowList );
                }
            else
                {
                HeaderList.add( "File" );
                HeaderList.add( "lastModifiedTime" );
                HeaderList.add( "Size" );
                HeaderList.add( "isDirectory" );
                HeaderList.add( "isSymbolicLink" );
                }

            //FilesTblModel filesTblModel = new FilesTblModel( HeaderList, PathsInfoList );
            return new FilesTblModel( HeaderList, PathsInfoList );
            }
    }
    
    public static FilesTblModel emptyFilesTableModel() 
        {
        synchronized( dataSyncLock ) 
            {
            System.out.println( "entered JFileFinder.emptyFilesTableModel()" );
            ArrayList<String> HeaderList = new ArrayList<String>();
            ArrayList<ArrayList> PathsInfoList = new ArrayList<ArrayList>();

            HeaderList.add( " " );
            
            ArrayList<Object> rowList = new ArrayList<Object>();
            rowList.add( "filling table . . ." );
            PathsInfoList.add( rowList );

            return new FilesTblModel( HeaderList, PathsInfoList );
            }
    }
    
    public static ResultsData getResultsData() {
        //ResultsData resultsData = new ResultsData( getFilesTableModel(), cancelFlag, finder.getNumTested(), finder.getNumMatches() );
        ResultsData resultsData = new ResultsData( matchedPathsList, cancelFlag, cancelFillFlag, finder.getNumTested(), finder.getNumMatches() );
        return resultsData;
    }
    
    public static class Finder
        extends SimpleFileVisitor<Path> {

        private long numMatches = 0;
        private long numTested = 0;

        Finder(String pattern) {
        }

        // Compares the glob pattern against
        // the file or directory name.
//        void find(Path file) {
//            Path name = file.getFileName();
//            if (name != null && matcher.matches(name)) {
//                numMatches++;
//                System.out.println( "find =" + file );
//            }
//        }

        // Compares the glob pattern against
        // the file or directory name.
        void processFile(Path file)
            {
            numTested++;
            if ( chainFilterList != null )
                {
                BasicFileAttributes attr;
                try {
                    attr = Files.readAttributes( file, BasicFileAttributes.class );
                    if ( chainFilterList.testFilters( file, attr ) )
                        {
//                      rowList.add( fpath.toString() );
//                      rowList.add( new Date( attr.lastModifiedTime().toMillis() ) );
//                      rowList.add( attr.size() );
//                      rowList.add( attr.isDirectory() );
//                      rowList.add( attr.isSymbolicLink() );
                        numMatches++;
//                      System.out.println( "Match =" + file );
                        matchedPathsList.add( file );
//                      int at = pathStr.indexOf( System.getProperty( "file.separator" ), startingPathLength );
//                      System.out.println( "search from string =" + pathStr.substring(startingPathLength) + "=" );
//                      System.out.println( "startingPathLength =" + startingPathLength );
//                      System.out.println( "path separator >" + System.getProperty( "file.separator" ) + "<" );
//                      System.out.println( "at =" + at );
//                      String pkgpath = pathStr.substring( startingPathLength, at );
                        }
                    } 
                catch (Exception ex) 
                    {
                    Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            else
                {
                numMatches++;
                matchedPathsList.add( file );
                }
            }
        
        // Prints the total number of
        // matches to standard out.
        void done() {
            System.out.println( "Tested:  " + numTested );
            System.out.println( "Matched count: " + numMatches );
            
//            for ( Path mpath : matchedPathsList )
//                {
//                System.out.println( mpath );
//                }
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile( Path file, BasicFileAttributes attrs )
            {
            try
                {
                if ( cancelFlag )
                    {
                    System.out.println( "Search cancelled by user." );
                    return FileVisitResult.TERMINATE;
                    }
                processFile( file );
                }
            catch( Exception ex )
                {
                System.err.println( "Error parsing file =" + file + "=" );
                return FileVisitResult.TERMINATE;
                }
            return FileVisitResult.CONTINUE;
            }

        // Invoke the pattern matching
        // method on each directory.
//        @Override
//        public FileVisitResult
//            preVisitDirectory(Path dir,
//                BasicFileAttributes attrs) {
//            find(dir);
//            return CONTINUE;
//        }

        @Override
        public FileVisitResult visitFileFailed( Path file, IOException exc ) 
            {
            //System.err.println( exc + "  for file =" + file.toString() );
            if ( new File( file.toString() ).isDirectory() )
                {
                System.err.println( "skipping inaccessible folder: " + file.toString() );
                return FileVisitResult.SKIP_SUBTREE;
                }
            return CONTINUE;
            }
            
        public long getNumTested()
            {
            return numTested;
            }
            
        public long getNumMatches()
            {
            return numMatches;
            }
    }

    static void usage() {
        System.err.println("java Find <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public void run() 
    {
        startingPathLength = startingPath.endsWith( System.getProperty( "file.separator" ) ) ? startingPath.length() : startingPath.length() + 1;
        Path startingDir = Paths.get( startingPath );

        //basePathCount = startingDir.getNameCount();
        basePathLen = startingDir.toString().length();
        
        System.err.println( "startingPath =" + startingPath + "=" );
        System.err.println( "startingDir =" + startingDir + "=" );
        System.err.println( "patternType =" + patternType + "=" );
        System.err.println( "filePattern =" + filePattern + "=" );
        System.err.println( "matching filePattern =" + (startingPath + filePattern).replace( "\\", "\\\\" ) + "=" );
        System.err.println( "basePathLen =" + basePathLen + "=" );
        
        finder = new Finder( (startingPath + filePattern).replace( "\\", "\\\\" ) );
        try {
            synchronized( dataSyncLock ) 
                {            
                cancelFlag = false;
                cancelFillFlag = false;
                Files.walkFileTree( startingDir, finder );
                }
        } catch (IOException ex) {
            Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        finder.done();
    }
        
    public static void main(String[] args) throws IOException 
        {
//        if (args.length < 3
//            || !args[1].equals("-name"))
//            usage();

//        Path startingDir = Paths.get(args[0]);
//        String pattern = args[2];

//        startingPath = "F:/data";
//        filePattern = "*.xml";
//        startingPath = args[0];
//        filePattern = args[1];
        System.err.println("java Find args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

        JFileFinder jfilefinder = new JFileFinder( args[0], args[1], args[2], null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }
}    
