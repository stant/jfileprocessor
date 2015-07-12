package com.omt.clipboard.main;
 
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
 
public class ClipboardFiles implements Transferable, ClipboardOwner {
 
    private ArrayList<File> list;
 
    public ClipboardFiles(ArrayList<File> list) {
        this.list = list;
    }
 
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
 
        if (flavor == DataFlavor.javaFileListFlavor) {
            return list;
        }
 
        return new UnsupportedFlavorException(flavor);
    }
 
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] dataFlavors = new DataFlavor[1];
        dataFlavors[0] = DataFlavor.javaFileListFlavor;
        return dataFlavors;
    }
 
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
 
        if (flavor == DataFlavor.javaFileListFlavor) {
            return true;
        }
 
        return false;
    };
 
    @Override
    public void lostOwnership(java.awt.datatransfer.Clipboard clipboard,
            Transferable contents) {
 
    };
 
}
