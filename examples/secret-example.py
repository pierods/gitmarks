import gi
gi.require_version('Gtk', '3.0')
gi.require_version('Secret', '1')

from gi.repository import Gtk, Gdk, Secret



schema_git_username = Secret.Schema.new("com.github.pierods.gitmarks.git.username", Secret.SchemaFlags.NONE, {})
schema_git_password = Secret.Schema.new("com.github.pierods.gitmarks.git.password", Secret.SchemaFlags.NONE, {})

def store_git_credentials(secret_service:Secret.Service):
    secret_service.store_sync(schema_git_username, {}, "default", "git_username", Secret.Value.new("pinco", len("pinco"), "string"), None)
    secret_service.store_sync(schema_git_password, {}, "default", "git_password", Secret.Value.new("pallino", len("pallino"), "string"), None)
    print("secret stored")



def get_callback(source_object, res, user_data):
    secret_service = Secret.Service.get_finish(res)
    print("gotten secret service")
    #store_git_credentials(secret_service)

    username = secret_service.lookup_sync(schema_git_username, {}, None)
    print(username.get(), username.get_text(), username.get_content_type())



Secret.Service.get(Secret.ServiceFlags.OPEN_SESSION | Secret.ServiceFlags.LOAD_COLLECTIONS, None, get_callback, None)


Gtk.main()