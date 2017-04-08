import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gio, GLib

import settings
import  subprocess

repo_dir = settings.GitmarksSettings().get_repo_dir()

git_status = "cd " + repo_dir + ";" + "git status"

#git_status = "echo a; echo b"

process = subprocess.Popen(git_status, stdout=subprocess.PIPE, shell=True)
output = process.communicate()[0].strip()

print(output)