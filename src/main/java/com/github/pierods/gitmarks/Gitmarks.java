package com.github.pierods.gitmarks;

/**
 * Created by piero on 3/21/17.
 */

import org.gnome.gdk.Event;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.AcceleratorGroup;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.CheckMenuItem;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnBoolean;
import org.gnome.gtk.DataColumnInteger;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Label;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.MenuToolButton;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToggleToolButton;
import org.gnome.gtk.ToolButton;
import org.gnome.gtk.ToolItem;
import org.gnome.gtk.Toolbar;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;

/**
 * Gitmarks - save your bookmarks to git.
 *
 * @author Piero de Salvia
 */
public class Gitmarks {

  public static void dispatcher(String source, String command) {
    System.out.println("Source: " + source + " - command: " + command);
  }

  public Gitmarks() {

    final Window window = new Window();
    final AcceleratorGroup acceleratorGroup;

    acceleratorGroup = new AcceleratorGroup();
    window.addAcceleratorGroup(acceleratorGroup);

    final VBox box;
    box = new VBox(false, 3);
    window.add(box);

    final Label label;

    label = new Label("Select an action in a menu");
    label.setWidthChars(30);
    label.setAlignment(0.0f, 0.5f);

    box.packStart(makeMenu(acceleratorGroup), false, false, 0);
    box.packStart(makeToolbar(), false, false, 0);
    box.packStart(makeTree(), false, false, 0);
    box.packStart(label, false, false, 0);

    window.setTitle("Gitmarks");
    window.showAll();

    window.connect(new Window.DeleteEvent() {
      public boolean onDeleteEvent(Widget source, Event event) {
        Gtk.mainQuit();
        return false;
      }
    });
  }

