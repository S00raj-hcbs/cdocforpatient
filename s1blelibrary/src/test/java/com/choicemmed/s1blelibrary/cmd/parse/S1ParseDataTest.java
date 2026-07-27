package com.choicemmed.s1blelibrary.cmd.parse;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lazy_xia on 17/1/9.
 */
public class S1ParseDataTest {
    private S1ParseData s1ParseData;

    @Before
    public void setUp() throws Exception {
        s1ParseData = new S1ParseData();
    }


    @Test
    public void parseWeight() throws Exception {
        //55aa0540017702bf 63.1
        assertEquals(63.1
                , s1ParseData.parseWeight(new byte[]{0x55, (byte) 0xaa, 0x05, 0x40, 0x01, 0x77, 0x02, (byte) 0xbf})
                , 0);
    }

    @Test
    public void matchSuccess() throws Exception {
        assertTrue(s1ParseData.parseMatchResult("55aa03b100b4"));
    }

    @Test
    public void parseUnitResult() throws Exception {
        assertTrue(s1ParseData.parseUnitResult(new byte[]{0x55, (byte) 0xaa, 0x02, (byte) 0xf0, (byte) 0xf2}));
    }
 
}