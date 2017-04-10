class Dispatcher:

  def load_handlers(self, handlers):
    self.handler_map = dict()
    for handler in handlers:
      self.handler_map[handler.command_name()] = handler

  def dispatch(self, source, command):
    self.handler_map[command].handle(source, command)
