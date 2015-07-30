/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class ChainFilterOfPreVisitMinDepth implements FilterChainFilter {

    int minDepth;
    
    public ChainFilterOfPreVisitMinDepth()
        {
        //System.out.println( "new ChainFilterOfMinDepth()" );
        }
    
    public ChainFilterOfPreVisitMinDepth( String startingFolder, String minDepth ) 
        {
        this.minDepth = Paths.get( startingFolder ).getNameCount() + Integer.parseInt( minDepth );
        //System.out.println( "preVisit Paths.get( startingFolder ).getNameCount() =" + Paths.get( startingFolder ).getNameCount() + "   Integer.parseInt( minDepth ) =" + Integer.parseInt( minDepth ) );
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean accept( Path fpath, BasicFileAttributes attr )
        {
        //System.out.print( "mindepth filevisit for path =" + fpath + "   depthcount =" + fpath.getNameCount() );
        if ( ! attr.isDirectory() )
            {
            //System.out.println( "not dir and >  min =" + minDepth + "  true/false =" + (fpath.getNameCount() > minDepth) );
            return fpath.getNameCount() > minDepth;
            }
        //System.out.println( " IS DIR true/false =true always" );
        return true;
        }
}
