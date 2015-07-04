/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class FilterChainFilter {
    
    public Boolean accept( Path fpath, BasicFileAttributes attr )
        {
        return false;  // expect this method to be overriden
        }

    public Boolean accept2( Path fpath, BasicFileAttributes attr )
        {
        return false;  // expect this method to be overriden
        }
    
}
