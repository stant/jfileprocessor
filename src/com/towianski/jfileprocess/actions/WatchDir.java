package com.towianski.jfileprocess.actions;

import com.sun.nio.file.SensitivityWatchEventModifier;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.ResultsData;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir implements Runnable
    {
    JFileFinderWin jFileFinderWin = null;
    private WatchService watcher = null;
    private Map<WatchKey,Path> keys = null;
    private boolean recursiveFlag = false;
    private boolean trace = false;
    private Path dirToWatch = null;
    Boolean cancelFlag = false;
    boolean registerOk = false;
    Boolean triggerSearchFlag = false;

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir( JFileFinderWin jFileFinderWin, Path dirToWatch, boolean recursiveFlag )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.dirToWatch = dirToWatch;
        this.recursiveFlag = recursiveFlag;
        this.keys = new HashMap<WatchKey,Path>();
        this.trace = true;
        }
    
    public void cancelWatch()
        {
        System.out.println("WatchDir set cancelFlag to true");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        System.out.println("cancelRegister() folder for watch " );
        cancelFlag = true;
        registerOk = true;

        try {
            watcher.close();
            }
        catch (Exception ex)
            {
            System.out.println("WatchDir set cancelFlag caught error !");
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("WatchDir exit cancelSearch()");
        }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    public void register(Path dir) 
        {
        System.out.println("register()  trace =" + trace + "   dir =" + dir );
        registerOk = false;
        while( ! cancelFlag && ! registerOk )
            {
            try
                {
                WatchKey key = null;
                if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
                    {
                    // Mac code is not good and is slow so saw this might help.
                    key = dir.register( watcher, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH );
                    }
                else
                    {
                    key = dir.register( watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY );
                    }
                if (trace) {
                    Path prev = keys.get(key);
                    if (prev == null) {
                        System.out.format("register: %s\n", dir);
                    } else {
                        if (!dir.equals(prev)) {
                            System.out.format("update: %s -> %s\n", prev, dir);
                            }
                        }
                    }
                keys.put(key, dir);
                registerOk = true;
                }
            catch (Exception ex) 
                {
                System.out.println( "could not start watchDir on folder = " + dir );
                System.out.println( "exception was: " + ex.getLocalizedMessage() );
                try {
                    System.out.println( "so wait 1 second" );
                    Thread.sleep(1000);
                    } 
                catch (InterruptedException ex2) 
                    {
                    System.out.println("Background interrupted");
                    }
                }
            }
        System.out.println("exit register()" );
        }

    /**
     * Register the given directory with the WatchService
     */
    public void unRegisterExisting()
//            throws IOException 
        {
        try {
            for ( Map.Entry<WatchKey,Path> entry : keys.entrySet())
                {
                System.out.format( "UNregister: %s\n", entry.getKey().watchable().toString() );
                entry.getKey().cancel();
                keys.remove( entry.getKey() );
                }
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
    }


    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     * not used so far......
    */
    public void registerRecursive(final Path start) 
        {
        try {
            // register directory and sub-directories
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException
                {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        System.out.println("entered watchDir.processEvents()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        triggerSearchFlag = false;
        while ( ! cancelFlag && ! triggerSearchFlag ) 
            {
            System.out.println("watchDir.processEvents() start loop" );

            // wait for key to be signalled
            WatchKey key = null;
            try {
                key = watcher.take();
                }
            catch( ClosedWatchServiceException cwe )
                {
                System.out.println("watchDir.processEvents() ClosedWatchServiceException but CONTINUE" );
                //return;
                }
            catch ( InterruptedException ix ) 
                {
                System.out.println("watchDir.processEvents() InterruptedException but CONTINUE" );
                //return;
                }
            System.out.println("watchDir.processEvents() after watcher.take()" );

            Path dir = keys.get(key);
            if (dir == null) 
                {
                System.out.println("WatchKey not recognized!!");
                try {
                    key.reset();
                    }
                catch( Exception exc )
                    {
                    System.out.println( "watchDir.processEvents() key.reset() error 1" );
                    }
                continue;
                }

            try {
                for (WatchEvent<?> event: key.pollEvents()) 
                    {
                    WatchEvent.Kind kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) 
                        {
                        try {
                            key.reset();
                            }
                        catch( Exception exc )
                            {
                            System.out.println( "watchDir.processEvents() key.reset() error 2" );
                            }
                        continue;
                        }

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    // print out event
                    System.out.format("watchDir  %s: %s\n", event.kind().name(), child);

                    boolean foundIgnore = false;
                    for ( String ignorePath : jFileFinderWin.pathsToNotWatch() )
                        {
                        System.out.println( "watchDir child      =" + child + "=" );
                        System.out.println( "watchDir ignorePath =" + ignorePath + "=" );
                        if ( child.toString().trim().equals( ignorePath ) )
                            {
                            foundIgnore = true;
                            }
                        }
                    System.out.println( "watchDir  foundIgnore flag = " + foundIgnore );
                    if ( ! foundIgnore )
                        {   
                        triggerSearchFlag = true;
                        }
                    else
                        {
                        // pause 1 second to keep from too frequently refreshing as files change
                        try {
                            System.out.println( "skip watch on this file so sleep 2 seconds" );
                            Thread.sleep(2000);
                            } 
                        catch (InterruptedException ex2) 
                            {
                            System.out.println( "Background interrupted" );
                            }
                        }

                    // if directory is created, and watching recursively, then
                    // register it and its sub-directories
                    if (recursiveFlag && (kind == ENTRY_CREATE)) 
                        {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) 
                                {
                                registerRecursive(child);
                                }
                            }
                        catch (Exception x) 
                            {
                            // ignore to keep sample readbale
                            }
                        }
                    } // end for
                }
            catch (Exception exc) 
                {
                exc.printStackTrace();
                }

            // reset key and remove from set if directory no longer accessible
            boolean valid = false;
            try {
                valid = key.reset();
                }
            catch( Exception exc )
                {
                System.out.println( "watchDir.processEvents() key.reset() error last" );
                }
//            if (!valid) 
//                {
//                keys.remove(key);
//
//                // all directories are inaccessible
//                if (keys.isEmpty()) 
//                    {
//                    break;
//                    }
//                }
            } // end while
        
        try {
            this.watcher.close();
            }
        catch (IOException ex)
            {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
            }
    System.out.println( "exiting watchDir process loop !" );
    }

    static void usage() {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }

    @Override
    public void run() {
        System.out.println( "entered watchDir run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        cancelFlag = false;

        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            } 
        catch (IOException ex)
            {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
            }

        System.out.println("WatchDir() dir =" + dirToWatch + "=" );
        if ( recursiveFlag )
            {
            registerRecursive( dirToWatch );
            } 
        else 
            {
            register( dirToWatch );
            }
    
//            WatchDir watchDir = new WatchDir( dirToWatch, false );
//            watchDir.processEvents();
        processEvents();
        System.out.println( "after watchDir processEvents()  - call search button" );
        System.out.println( "watchDir.run() after watchDir.run()" );
        System.out.println("watchDir.run() triggerSearchFlag = " + triggerSearchFlag );
        if ( triggerSearchFlag )
            {
            jFileFinderWin.callSearchBtnActionPerformed( null );
            
//                SwingUtilities.invokeLater(new Runnable() 
//                {
//                    public void run() {
//                        System.out.println( "WatchDir.run()   invokeLater()" );
//                        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//                        //                watchDirSw.setIsDone(true);
//                        jFileFinderWin.callSearchBtnActionPerformed( null );
//                    }
//                });
               
            // pause 1 second to keep from too frequently refreshing as files change
//            try {
//                Thread.sleep(1000);
//                } 
//            catch (InterruptedException ex2) 
//                {
//                System.out.println( "Background interrupted" );
//                }
            
            }
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        boolean recursive = false;
        int dirArg = 0;
        if (args[0].equals("-r")) {
            if (args.length < 2)
                usage();
            recursive = true;
            dirArg++;
        }

        // register directory and process its events
        Path dir = Paths.get(args[dirArg]);
//        new WatchDir(dir, recursive).processEvents();
    }
}
