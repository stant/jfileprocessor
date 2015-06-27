/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 *
 * @author stan
 */
public class MyMouseAdapter extends MouseAdapter 
{ 
    JPopupMenu jPopupMenu = null;
    
    public MyMouseAdapter( JPopupMenu jPopupMenu )
        {
        this.jPopupMenu = jPopupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
         
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
}
