import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gdk, Gio

default_settings_schema_source = Gio.SettingsSchemaSource.get_default()
gitmarks_schema_source = Gio.SettingsSchemaSource.new_from_directory("/home/piero/code/gitmarks", default_settings_schema_source, True)
gitmarks_schema = gitmarks_schema_source.lookup("com.github.pierods.gitmarks", False)
gitmarks_settings = Gio.Settings.new_full(gitmarks_schema, None, None)

gitmarks_settings.set_string("test", "test")