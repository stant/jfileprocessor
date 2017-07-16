/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 *
 * @author Stan Towianski
 */
public class Copier extends SimpleFileVisitor<Path> 
{
    //private static final Logger logger = Logger.getLogger( MyLogger.class.getName() );
    MyLogger logger = MyLogger.getLogger( CopyFrame.class.getName() );  // because this is just used by the copyFrame

    private JFileFinderWin jFileFinderWin = null;
    private Boolean isDoingCutFlag = false;
    private Path startingPath;
    private Path toPath;
    private Path fromPath;
    private CopyOption[] copyOptions = null;
    private long numFileMatches = 0;
    private long numFolderMatches = 0;
    private long numTested = 0;

    boolean cancelFlag = false;
    boolean cancelFillFlag = false;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    Boolean dataSyncLock = false;
    String processStatus = "";
    String message = "";
    String toPathFileSeparator = "";
    HashMap<Path,Path> renameDirHm = new HashMap<Path,Path>();

    public Copier( JFileFinderWin jFileFinderWin, Boolean isDoingCutFlag, CopyOption[] copyOptions )
    {
        this.jFileFinderWin = jFileFinderWin;
        this.isDoingCutFlag = isDoingCutFlag;
        this.copyOptions = copyOptions;
        System.err.println("Copier this.startingPath (startingPath) =" + this.startingPath + "   this.toPath =" + this.toPath + "=" );
        System.err.println( "isDoingCutFlag =" + isDoingCutFlag );
        cancelFlag = false;
        
        logger.setLevel( Level.OFF );
        System.err.println( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
        System.err.println( "Copier logger.getLogString() =" + logger.getLogString() + "=" );
        logger.clearLog();
        
        logger.setLevel( Level.SEVERE );
        System.err.println( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
        System.err.println( "Copier logger.getLogString() =" + logger.getLogString() + "=" );
        logger.clearLog();

        logger.setLevel( Level.INFO );
        System.err.println( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
        System.err.println( "Copier logger.getLogString() =" + logger.getLogString() + "=" );
        logger.clearLog();

        logger.setLevel( Level.FINE );
        System.err.println( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
        System.err.println( "Copier logger.getLogString() =" + logger.getLogString() + "=" );
        logger.clearLog();

        System.err.println( "Copier jFileFinderWin.getLogLevel() =" + jFileFinderWin.getLogLevel() + "=" );
        logger.setLevel( jFileFinderWin.getLogLevel() );
        System.err.println( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
        System.err.println( "Copier logger.getLogString() =" + logger.getLogString() + "=" );
        
        logger.clearLog();
    }

    public void setPaths( Path fromPath, String startingPath, String toPath ) {
        this.fromPath = fromPath;
        System.err.println( "called set fromPath =" + this.fromPath + "=" );
        
        this.startingPath = Paths.get( startingPath );
        this.toPath = Paths.get( toPath );
        System.err.println( "Copier new File( toPath ).toURI() =" + new File( toPath ).toURI() + "=" );
        toPathFileSeparator = this.toPath.getFileSystem().getSeparator();

        System.err.println( "this.startingPath =" + this.startingPath + "   this.fromPath =" + this.fromPath + "=" );
        System.err.println( "this.toPath =" + this.toPath + "=" );
        Path fromPathOrig = this.fromPath;
        Path fromParent = fromPath.getParent();
        String ans = "";

        Path targetPath = this.toPath.resolve( this.startingPath.relativize( fromPath ) );
        System.err.println( "relativize =" + this.startingPath.relativize( fromPath ) + "=" );
        System.err.println( "toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );

        while ( fromPath.toFile().isDirectory() &&
                this.startingPath.equals( this.toPath ) )
            {
            ans = JOptionPane.showInputDialog( "Folder exists. New name: ", fromPath.getFileName() );
            if ( ans == null )
                {
                cancelFlag = true;
                break;
                }
            this.toPath = Paths.get( fromParent + toPathFileSeparator + ans );
            this.startingPath = fromPath;
            System.err.println( "new this.startingPath =" + this.startingPath + "   this.fromPath =" + this.fromPath + "=" );
            System.err.println( "new this.toPath =" + this.toPath + "=" );
            }
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        }

    @Override
    public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
            throws IOException 
        {
        Path targetPath = this.toPath.resolve( this.startingPath.relativize( dir ) );
//        System.err.println( );
//        System.err.println( "preVisitDir relativize =" + this.startingPath.relativize( dir ) + "=" );
//        System.err.println( "preVisitDir toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );

        numTested++;
        numFolderMatches++;
        if ( cancelFlag )
            {
            System.err.println( "Search cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        if ( ! Files.exists( targetPath ) )
            {
//            System.err.println( "preVisitDir would do Files.createDirectory( " + targetPath + ")" );
            Files.createDirectory( targetPath );
            }
        return FileVisitResult.CONTINUE;
        }

    @Override
    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) 
            throws IOException 
        {
        Path targetPath = toPath.resolve( startingPath.relativize( file ) );
//        System.err.println( );
//        System.err.println("preVisit startingPath =" + startingPath + "   file =" + file + "=" );
//        System.err.println("preVisit relativize =" + startingPath.relativize( file ) + "=" );
//        System.err.println( "preVisit toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );
//        System.err.println( "VisitFile copy file =" + file + "=    toPath.resolve( startingPath.relativize( file ) ) =" + toPath.resolve( startingPath.relativize( file ) ) + "=" );
        logger.log(Level.FINEST, "VisitFile copy file =" + file + "=    toPath.resolve( startingPath.relativize( file ) ) =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
        if ( cancelFlag )
            {
            System.err.println( "Search cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        
        if ( file.compareTo(toPath.resolve(startingPath.relativize( file ) ) ) == 0 )
            {
            System.err.println( "Skip Copy to Itself." );
            return FileVisitResult.CONTINUE;
            }

//            CopyOption[] copyOpts = new CopyOption[3];
//            //copyOpts[0] = StandardCopyOption.REPLACE_EXISTING;
//            copyOpts[1] = StandardCopyOption.COPY_ATTRIBUTES;
//            //copyOpts[2] = LinkOption.NOFOLLOW_LINKS;
        try {
            if ( copyOptions == null || copyOptions.length < 1 )
                {
//                System.err.println("copy with default options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                Files.copy( file, toPath.resolve( startingPath.relativize( file ) ) );
                }
            else
                {
//                System.err.println("copy with sent options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                Files.copy( file, toPath.resolve( startingPath.relativize( file ) ), copyOptions );
                }
            }
        catch ( java.nio.file.NoSuchFileException noSuchFileExc ) 
            {
            logger.log( Level.INFO, "CAUGHT ERROR  " + noSuchFileExc.getClass().getSimpleName() + ": " + file );
            System.err.println( "CAUGHT ERROR  " + noSuchFileExc.getClass().getSimpleName() + ": " + file );
            return FileVisitResult.CONTINUE;
            }
        catch ( java.nio.file.FileAlreadyExistsException faeExc )
            {
            logger.log( Level.INFO, "CAUGHT ERROR  " + faeExc.getClass().getSimpleName() + ": " + file );
            System.err.println( "CAUGHT ERROR  " + faeExc.getClass().getSimpleName() + ": " + file );
            message = "ERROR: " + faeExc.getClass().getSimpleName() + ": " + file;
            return FileVisitResult.TERMINATE;
            }
        catch ( Exception exc )
            {
            logger.log(Level.SEVERE, "CAUGHT ERROR  " + exc.getClass().getSimpleName() + ": " + file );
            System.err.println( "CAUGHT ERROR  " + exc.getClass().getSimpleName() + ": " + file );
            processStatus = "Error";
            message = exc.getClass().getSimpleName() + ": " + file;
            return FileVisitResult.TERMINATE;
            }
    
        if ( isDoingCutFlag )
            {
            Files.delete( file );
            //System.err.println( "would delete file =" + file );
            }
        
        numFileMatches++;
        return FileVisitResult.CONTINUE;
        }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException ex) 
            throws IOException
        {
        try {
            //numTested++;
            if ( isDoingCutFlag )
                {
                Files.delete( dir );
                //System.err.println( "would delete folder =" + dir );
                //numFoldersDeleted++;
                }
            return FileVisitResult.CONTINUE;
            }
        //throw ex;
//        catch (RuntimeException ex3) 
//            {
//            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex3 );
//        System.err.println( "CAUGHT RUNTIME ERROR  " + "my error msg" + ex3.getClass().getSimpleName() + ": " + dir );
//            throw new IOException( "my runtime msg" + ex3.getClass().getSimpleName() + ": " + dir );
//            }
        catch (Exception ex2) 
            {
            logger.log(Level.SEVERE, null, ex2 );
            //System.err.println( "CAUGHT ERROR  " + "my error msg" + ex2.getClass().getSimpleName() + ": " + dir );
            throw new IOException( ex2.getClass().getSimpleName() + ": " + dir );
            }
        //return FileVisitResult.TERMINATE;
        }
    
        
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        System.err.println( "Tested:  " + numTested );
        System.err.println( "Copied count: " + numFileMatches );

//            for ( Path mpath : matchedPathsList )
//                {
//                System.err.println( mpath );
//                }
        }
    
    public long getNumTested()
        {
        return numTested;
        }

    public long getNumFileMatches()
        {
        return numFileMatches;
        }

    public long getNumFolderMatches() {
        return numFolderMatches;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }
        
}
