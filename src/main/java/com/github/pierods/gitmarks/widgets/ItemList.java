package com.github.pierods.gitmarks.widgets;

import com.github.pierods.gitmarks.Gitmarks.Dispatcher;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnBoolean;
import org.gnome.gtk.DataColumnInteger;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;

/**
 * Created by piero on 3/23/17.
 */
public class ItemList {

  public TreeView makeItemList(Dispatcher dispatcher) {

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

        dispatcher.dispatch(source.getName(), "You want to go to " + place + " in order to climb to " + height);
      }
    });
    return treeView;
  }


}
