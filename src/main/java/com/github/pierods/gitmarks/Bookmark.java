package com.github.pierods.gitmarks;

import java.time.LocalDateTime;

/**
 * Created by piero on 3/22/17.
 */
public class Bookmark {
  String type;
  String guid;
  int id;
  String title;
  int index;
  LocalDateTime dateAdded, lastModified;
  String uri;
  String iconuri;
  Bookmark[] children;
}
