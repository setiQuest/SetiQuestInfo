class HomeController < ApplicationController

  layout = :obsinfo

  def index
    command = params[:command]

    filename = "public/obsinfo.html";

    if(command == "obsinfo")
       filename = "public/obsinfo.html";
    elsif(command == "feedinfo")
       filename = "public/datafeeds.html";
    elsif(command == "obsinfo2")
       filename = "public/obsinfo2.html";
    elsif(command == "cam1")
       filename = "public/cam1.html";
    elsif(command == "cam2")
       filename = "public/cam2.html";
    elsif(command == "keplerfield")
       filename = "public/keplerfield.html";
    elsif(command == "Kepler_FOV_hiRes-full")
       filename = "public/Kepler_FOV_hiRes-full.html";
    elsif(command == "MilkyWay-Kepler-cRoberts-1-full")
       filename = "public/MilkyWay-Kepler-cRoberts-1-full.html";
    end 

    @weather_content = File.open("public/weather.html", "r").read.html_safe
    @page_content = File.open(filename, "r").read.html_safe

  end

  def xy
    @x= params[:x].to_f
    @y= params[:y].to_f
    @xxyy = XY.new
    @xxyy.x=@x
    @xxyy.y=@y
  end 

  private
    def what_layout
      command = params[:command]
      if(command == "obsinfo")
        return "obsinfo";
      else
        return "application";
      end
    end
end
