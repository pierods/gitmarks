import json
import sys

from pprint import pprint

class Bookmark:

  def __init__(self, bookmark_type, guid, id, tags, title, description, index, date_added, last_modified, uri, iconuri, children):
    self.bookmark_type = bookmark_type
    self.guid = guid
    self.id = id
    self.tags = tags
    self.title = title
    self.description = description
    self.index = index
    self.date_added = date_added
    self.last_modified = last_modified
    self.uri = uri
    self. iconuri = iconuri
    self.children = children

class FirefoxImporter:


    def object_decoder(self, obj):
        if not 'type'in obj: # annos - store
            self.annos = obj
            return None
        if 'annos' in obj and self.annos['name'] == "bookmarkProperties/description":
            description = self.annos['value']
        else:
            description = None
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
        if 'tags' not in obj:
                tags = None
        else:
            tags = obj['tags']

        return Bookmark(obj['type'], obj['guid'], obj['id'], tags, obj['title'], description, obj['index'], obj['dateAdded'], obj['lastModified'], uri, iconuri, children)


    def import_firefox_json(self, json_file):
        json_data = open(json_file)
        bookmark_root = json.load(json_data, object_hook=self.object_decoder)
        json_data.close()
        return bookmark_root