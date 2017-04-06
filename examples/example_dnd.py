import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gdk, GdkPixbuf


class DragDropWindow(Gtk.Window):

    def __init__(self):
        Gtk.Window.__init__(self, title="Drag and Drop Demo")

        vbox = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=6)
        self.add(vbox)

        hbox = Gtk.Box(spacing=12)
        vbox.pack_start(hbox, True, True, 0)

        self.drop_area = DropArea()
        hbox.pack_start(self.drop_area, True, True, 0)

class DragSourceIconView(Gtk.IconView):

    def __init__(self):
        Gtk.IconView.__init__(self)


class DropArea(Gtk.Label):

    def __init__(self):
        Gtk.Label.__init__(self, "Drop something on me!")

        self.drag_dest_set(Gtk.DestDefaults.ALL, [], Gdk.DragAction.COPY)
        self.drag_dest_add_uri_targets()
        self.drag_dest_add_text_targets()
        self.drag_dest_add_image_targets()

        #self.drag_dest_set_target_list(list)

        self.connect("drag-data-received", self.on_drag_data_received)
        self.connect("drag-motion", self.on_drag_motion)
        self.connect("drag-drop", self.on_drag_drop)

    def on_drag_data_received(self, widget, drag_context, x,y, data,info, time):
        print("on_drag_data_received")

    def on_drag_motion(self, widget, context, x, y, time):
        print("odm")

    def on_drag_drop(self, widget, context, x, y, time):
        print("odd")

win = DragDropWindow()
win.connect("delete-event", Gtk.main_quit)
win.show_all()
Gtk.main()