###!/usr/bin/env ruby

require 'rubygems'
require 'nokogiri'
require 'open-uri'
require 'tzinfo'
require 'time'
require 'date'

class WeatherReport

  #####################################
  # Static method to create the report.
  #####################################
  def self.createReport(weatherFilename, outputName)

    status = Nokogiri::HTML(open(weatherFilename));
  
    datetime = "";
    windspeedmax = "0.0";
    windspeedavg = "0.0";
    winddiravg = "0.0";
    tempc = "0.0";

    status.xpath('//weather_report').each do |node|
      datetime = node.xpath('datetime').inner_text;
    end

    status.xpath('//weather_report/data').each do |node|
      name = node.xpath('name').inner_text;
      if(name.index("WindSpeedMax"))
        windspeedmax = node.xpath('value').inner_text;  
      elsif(name.index("WindSpeedAvg"))
        windspeedavg = node.xpath('value').inner_text;  
      elsif(name.index("OutsideTemp"))
        tempc = node.xpath('value').inner_text;  
      end
    end

   tempf = (((tempc.to_f*9.0)/5.0)+32.0).to_s;

   s = '<TABLE class="obs"><tr><td align="center" colspan="2">' + "\n";
   s += "Wind " + windspeedavg + "mph<br>\n";
   
   if(windspeedmax.to_f > 30.0)
     s += '<font color="#FF0000"><b>Gusts to ' + windspeedmax + "mph</b><br></font>\n";
   else
     s += '<font color="#0000FF">Gusts to ' + windspeedmax + "mph<br></font>\n";
   end
   s +=  "%.1f" % tempf + "&deg;F&nbsp;/&nbsp;" + "%.1f" % tempc + "&deg;C";
   s += "</td></tr>\n";

   doc = Nokogiri::HTML( open("http://forecast.weather.gov/MapClick.php?CityName=Hat+Creek&state=CA&site=STO&textField1=40.8306&textField2=-121.512&e=0&TextType=2"))
   tds = doc.css('td').map

   count = 0;
   tds.each do |td|
     if(count < 3 && (td.to_s.index('width="55"') != nil))
       if(count > 0)
         if(count == 1) 
           s += '<tr><td align="center" valign="top" width="50%">' + "\n";
         else
           s += '<td align="center" valign="top" width="50%">' + "\n";
         end
         #text = td.to_s.gsub("/images", "http://forecast.weather.gov/images").gsub('<td width="11%">', "").gsub('</td>', '').gsub('<br><br>', '<br>').gsub("<br></b><br>", "</b><br>") + "\n";
         text = td.to_s.gsub("/images", "http://forecast.weather.gov/images").gsub('<td width="11%">', "").gsub('</td>', '') + "\n";
         text = text.gsub('<img', '<a href="http://forecast.weather.gov/MapClick.php?CityName=Hat+Creek&state=CA&site=STO&textField1=40.8306&textField2=-121.512&e=0&TextType=2" target="_other"><img');
         text = text.gsub('"><br>', '"></a><br>');
         s += text;
         s += "</td>\n";
       end
       count = count + 1;
     end
   end

   s += "</tr><tr><td align=\"center\" colspan=\"2\" style=\"border-bottom: 0px;\">" + datetime + "</td></tr>\n";
   s += "<tr>";
   s += "<td align=\"center\" colspan=\"1\" style=\"border-bottom: 0px; padding: 0px;\"><a href=\"http://forecast.weather.gov/MapClick.php?CityName=Hat+Creek&state=CA&site=STO&textField1=40.8306&textField2=-121.512&e=0&TextType=2\" target=\"_other\"><img src=\"http://www.crh.noaa.gov/images/lsx/images/noaa_logo.jpg\" width=\"64\" /></a></td>";
   s+= "<td align=\"center\" colspan=\"1\" style=\"border-bottom: 0px; padding: 0px;\"><a href=\"http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=KCAHATCR2\" target=\"_other\"><img src=\"http://icons-ak.wxug.com/i/wu/wuLogoUrl.png\" width=\"64\" /></a></td>"
   s+= "</tr>\n";
   s += "</table>\n";

      #outputName
      File.open(outputName, 'w') {|f| f.write(s) } ;
      return s;
  end

end

#puts WeatherReport.createReport("../../public/feeds/weather.xml",
#                            "../../public/weather.html") ;
