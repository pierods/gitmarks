import gi, os
gi.require_version('Secret', '1')
gi.require_version('Gtk', '3.0')
from gi.repository import Gio, GLib, Secret, Gtk

import time

class GitmarksSettings:

    def __init__(self):
        self.schema_git_username = Secret.Schema.new("com.github.pierods.gitmarks.git.username", Secret.SchemaFlags.NONE, {})
        self.schema_git_password = Secret.Schema.new("com.github.pierods.gitmarks.git.password", Secret.SchemaFlags.NONE, {})
        self.secret_service = None

    def create_gio_settings(self, path:str):
        if not os.path.isfile(path + "/gschemas.compiled"):
            raise FileNotFoundError
        default_settings_schema_source = Gio.SettingsSchemaSource.get_default()
        gitmarks_schema_source = Gio.SettingsSchemaSource.new_from_directory(path,default_settings_schema_source, True)
        gitmarks_schema = gitmarks_schema_source.lookup("com.github.pierods.gitmarks", False)
        gitmarks_settings = Gio.Settings.new_full(gitmarks_schema, None, None)

        return gitmarks_settings

    def get_app_dir(self):
        user_dir = GLib.get_user_data_dir()
        gm_dir = user_dir + "/gitmarks"

        if not os.path.isdir(gm_dir):
            os.makedirs(gm_dir)

        return gm_dir

    def get_repo_dir(self):

        app_dir = self.get_app_dir()
        repo_dir = app_dir + "/repo"

        if not os.path.isdir(repo_dir):
            os.makedirs(repo_dir)
        return repo_dir


    def get_callback(self, source_object, res, user_data):
        self.secret_service = Secret.Service.get_finish(res)
        self.done = True

    def lookup_callback(self, source_object, res, user_data):
        self.secret_value = self.secret_service.lookup_finish(res)
        self.done = True

    def get_git_credentials(self):
        if self.secret_service is None:
            self.done = False
            Secret.Service.get(Secret.ServiceFlags.OPEN_SESSION | Secret.ServiceFlags.LOAD_COLLECTIONS, None, self.get_callback, None)
            while not self.done:
                time.sleep(0.01)
                Gtk.main_iteration()
        self.done = False
        self.secret_service.lookup(self.schema_git_username, {}, None, self.lookup_callback, None)
        while not self.done:
            time.sleep(0.01)
            Gtk.main_iteration()
        username = self.secret_value.get_text()
        self.done = False
        self.secret_service.lookup(self.schema_git_password, {}, None, self.lookup_callback, None)
        while not self.done:
            time.sleep(0.01)
            Gtk.main_iteration()
        password = self.secret_value.get_text()

        return (username, password)

    def store_callback(self, source_object, res, user_data):
        stored = self.secret_service.store_finish(res)
        self.done = True

    def store_git_credentials(self, username:str, password:str):
        if self.secret_service is None:
            self.done = False
            Secret.Service.get(Secret.ServiceFlags.OPEN_SESSION | Secret.ServiceFlags.LOAD_COLLECTIONS, None, self.get_callback, None)
            while not self.done:
                time.sleep(0.01)
                Gtk.main_iteration()
        self.done = False
        self.secret_service.store(self.schema_git_username, {}, "default", "git_username", Secret.Value.new(username, len(username), "string"), None, self.store_callback, None)
        while not self.done:
            time.sleep(0.01)
            Gtk.main_iteration()
        self.done = False
        self.secret_service.store(self.schema_git_password, {}, "default", "git_password", Secret.Value.new(password, len(password), "string"), None, self.store_callback, None)
        while not self.done:
            time.sleep(0.01)
            Gtk.main_iteration()
