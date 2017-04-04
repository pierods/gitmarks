import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

import dispatcher
import widgets

class GMWindow(Gtk.Window):

    def __init__(self):

        Gtk.Window.__init__(self, title="Gitmarks")
        self.status_bar = Gtk.Statusbar.new()

        self.tree_view = None

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

    def draw_tree(self):
        if not self.tree_view == None:
            self.hbox.remove(self.tree_view)
            self.tree_view.destroy()

        self.tree_view = widgets.FolderTree().make_tree()
        self.hbox.pack_start(self.tree_view, True, True, 0)
        self.tree_view.show()


win = GMWindow()
win.connect("delete-event", Gtk.main_quit)
win.show_all()
Gtk.main()