import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gio
import os

def create_settings(settings_dir:str):

    if not os.path.isfile(settings_dir + "/gschemas.compiled"):
        raise FileNotFoundError
    default_settings_schema_source = Gio.SettingsSchemaSource.get_default()
    gitmarks_schema_source = Gio.SettingsSchemaSource.new_from_directory("/home/piero/code/gitmarks", default_settings_schema_source, True)
    gitmarks_schema = gitmarks_schema_source.lookup("com.github.pierods.gitmarks", False)
    gitmarks_settings = Gio.Settings.new_full(gitmarks_schema, None, None)

    return gitmarks_settings

gitmarks_settings = create_settings("/home/piero/code/gitmarks")

gitmarks_settings.set_string("profiles", "")

#print(gitmarks_settings.get_string("profiles"))