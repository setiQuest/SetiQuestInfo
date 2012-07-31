class ApplicationController < ActionController::Base

def dataFeed
    @data= params[:data].to_s
    @type= params[:type].to_s
    @feed = DataFeed.new
    @feed.data=@data
    @feed.type=@type
  end 

end
