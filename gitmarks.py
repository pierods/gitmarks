import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

import dispatcher
import widgets

main_window = Gtk.Window(title="Gitmarks")
status_bar = Gtk.Statusbar.new()

ofh = dispatcher.OpenFileHandler(main_window, status_bar)
handlers = [ofh]
dispatcher = dispatcher.Dispatcher(handlers)

vbox = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=3)
main_window.add(vbox)

header_bar = widgets.MyHeaderBar(dispatcher).make_headerbar("Gitmarks")
main_window.set_titlebar(header_bar)
vbox.pack_end(status_bar, False, True, 0)
main_window .connect("delete-event", Gtk.main_quit)
main_window .show_all()
Gtk.main()