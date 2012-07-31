
class DataController < ApplicationController

  def feeds
    @data= params[:data].to_s
    @type= params[:type].to_s
    @webcam_num= params[:webcam_num].to_s
    @datatype= params[:datatype].to_s
    @feed = Datafeed.new
    @feed.data=@data
    @feed.type=@type
    @feed.datatype=@datatype
    @feed.webcam_num=@webcam_num
    @feed.process
  end

  def sats
  end

end
