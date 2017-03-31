package com.github.pierods.gitmarks.commandhandlers;

import com.github.pierods.gitmarks.Gitmarks;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserDialog;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Window;

/**
 * Created by piero on 3/30/17.
 */
public class OpenHandler implements Gitmarks.CommandHandler {

  private final Window parent;

  public OpenHandler(Window parent) {
    this.parent = parent;
  }
  private final String commandName = "file-open";

  @Override
  public String getCommand() {
    return commandName;
  }

  @Override
  public void handleCommand(String source) {
    System.out.println(source + " - " + commandName);

    FileChooserDialog dialog;
    ResponseType response;

    // instantiate
    dialog = new FileChooserDialog("Open File", parent, FileChooserAction.OPEN);
    // run the Dialog
    response = dialog.run();
    dialog.hide();

    // deal with the result
    if (response == ResponseType.OK) {
      System.out.println(dialog.getFilename());
    }
  }
}