  private Toolbar makeToolbar() {
    final Toolbar toolbar;
    final ToolButton buttonNew;
    final MenuToolButton mtb;
    final Menu openMenu;
    final ToolItem item;
    final ToggleToolButton boldButton, italicButton;

    toolbar = new Toolbar();

    buttonNew = new ToolButton(Stock.NEW);
    toolbar.add(buttonNew);

    buttonNew.connect(new ToolButton.Clicked() {
      public void onClicked(ToolButton source) {
        Gitmarks.dispatcher("toolbar-new","You have clicked NEW ToolButton");
      }
    });

         /*
         * Sometimes you need a ToolButton that also has a dropdown Menu, to
         * allow the user select alternative actions. You can do that with a
         * MenuToolButton.
         */
    mtb = new MenuToolButton(Stock.OPEN);
    toolbar.add(mtb);

        /*
         * You can add your Menu to this kind of ToolButtons
         */
    openMenu = new Menu();
    openMenu.append(new MenuItem("File _A", new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.dispatcher("toolbar-openmenu","You have selected File A in the Menu");
      }
    }));
    openMenu.append(new MenuItem("File _B", new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.dispatcher("toolbar-openmenu","You have selected File B in the Menu");
      }
    }));
    openMenu.showAll();
    mtb.setMenu(openMenu);
    return toolbar;
  }

  private MenuBar makeMenu(AcceleratorGroup acceleratorGroup) {

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
        Gitmarks.dispatcher("connect", "You have selected File->New menu.");
      }
    });
    save.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.dispatcher("save", "You have selected File->Save.");
      }
    });

    close.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.dispatcher("close", "You have selected File->Close.");
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
        Gitmarks.dispatcher("edit-copy", "You have selected Edit->Copy.");
      }
    });
    paste.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.dispatcher("edit-paste", "You have selected Edit->Paste.");
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
          Gitmarks.dispatcher("view-hidetoggle", "label.hide");
        } else {
          Gitmarks.dispatcher("view-hidetoggle", "label.hide");
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

  private TreeView makeTree() {

    final TreeView treeView;
    final ListStore model;
    TreeIter treeRow;
    CellRendererText cellRendererText;
    TreeViewColumn vertical;

    final DataColumnString placeName;
    final DataColumnString trailHead;
    final DataColumnString elevationFormatted;
    final DataColumnInteger elevationSort;
    final DataColumnBoolean accessibleByTrain;

    /*
     * So we begin our explorations of the TreeView API here. First thing
     * is to create a model. You'll notice we declared the variables
     * above. You don't have to do that, of course, but it makes calling
     * the ListStore constructor more palatable. More generally, you will
     * frequently have the DataColumns as instance field variables (so
     * they can be accessed throughout the file), rather than just local
     * variables. So you'll end up pre-declaring them regardless, and that
     * makes the constructor call nice and compact:
     */

    model = new ListStore(new DataColumn[]{
        placeName = new DataColumnString(),
        trailHead = new DataColumnString(),
        elevationFormatted = new DataColumnString(),
        elevationSort = new DataColumnInteger(),
        accessibleByTrain = new DataColumnBoolean()
    });

    /*
     * You almost never populate your TreeModels with data statically from
     * your source code. We have done so here to demonstrate the use of
     * appendRow() to get a new horizontal data treeRow and then the various
     * forms of setValue() to store data in that treeRow.
     *
     * In practise, however, you will find the setting of data to be
     * fairly involved; after all, it takes some trouble to decide what
     * you want to display, extract if from your domain object layer and
     * format it prior to insertion into the TreeModel for display by a
     * TreeView.
     *
     * Notice the use of Pango markup in the placeName column. This can be
     * a powerful way of improving your information density, but be aware
     * that if some rows have multiple lines and some don't, the treeRow
     * height will be inconsistent and will look really ugly. So make sure
     * they all have the same font information and number of newlines. You
     * would use utility methods to help keep things organized, of course,
     * but we've kept it straight forward here.
     */

    treeRow = model.appendRow();
    model.setValue(treeRow, placeName,
        "Blue Mountains national park\n<small>(Six Foot Track)</small>");
    model.setValue(treeRow, trailHead, "Katoomba, NSW, Australia");
    model.setValue(treeRow, elevationFormatted, "1015 m");
    model.setValue(treeRow, elevationSort, 1005);
    model.setValue(treeRow, accessibleByTrain, true);

    treeRow = model.appendRow();
    model.setValue(treeRow, placeName, "Kilimanjaro\n<small>(Machame route)</small>");
    model.setValue(treeRow, trailHead, "Nairobi, Kenya");
    model.setValue(treeRow, elevationFormatted, "5894 m");
    model.setValue(treeRow, elevationSort, 5894);
    model.setValue(treeRow, accessibleByTrain, false);

    treeRow = model.appendRow();
    model
        .setValue(treeRow, placeName, "Appalachian Trail\n<small>(roller coaster section)</small>");
    model.setValue(treeRow, trailHead, "Harpers Ferry, West Virginia, USA");
    model.setValue(treeRow, elevationFormatted, "147 m");
    model.setValue(treeRow, elevationSort, 147);
    model.setValue(treeRow, accessibleByTrain, true);

    /*
     * Now onto the treeView side. First we need the top level TreeView which
     * is the master Widget into which everything else is mixed.
     */
    treeView = new TreeView(model);

    /*
     * Now the vertical display columns. The sequence is to get a
     * TreeViewColumn, then create the CellRenderer to be put into it, and
     * then set whatever data mappings are appropriate along with any
     * settings for the vertical TreeViewColumn. This is the heart of the
     * TreeView/TreeModel API
     *
     * Note that we reuse the cellRendererText and vertical variables; unlike the
     * DataColumns, there's no need to keep coming up with unique column
     * names as you rarely need to reference them later.
     */
    vertical = treeView.appendColumn();
    vertical.setTitle("Place");
    cellRendererText = new CellRendererText(vertical);
    cellRendererText.setMarkup(placeName);

    vertical = treeView.appendColumn();
    vertical.setTitle("Nearest town");
    cellRendererText = new CellRendererText(vertical);
    cellRendererText.setText(trailHead);
    cellRendererText.setAlignment(0.0f, 0.0f);
    vertical.setExpand(true);

    vertical = treeView.appendColumn();
    vertical.setTitle("Elevation");
    cellRendererText = new CellRendererText(vertical);
    cellRendererText.setText(elevationFormatted);
    cellRendererText.setAlignment(0.0f, 0.0f);

    /*
     * Because the elevation is a formatted String, it won't sort
     * properly. So we use a DataColumnInteger populated with the raw
     * altitude number to indicate the sort order. (If you don't believe
     * me, try changing the sort column to elevationFormatted instead of
     * elevationSort, and you'll see it order as 1015, 147, 5894). As
     * you'll see from the documentation, setSortColumn() also makes the
     * headers clickable, which saves you having to mess with TreeView's
     * setHeadersClickable() or TreeViewColumn's setClickable(), although
     * there are occasional use cases for them.
     *
     * Then we call emitClicked() to force the header we want things to be
     * sorted on to actually be active. This is especially necessary if
     * you've defined sorting for more than one vertical column, but if
     * you want sorting on from the start, you need to call it.
     */
    vertical.setSortColumn(elevationSort);
    vertical.emitClicked();

    /*
     * And that's it! You've now done everything you need to have a
     * working TreeView.
     *
     * ... except that if it's for more than information, you probably
     * want something to happen when someone clicks on a treeRow. So, we hook
     * up a handler to the TreeView.RowActivated signal. The TreePath it
     * gives you is the useful bit.
     */
    treeView.connect(new TreeView.RowActivated() {
      public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
        final TreeIter row;
        final String place, height;

        row = model.getIter(path);

        place = model.getValue(row, trailHead);
        height = model.getValue(row, elevationFormatted);

        System.out.println("You want to go to " + place + " in order to climb to " + height);
      }
    });
    return treeView;
  }

  public static void main(String[] args) {
    Gtk.init(args);

    new Gitmarks();

    Gtk.main();
  }
}