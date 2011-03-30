var cnc, server, sys, ws;
sys = require("sys");
cnc = require("./cnc");
ws = require("./vendor/websocket-server/lib/ws/server");
server = ws.createServer();
server.addListener("connection", function(connection) {
  return connection.addListener("message", function(msg) {
    return server.send(msg);
  });
});
server.listen(8080);
cnc.onConnect(function() {
  return cnc.execute({
    command: "dance",
    "for": 10
  }, function(resp) {
    return sys.puts("command executed: " + resp.ok);
  });
});
cnc.start();