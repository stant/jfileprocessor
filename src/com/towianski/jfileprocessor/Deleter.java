/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stan Towianski
 */
public class Deleter extends SimpleFileVisitor<Path> 
{
    private Path fromPath;
    private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
    private long numFilesDeleted = 0;
    private long numFoldersDeleted = 0;
    private long numTested = 0;

    boolean cancelFlag = false;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    Boolean dataSyncLock = false;
    Boolean deleteFilesOnlyFlag = false;
    
    public Deleter( String startingPath, ArrayList<Path> copyPaths, Boolean deleteFilesOnlyFlag )
    {
        this.fromPath = Paths.get( startingPath );
        this.copyPaths = copyPaths;
        this.deleteFilesOnlyFlag = deleteFilesOnlyFlag;
        System.err.println( "Deleter this.fromPath =" + this.fromPath + "=" );
        cancelFlag = false;
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        }

    @Override
    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) 
            throws IOException 
        {
        if ( cancelFlag )
            {
            System.out.println( "Delete cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        Files.delete( file );
        //System.out.println( "would delete file =" + file );
        numFilesDeleted++;
        return FileVisitResult.CONTINUE;
        }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException ex) 
            throws IOException
        {
        //if ( ex == null )
        try {
            numTested++;
            if ( ! deleteFilesOnlyFlag )
                {
                Files.delete( dir );
                numFoldersDeleted++;
                }
            //System.out.println( "would delete folder =" + dir );
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
            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex2 );
        System.out.println( "CAUGHT ERROR  " + "my error msg" + ex2.getClass().getSimpleName() + ": " + dir );
            throw new IOException( "my error msg" + ex2.getClass().getSimpleName() + ": " + dir );
            }
        //return FileVisitResult.TERMINATE;
        }
    
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        System.out.println( "Tested:  " + numTested );
        System.out.println( "Deleted Files count: " + numFilesDeleted );
        System.out.println( "Deleted Folders count: " + numFoldersDeleted );

//            for ( Path mpath : matchedPathsList )
//                {
//                System.out.println( mpath );
//                }
        }
    
    public long getNumTested()
        {
        return numTested;
        }

    public long getNumFilesDeleted()
        {
        return numFilesDeleted;
        }

    public long getNumFoldersDeleted() {
        return numFoldersDeleted;
    }
        
}
