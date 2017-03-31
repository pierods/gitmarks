import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio

class Dispatcher:

  def __init__(self, handlers):
    self.handler_map = dict()
    for handler in handlers:
      self.handler_map[handler.command_name()] = handler

  def dispatch(self, source, command):
    self.handler_map[command].handle(source, command)

class OpenFileHandler:

  def __init__(self, parent):
    self.parent = parent

  def command_name(self):
    return "open_file"

  def handle(self, source, command):
    print(source, command)
    dialog = Gtk.FileChooserDialog("Please choose a file", self.parent, Gtk.FileChooserAction.OPEN, (Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL, Gtk.STOCK_OPEN, Gtk.ResponseType.OK))
    #self.parent.add_filters(dialog)
    response = dialog.run()
    if response == Gtk.ResponseType.OK:
        print("Open clicked")
        print("File selected: " + dialog.get_filename())
    elif response == Gtk.ResponseType.CANCEL:
        print("Cancel clicked")

    dialog.destroy()

