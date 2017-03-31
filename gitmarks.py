import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

import widgets


main_window = Gtk.Window(title="Gitmarks")

vbox = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=3)
main_window.add(vbox)

toolbar = widgets.MyToolbar().make_toolbar()
toolbar.set_hexpand(True)
toolbar.show()

vbox.pack_start(toolbar, False, False, 0)
main_window .connect("delete-event", Gtk.main_quit)
main_window .show_all()
Gtk.main()