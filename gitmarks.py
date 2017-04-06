import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gdk

import dispatcher
import widgets
import bookmarks

class GMWindow(Gtk.Window):

    def __init__(self):

        h = Gdk.Screen().height()
        w = Gdk.Screen.width()

        Gtk.Window.__init__(self, title="Gitmarks")
        self.props.resizable = True
        self.props.default_width = w/5
        self.props.default_height = h
        self.status_bar = Gtk.Statusbar.new()
        self.tree_view = None
        self.item_list = None

        gm_dispatcher = dispatcher.Dispatcher()
        ofh = dispatcher.OpenFileHandler(self, self.status_bar, gm_dispatcher)
        handlers = [ofh]
        gm_dispatcher.load_handlers(handlers)

        self.vbox = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=3)
        self.hbox = Gtk.Paned(orientation=Gtk.Orientation.HORIZONTAL)

        self.add(self.vbox)

        header_bar = widgets.HeaderBar(gm_dispatcher).make_headerbar("Gitmarks")
        self.set_titlebar(header_bar)

        self.vbox.pack_start(self.hbox, True, True, 0)
        self.vbox.pack_end(self.status_bar, False, True, 0)

    def draw_tree(self, root_bookmark:bookmarks.Bookmark):
        if self.tree_view is not None:
            self.hbox.remove(self.tree_view)
            self.tree_view.destroy()

        if self.item_list is not None:
            self.hbox.remove(self.item_list)
            self.item_list.destroy()

        self.tree_view = widgets.FolderTree().make_tree(root_bookmark)
        self.hbox.pack1(self.tree_view, True, True)
        select_event = self.tree_view.get_selection()
        select_event.connect("changed", self.on_tree_selection_changed)
        self.tree_view.show()

        self.item_list = widgets.ItemList().make_empty_list()
        self.hbox.pack2(self.item_list, True, True)
        self.item_list.show()

        target = Gtk.TargetEntry().new("folder-tree", Gtk.TargetFlags.SAME_WIDGET, 0)

        self.tree_view.enable_model_drag_source(Gdk.ModifierType.BUTTON1_MASK, [target], Gdk.DragAction.MOVE)
        self.tree_view.enable_model_drag_dest([target], Gdk.DragAction.MOVE)
        self.tree_view.connect("drag-data-received", self.on_drag_and_drop)

    def on_tree_selection_changed(self, selection):
        model, treeiter = selection.get_selected()
        if treeiter is not None:
            folder = model[treeiter][2]
            if self.item_list is not None:
                self.hbox.remove(self.item_list)
                self.item_list.destroy()

            self.item_list = widgets.ItemList().make_list(folder)
            self.hbox.pack2(self.item_list, True, True)
            self.item_list.show()

    def on_drag_and_drop(self, widget, drag_context, x, y, data, info, time):
        print(widget)

win = GMWindow()
win.connect("delete-event", Gtk.main_quit)
win.show_all()
Gtk.main()