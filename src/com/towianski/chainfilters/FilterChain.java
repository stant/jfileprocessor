/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class FilterChain {
    
    private ArrayList<FilterChainFilter> filterList = new ArrayList<FilterChainFilter>();
    private String andOrTests = CHAINFILTERA_AND_TEST;
    private FilterChainFilter currentChainFilter = null;
    private FilterChainFilter nextChainFilter = null;
    private int atIdx = -1;
    final public static String CHAINFILTERA_AND_TEST = "AND";
    final public static String CHAINFILTERA_OR_TEST = "OR";
    
    public FilterChain()
        {
        }
    
    public FilterChain( String andOrTests )
        {
        this.andOrTests = andOrTests;
        }
    
    public void addFilter( FilterChainFilter nextChainFilter )
        {
        this.filterList.add( nextChainFilter );
        }
    
    public Boolean testFilters( Path fpath, BasicFileAttributes attr )
        {
        int max = filterList.size();
        //System.out.println( "entered " + this.toString() + ".incAndTest(" + andOrTests + ")   path =" + fpath );
        if ( max < 1 )
            {
            return true;
            }
        
        for ( int filter = 0; filter < max; filter++ )
            {
            //System.out.println( "\ntest filter =" + filterList.get( filter ) );
            if ( filterList.get( filter ).accept( fpath, attr ) )
                {
                //System.out.println( "accepted" );
                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_OR_TEST ) )
                    {
                    //System.out.println( "return true because OR test" );
                    return true;
                    }
                }
            else   // test failed
                {
                //System.out.println( "failed" );
                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_AND_TEST ) )
                    {
                    //System.out.println( "return false because AND test" );
                    return false;
                    }
                }
            }

        if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_AND_TEST ) )
            {
            //System.out.println( "return true because no more AND tests" );
            return true;
            }
        else
            {
            //System.out.println( "return false because no more OR tests" );
            return false;
            }

        }
    
    public Boolean testFilters2( Path fpath, BasicFileAttributes attr )
        {
        int max = filterList.size();
        //System.out.println( "entered " + this.toString() + ".incAndTest(" + andOrTests + ")   path =" + fpath );
        if ( max < 1 )
            {
            return true;
            }
        
        for ( int filter = 0; filter < max; filter++ )
            {
            //System.out.println( "\ntest filter2 =" + filterList.get( filter ) );
            if ( filterList.get( filter ).accept2( fpath, attr ) )
                {
                //System.out.println( "accepted" );
                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_OR_TEST ) )
                    {
                    //System.out.println( "return true because OR test" );
                    return true;
                    }
                }
            else   // test failed
                {
                //System.out.println( "failed" );
                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_AND_TEST ) )
                    {
                    //System.out.println( "return false because AND test" );
                    return false;
                    }
                }
            }

        if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_AND_TEST ) )
            {
            //System.out.println( "return true because no more AND tests" );
            return true;
            }
        else
            {
            //System.out.println( "return false because no more OR tests" );
            return false;
            }

        }
    
//    private void incrementFilter()
//        {
//        atIdx++;
//        if ( atIdx >= filterList.size() )
//            {
//            currentChainFilter = null;
//            nextChainFilter = null;
//            }
//        else if ( atIdx == (filterList.size() - 1) )
//            {
//            currentChainFilter = filterList.get( atIdx );
//            nextChainFilter = null;
//            }
//        else if ( atIdx >= 0 )
//            {
//            currentChainFilter = filterList.get( atIdx - 1 );
//            nextChainFilter = filterList.get( atIdx );
//            }
//        System.out.println( "currentChainFilter = " + currentChainFilter + "   nextChainFilter =" + nextChainFilter + "=" );
//        }
//    
//    private Boolean incAndTest( Path fpath, BasicFileAttributes attr )
//        {
//        return incAndTest( andOrTests, fpath, attr );
//        }
//    
//    private Boolean incAndTest( String andOrTests, Path fpath, BasicFileAttributes attr )
//        {
//        System.out.println( "entered " + this.toString() + ".incAndTest(" + andOrTests + ")   path =" + fpath );
//        incrementFilter();
//
//            if ( currentChainFilter.accept( fpath, attr ) )
//                {
//                System.out.println( "accepted" );
//                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_OR_TEST ) )
//                    {
//                    System.out.println( "return true because OR test" );
//                    return true;
//                    }
//                else if ( nextChainFilter == null )
//                    {
//                    System.out.println( "return true because no more tests" );
//                    return true;
//                    }
//                return incAndTest( andOrTests, fpath, attr );
//                }
//            else   // test failed
//                {
//                System.out.println( "failed" );
//                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_AND_TEST ) )
//                    {
//                    System.out.println( "return false because AND test" );
//                    return false;
//                    }
//                else if ( nextChainFilter == null )
//                    {
//                    System.out.println( "return false because no more tests" );
//                    return false;
//                    }
//                return incAndTest( andOrTests, fpath, attr );
//                }
//        }
//    
    public Boolean accept( Path fpath, BasicFileAttributes attr )
        {
        return false;  // expect this method to be overriden
        }

    public Boolean accept2( Path fpath, BasicFileAttributes attr )
        {
        return false;  // expect this method to be overriden
        }
    
}
