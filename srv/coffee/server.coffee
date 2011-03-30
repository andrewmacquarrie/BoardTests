sys = require "sys"

cnc = require "./cnc"
ws = require "./vendor/websocket-server/lib/ws/server"

server = ws.createServer()
server.addListener "connection", (connection) -> 
  connection.addListener "message", (msg) ->
    server.send msg

server.listen(8080);

cnc.onConnect ->
	cnc.execute {command: "dance", for: 10}, (resp) ->
		sys.puts "command executed: #{resp.ok}"

cnc.start()