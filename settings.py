import gi, os
gi.require_version('Gtk', '3.0')
from gi.repository import Gio, GLib

class GioSettings:
    def create_settings(self, path:str):
        if not os.path.isfile(path + "/gschemas.compiled"):
            raise FileNotFoundError
        default_settings_schema_source = Gio.SettingsSchemaSource.get_default()
        gitmarks_schema_source = Gio.SettingsSchemaSource.new_from_directory(path,default_settings_schema_source, True)
        gitmarks_schema = gitmarks_schema_source.lookup("com.github.pierods.gitmarks", False)
        gitmarks_settings = Gio.Settings.new_full(gitmarks_schema, None, None)

        return gitmarks_settings

