/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stan
 */
public class ClipboardFilesList {
 
    public static ArrayList<Path> getClipboardFilesList()
        {
        ArrayList<Path> copyPaths = new ArrayList<Path>();
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();

            int max = dataFlavors.length;
            System.out.println( "clipboard Size :" + max );

            for (int count = 0; count < max; count++) 
                {
                //System.out.println(" : " + dataFlavors[count]);

                if ( DataFlavor.stringFlavor != dataFlavors[count] ) 
                    {
                    Object object = transferable.getTransferData( dataFlavors[count] );
                    if (object instanceof List) 
                        {
                        System.out.println("found list on clipboard" );
                        List selectedFileList = (List) object;
                        int size = selectedFileList.size();

                        for (int index = 0; index < size; index++) 
                            {
                            File file = (File) selectedFileList.get(index);
                            copyPaths.add( file.toPath() );
                            //System.out.println( "clipboard got file =" + file.toPath().toString() + "=" );
                            }
                        }
                    }
                } // for
            }
        catch (Exception exception) 
            {
            exception.printStackTrace();
            }
        return copyPaths;
        }    
}
