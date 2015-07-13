package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.models.ResultsData;
import com.towianski.models.FilesTblModel;
import com.towianski.chainfilters.FilterChain;
import java.io.File;
import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
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
    static FilterChain chainFilterFolderList = null;
    
    public JFileFinder( String startingPathArg, String patternTypeArg, String filePatternArg, FilterChain chainFilterList, FilterChain chainFilterFolderList )
    {
        startingPath = startingPathArg;   //.replace( "\\\\", "/" ).replace( "\\", "/" );
        patternType = patternTypeArg;
        filePattern = filePatternArg;  //.replace( "\\\\", "/" ).replace( "\\", "/" );
        cancelFlag = false;
        matchedPathsList = new ArrayList<Path>();
        this.chainFilterList = chainFilterList;
        this.chainFilterFolderList = chainFilterFolderList;
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

                    rowList.add( Files.isSymbolicLink( fpath ) );  // needed to make linux work
                    rowList.add( attr.isDirectory() );
                    rowList.add( fpath.toString() );
                    rowList.add( new Date( attr.lastModifiedTime().toMillis() ) );
                    rowList.add( attr.size() );

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
                ArrayList<Object> newRow = new ArrayList<Object>();
//                newRow.add( false );
//                newRow.add( true );
                newRow.add( "No Files Found" );
//                newRow.add( Calendar.getInstance().getTime() );
//                newRow.add( (long) 0 );
                PathsInfoList.add( newRow );
                }
            else
                {
                HeaderList.add( "Link" );
                HeaderList.add( "Dir" );
                HeaderList.add( "File" );
                HeaderList.add( "last Modified Time" );
                HeaderList.add( "Size" );
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
        ResultsData resultsData = new ResultsData( matchedPathsList, cancelFlag, cancelFillFlag, finder.getNumTested(), finder.getNumFileMatches(), finder.getNumFolderMatches() );
        return resultsData;
    }
    
    public static class Finder extends SimpleFileVisitor<Path> 
        {
        private long numFileMatches = 0;
        private long numFolderMatches = 0;
        private long numTested = 0;

        Finder(String pattern) {
        }

        // Compares the glob pattern against
        // the file or directory name.
//        void find(Path file) {
//            Path name = file.getFileName();
//            if (name != null && matcher.matches(name)) {
//                numFileMatches++;
//                System.out.println( "find =" + file );
//            }
//        }

        // Compares the glob pattern against
        // the file or directory name.
        void processFile( Path file, BasicFileAttributes attrs )
            {
            numTested++;
            if ( chainFilterList != null )
                {
                //BasicFileAttributes attrs;
                try {
                    //attrs = Files.readAttributes( file, BasicFileAttributes.class );
                    if ( chainFilterList.testFilters( file, attrs ) )
                        {
//                      rowList.add( fpath.toString() );
//                      rowList.add( new Date( attr.lastModifiedTime().toMillis() ) );
//                      rowList.add( attr.size() );
//                      rowList.add( attr.isDirectory() );
//                      rowList.add( attr.isSymbolicLink() );
                        numFileMatches++;
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
                numFileMatches++;
                matchedPathsList.add( file );
                }
            }
        
        // Compares the glob pattern against
        // the file or directory name.
        void processFolder( Path file, BasicFileAttributes attrs )
            {
            numTested++;
            if ( chainFilterFolderList != null )
                {
                try {
                    if ( chainFilterFolderList.testFilters2( file, attrs ) )
                        {
                        numFolderMatches++;
//                      System.out.println( "Match =" + file );
                        matchedPathsList.add( file );
                        }
                    } 
                catch (Exception ex) 
                    {
                    Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            else
                {
                numFolderMatches++;
                matchedPathsList.add( file );
                }
            }
        
        // Prints the total number of
        // matches to standard out.
        void done() {
            System.out.println( "Tested:  " + numTested );
            System.out.println( "Matched Files count: " + numFileMatches );
            System.out.println( "Matched Folders count: " + numFolderMatches );
            
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
                processFile( file, attrs );
                }
            catch( Exception ex )
                {
                System.err.println( "Error parsing file =" + file + "=" );
                return FileVisitResult.TERMINATE;
                }
            return FileVisitResult.CONTINUE;
            }

        // Invoke the pattern matching method on each directory.
        @Override
        public FileVisitResult preVisitDirectory( Path fpath, BasicFileAttributes attrs )
            {
            //  First check is do we show this folder?
            try {
                if ( cancelFlag )
                    {
                    System.out.println( "Search cancelled by user." );
                    return FileVisitResult.TERMINATE;
                    }
                processFolder( fpath, attrs );
                }
            catch (Exception ex) 
                {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                }

            // Second check is do we go into this folder or skip it?
            if ( chainFilterList != null )
                {
                try {
                    //System.err.println( "previsit folder =" + fpath.toString() );
                    if ( chainFilterFolderList.testFilters( fpath, attrs ) )
                        {
                        return CONTINUE;
                        }
                    else
                        {
                        //System.err.println( "SKIP folder =" + fpath.toString() );
                        return FileVisitResult.SKIP_SUBTREE;
                        }
                    }
                catch (Exception ex) 
                    {
                    Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            return FileVisitResult.SKIP_SUBTREE;
            }

        // Print each directory visited.
//        @Override
//        public FileVisitResult postVisitDirectory( Path dir, IOException exc )
//            {
            //System.out.format("Directory: %s%n", dir);
//            BasicFileAttributes attrs;
//            try {
//                attrs = Files.readAttributes( dir, BasicFileAttributes.class );
//                if ( cancelFlag )
//                    {
//                    System.out.println( "Search cancelled by user." );
//                    return FileVisitResult.TERMINATE;
//                    }
//                processFolder( dir, attrs );
//                }
//            catch (Exception ex) 
//                {
//                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            return CONTINUE;
//            }

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
            
        public long getNumFileMatches()
            {
            return numFileMatches;
            }
            
        public long getNumFolderMatches()
            {
            return numFolderMatches;
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
                EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
                Files.walkFileTree( startingDir, opts, Integer.MAX_VALUE, finder );
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

        JFileFinder jfilefinder = new JFileFinder( args[0], args[1], args[2], null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }
}    
