package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.chainfilters.ChainFilterArgs;
import com.towianski.models.ResultsData;
import com.towianski.models.FilesTblModel;
import com.towianski.chainfilters.FilterChain;
import com.towianski.utils.MyLogger;
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
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.logging.Level;
import com.towianski.jfileprocessor.JFileFinderWin;



public class JFileFinder //  implements Runnable 
{
    static MyLogger logger = MyLogger.getLogger( JFileFinder.class.getName() );

    static JFileFinderWin jFileFinderWin = null;
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
    static FilterChain chainFilterPreVisitFolderList = null;
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd hh:mm:ss");
    Date begDate = null;
    Date endDate = null;
    
    public JFileFinder( JFileFinderWin jFileFinderWinArg, String startingPathArg, String patternTypeArg, String filePatternArg, FilterChain chainFilterList, FilterChain chainFilterFolderList, FilterChain chainFilterPreVisitFolderList )
    {
        this.jFileFinderWin = jFileFinderWinArg;
        startingPath = startingPathArg;   //.replace( "\\\\", "/" ).replace( "\\", "/" );
        patternType = patternTypeArg;
        filePattern = filePatternArg;  //.replace( "\\\\", "/" ).replace( "\\", "/" );
        cancelFlag = false;
        matchedPathsList = new ArrayList<Path>();
        this.chainFilterList = chainFilterList;
        this.chainFilterFolderList = chainFilterFolderList;
        this.chainFilterPreVisitFolderList = chainFilterPreVisitFolderList;
        logger.setLevel( Level.SEVERE );
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

            if ( jFileFinderWin.getFilesysType() == jFileFinderWin.FILESYSTEM_POSIX )
                {
                getPosixFileInfo( PathsInfoList );
                }
            else if ( jFileFinderWin.getFilesysType() == jFileFinderWin.FILESYSTEM_DOS )
                {
                getDosFileInfo( PathsInfoList );
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
//                if ( jFileFinderWin.isShowOwnerFlag() )
                    {
                    HeaderList.add( "Owner" );
                    }
//                if ( jFileFinderWin.isShowGroupFlag() )
                    {
                    HeaderList.add( "Group" );
                    }
//                if ( jFileFinderWin.isShowPermsFlag() )
                    {
                    HeaderList.add( "Perms" );
                    }
                }

            //FilesTblModel filesTblModel = new FilesTblModel( HeaderList, PathsInfoList );
            return new FilesTblModel( HeaderList, PathsInfoList );
            }
    }
    
    public static void getPosixFileInfo( ArrayList<ArrayList> PathsInfoList )
        {
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

                    PosixFileAttributes fsattr = Files.readAttributes( fpath, PosixFileAttributes.class );
//                    if ( jFileFinderWin.isShowOwnerFlag() )
                        {
                        rowList.add( fsattr.owner() );
                        }
//                    if ( jFileFinderWin.isShowGroupFlag() )
                        {
                        rowList.add( fsattr.group() );
                        }
//                    if ( jFileFinderWin.isShowPermsFlag() )
                        {
                        rowList.add( PosixFilePermissions.toString( fsattr.permissions() ) );
                        }

                    PathsInfoList.add( rowList );
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.toString());
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
        }

    public static void getDosFileInfo( ArrayList<ArrayList> PathsInfoList )
        {
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

                    DosFileAttributes fsattr = Files.readAttributes( fpath, DosFileAttributes.class );
//                    if ( jFileFinderWin.isShowOwnerFlag() )
                        {
                        rowList.add( Files.getOwner( fpath ) );
                        }
//                    if ( jFileFinderWin.isShowGroupFlag() )
                        {
                        rowList.add( "" );
                        }
//                    if ( jFileFinderWin.isShowPermsFlag() )
                        {
                        rowList.add( (fsattr.isReadOnly() ? "R" : "-") + (fsattr.isArchive() ? "A" : "-") + (fsattr.isSystem() ? "S" : "-") );
                        }
                    
                    PathsInfoList.add( rowList );
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.toString());
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
        }

    public static FilesTblModel emptyFilesTableModel( Boolean countOnlyFlag ) 
        {
        synchronized( dataSyncLock ) 
            {
            System.out.println( "entered JFileFinder.emptyFilesTableModel()" );
            ArrayList<String> HeaderList = new ArrayList<String>();
            ArrayList<ArrayList> PathsInfoList = new ArrayList<ArrayList>();

            HeaderList.add( " " );
            
            ArrayList<Object> rowList = new ArrayList<Object>();
            if ( countOnlyFlag )
                {
                rowList.add( "count only." );
                }
            else
                {
                rowList.add( "filling table . . ." );
                }
            PathsInfoList.add( rowList );

            return new FilesTblModel( HeaderList, PathsInfoList );
            }
    }
    
    public static ResultsData getResultsData() {
        //ResultsData resultsData = new ResultsData( getFilesTableModel(), cancelFlag, finder.getNumTested(), finder.getNumMatches() );
        ResultsData resultsData = new ResultsData( matchedPathsList, cancelFlag, cancelFillFlag, finder.getNumTested()
                    , finder.getNumFileMatches(), finder.getNumFolderMatches() , finder.getNumFileTests(), finder.getNumFolderTests() );
        return resultsData;
    }
    
    public static class Finder extends SimpleFileVisitor<Path> 
        {
        private long numFileMatches = 0;
        private long numFolderMatches = 0;
        private long numFileTests = 0;
        private long numFolderTests = 0;
        private long numTested = 0;
        private ChainFilterArgs chainFilterArgs = new ChainFilterArgs();
        private JFileFinder jFileFinder = null;
        
        Finder(String pattern, JFileFinder jFileFinder) {
            chainFilterArgs.setNumFileMatches(numFileMatches);
            chainFilterArgs.setNumFolderMatches(numFolderMatches);
            chainFilterArgs.setNumFileTests(numFileTests);
            chainFilterArgs.setNumFolderTests(numFolderTests);
            chainFilterArgs.setNumTested(numTested);
            this.jFileFinder = jFileFinder;
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
        Boolean processFile( Path file, BasicFileAttributes attrs )
            {
            numFileTests++;
            numTested++;
            chainFilterArgs.setNumFileMatches(numFileMatches);
            if ( chainFilterList != null )
                {
                //BasicFileAttributes attrs;
                try {
                    //attrs = Files.readAttributes( file, BasicFileAttributes.class );
                    if ( chainFilterList.testFilters( file, attrs, chainFilterArgs, jFileFinder ) )
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
                    logger.log(Level.SEVERE, null, ex);
                    return false;
                    }
                }
            else
                {
                numFileMatches++;
                matchedPathsList.add( file );
                }
            return true;
            }
        
        // Compares the glob pattern against
        // the file or directory name.
        Boolean processFolder( Path fpath, BasicFileAttributes attrs )
            {
            numFolderTests++;
            numTested++;
            chainFilterArgs.setNumFolderMatches(numFolderMatches);
            if ( chainFilterFolderList != null )
                {
                try {
//                    System.out.println( "folder match ? =" + fpath.toString() );
//                    System.out.println( "folder match result = " + chainFilterFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) );
                    if ( chainFilterFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) )
                        {
                        numFolderMatches++;
//                        System.out.println( "Match =" + fpath + "   numFolderMatches =" + numFolderMatches );
                        matchedPathsList.add( fpath );
                        }
                    } 
                catch (Exception ex) 
                    {
                    logger.log(Level.SEVERE, null, ex);
                    return false;
                    }
                }
            else
                {
                numFolderMatches++;
//                System.out.println( "no filter Match =" + fpath + "   numFolderMatches =" + numFolderMatches );
                matchedPathsList.add( fpath );
                }
            return true;
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
        public FileVisitResult visitFile( Path fpath, BasicFileAttributes attrs )
            {
            try
                {
                if ( cancelFlag )
                    {
                    System.out.println( "Search cancelled by user." );
                    return FileVisitResult.TERMINATE;
                    }
                //System.out.println( "test file ? =" + fpath.toString() );
                processFile( fpath, attrs );
                }
            catch( Exception ex )
                {
                System.out.println( "Error parsing file =" + fpath + "=" );
                return FileVisitResult.TERMINATE;
                }
            return FileVisitResult.CONTINUE;
            }

        // Invoke the pattern matching method on each directory.
        @Override
        public FileVisitResult preVisitDirectory( Path fpath, BasicFileAttributes attrs )
            {
//            System.out.println( "preVisitDirectory() chainFilterFolderlist.size() =" + chainFilterFolderList.size() + "=" );
//            System.out.println( "preVisitDirectory() chainFilterPreVisitFolderList.size() =" + chainFilterPreVisitFolderList.size() + "=" );

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
                logger.log(Level.SEVERE, null, ex);
                }

            // Second check is do we go into this folder or skip it?
            if ( chainFilterPreVisitFolderList != null )
                {
                try {
                    //System.out.println( "previsit folder ? =" + fpath.toString() );
                    if ( chainFilterPreVisitFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) )
                        {
                        return CONTINUE;
                        }
                    else
                        {
                        //System.out.println( "SKIP folder =" + fpath.toString() );
                        return FileVisitResult.SKIP_SUBTREE;
                        }
                    }
                catch (Exception ex) 
                    {
                    logger.log(Level.SEVERE, null, ex);
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
            //System.out.println( exc + "  for file =" + file.toString() );
            if ( new File( file.toString() ).isDirectory() )
                {
                System.out.println( "skipping inaccessible folder: " + file.toString() );
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

        public long getNumFileTests() {
            return numFileTests;
        }

        public long getNumFolderTests() {
            return numFolderTests;
        }
        
    }

    static void usage() {
        System.out.println("java Find <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public void run() 
    {
        startingPathLength = startingPath.endsWith( System.getProperty( "file.separator" ) ) ? startingPath.length() : startingPath.length() + 1;
        Path startingDir = Paths.get( startingPath );

        //basePathCount = startingDir.getNameCount();
        basePathLen = startingDir.toString().length();
        
        System.out.println( "startingPath =" + startingPath + "=" );
        System.out.println( "startingDir =" + startingDir + "=" );
        System.out.println( "patternType =" + patternType + "=" );
        System.out.println( "filePattern =" + filePattern + "=" );
        System.out.println( "matching filePattern =" + (startingPath + filePattern).replace( "\\", "\\\\" ) + "=" );
        System.out.println( "basePathLen =" + basePathLen + "=" );
        
        finder = new Finder( (startingPath + filePattern).replace( "\\", "\\\\" ), this );
        try {
            synchronized( dataSyncLock ) 
                {            
                cancelFlag = false;
                cancelFillFlag = false;
                EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);

                begDate = Calendar.getInstance().getTime();
                Files.walkFileTree( startingDir, opts, Integer.MAX_VALUE, finder );
                endDate = Calendar.getInstance().getTime();

                System.out.println( "BEG: " + sdf.format( begDate ) );
                System.out.println( "END: " + sdf.format( endDate ) );
                }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
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
        System.out.println("java Find args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

        JFileFinder jfilefinder = new JFileFinder( new JFileFinderWin(), args[0], args[1], args[2], null, null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }
}    
