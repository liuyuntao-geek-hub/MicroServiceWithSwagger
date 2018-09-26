package com.anthem.hca.smartpcp.audit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		ApplicationTest is used to test the application
 *  
 * @author AF56159 
 */

/**
 * Unit test for simple App.
 */
public class ApplicationTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ApplicationTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ApplicationTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
