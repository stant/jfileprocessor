package com.omt.clipboard.main;
 
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
 
import javax.imageio.ImageIO;
 
public class Main {
 
    public static void main(String[] args) {
        try {
 
            Transferable transferable = Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getContents(null);
            DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
 
            System.out.println("Size :" + dataFlavors.length);
 
            for (int count = 0; count < dataFlavors.length; count++) {
                System.out.println(" : " + dataFlavors[count]);
 
                if (DataFlavor.stringFlavor == dataFlavors[count]) {
                    System.out.println("It is String in Clipboard");
                    Object object = transferable
                            .getTransferData(dataFlavors[count]);
 
                    if (object instanceof String) {
                        System.out.println("String in clipboard :"
                                + object.toString());
                    }
 
                } else {
 
                    Object object = transferable
                            .getTransferData(dataFlavors[count]);
 
                    if (object instanceof Image) {
 
                        BufferedImage image = (BufferedImage) object;
                        File f = new File("omt.png");
                        if (f.canExecute()) {
                            f.delete();
                        }
                        f.createNewFile();
                        ImageIO.write(image, "png", f);
 
                    } else if (object instanceof List) {
                        System.out.println("found list on clipboard" );
                        List selectedFileList = (List) object;
                        int size = selectedFileList.size();
 
                        for (int index = 0; index < size; index++) {
 
                            File file = (File) selectedFileList.get(index);
                            if (file.isDirectory()) {
                                System.out.println("Directory Selected :"
                                        + file.getName());
                            } else {
                                System.out.println("File Selected :"
                                        + file.getName());
                            }
 
                        }
 
                    }
 
                }
 
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
