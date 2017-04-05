import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio, Gdk, GdkPixbuf

import dispatcher
import bookmarks

class HeaderBar:

  def __init__(self, gm_dispatcher:dispatcher.Dispatcher):
    self.gm_dispatcher = gm_dispatcher

  def make_headerbar(self, title):
    self.headerbar = Gtk.HeaderBar()
    self.headerbar.set_show_close_button(True)
    self.headerbar.props.title = title

    self.open_button = Gtk.Button().new_from_stock(Gtk.STOCK_OPEN)
    self.headerbar.pack_start(self.open_button)

    self.open_button.connect("clicked", self.open_msg_handler)
    return self.headerbar

  def open_msg_handler(self, widget):
    self.gm_dispatcher.dispatch("open_button", "open_file")


class FolderTree:

    def __init__(self):
        self.tree_view = Gtk.TreeView()
        self.model = Gtk.TreeStore(str, GdkPixbuf.Pixbuf)
        self.icon = Gtk.IconTheme.get_default().load_icon("folder", 24, 0)


    def make_tree(self, root_bookmark:bookmarks.Bookmark):

        self.populate_store(root_bookmark)
        self.tree_view.set_model(self.model)

        self.folder_name_renderer = Gtk.CellRendererText()
        self.folder_icon_renderer = Gtk.CellRendererPixbuf()

        self.column = Gtk.TreeViewColumn("Folder Name", self.folder_name_renderer, text=0)
        self.column = Gtk.TreeViewColumn("Folder Name")
        self.column.pack_start(self.folder_icon_renderer, False)
        self.column.pack_start(self.folder_name_renderer, True)
        self.column.add_attribute(self.folder_icon_renderer, "pixbuf", 1)
        self.column.add_attribute(self.folder_name_renderer, "text", 0)

        self.tree_view.append_column(self.column)

        return self.tree_view

    def populate_store(self, root_bookmark:bookmarks.Bookmark):
        # None = append to root
        #last_added = self.model.append(None, ["Folder 1", self.icon])
        #self.model.append(last_added, ["Folder One.One", self.icon])
        if root_bookmark.bookmark_type == "text/x-moz-place-container":
            name = root_bookmark.guid
        else:
            name = root_bookmark.title
        last_added = self.model.append(None, [name, self.icon])
