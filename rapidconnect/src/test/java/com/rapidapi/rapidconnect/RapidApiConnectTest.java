package com.rapidapi.rapidconnect;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RapidApiConnectTest {
  private static final String PROJECT = "Rapid_Api_Unit_Tests";
  private static final String KEY = "ff0df8fb-2cd5-448f-be44-44ec3c318338";

  private RapidApiConnect testObject;

  @Before
  public void setUp() throws Exception {
    testObject = new RapidApiConnect(PROJECT, KEY);
  }

  @Test
  public void blockUrlBuilding() {
    String actual = RapidApiConnect.blockUrlBuild("pck", "blk");
    assertEquals("https://rapidapi.io/connect/pck/blk", actual);
  }

  @Test
  public void zeroParameterServiceCallShouldNotThrowIllegalArgumentExceptions() {
    try {
      Map<String, Object> result = testObject.call("HackerNews", "getJobStories", Collections.<String, Argument>emptyMap());
      assertNotNull(result);
      assertNotNull(result.get("success"));
    } catch (Throwable e) {
      fail("No exception expected: " + e);
    }
  }

  @Test
  public void nullBodyMapIsHandledGracefully() {
    try {
      Map<String, Object> result = testObject.call("HackerNews", "getJobStories", null);
      assertNotNull(result);
      assertNotNull(result.get("success"));
    } catch (Throwable e) {
      fail("No exception expected: " + e);
    }
  }

}