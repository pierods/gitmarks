package com.github.pierods.gitmarks;

/**
 * Created by piero on 3/21/17.
 */

import org.gnome.gdk.Event;
        import org.gnome.gtk.CellRendererText;
        import org.gnome.gtk.DataColumn;
        import org.gnome.gtk.DataColumnBoolean;
        import org.gnome.gtk.DataColumnInteger;
        import org.gnome.gtk.DataColumnString;
        import org.gnome.gtk.Gtk;
        import org.gnome.gtk.ListStore;
        import org.gnome.gtk.TreeIter;
        import org.gnome.gtk.TreePath;
        import org.gnome.gtk.TreeView;
        import org.gnome.gtk.TreeViewColumn;
        import org.gnome.gtk.Widget;
        import org.gnome.gtk.Window;

/**
 * A tutorial example of using a TreeView backed by a ListStore. In this
 * little demonstration, we list a number of famous hiking trails and how to
 * get to them.
 *
 * @author Andrew Cowie
 */
public class Gitmarks
{
    private TreeView makeTree() {

        final TreeView treeView;
        final ListStore model;
        TreeIter treeRow;
        CellRendererText cellRendererText;
        TreeViewColumn vertical;


        // ------------------------------------------------

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

        model = new ListStore(new DataColumn[] {
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
        model.setValue(treeRow, placeName, "Blue Mountains national park\n<small>(Six Foot Track)</small>");
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
        model.setValue(treeRow, placeName, "Appalachian Trail\n<small>(roller coaster section)</small>");
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

    public Gitmarks() {

        final Window window = new Window();

        window.add(makeTree());
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
