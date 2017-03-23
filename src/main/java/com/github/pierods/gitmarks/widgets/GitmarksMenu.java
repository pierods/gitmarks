package com.github.pierods.gitmarks.widgets;

import com.github.pierods.gitmarks.Gitmarks;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.AcceleratorGroup;
import org.gnome.gtk.CheckMenuItem;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;

/**
 * Created by piero on 3/23/17.
 */
public class GitmarksMenu {

  public MenuBar makeMenu(Gitmarks.Dispatcher dispatcher, AcceleratorGroup acceleratorGroup) {

    final Menu fileMenu, editMenu, viewMenu;
    final MenuItem file, edit, view;
    final MenuItem save, copy, paste;
    final ImageMenuItem nouveau, close;
    final AcceleratorGroup group;
    final MenuBar bar;


        /*
         * Most applications will use several Menus in a MenuBar:
         */
    fileMenu = new Menu();
    editMenu = new Menu();
    viewMenu = new Menu();

    /*
     * Create all of MenuItems that will be used:
     */
    nouveau = new ImageMenuItem(Stock.NEW);
    save = new MenuItem("_Save");
    close = new ImageMenuItem(Stock.CLOSE);
    copy = new MenuItem("_Edit");
    paste = new MenuItem("_Paste");

     /*
     * Now we add the keybindings for the menu items. This has to be done
     * before you append them to their Menus.
     */
    save.setAccelerator(acceleratorGroup, Keyval.s, ModifierType.CONTROL_MASK);
    copy.setAccelerator(acceleratorGroup, Keyval.c, ModifierType.CONTROL_MASK);
    paste.setAccelerator(acceleratorGroup, Keyval.v, ModifierType.CONTROL_MASK);

    /*
     * For ImageMenuItems you can activate the keybinding that comes with
     * the Stock item instead.
     */
    nouveau.setAccelerator(acceleratorGroup);

     /*
     * Despite 'close' also being an ImageMenuItem we could use the
     * keybinding that is set for Stock.CLOSE. But since we have already
     * set Control + C for editCopy we set this one manually to another
     * keybinding:
     */
    close.setAccelerator(acceleratorGroup, Keyval.w, ModifierType.CONTROL_MASK);

    fileMenu.setAcceleratorGroup(acceleratorGroup);
    editMenu.setAcceleratorGroup(acceleratorGroup);

    fileMenu.append(nouveau);
    fileMenu.append(save);

    /*
     * A SeparatorMenuItem can be used to differentiate between unrelated
     * menu options; in practise, though, only use sparingly.
     */
    fileMenu.append(new SeparatorMenuItem());

    /*
     * And add the rest of the menu items.
     */
    fileMenu.append(close);
    editMenu.append(copy);
    editMenu.append(paste);

    /*
     * Usually you will want to connect to the MenuItem.Activate signal,
     * that is emitted when the user "activates" the menu by either
     * clicking it with the mouse or navigating to it with the keyboard
     * and pressing <ENTER>.
     */
    nouveau.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        dispatcher.dispatch("connect", "You have selected File->New menu.");
      }
    });
    save.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        dispatcher.dispatch("save", "You have selected File->Save.");
      }
    });

    close.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        dispatcher.dispatch("close", "You have selected File->Close.");
      }
    });

    /*
     * Given that in most cases you will connect to the MenuItem.Activate
     * signal on MenuItems, a convenience constructor is provided:
     */
    fileMenu.append(new MenuItem("_Quit", new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gtk.mainQuit();
      }
    }));

    /*
     * And now add the actions for the items making up the "edit" Menu.
     */
    copy.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        dispatcher.dispatch("edit-copy", "You have selected Edit->Copy.");
      }
    });
    paste.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        dispatcher.dispatch("edit-paste", "You have selected Edit->Paste.");
      }
    });

    /*
     * CheckMenuItems hold a boolean state. One use is to allow users to
     * hide some parts of the GUI, as in this example which we put into
     * the "view" Menu:
     */
    viewMenu.append(new CheckMenuItem("Hide _text", new CheckMenuItem.Toggled() {
      public void onToggled(CheckMenuItem source) {
        if (source.getActive()) {
          dispatcher.dispatch("view-hidetoggle", "label.hide");
        } else {
          dispatcher.dispatch("view-hidetoggle", "label.hide");
        }
      }
    }));

    /*
     * A MenuItem can have a "sub-menu", that will be expanded when the
     * user puts the mouse pointer over it. This is also used in creating
     * the elements for the top level MenuBar, but you can use it within
     * normal Menus as well. That said, submenus of Menus are considered
     * less "discoverable" because the user has to navigate through the
     * hierarchy to find out what options are available to them, rather
     * than seeing them at first glance.
     */
    file = new MenuItem("_File");
    file.setSubmenu(fileMenu);
    edit = new MenuItem("_Edit");
    edit.setSubmenu(editMenu);
    view = new MenuItem("_View");
    view.setSubmenu(viewMenu);

    /*
     * Finally, most applications make use of a MenuBar that is by
     * convention located at the top of the application Window. It
     * contains the top-level MenuItems.
     */
    bar = new MenuBar();
    bar.append(file);
    bar.append(edit);
    bar.append(view);

    return bar;
  }


}
