import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio, GLib

import os



dir = GLib.get_user_data_dir()
print(dir)


gm_dir = dir + "/gitmarks"

if not os.path.isdir(gm_dir):
    os.makedirs(dir + "/gitmarks")

if not os.path.isdir(gm_dir + "/profiles"):
    os.makedirs(gm_dir + "/profiles")




