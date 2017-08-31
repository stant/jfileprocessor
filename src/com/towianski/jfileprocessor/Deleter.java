/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.utils.DesktopUtils;
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
    private long numFilesDeleted = 0;
    private long numFoldersDeleted = 0;
    private long numTested = 0;

    boolean cancelFlag = false;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    Boolean dataSyncLock = false;
    Boolean deleteFilesOnlyFlag = false;
    Boolean deleteToTrashFlag = true;
    Boolean deleteReadonlyFlag = false;
    Path trashFolder = DesktopUtils.getTrashFolder().toPath();
    String processStatus = "";
    String message = "";
    
    public Deleter( String startingPath, ArrayList<Path> copyPaths, Boolean deleteFilesOnlyFlag, Boolean deleteToTrashFlag, Boolean deleteReadonlyFlag )
    {
        this.fromPath = Paths.get( startingPath );
        this.copyPaths = copyPaths;
        this.deleteFilesOnlyFlag = deleteFilesOnlyFlag;
        this.deleteToTrashFlag = deleteToTrashFlag;
        this.deleteReadonlyFlag = deleteReadonlyFlag;
        System.out.println( "Deleter this.fromPath =" + this.fromPath + "=" );
        cancelFlag = false;
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        }

    @Override
    public FileVisitResult visitFile( Path fpath, BasicFileAttributes attrs ) 
            throws IOException 
        {
        if ( cancelFlag )
            {
            System.out.println( "Delete cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        try {
            if ( deleteReadonlyFlag )
                {
                fpath.toFile().setWritable( true );
                }
            if ( deleteToTrashFlag )
                {
                Path trashparent = trashFolder.resolve( fromPath.relativize( fpath ) ).getParent();
                //System.out.println( "trashparent =" + trashparent );
                trashparent.toFile().mkdirs();
                Files.copy( fpath, trashFolder.resolve( fromPath.relativize( fpath ) ), StandardCopyOption.REPLACE_EXISTING );
                }
            Files.delete( fpath );
            }
        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
            {
            processStatus = "Error";
            message = exAccessDenied.getClass().getSimpleName() + ": " + fpath;
            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, exAccessDenied );
            System.out.println( "visitFile() AccessDeniedException: " + "  " + exAccessDenied.getClass().getSimpleName() + ": " + fpath );
            // I tried to catch accessDenied from an error trying to delete a readOnly file
            // and then do file.setwritable(true) and delete it again, but it did not work and 
            // just cascaded errors down the road. Stan
            return FileVisitResult.TERMINATE;
            }
        catch ( Exception ex ) 
            {
            processStatus = "Error";
            message = ex.getClass().getSimpleName() + ": " + fpath;
            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex );
            System.out.println( "visitFile() CAUGHT ERROR  " + "  " + ex.getClass().getSimpleName() + ": " + fpath );
            ex.printStackTrace();
            return FileVisitResult.TERMINATE;
            }
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
            processStatus = "Error";
            message = ex2.getClass().getSimpleName() + ": " + dir;
            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex2 );
            System.out.println( "postVisitDirectory() delete folder ERROR  " + "  " + ex2.getClass().getSimpleName() + ": " + dir );
            ex2.printStackTrace();
            return FileVisitResult.TERMINATE;
            }
        //return FileVisitResult.CONTINUE;
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

    public String getProcessStatus() {
        return processStatus;
    }

    public String getMessage() {
        return message;
    }
        
}
