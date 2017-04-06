import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio, Gdk, GdkPixbuf, GObject, Pango

import dispatcher
import bookmarks

from collections import deque

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
        # None = append to root of treeview
        deq = deque([Node(root_bookmark, None)])

        while len(deq) > 0:
            node = deq.popleft()
            #print(node.bookmark.bookmark_type + "*" + node.bookmark.guid + "*" + node.bookmark.title + "*", node.bookmark.uri)
            if node.bookmark.bookmark_type == "text/x-moz-place": # a bookmark
                continue
            if node.bookmark.bookmark_type == "text/x-moz-place-separator":
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

        folder_icon = Gtk.IconTheme.get_default().load_icon("folder", 24, 0)
        folder_icon_renderer = Gtk.CellRendererPixbuf()
        iconuri_renderer = Gtk.CellRendererPixbuf()

        tree_view = Gtk.TreeView()
        model = Gtk.ListStore(str, str, str, str, GdkPixbuf.Pixbuf, GObject.TYPE_PYOBJECT)

        if parent.children is not None: # empty folder
            for child in parent.children:
                if child.bookmark_type == "text/x-moz-place-container":
                    icon = folder_icon
                else:
                    icon = None

                model.append([child.title, child.uri, child.description, child.tags, icon, child])

        tree_view.set_model(model)

        title_renderer = Gtk.CellRendererText()
        title_renderer.props.ellipsize = Pango.EllipsizeMode.MIDDLE
        uri_renderer = Gtk.CellRendererText()
        uri_renderer.props.ellipsize = Pango.EllipsizeMode.MIDDLE
        description_renderer = Gtk.CellRendererText()
        description_renderer.props.ellipsize = Pango.EllipsizeMode.MIDDLE
        tags_renderer = Gtk.CellRendererText()

        title_column = Gtk.TreeViewColumn("Title")
        title_column.pack_start(folder_icon_renderer, False)
        title_column.pack_start(title_renderer, True)
        title_column.add_attribute(folder_icon_renderer, "pixbuf", 4)
        title_column.add_attribute(title_renderer, "text", 0)
        title_column.set_min_width(100)
        title_column.set_resizable(True)
        title_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        uri_column = Gtk.TreeViewColumn("URL", uri_renderer, text=1)
        uri_column.set_resizable(True)
        uri_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        description_column = Gtk.TreeViewColumn("Description", description_renderer, text = 2)
        description_column.set_resizable(True)
        description_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        tags_column = Gtk.TreeViewColumn("Tags", tags_renderer, text = 3)
        tags_column.set_resizable(True)
        tags_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        tree_view.append_column(title_column)
        tree_view.append_column(uri_column)
        tree_view.append_column(description_column)
        tree_view.append_column(tags_column)

        return tree_view
    
    def make_empty_list(self):

        tree_view = Gtk.TreeView()
        model = Gtk.TreeStore(str, str, GObject.TYPE_PYOBJECT)

        tree_view.set_model(model)
        title_renderer = Gtk.CellRendererText()
        uri_renderer = Gtk.CellRendererText()

        title_column = Gtk.TreeViewColumn("Title", title_renderer, text=0)
        uri_column = Gtk.TreeViewColumn("URL", uri_renderer, text=1)

        tree_view.append_column(title_column)
        tree_view.append_column(uri_column)

        return tree_view
