package com.github.pierods.gitmarks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by piero on 3/22/17.
 */
public class FirefoxImporterTest {

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void importFirefoxJson() throws Exception {

    FirefoxImporter fi = new FirefoxImporter();

    fi.importFirefoxJson("/home/piero/temp/bookmarks-2017-03-22.json");


  }
}