import gi
gi.require_version('Gtk', '3.0')
gi.require_version('Secret', '1')

from gi.repository import Gtk, Gdk, Secret


import time

class GitCredentials:
    def __init__(self, uname, pw):
        self.uname = uname
        self.pw = pw

credentials = GitCredentials("pinco", "pallino")



def callback(source_object, res, user_data):
    print("fatto")


schema = Secret.Schema.new("com.github.pierods.gitcredentials", Secret.SchemaFlags.NONE, {})
#secret_service = Secret.Service.get(Secret.ServiceFlags.OPEN_SESSION | Secret.ServiceFlags.LOAD_COLLECTIONS, None, callback, None)


secret_service = Secret.Service.get_sync(Secret.ServiceFlags.OPEN_SESSION | Secret.ServiceFlags.LOAD_COLLECTIONS, None)
collections = secret_service.get_collections()
print(collections)
