sys = require "sys"
net = require "net"

client = null
messageIdx = 0
callbacks = []
onConnectCallback = null

server = net.createServer (c) ->
	c.setEncoding "utf8"
	c.on "connect", ->
		return c.end() if client
		client = c
		sys.puts "[CNC] DroidBot connected"
		onConnectCallback() if onConnectCallback?
		
	c.on "end", ->
		c.end()
		return if c != client
		sys.puts "[CNC] DroidBot disconnected"
		client = null
		messageIdx = 0
	
	c.on "data", (data) ->
		command = JSON.parse(data)
		messageId = command.__messageId
		command.__messageId = undefined
		callbacks[messageId](command) if callbacks[messageId]?
		callbacks[messageId] = null
	
exports.start = ->
	server.listen 7000, "127.0.0.1"
	sys.puts "[CNC] Listening on port 127.0.0.1:7000"

exports.onConnect = (callback) ->
	onConnectCallback = callback
	
exports.execute = (command, replyCallback) ->
	command.__messageId = messageIdx
	client.write JSON.stringify(command) + "\n"
	callbacks[messageIdx] = replyCallback
	messageIdx++
