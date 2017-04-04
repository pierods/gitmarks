import json
import sys

from pprint import pprint

class Bookmark:

  def __init__(self, bookmark_type, guid, id, title, index, date_added, last_modified, uri, iconuri, children):
    self.bookmark_type = bookmark_type
    self.guid = guid
    self.id = id
    self.title = title
    self.index = index
    self.date_added = date_added
    self.last_modified = last_modified
    self.uri = uri
    self. iconuri = iconuri
    self.children = children

class FirefoxImporter:

    def object_decoder(self, obj):
        if not 'type'in obj: # Places/SmartBookmark case; only works on firefox, but I'll leave it as an empty folder
            return obj
        if not 'iconuri' in obj:
            iconuri = None
        else:
            iconuri = obj['iconuri']
        if not 'children' in obj:
            children = None
        else:
            children = obj['children']
        if not 'uri' in obj:
            uri = None
        else:
            uri = obj['uri']
        return Bookmark(obj['type'], obj['guid'], obj['id'], obj['title'], obj['index'], obj['dateAdded'], obj['lastModified'], uri, iconuri, children)


    def import_firefox_json(self, json_file):
        json_data = open(json_file)
        self.bookmark_root = json.load(json_data, object_hook=self.object_decoder)
        json_data.close()