import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio

import dispatcher

class MyHeaderBar:

  def __init__(self, dispatcher:dispatcher.Dispatcher):
    self.dispatcher = dispatcher

  def make_headerbar(self, title):
    self.headerbar = Gtk.HeaderBar()
    self.headerbar.set_show_close_button(True)
    self.headerbar.props.title = title

    self.open_button = Gtk.Button().new_from_stock(Gtk.STOCK_OPEN)
    self.headerbar.pack_start(self.open_button)

    self.open_button.connect("clicked", self.open_msg_handler)
    return self.headerbar

  def open_msg_handler(self, widget):
    self.dispatcher.dispatch("open_button", "open_file")