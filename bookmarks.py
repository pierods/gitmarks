import json

from pprint import pprint

class Bookmark:

  def __init__(self):
    self.type = ''
    self.guid = ''
    self.id = 0
    self.title = ''
    self.index = 0
    this.dateAdded = None
    this.lastModified = None
    uri = None
    iconuri = None
    self.children = []


class FirefoxImporter:


  def import_firefox_json(self, json_file):
    json_file = "/home/piero/temp/bookmarks-2017-03-22.json"

    json_data = open(json_file)
    data = json.load(json_data)
    json_data.close()
    pprint(data)




FirefoxImporter().import_firefox_json("")