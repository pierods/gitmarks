import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

import dispatcher
import widgets
import bookmarks

class GMWindow(Gtk.Window):

    def __init__(self):

        Gtk.Window.__init__(self, title="Gitmarks")
        self.props.window_position = Gtk.WindowPosition.CENTER
        self.props.default_width = 400
        self.props.default_height = 400
        self.status_bar = Gtk.Statusbar.new()

        self.tree_view = None
        self.item_list = None

        gm_dispatcher = dispatcher.Dispatcher()
        ofh = dispatcher.OpenFileHandler(self, self.status_bar, gm_dispatcher)
        handlers = [ofh]
        gm_dispatcher.load_handlers(handlers)

        self.vbox = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=3)
        self.hbox = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=3)

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
        self.hbox.pack_start(self.tree_view, True, True, 0)
        select_event = self.tree_view.get_selection()
        select_event.connect("changed", self.on_tree_selection_changed)
        self.tree_view.show()

        self.item_list = widgets.ItemList().make_empty_list()
        self.hbox.pack_start(self.item_list, True, True, 0)
        self.item_list.show()

    def on_tree_selection_changed(self, selection):
        model, treeiter = selection.get_selected()
        if treeiter is not None:
            folder = model[treeiter][2]
            if self.item_list is not None:
                self.hbox.remove(self.item_list)
                self.item_list.destroy()

            self.item_list = widgets.ItemList().make_list(folder)
            self.hbox.pack_start(self.item_list, True, True, 0)
            self.item_list.show()


win = GMWindow()
win.connect("delete-event", Gtk.main_quit)
win.show_all()
Gtk.main()