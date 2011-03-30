require "rubygems"
require "bundler"

Bundler.require

get '/' do
  haml :index
end

get '/javascripts/:name.js' do
  content_type 'text/javascript', :charset => 'utf-8'
  CoffeeScript.compile File.read(File.expand_path("../coffee/#{params[:name]}.coffee", __FILE__))
end