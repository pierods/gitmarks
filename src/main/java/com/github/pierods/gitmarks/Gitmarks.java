package com.github.pierods.gitmarks;

/**
 * Created by piero on 3/21/17.
 */

import com.github.pierods.gitmarks.widgets.FolderTree;
import com.github.pierods.gitmarks.widgets.GitmarksMenu;
import com.github.pierods.gitmarks.widgets.GitmarksToolbar;
import com.github.pierods.gitmarks.widgets.ItemList;
import org.gnome.gdk.Event;
import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.AcceleratorGroup;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Statusbar;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;

/**
 * Gitmarks - save your bookmarks to git.
 *
 * @author Piero de Salvia
 */
public class Gitmarks {

  private final Statusbar statusbar = new Statusbar();

  public interface Dispatcher {
    public void dispatch(String source, String command);
  }

  class StatusBarDispatcher implements Dispatcher {
    @Override
    public void dispatch(String source, String command) {
      System.out.println("Source: " + source + " - command: " + command);
      statusbar.setMessage("Source: " + source + " - command: " + command);
    }
  }

  public Gitmarks() {

    final Dispatcher dispatcher = new StatusBarDispatcher();
    final Window window = new Window();
    final AcceleratorGroup acceleratorGroup;

    acceleratorGroup = new AcceleratorGroup();
    window.addAcceleratorGroup(acceleratorGroup);

    final VBox vBox;
    final HBox hBox;

    vBox = new VBox(false, 3);
    window.add(vBox);
    hBox = new HBox(false, 3);

    Pixbuf folderIcon = Gtk.renderIcon(window, Stock.DIRECTORY, IconSize.SMALL_TOOLBAR);

    vBox.packStart(new GitmarksMenu().makeMenu(dispatcher, acceleratorGroup), false, false, 0);
    vBox.packStart(new GitmarksToolbar().makeToolbar(dispatcher), false, false, 0);
    hBox.packStart(new FolderTree().makeTree(folderIcon), true, true, 0);
    hBox.packStart(new ItemList().makeItemList(dispatcher), true, true, 0);
    vBox.packStart(hBox, true, false, 0);
    vBox.packEnd(statusbar, false, true, 0);

    window.setTitle("Gitmarks");
    window.showAll();

    window.connect(new Window.DeleteEvent() {
      public boolean onDeleteEvent(Widget source, Event event) {
        Gtk.mainQuit();
        return false;
      }
    });
  }

  public static void main(String[] args) {
    Gtk.init(args);

    new Gitmarks();

    Gtk.main();
  }
}