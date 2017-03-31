import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

class MyToolbar:

  def make_toolbar(self):
    self.toolbar = Gtk.Toolbar()
    self.toolbar.get_style_context().add_class(Gtk.STYLE_CLASS_PRIMARY_TOOLBAR)
    self.new_button = Gtk.ToolButton.new_from_stock(Gtk.STOCK_NEW)
    self.new_button.set_is_important(True)
    self.toolbar.insert(self.new_button, 0)
    self.new_button.show()
    self.new_button.set_action_name("app.new")

    return self.toolbar
