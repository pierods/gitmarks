import gi

gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gdk, GLib

import dispatcher
import widgets
import bookmarks
import settings
import git
import json
import sys


class OpenFileHandler:
    def __init__(self, parent, status_bar, gm_dispatcher: dispatcher.Dispatcher):
        self.parent = parent
        self.status_bar = status_bar
        self.gm_dispatcher = gm_dispatcher
        self.status_bar_context_id = status_bar.get_context_id("OpenFileHandlerContext")

    def command_name(self):
        return "open_file"

    def handle(self, source, command):
        dialog = Gtk.FileChooserDialog("Please choose a file", self.parent, Gtk.FileChooserAction.OPEN,
                                       (Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL, Gtk.STOCK_OPEN, Gtk.ResponseType.OK))
        # self.parent.add_filters(dialog)
        json_file = None
        response = dialog.run()
        if response == Gtk.ResponseType.OK:
            json_file = dialog.get_filename()
            self.status_bar.push(self.status_bar_context_id, "Opening " + json_file)
        elif response == Gtk.ResponseType.CANCEL:
            self.status_bar.push(self.status_bar_context_id, "No file selected")
        dialog.destroy()

        if not json_file == None:
            root_bookmark = bookmarks.FirefoxMarshaller().import_firefox_json(json_file)
            self.parent.draw_tree(root_bookmark)
            print(self.profile)


class GMWindow(Gtk.Window):
    def __init__(self):

        h = Gdk.Screen().height()
        w = Gdk.Screen.width()

        Gtk.Window.__init__(self, title="Gitmarks")
        self.props.resizable = True
        self.props.default_width = w / 3
        self.props.default_height = h
        self.status_bar = Gtk.Statusbar.new()
        self.tree_view = None
        self.item_list = None

        gm_dispatcher = dispatcher.Dispatcher()
        ofh = OpenFileHandler(self, self.status_bar, gm_dispatcher)
        handlers = [ofh]
        gm_dispatcher.load_handlers(handlers)

        self.vbox = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=3)
        self.hbox = Gtk.Paned(orientation=Gtk.Orientation.HORIZONTAL)

        self.add(self.vbox)

        hb_factory = widgets.HeaderBar(gm_dispatcher)
        header_bar = hb_factory.make_headerbar("Gitmarks")
        self.profile_label = hb_factory.get_profile_label()
        self.set_titlebar(header_bar)

        self.vbox.pack_start(self.hbox, True, True, 0)
        self.vbox.pack_end(self.status_bar, False, True, 0)
        self.connect("delete-event", Gtk.main_quit)

        self.gio_settings = settings.GitmarksSettings().create_gio_settings(GLib.get_current_dir())

        if self.init_settings() is None:
            sys.exit(0)

    def draw_tree(self, root_bookmark: bookmarks.Bookmark):
        if self.tree_view is not None:
            self.hbox.remove(self.tree_view)
            self.tree_view.destroy()

        if self.item_list is not None:
            self.hbox.remove(self.item_list)
            self.item_list.destroy()

        self.tree_view = widgets.FolderTree().make_tree(root_bookmark)
        self.tv_scroll = widgets.PropagateScrollbar(None, None)
        self.tv_scroll.set_propagate_natural_width(True)
        self.tv_scroll.set_policy(Gtk.PolicyType.AUTOMATIC, Gtk.PolicyType.AUTOMATIC)
        self.tv_scroll.add(self.tree_view)
        self.hbox.pack1(self.tv_scroll, True, False)
        select_event = self.tree_view.get_selection()
        select_event.connect("changed", self.on_tree_selection_changed)
        self.tv_scroll.show()
        self.tree_view.show()

        self.item_list = widgets.ItemList().make_empty_list()
        self.itemlist_scroll = widgets.PropagateScrollbar(None, None)
        self.itemlist_scroll.set_propagate_natural_width(True)
        self.itemlist_scroll.set_policy(Gtk.PolicyType.AUTOMATIC, Gtk.PolicyType.AUTOMATIC)
        self.itemlist_scroll.add(self.item_list)
        self.hbox.pack2(self.itemlist_scroll, True, True)
        self.itemlist_scroll.show()
        self.item_list.show()

    def on_tree_selection_changed(self, selection):
        model, treeiter = selection.get_selected()
        if treeiter is not None:
            folder = model[treeiter][2]
            if self.item_list is not None:
                self.hbox.remove(self.item_list)
                self.item_list.destroy()

            self.item_list = widgets.ItemList().make_list(folder)
            self.itemlist_scroll.add(self.item_list)
            self.hbox.pack2(self.itemlist_scroll, True, True)
            self.itemlist_scroll.show()
            self.item_list.show()

    def init_settings(self):

        default_profile = self.gio_settings.get_string("default-profile")
        if default_profile == "":
            dialog = Gtk.Dialog("No profile found", self, 0,
                                (Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL, Gtk.STOCK_OK, Gtk.ResponseType.OK))
            dialog.set_modal(True)
            dialog.set_default_size(350, 100)
            label = Gtk.Label.new("Enter a new profile name")
            entry = Gtk.Entry()
            content_area = dialog.get_content_area()
            content_area.add(label)
            content_area.add(entry)
            dialog.show_all()
            response = dialog.run()
            entered_text = entry.get_text()
            dialog.destroy()

            if response == Gtk.ResponseType.CANCEL:
                return None
            else:
                default_profile = entered_text
                json_profile = json.dump(default_profile)
                self.gio_settings.set_string("profiles", json_profile)
                self.gio_settings.set_string("default-profile", json_profile)
        else:
            default_profile = json.loads(default_profile)
            self.profile_label.set_text(default_profile)

        self.profile_label.set_text(default_profile)
        self.profile = default_profile

        repo_dir = settings.GitmarksSettings().get_repo_dir()
        self.git = git.Git(repo_dir)
        if not self.git.hasprofile(default_profile):
            self.git.create_profile(default_profile)
        return default_profile


win = GMWindow()
win.show_all()
Gtk.main()
