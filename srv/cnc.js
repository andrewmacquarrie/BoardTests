var callbacks, client, messageIdx, net, onConnectCallback, server, sys;
sys = require("sys");
net = require("net");
client = null;
messageIdx = 0;
callbacks = [];
onConnectCallback = null;
server = net.createServer(function(c) {
  c.setEncoding("utf8");
  c.on("connect", function() {
    if (client) {
      return c.end();
    }
    client = c;
    sys.puts("[CNC] DroidBot connected");
    if (onConnectCallback != null) {
      return onConnectCallback();
    }
  });
  c.on("end", function() {
    c.end();
    if (c !== client) {
      return;
    }
    sys.puts("[CNC] DroidBot disconnected");
    client = null;
    return messageIdx = 0;
  });
  return c.on("data", function(data) {
    var command, messageId;
    command = JSON.parse(data);
    messageId = command.__messageId;
    command.__messageId = void 0;
    if (callbacks[messageId] != null) {
      callbacks[messageId](command);
    }
    return callbacks[messageId] = null;
  });
});
exports.start = function() {
  server.listen(7000, "127.0.0.1");
  return sys.puts("[CNC] Listening on port 127.0.0.1:7000");
};
exports.onConnect = function(callback) {
  return onConnectCallback = callback;
};
exports.execute = function(command, replyCallback) {
  command.__messageId = messageIdx;
  client.write(JSON.stringify(command) + "\n");
  callbacks[messageIdx] = replyCallback;
  return messageIdx++;
};