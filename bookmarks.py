import json

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

class FirefoxMarshaller:

    def import_firefox_json(self, json_file):
        json_data = open(json_file)
        bookmark_root = json.load(json_data, object_hook=self.decode)
        json_data.close()
        return bookmark_root

    def export_to_json(self, bookmark_root:Bookmark):
        return json.dumps(bookmark_root, cls=Encoder)

    def decode(self, obj):
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
        if obj['type'] == "text/x-moz-place-separator":
            title = "--------------------------------------------------------"
        else:
            title = obj['title']
        return Bookmark(obj['type'], obj['guid'], obj['id'], tags, title, description, obj['index'], obj['dateAdded'], obj['lastModified'], uri, iconuri, children)

class Encoder(json.JSONEncoder):
    def default(self, o):
        return o.__dict__