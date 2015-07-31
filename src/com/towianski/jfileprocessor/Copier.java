/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stan Towianski
 */
public class Copier extends SimpleFileVisitor<Path> 
{
    private Boolean isDoingCutFlag = false;
    private Path fromPath;
    private Path toPath;
    private CopyOption[] copyOptions = null;
    private long numFileMatches = 0;
    private long numFolderMatches = 0;
    private long numTested = 0;

    boolean cancelFlag = false;
    boolean cancelFillFlag = false;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    Boolean dataSyncLock = false;
    
    public Copier( Boolean isDoingCutFlag, String startingPath, ArrayList<Path> copyPaths, String toPath, CopyOption[] copyOptions )
    {
        this.isDoingCutFlag = isDoingCutFlag;
        this.fromPath = Paths.get( startingPath );
        this.copyPaths = copyPaths;
        this.toPath = Paths.get( toPath );
        this.copyOptions = copyOptions;
        System.err.println( "Copier this.fromPath =" + this.fromPath + "   this.toPath =" + this.toPath + "=" );
        System.out.println( "isDoingCutFlag =" + isDoingCutFlag );
        cancelFlag = false;
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        }

    @Override
    public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
            throws IOException 
        {
        Path targetPath = toPath.resolve( fromPath.relativize( dir ) );
//        System.err.println( );
//        System.err.println( "preVisitDir fromPath =" + fromPath + "   dir =" + dir + "=" );
//        System.err.println( "preVisitDir relativize =" + fromPath.relativize( dir ) + "=" );
//        System.err.println( "preVisitDir toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );
        numTested++;
        numFolderMatches++;
        if ( cancelFlag )
            {
            System.out.println( "Search cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        if( ! Files.exists( targetPath ) )
            {
            Files.createDirectory( targetPath );
            //System.err.println( "preVisitDir would do Files.createDirectory( " + targetPath + ")" );
            }
        return FileVisitResult.CONTINUE;
        }

    @Override
    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) 
            throws IOException 
        {
        //System.err.println( "VisitFile copy file =" + file + "=    toPath.resolve( fromPath.relativize( file ) ) =" + toPath.resolve( fromPath.relativize( file ) ) + "=" );
        if ( cancelFlag )
            {
            System.out.println( "Search cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        
        if ( file.compareTo( toPath.resolve( fromPath.relativize( file ) ) ) == 0 )
            {
            System.out.println( "Skip Copy to Itself." );
            return FileVisitResult.CONTINUE;
            }

//            CopyOption[] copyOpts = new CopyOption[3];
//            //copyOpts[0] = StandardCopyOption.REPLACE_EXISTING;
//            copyOpts[1] = StandardCopyOption.COPY_ATTRIBUTES;
//            //copyOpts[2] = LinkOption.NOFOLLOW_LINKS;
        
        if ( copyOptions == null || copyOptions.length < 1 )
            {
            //System.err.println( "copy with default options" );
            Files.copy( file, toPath.resolve( fromPath.relativize( file ) ) );
            }
        else
            {
            Files.copy( file, toPath.resolve( fromPath.relativize( file ) ), copyOptions );
            }
    
        if ( isDoingCutFlag )
            {
            Files.delete( file );
            //System.out.println( "would delete file =" + file );
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
                //System.out.println( "would delete folder =" + dir );
                //numFoldersDeleted++;
                }
            return FileVisitResult.CONTINUE;
            }
        //throw ex;
//        catch (RuntimeException ex3) 
//            {
//            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex3 );
//        System.out.println( "CAUGHT RUNTIME ERROR  " + "my error msg" + ex3.getClass().getSimpleName() + ": " + dir );
//            throw new IOException( "my runtime msg" + ex3.getClass().getSimpleName() + ": " + dir );
//            }
        catch (Exception ex2) 
            {
            Logger.getLogger(Copier.class.getName()).log(Level.SEVERE, null, ex2 );
            System.out.println( "CAUGHT ERROR  " + "my error msg" + ex2.getClass().getSimpleName() + ": " + dir );
            throw new IOException( ex2.getClass().getSimpleName() + ": " + dir );
            }
        //return FileVisitResult.TERMINATE;
        }
    
        
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        System.out.println( "Tested:  " + numTested );
        System.out.println( "Copied count: " + numFileMatches );

//            for ( Path mpath : matchedPathsList )
//                {
//                System.out.println( mpath );
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
        
}
