package com.github.pierods.gitmarks;

/**
 * Created by piero on 3/21/17.
 */

import org.freedesktop.icons.Icon;
import org.freedesktop.icons.PlaceIcon;
import org.gnome.gdk.Event;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.AcceleratorGroup;
import org.gnome.gtk.CellRendererPixbuf;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.CheckMenuItem;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnBoolean;
import org.gnome.gtk.DataColumnInteger;
import org.gnome.gtk.DataColumnPixbuf;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.IconTheme;
import org.gnome.gtk.Image;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.InfoBar;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.MenuToolButton;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.SortType;
import org.gnome.gtk.Statusbar;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToggleToolButton;
import org.gnome.gtk.ToolButton;
import org.gnome.gtk.ToolItem;
import org.gnome.gtk.Toolbar;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeStore;
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

  private final Statusbar statusbar = new Statusbar();

  public void dispatcher(String source, String command) {
    System.out.println("Source: " + source + " - command: " + command);
    statusbar.setMessage("Source: " + source + " - command: " + command);
  }

  public Gitmarks() {

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

    vBox.packStart(makeMenu(acceleratorGroup), false, false, 0);
    vBox.packStart(makeToolbar(), false, false, 0);
    hBox.packStart(makeTree(folderIcon), true, true, 0);
    hBox.packStart(makeItemList(), true, true, 0);
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
        Gitmarks.this.dispatcher("toolbar-new", "You have clicked NEW ToolButton");
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
        Gitmarks.this.dispatcher("toolbar-openmenu", "You have selected File A in the Menu");
      }
    }));
    openMenu.append(new MenuItem("File _B", new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.this.dispatcher("toolbar-openmenu", "You have selected File B in the Menu");
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
        Gitmarks.this.dispatcher("connect", "You have selected File->New menu.");
      }
    });
    save.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.this.dispatcher("save", "You have selected File->Save.");
      }
    });

    close.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.this.dispatcher("close", "You have selected File->Close.");
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
        Gitmarks.this.dispatcher("edit-copy", "You have selected Edit->Copy.");
      }
    });
    paste.connect(new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        Gitmarks.this.dispatcher("edit-paste", "You have selected Edit->Paste.");
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
          Gitmarks.this.dispatcher("view-hidetoggle", "label.hide");
        } else {
          Gitmarks.this.dispatcher("view-hidetoggle", "label.hide");
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

  private TreeView makeItemList() {

    final TreeView treeView;
    final ListStore datastore;
    TreeIter treeRow;
    CellRendererText cellRendererText;
    TreeViewColumn vertical;

    final DataColumnString placeName = new DataColumnString();
    final DataColumnString trailHead = new DataColumnString();
    final DataColumnString elevationFormatted = new DataColumnString();
    final DataColumnInteger elevationSort = new DataColumnInteger();
    final DataColumnBoolean accessibleByTrain = new DataColumnBoolean();

    // define columns
    final DataColumn[] baseRow = new DataColumn[]{
        placeName,
        trailHead,
        elevationFormatted,
        elevationSort,
        accessibleByTrain
    };

    // define a List Item View datastore with my base row
    datastore = new ListStore(baseRow);

    // append rows
    treeRow = datastore.appendRow();
    datastore.setValue(treeRow, placeName, "Blue Mountains national park>");
    datastore.setValue(treeRow, trailHead, "Katoomba, NSW, Australia");
    datastore.setValue(treeRow, elevationFormatted, "1015 m");
    datastore.setValue(treeRow, elevationSort, 1005);
    datastore.setValue(treeRow, accessibleByTrain, true);

    treeRow = datastore.appendRow();
    datastore.setValue(treeRow, placeName, "Kilimanjaro");
    datastore.setValue(treeRow, trailHead, "Nairobi, Kenya");
    datastore.setValue(treeRow, elevationFormatted, "5894 m");
    datastore.setValue(treeRow, elevationSort, 5894);
    datastore.setValue(treeRow, accessibleByTrain, false);

    treeRow = datastore.appendRow();
    datastore.setValue(treeRow, placeName, "Appalachian Trail");
    datastore.setValue(treeRow, trailHead, "Harpers Ferry, West Virginia, USA");
    datastore.setValue(treeRow, elevationFormatted, "147 m");
    datastore.setValue(treeRow, elevationSort, 147);
    datastore.setValue(treeRow, accessibleByTrain, true);

    // associate my List View datastore with a treeview component
    treeView = new TreeView(datastore);

    // define first visible column - associated to first column in datastore
    vertical = treeView.appendColumn();
    // define its title
    vertical.setTitle("Place");
    // mark it as text (could be spinner, combo, image...)
    cellRendererText = new CellRendererText(vertical);
    // mark it as pango-formattable (could be editable also)
    cellRendererText.setMarkup(placeName);

    // define second visible column - associated with second column in datastore
    vertical = treeView.appendColumn();
    vertical.setTitle("Nearest town");
    cellRendererText = new CellRendererText(vertical);
    cellRendererText.setText(trailHead);
    cellRendererText.setAlignment(0.0f, 0.0f);
    vertical.setExpand(true);

    // define third visible column - associated to third column in datastore
    vertical = treeView.appendColumn();
    vertical.setTitle("Elevation");
    cellRendererText = new CellRendererText(vertical);
    cellRendererText.setText(elevationFormatted);
    cellRendererText.setAlignment(0.0f, 0.0f);

    // associate fourth colummn with third column sorting
    vertical.setSortColumn(elevationSort);
    vertical.emitClicked();

    // hanndle doble click
    treeView.connect(new TreeView.RowActivated() {
      public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
        final TreeIter row;
        final String place, height;

        row = datastore.getIter(path);

        place = datastore.getValue(row, trailHead);
        height = datastore.getValue(row, elevationFormatted);

        Gitmarks.this.dispatcher(source.getName(), "You want to go to " + place + " in order to climb to " + height);
      }
    });
    return treeView;
  }

  private TreeView makeTree(Pixbuf folderIcon) {

    final TreeView treeView;
    final TreeStore model;
    TreeIter treeRow, childRow;
    CellRendererText cellRendererText;
    TreeViewColumn vertical;

    final DataColumnPixbuf icon;
    final DataColumnString folderId;
    final DataColumnInteger folderIdSort;
    final DataColumnString folderName;

    DataColumn[] baseRow = new DataColumn[]{
        icon = new DataColumnPixbuf(),
        folderId = new DataColumnString(),
        folderIdSort = new DataColumnInteger(),
        folderName = new DataColumnString()
    };

    model = new TreeStore(baseRow);

    // initial sort
    //model.setSortColumn(folderIdSort, SortType.ASCENDING  );

    treeRow = model.appendRow();
    model.setValue(treeRow, icon, folderIcon);
    model.setValue(treeRow, folderId, Integer.toString(1));
    model.setValue(treeRow, folderIdSort, 1);
    model.setValue(treeRow, folderName, "Folder One");

    childRow = model.appendChild(treeRow);
    model.setValue(childRow, folderId, Integer.toString(11));
    model.setValue(childRow, folderIdSort, 11);
    model.setValue(childRow, folderName, "Folder One.One");

    childRow = model.appendChild(treeRow);
    model.setValue(childRow, folderId, Integer.toString(12));
    model.setValue(childRow, folderIdSort, 12);
    model.setValue(childRow, folderName, "Folder One.Two");

    treeRow = model.appendRow();
    model.setValue(treeRow, folderId, Integer.toString(2));
    model.setValue(treeRow, folderIdSort, 2);
    model.setValue(treeRow, folderName, "Folder Two");

    treeRow = model.appendRow();
    model.setValue(treeRow, folderId, Integer.toString(3));
    model.setValue(treeRow, folderIdSort, 3);
    model.setValue(treeRow, folderName, "Folder Three");

    childRow = model.appendChild(treeRow);
    model.setValue(childRow, folderId, Integer.toString(31));
    model.setValue(childRow, folderIdSort, 31);
    model.setValue(childRow, folderName, "Folder Three.One");

    /*
     * Now onto the treeView side. First we need the top level TreeView which
     * is the master Widget into which everything else is mixed.
     */
    treeView = new TreeView(model);


    CellRendererPixbuf cellRendererPixbuf;

    vertical = treeView.appendColumn();
    vertical.setTitle("ID");
    //cellRendererText = new CellRendererText(vertical);
    //cellRendererText.setText(folderId);
    //cellRendererText.setAlignment(0.0f, 0.0f);
    cellRendererPixbuf = new CellRendererPixbuf(vertical);
    cellRendererPixbuf.setPixbuf(icon);

    vertical.setSortColumn(folderIdSort);

    vertical = treeView.appendColumn();
    vertical.setTitle("Name");
    cellRendererText = new CellRendererText(vertical);
    cellRendererText.setText(folderName);
    cellRendererText.setAlignment(0.0f, 0.0f);

    treeView.connect(new TreeView.RowActivated() {
      public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
        final TreeIter row;
        final int id;
        final String name;

        row = model.getIter(path);

        id = model.getValue(row, folderIdSort);
        name = model.getValue(row, folderName);

        System.out.println("folder id " + id + " folder name " + name);
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