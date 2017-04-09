import gi

gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio, Gdk, GdkPixbuf, GObject, Pango

import dispatcher
import bookmarks

from collections import deque

class HeaderBar:
    def __init__(self, gm_dispatcher: dispatcher.Dispatcher):
        self.gm_dispatcher = gm_dispatcher

    def make_headerbar(self, title):
        self.headerbar = Gtk.HeaderBar()
        self.headerbar.set_show_close_button(True)
        self.headerbar.props.title = title

        self.open_button = Gtk.Button().new_from_stock(Gtk.STOCK_OPEN)
        self.headerbar.pack_start(self.open_button)

        self.open_button.connect("clicked", self.open_msg_handler)

        self.profile_name = Gtk.Label()
        self.headerbar.pack_end(self.profile_name)

        return self.headerbar

    def open_msg_handler(self, widget):
        self.gm_dispatcher.dispatch("open_button", "open_file")

    def get_profile_label(self):
        return self.profile_name


class FolderTree:
    def on_drag_and_drop_received(self, widget, drag_context, x, y, data: Gtk.SelectionData, info, time):
        tp, pos = self.tree_view.get_dest_row_at_pos(x, y)
        tree_path, drop_position = self.tree_view.get_drag_dest_row()
        print(tp, "*", pos, "*", tree_path, "*", drop_position)

    def on_drag_and_drop_get(self, widget, context, data: Gtk.SelectionData, info, time):
        model, treeiter = self.tree_view.get_selection().get_selected()
        if treeiter is not None:
            self.dnd_source = model[treeiter][2]

    def __init__(self):
        self.tree_view = Gtk.TreeView()

        target_reorder = Gtk.TargetEntry().new("gitmarks-treeview", Gtk.TargetFlags.SAME_WIDGET, 132)
        target_dnd = Gtk.TargetEntry().new("firefox", Gtk.TargetFlags.OTHER_APP, 291)

        self.tree_view.enable_model_drag_source(Gdk.ModifierType.BUTTON1_MASK, [target_reorder], Gdk.DragAction.COPY)
        self.tree_view.enable_model_drag_dest([target_reorder], Gdk.DragAction.COPY)
        self.tree_view.connect("drag-data-received", self.on_drag_and_drop_received)
        self.tree_view.connect("drag-data-get", self.on_drag_and_drop_get)

        self.model = Gtk.TreeStore(str, GdkPixbuf.Pixbuf, GObject.TYPE_PYOBJECT)
        self.icon = Gtk.IconTheme.get_default().load_icon("folder", 24, 0)

    def make_tree(self, root_bookmark: bookmarks.Bookmark):

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

    def populate_store(self, root_bookmark: bookmarks.Bookmark):
        # None = append to root of treeview
        deq = deque([Node(root_bookmark, None)])

        while len(deq) > 0:
            node = deq.popleft()
            # print(node.bookmark.bookmark_type + "*" + node.bookmark.guid + "*" + node.bookmark.title + "*", node.bookmark.uri)
            if node.bookmark.bookmark_type == "text/x-moz-place":  # a bookmark
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
    def make_list(self, parent: bookmarks.Bookmark):

        folder_icon = Gtk.IconTheme.get_default().load_icon("folder", 24, 0)
        folder_icon_renderer = Gtk.CellRendererPixbuf()
        iconuri_renderer = Gtk.CellRendererPixbuf()

        list_view = Gtk.TreeView()
        model = Gtk.ListStore(str, str, str, str, GdkPixbuf.Pixbuf, GObject.TYPE_PYOBJECT)

        if parent.children is not None:  # empty folder
            for child in parent.children:
                if child.bookmark_type == "text/x-moz-place-container":
                    icon = folder_icon
                else:
                    icon = None

                model.append([child.title, child.uri, child.description, child.tags, icon, child])

        list_view.set_model(model)

        title_renderer = Gtk.CellRendererText()
        # title_renderer.props.ellipsize = Pango.EllipsizeMode.MIDDLE
        uri_renderer = Gtk.CellRendererText()
        uri_renderer.props.ellipsize = Pango.EllipsizeMode.MIDDLE
        description_renderer = Gtk.CellRendererText()
        # description_renderer.props.ellipsize = Pango.EllipsizeMode.MIDDLE
        tags_renderer = Gtk.CellRendererText()

        title_column = Gtk.TreeViewColumn("Title")
        title_column.pack_start(folder_icon_renderer, False)
        title_column.pack_start(title_renderer, True)
        title_column.add_attribute(folder_icon_renderer, "pixbuf", 4)
        title_column.add_attribute(title_renderer, "text", 0)
        title_column.set_min_width(100)
        title_column.set_resizable(True)
        # title_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        uri_column = Gtk.TreeViewColumn("URL", uri_renderer, text=1)
        uri_column.set_resizable(True)
        uri_column.set_min_width(100)
        # uri_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        description_column = Gtk.TreeViewColumn("Description", description_renderer, text=2)
        description_column.set_resizable(True)
        # description_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        tags_column = Gtk.TreeViewColumn("Tags", tags_renderer, text=3)
        tags_column.set_resizable(True)
        # tags_column.set_sizing(Gtk.TreeViewColumnSizing.GROW_ONLY)

        list_view.append_column(title_column)
        list_view.append_column(uri_column)
        list_view.append_column(description_column)
        list_view.append_column(tags_column)

        list_view.set_name("bookmark-list")

        return list_view

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


class PropagateScrollbar(Gtk.ScrolledWindow):
    def do_get_preferred_width(self):
        child = self.get_child()
        return child.get_preferred_width()
