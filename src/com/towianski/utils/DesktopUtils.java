/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author stan
 */
public class DesktopUtils 
{
    
   public static File getTrashFolder()
   {
      System.out.println( "os.name =" + System.getProperty( "os.name" ) + "=" );
      File trashFolder = null;
      File trashFolder1 = null;
      File trashFolder2 = null;
      File trashFolder3 = null;
      File trashFolder4 = null;
      String missingHomeErrMsg = "";
              
      if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
        {
        trashFolder1 = new File( System.getProperty( "user.home" ) + "/Library/Application Support", "JFileProcessor" );
        System.out.println( "try trashFolder folder =" + trashFolder1 + "=" );
        if ( trashFolder1.exists() )
            {
            trashFolder = trashFolder1;
            }
        else
            {
            trashFolder2 = new File( System.getProperty( "user.home" ) + "/Library/Preferences", "JFileProcessor" );
            System.out.println( "try trashFolder folder =" + trashFolder2 + "=" );
            if ( trashFolder2.exists() )
                {
                trashFolder = trashFolder2;
                }
            else
                {
                trashFolder3 = new File( "/Library/Preferences", "JFileProcessor" );
                System.out.println( "try trashFolder folder =" + trashFolder3 + "=" );
                if ( trashFolder3.exists() )
                    {
                    trashFolder = trashFolder3;
                    }
                else
                    {
                    trashFolder4 = new File( System.getProperty( "user.home" ) + "/Library", "JFileProcessor" );
                    System.out.println( "try trashFolder folder =" + trashFolder4 + "=" );
                    if ( trashFolder4.exists() )
                        trashFolder = trashFolder4;
                    } // 3
                } // 2
            } // 1
        
        // I am assuming at this point that these Mac folders do exist.
        
        if ( trashFolder == null )
            {
            System.out.println( "Could not find so assuming trashFolder folder =" + trashFolder1 + "=" );
            trashFolder = trashFolder1;
            missingHomeErrMsg = "\n\nI looked in these 4 places in this order: \n\n"
                        + trashFolder1 + "\n"
                        + trashFolder2 + "\n"
                        + trashFolder3 + "\n"
                        + trashFolder4 + "\n";
            }
        }
      else  // windows + Linux : test for moneydance folder
        {
        trashFolder1 = new File( System.getProperty( "user.home" ), ".JFileProcessor" );
        System.out.println( "try trashFolder folder =" + trashFolder1 + "=" );
        if ( trashFolder1.exists() )
            trashFolder = trashFolder1;

        if ( trashFolder == null )
            {
            System.out.println( "Could not find so assuming trashFolder folder =" + trashFolder1 + "=" );
            trashFolder = trashFolder1;
            missingHomeErrMsg = "";   //\n\nI looked in this place: \n\n"
                                      //+ trashFolder + "\n";
            }
        }

      // for all os's
    if ( ! trashFolder.exists() )
        {
        boolean ok = trashFolder.mkdirs();
        JOptionPane.showMessageDialog( null, "Could not find a JFileProcessor Bookmarks file so I created one here: \n\n" + trashFolder
                                        + missingHomeErrMsg
                                        );
        if ( ! ok )
            {
            JOptionPane.showMessageDialog( null, "*** Error creating JFileProcessor Bookmarks file: \n\n" + trashFolder );
            }
        }
    trashFolder = new File( trashFolder, "TrashFolder" );
      
      // all systems - trashFolder now includes properties file path
      try {
        if ( ! trashFolder.exists() )
            {
            boolean ok = trashFolder.mkdirs();
            JOptionPane.showMessageDialog( null, "Could not find its JFileProcessor Bookmarks file so I created one here: \n\n" + trashFolder
                        );
            }
        }
      catch (Exception ex) 
        {
        Logger.getLogger( DesktopUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

      return trashFolder;
   }
    
   public static File getBookmarks()
   {
      System.out.println( "os.name =" + System.getProperty( "os.name" ) + "=" );
      File bookmarks = null;
      File bookmarks1 = null;
      File bookmarks2 = null;
      File bookmarks3 = null;
      File bookmarks4 = null;
      String missingHomeErrMsg = "";
              
      if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
        {
        bookmarks1 = new File( System.getProperty( "user.home" ) + "/Library/Application Support", "JFileProcessor" );
        System.out.println( "try bookmarks folder =" + bookmarks1 + "=" );
        if ( bookmarks1.exists() )
            {
            bookmarks = bookmarks1;
            }
        else
            {
            bookmarks2 = new File( System.getProperty( "user.home" ) + "/Library/Preferences", "JFileProcessor" );
            System.out.println( "try bookmarks folder =" + bookmarks2 + "=" );
            if ( bookmarks2.exists() )
                {
                bookmarks = bookmarks2;
                }
            else
                {
                bookmarks3 = new File( "/Library/Preferences", "JFileProcessor" );
                System.out.println( "try bookmarks folder =" + bookmarks3 + "=" );
                if ( bookmarks3.exists() )
                    {
                    bookmarks = bookmarks3;
                    }
                else
                    {
                    bookmarks4 = new File( System.getProperty( "user.home" ) + "/Library", "JFileProcessor" );
                    System.out.println( "try bookmarks folder =" + bookmarks4 + "=" );
                    if ( bookmarks4.exists() )
                        bookmarks = bookmarks4;
                    } // 3
                } // 2
            } // 1
        
        // I am assuming at this point that these Mac folders do exist.
        
        if ( bookmarks == null )
            {
            System.out.println( "Could not find so assuming bookmarks folder =" + bookmarks1 + "=" );
            bookmarks = bookmarks1;
            missingHomeErrMsg = "\n\nI looked in these 4 places in this order: \n\n"
                        + bookmarks1 + "\n"
                        + bookmarks2 + "\n"
                        + bookmarks3 + "\n"
                        + bookmarks4 + "\n";
            }
        }
      else  // windows + Linux : test for moneydance folder
        {
        bookmarks1 = new File( System.getProperty( "user.home" ), ".JFileProcessor" );
        System.out.println( "try bookmarks folder =" + bookmarks1 + "=" );
        if ( bookmarks1.exists() )
            bookmarks = bookmarks1;

        if ( bookmarks == null )
            {
            System.out.println( "Could not find so assuming bookmarks folder =" + bookmarks1 + "=" );
            bookmarks = bookmarks1;
            missingHomeErrMsg = "";   //\n\nI looked in this place: \n\n"
                                      //+ bookmarks + "\n";
            }
        }

      // for all os's
    if ( ! bookmarks.exists() )
        {
        boolean ok = bookmarks.mkdir();
        JOptionPane.showMessageDialog( null, "Could not find a JFileProcessor folder so I created one here: \n\n" + bookmarks
                                        + missingHomeErrMsg
                                        );
        if ( ! ok )
            {
            JOptionPane.showMessageDialog( null, "*** Error creating JFileProcessor folder: \n\n" + bookmarks );
            }
        }
    bookmarks = new File( bookmarks, "Bookmarks.txt" );
      
      // all systems - bookmarks now includes properties file path
      try {
        if ( ! bookmarks.exists() )
            {
            boolean ok = bookmarks.createNewFile();
            JOptionPane.showMessageDialog( null, "Could not find its JFileProcessor Bookmarks file so I created one here: \n\n" + bookmarks
                        );
            }
        }
      catch (Exception ex) 
        {
        Logger.getLogger( DesktopUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

      return bookmarks;
   }
    
    //public static Rectangle getScreenViewableBounds(Window window) {
    //    return getScreenViewableBounds((Component) window);
    //}
    //
    //public static Rectangle getScreenViewableBounds(Component comp) {
    //    return getScreenViewableBounds(getGraphicsDevice(comp));
    //}
    //
    //public static Rectangle getScreenViewableBounds(GraphicsDevice gd) {
    //    Rectangle bounds = new Rectangle(0, 0, 0, 0);
    //    if (gd == null) {
    //        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    //        gd = ge.getDefaultScreenDevice();
    //    }
    //
    //    if (gd != null) {
    //        GraphicsConfiguration gc = gd.getDefaultConfiguration();
    //        bounds = gc.getBounds();
    //
    //        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
    //        bounds.x += insets.left;
    //        bounds.y += insets.top;
    //        bounds.width -= (insets.left + insets.right);
    //        bounds.height -= (insets.top + insets.bottom);
    //    }
    //
    //    return bounds;
    //}   

    public static void moveJframeToBottomScreen( JFrame f )
        {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle bounds = defaultScreen.getDefaultConfiguration().getBounds();
        GraphicsConfiguration gc = defaultScreen.getDefaultConfiguration();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= (insets.left + insets.right);
        bounds.height -= (insets.top + insets.bottom);
        int x = (int) (bounds.getMaxX() / 2) - (f.getWidth() / 2);
        int y = (int) bounds.getMaxY() - f.getHeight();
        f.setLocation(x, y);
        f.setVisible(true);
        }
}
