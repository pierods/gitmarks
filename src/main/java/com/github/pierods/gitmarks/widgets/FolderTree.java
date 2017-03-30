package com.github.pierods.gitmarks.widgets;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.CellRendererPixbuf;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnInteger;
import org.gnome.gtk.DataColumnPixbuf;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeStore;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;

/**
 * Created by piero on 3/23/17.
 */
public class FolderTree {

  public TreeView makeTree(Pixbuf folderIcon) {

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
    model.setValue(treeRow, icon, folderIcon);
    model.setValue(childRow, folderId, Integer.toString(11));
    model.setValue(childRow, folderIdSort, 11);
    model.setValue(childRow, folderName, "Folder One.One");

    childRow = model.appendChild(treeRow);
    model.setValue(treeRow, icon, folderIcon);
    model.setValue(childRow, folderId, Integer.toString(12));
    model.setValue(childRow, folderIdSort, 12);
    model.setValue(childRow, folderName, "Folder One.Two");

    treeRow = model.appendRow();
    model.setValue(treeRow, icon, folderIcon);
    model.setValue(treeRow, folderId, Integer.toString(2));
    model.setValue(treeRow, folderIdSort, 2);
    model.setValue(treeRow, folderName, "Folder Two");

    treeRow = model.appendRow();
    model.setValue(treeRow, icon, folderIcon);
    model.setValue(treeRow, folderId, Integer.toString(3));
    model.setValue(treeRow, folderIdSort, 3);
    model.setValue(treeRow, folderName, "Folder Three");

    childRow = model.appendChild(treeRow);
    model.setValue(treeRow, icon, folderIcon);
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
    vertical.setTitle("Name");
    cellRendererPixbuf = new CellRendererPixbuf(vertical);
    cellRendererPixbuf.setPixbuf(icon);
    cellRendererText = new CellRendererText(vertical);
    cellRendererText.setText(folderName);
    cellRendererText.setAlignment(0.0f, 0.0f);


    treeView.connect(new TreeView.RowActivated() {
      public void onRowActivated(TreeView source, TreePath path, TreeViewColumn vertical) {
        final TreeIter row;
        final String id;
        final String name;

        row = model.getIter(path);

        id = model.getValue(row, folderId);
        name = model.getValue(row, folderName);

        System.out.println("folder id " + id + " folder name " + name);
      }
    });
    return treeView;
  }

}
