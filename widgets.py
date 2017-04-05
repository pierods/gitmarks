import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio, Gdk, GdkPixbuf, GObject

import dispatcher
import bookmarks

from collections import  deque

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
        self.model = Gtk.TreeStore(str, GdkPixbuf.Pixbuf, GObject.TYPE_PYOBJECT)
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
        deq = deque([Node(root_bookmark, None)])

        while len(deq) > 0:
            node = deq.popleft()
            print(node.bookmark.bookmark_type + "*" + node.bookmark.guid + "*" + node.bookmark.title)
            if node.bookmark.bookmark_type == "text/x-moz-place": # a bookmark
                continue
            if node.bookmark.guid == "root________":
                name = "root"
            else:
                name = node.bookmark.title
            last_added = self.model.append(node.parent, [name, self.icon, node.bookmark])
            if node.bookmark.children is not None:
                for child in node.bookmark.children:
                    deq.append(Node(child, last_added))


class Node:
    def __init__(self, bookmark, parent):
            self.bookmark = bookmark
            self.parent = parent

class ItemList:

    def make_list(self, parent:bookmarks.Bookmark):

        tree_view = Gtk.TreeView()
        model = Gtk.TreeStore(str, str, GObject.TYPE_PYOBJECT)

        for child in parent.children:
            model.append(child.title, child.uri, child)

        tree_view.set_model(model)
        title_renderer = Gtk.CellRendererText()
        uri_renderer = Gtk.CellRendererText()

        title_column = Gtk.TreeViewColumn("Title", title_renderer, text=0)
        uri_column = Gtk.TreeViewColumn("URL", uri_renderer, text=0)

        tree_view.append_column(title_column)
        tree_view.append_column(uri_column)

        return tree_view
    
    def make_empty_list(self):

        tree_view = Gtk.TreeView()
        model = Gtk.TreeStore(str, str, GObject.TYPE_PYOBJECT)

        tree_view.set_model(model)
        title_renderer = Gtk.CellRendererText()
        uri_renderer = Gtk.CellRendererText()

        title_column = Gtk.TreeViewColumn("Title", title_renderer, text=0)
        uri_column = Gtk.TreeViewColumn("URL", uri_renderer, text=0)

        tree_view.append_column(title_column)
        tree_view.append_column(uri_column)

        return tree_view

