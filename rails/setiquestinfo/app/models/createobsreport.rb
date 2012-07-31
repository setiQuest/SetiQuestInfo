###!/usr/bin/env ruby

require 'rubygems'
require 'nokogiri'
require 'open-uri'
require 'tzinfo'
require 'time'
require 'date'

#########################################################
# Class to hold information about the state of one beam.
#########################################################
class Beam
  attr_accessor :number, :datetime, :state, :targetid, :targetname, :ra, :dec, :freqmhz;  

  def initialize(number, datetime, state, targetid, targetname, ra, dec, freqmhz)
    @number = number;
    @datetime = datetime;
    @state = state;
    @targetid = targetid;
    @targetname = targetname;
    @ra = ra;
    @dec = dec;
    @freqmhz  = freqmhz;

    if(@targetid == "")
      @targetid = "undef";
    end
    if(@targetname == "")
      @targetname = "undef";
    end
    if(@ra == "")
      @ra = "0.0";
    end
    if(@dec == "")
      @dec = "0.0";
    end
    if(@freqmhz == "")
      @freqmhz = "0.0";
    end

  end

  def isActive()
    if(@state == "inactive")
      return false;
    end
    return true
  end

  def to_s
    return "Beam[" + @number + "] = " + @state + "," + @datetime + "," + @targetid + "," + @targetname + "," + @ra + "," + @dec + "," +  freqmhz;
  end


  def to_html
    s = "";

    if(isActive())
      s  = "\t<tr>\n";
      s += "\t\t<td align=\"center\">" + @number + "</td>\n";
      s += "\t\t<td align=\"center\">" + @state + "</td>\n";
      s += "\t\t<td align=\"center\">" + @targetid + "</td>\n";
      s += "\t\t<td align=\"center\">" + @targetname + "</td>\n";
      s += "\t<td align=\"center\">" + ('%.04f' % @ra) + "</td>\n";
      s += "\t\t<td align=\"center\">" + ('%.04f' % @dec) + "</td>\n";
      s += "\t<td align=\"center\">" + ('%.06f' % @freqmhz) + "</td>\n";
      s += "\t</tr>\n";
    else
      s  = "\t<tr>\n";
      s += "\t\t<td align=\"center\">" + @number + "</td>\n";
      s += "\t\t<td align=\"center\" colspan=\"1\">" + @state + "</td>\n";
      s += "\t</tr>\n";
    end

    return s;
  end

end

#########################################################
# Class to hold the observation information
#########################################################
class Obs
  attr_accessor :datetime, :activityid, :state, :ra, :dec;

  def initialize(datetime, activityid, state, ra, dec)
    @datetime = datetime;
    @activityid = activityid;
    @state = state;
    @ra = ra;
    @dec = dec;

    if(@activityid == "")
      @activityid = "0";
    end
    if(@ra == "")
      @ra = "0.0";
    end
    if(@dec == "")
      @dec = "0.0";
    end
  end

  def to_s
    return "Obs: " + @datetime + "," + @activityid + "," + @state + "," + @ra + "," + @dec;
  end

  def isActive()
    if(@state.index('Idle') != nil)
      return false;
    end

    return true;
  end

  def to_html

    s = "";

    if(isActive())
      s  = "\t<tr>\n";
      s += "\t\t<td>" + @state + "</td>\n";
      s += "\t\t<td>" + @activityid + "</td>\n";
      s += "\t\t<td>" + ('%.04f' % @ra) + "</td>\n";
      s += "\t\t<td>" + ('%.04f' % @dec) + "</td>\n";
      s += "\t</tr>\n";
    else
      s  = "\t<tr>\n";
      s += "\t\t<td>No observations are running at this time.</td>\n";
      s += "\t</tr>\n";
    end

    return s;
  end

  def getTime
    d = DateTime.parse(@datetime.to_s);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return d.year.to_s + "-" + "%02d" % d.mon.to_s + "-" + "%02d" % d.mday.to_s + " " + "%02d" % d.hour.to_s + ":" + "%02d" % d.min.to_s;
  end

  def getTimeLocal
    d = DateTime.parse(@datetime.to_s);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return d.year.to_s + "-" + "%02d" % d.mon.to_s + "-" + "%02d" % d.mday.to_s + " " + "%02d" % d.hour.to_s + ":" + "%02d" % d.min.to_s;
  end

  def getTZ
    d = DateTime.parse(@datetime.to_s);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return tz.strftime("%Z", d);
  end

end

# Class to hold info about the observation schedule


####################################################################
# Class to hols info about the start or end info of an obs schedule.
####################################################################

class ObsStartEnd

  attr_accessor :year, :month, :day, :hour, :minute;

  def initialize()
  end

  def to_s
    return @year + "," + @month + "," + @day + "," + @hour + "," + @minute;
  end

  def date_to_s
    d = DateTime.new(@year.to_i, @month.to_i, @day.to_i, @hour.to_i, @minute.to_i, 0);
    return d.strftime("%a, %b %d - %I:%M %P");
  end

  def local_to_s
    d = DateTime.new(@year.to_i, @month.to_i, @day.to_i, @hour.to_i, @minute.to_i, 0);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return d.year.to_s + "-" + "%02d" % d.mon.to_s + "-" + "%02d" % d.mday.to_s + " " + "%02d" % d.hour.to_s + ":" + "%02d" % d.min.to_s;
  end

  def getTZ
    d = DateTime.new(@year.to_i, @month.to_i, @day.to_i, @hour.to_i, @minute.to_i, 0);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return tz.strftime("%Z", d);
  end

  def to_html(prefix, command, count, isCurrent)

    s = "";
   
    if((count == 0) && (command != nil))
      s += "<table><tbody>\n";
      s += "\t<tr> <td>\n";
      s += "\t<table class=\"obs\"><tbody>\n";
    end
    if(count == 1 && (command != nil))
      s += "<table class=\"obs\"><tbody>\n";
    end

    if(command != nil)
      s += "\t<tr>\n";
      s += "\t\t<th>" + command + "</th>\n";
      s += "<th align=\"center\">" + "GMT" + "</th>\n";
      s += "<th align=\"center\">" + getTZ() + "</th>\n";
      s += "\t</tr>\n";
    end

    s += "<tr>\n";
    s += "\t\t<td>" + prefix + "</td><td>" + date_to_s +  "</td><td><span style=\"color:blue\">" + local_to_s() + "</span></td>\n";

    #s += "\t</tr>\n";

    if((count == 0) && (command == nil))
      s += "\t</tbody></table></td>\n";
      if(isCurrent == true)
        s += "\t<td><img src=\"/images/search1.gif\" /></td>\n";
      else
        s += "\t<td><img src=\"/images/nervous.gif\" width=225 /></td>\n";
      end
      s += "\t</tr><tbody></table></tr>\n";
    else
      s += "\t</tr>\n";
    end

    return s;
  end

end

class SchedObs

  attr_accessor :current, :command, :start, :endd;

  ####################
  # Constructor
  ####################
  def initialize(current, command, start, endd)
    @current = current;
    @command = command;
    @start = start;
    @endd = endd;
  end
  
  def to_s
    s = "Obs: current=" + @current + "\n";
    s += "\tCommand: "  + @command + "\n";
    s += "\tStart  : " + @start.to_s + "\n";
    s += "\tEnd    : " + @endd.to_s;
    return s;
  end

  def isObserving
    if(@current == "yes")
      return true;
    end
    return false;
  end

  def getStartTime
    return d.strftime("%a, %b %d - %I:%M %P");
    #return @start.year + "-" + @start.month + "-" + @start.day + " " + @start.hour + ":" + @start.minute;
  end

  def getStartTimeLocal
    d = DateTime.new(@start.year.to_i, @start.month.to_i, @start.day.to_i, @start.hour.to_i, @start.minute.to_i, 0);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return d.year.to_s + "-" + "%02d" % d.mon.to_s + "-" + "%02d" % d.mday.to_s + " " + "%02d" % d.hour.to_s + ":" + "%02d" % d.min.to_s;
  end

  def getTZ
    d = DateTime.new(@start.year.to_i, @start.month.to_i, @start.day.to_i, @start.hour.to_i, @start.minute.to_i, 0);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return tz.strftime("%Z", d);
  end


  def getEndTime
    d = DateTime.new(@endd.year.to_i, @endd.month.to_i, @endd.day.to_i, @endd.hour.to_i, @endd.minute.to_i, 0);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return @endd.year + "-" + @endd.month + "-" + @endd.day + " " + @endd.hour + ":" + @endd.minute;
  end

  def getEndTimeLocal
    d = DateTime.new(@endd.year.to_i, @endd.month.to_i, @endd.day.to_i, @endd.hour.to_i, @endd.minute.to_i, 0);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return d.year.to_s + "-" + "%02d" % d.mon.to_s + "-" + "%02d" % d.mday.to_s + " " + "%02d" % d.hour.to_s + ":" + "%02d" % d.min.to_s;
  end

  def to_html(count)
    s = "";
    s += "\t<tr>\n";
    #s += "\t\t<td align=\"center\" colspan=2><span style=\"color:#008800\">" + @command + "</span></td>\n";
    s += "\t</tr>\n";
    if(@current == "yes")
      s += @start.to_html("Started: ", @command, count, false);
      s += @endd.to_html("Ends: ", nil, count, true);
    else
      s += @start.to_html("Starts: ", @command, count, false);
      s += @endd.to_html("Ends: ", nil, count, false);
    end
    return s;

  end

end


#########################################################
# Class to hole the schedule information.
#########################################################

class Schedule

  attr_accessor :datetime, :status, :obs;

  ####################
  # Constructor
  ####################
  def initialize(datetime, status)
    @datetime = datetime;
    @status = status;
    @obs = [];
  end

  def addObs(obs)
    @obs << obs;
  end

  ####################
  # Convert to string.
  ####################
  def to_s
    s = @datetime + "," + @status + "\n";
    @obs.each do |ob|
      s += ob.to_s + "\n";
    end
    return s;
  end

  def getTime
    d = DateTime.parse(@datetime);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return d.year.to_s + "-" + "%02d" % d.mon.to_s + "-" + "%02d" % d.mday.to_s + " " + "%02d" % d.hour.to_s + ":" + "%02d" % d.min.to_s;
  end

  def getTimeLocal
    d = DateTime.parse(@datetime);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return d.strftime("%a, %b %d - %I:%M %P");
    #return d.year.to_s + "-" + "%02d" % d.mon.to_s + "-" + "%02d" % d.mday.to_s + " " + "%02d" % d.hour.to_s + ":" + "%02d" % d.min.to_s;
  end

  def getTZ
    d = DateTime.parse(@datetime);
    tz = TZInfo::Timezone.get('America/Los_Angeles')
    d = tz.utc_to_local(d);
    return tz.strftime("%Z", d);
  end

  #################
  # Convert to HTML
  #################
  def to_html

    s  = "";
    #s  += "<h1>Under development - converting to GMT times, please be patient while debugging takes place.</h1>";
    #s += "<p>Report Created: " + @datetime + "</p>\n"; 
    #s += "<p>Status: " + @status + "</p>\n";
    #s += "<h1>ATA Observation Schedule</h1>\n";

    if(obs == nil || obs[0] == nil)
      s += "<p><b>The schedule is empty</b></p>\n";
      s += "<p><b>Stay tuned for updates!</b></p>\n";
      return s;
    end

    isObserving = obs[0].isObserving;

    if(isObserving == true)
      #s += "\n<div CLASS=roundedCorner id=\"updated_times_area2\" \">";
      #s += "<table border=0><tbody>\n";
      #s += "<tr><td align=\"left\">Ends:</td></tr><tr><td>" + obs[0].getEndTime + " GMT<br>";
      #s += "<span style=\"color:blue\">" + obs[0].getEndTimeLocal + " " + obs[0].getTZ() +"</span></td></tr>\n";
      #s += "</tbody></table>\n";
      #s += "</div>";
      s += "<center>";
      s += "<h2>Currently Observing from Schedule</h2>\n";
      #s += "<div id=\"search_animation\">";
      #s += "  <img src=\"/images/search1.gif\" />";
      #s += "</div>";

    else
      #s += "\n<div CLASS=roundedCorner id=\"updated_times_area2\" \">";
      #s += "<tr><td align=\"left\">Starts: </td></tr><tr><td>" + obs[0].getStartTime + " GMT<br>";
      #s += "<span style=\"color:blue\">" + obs[0].getStartTimeLocal + " " + obs[0].getTZ() +"</span></td></tr>\n";
      #s += "</tbody></table>\n";
      #s += "</div>";
      s += "<center>";
      s += "<br><p><b>Waiting for the next scheduled observation to begin</b></p>\n";
      #s += "<div id=\"search_animation\">";
      #s += "  <img src=\"/images/nervous.gif\" width=190 />";
      #s += "</div>";
    end

    #s+= "<br><p><u>Observation Schedule:</u></p>\n";

    #JR99s += "<table class=\"obs\"><tbody>\n";
    count = 0;
    @obs.each do |ob|
      s += ob.to_html(count) + "\n";
      #s += "<tr><td colspan=\"2\">&nbsp;</td></tr>\n";
      count = count + 1;
    end
    s += "</tbody></table>\n";
    s += "</center>";
    s+= "<br><br><p>NOTES:</p>\n";
    s += "<ul>\n";
    s += "\t<li>All times are GMT.</li>\n";
    s += "\t<li>Times are also displayed in Pacific Time (<span style=\"color:blue\">PST/PDT</span>) for the convenience of the ATA operators.</li>\n";
    s += "\t<li>The schedule is subject to frequent changes.</li>\n";
    s += "\t<li>The schedule is resampled and put on this server every 2 minutes.</li>\n";
    s += "</ul>\n";
    return s;
    
  end

end

class ObsReport

  #####################################
  # Static method to create the report.
  #####################################
  def self.createReport(sonataStatusFilename, sonataSchedFilename, outputName)

    #status = Nokogiri::HTML(open("/Users/jrichards/seti/temp/sonatastatus.xml"));
    status = Nokogiri::HTML(open(sonataStatusFilename));
  
    beams = []

    status.xpath('//info/beam').each do |node|
      beamnumber = node.attr('beamnumber');
      datetime = node.xpath('datetime').inner_text;
      state = node.xpath('state').inner_text;
      targetid = node.xpath('targetid').inner_text;
      targetname = node.xpath('targetname').inner_text;
      ra = node.xpath('ra').inner_text;
      dec = node.xpath('dec').inner_text;
      freqmhz = node.xpath('freqmhz').inner_text;
  
      beam = Beam.new(beamnumber, datetime, state, targetid, targetname, ra, dec, freqmhz);
      beams << beam;
    end

    # Read in the onservation state
    obs = "";
    status.xpath('//info/obs').each do |node|
      datetime = node.xpath('datetime').inner_text;
      activityid = node.xpath('activityid').inner_text;
      state = node.xpath('state').inner_text;
      ra = node.xpath('primarybeamra').inner_text;
      dec = node.xpath('primarybeamdec').inner_text;

      obs = Obs.new(datetime, activityid, state, ra, dec);
    end

    # Read in the schedule
    schedule = Nokogiri::HTML(open(sonataSchedFilename));
  
    sched = "";
    schedule.xpath('//schedule').each do |schednode|
      datetime = schednode.xpath('datetime').inner_text;
      status = schednode.xpath('status').inner_text;
  
      sched = Schedule.new(datetime, status);
  
      obsnode = schednode.xpath('obs');
  
      obsnode.each do |o|
      
        current = o.attr('current');
        command = o.xpath('command').inner_text;
  
        startnode = o.xpath('start');
        endnode = o.xpath('end');
  
        obsstart = ObsStartEnd.new;
        obsend = ObsStartEnd.new;
  
        startnode.each do |ose|
          obsstart.year = ose.xpath('year').inner_text;
          obsstart.month = ose.xpath('month').inner_text;
          obsstart.day = ose.xpath('day').inner_text;
          obsstart.hour = ose.xpath('hour').inner_text;
          obsstart.minute = ose.xpath('minute').inner_text;
        end
      
        endnode.each do |ose|
          obsend.year = ose.xpath('year').inner_text;
          obsend.month = ose.xpath('month').inner_text;
          obsend.day = ose.xpath('day').inner_text;
          obsend.hour = ose.xpath('hour').inner_text;
          obsend.minute = ose.xpath('minute').inner_text;
        end
  
        so = SchedObs.new(current, command, obsstart, obsend);
        sched.addObs(so);

      end

    end


    # Print out for debug
    #beams.each do |beam|
    #  puts beam.to_s + "," + beam.isActive().to_s +  "\n";
    #end
    #puts obs.to_s + "\n";
    #puts sched.to_s + "\n";

    anyActiveBeams = false;
    beams.each do |beam|
      if(beam.isActive == true)
        anyActiveBeams = true;
      end 
    end

    # Create the observing report
    s = "";

    # Updated area
    #s += "\n<div CLASS=roundedCorner id=\"updated_times_area\" \">";
    #s += "<table><tbody>\n";
    #s += "<tr><td align=\"left\">Updated:</td></tr><tr><td align=\"left\">" + obs.getTime + " GMT<br><span style=\"color:blue\">" + obs.getTimeLocal() + " " + obs.getTZ() + "</span></td></tr>";
    #s += "</tbody></table>\n";
    #s += "</div>";
#
    #s += "<table><tbody>\n";
    #s += "\t<tr>\n";
    #s += "<td align=center>";

    s += "<table align=center><tbody>\n";
    s += "\t<tr>\n";

    s += "<center><u><b>Observation Status</b></u></center>\n";
    s += "<td align=right valign=middle>";
    img = "<img src=\"/images/dish-red.png\">";
    if(obs.state.index("Idle"))
      img = "<img src=\"/images/dish-red.png\">";
    elsif (obs.state.index("Obs"))
      img = "<img src=\"/images/dish-green.png\">";
    else
      img = "<img src=\"/images/dish-yellow.png\">";
    end
    
    if(obs.state.index('eplay') != nil)
       s += "<center>" + img + "<br>" + obs.state.split(",").first + "<br>" + obs.state.split(",").last.split(" ").first + "<br>" + obs.state.split(",").last.split(" ").last + "<br>Act: " + obs.activityid + "</center>\n";
    else
       if(obs.state.index("Winds"))
           s += "<center>" + img + "<br>" + "Observing paused - high winds" + "</center>\n";
       elsif(obs.state.index("Idle"))
           s += "<center>" + img + "<br>" + "Not Observing" + "</center>\n";
       else
           s += "<center>" + img + "<br>" + obs.state + "<br>Act: " + obs.activityid + "</center>\n";
       end
    end
    s += "</td>\n";


    if(anyActiveBeams == true)
      s += "<td align=\"left\">\n";
      s += "<table align=left class=\"obs\"><tbody>\n";
      s += "<tr>\n";
      s += "\t<th align=\"center\">beam#</th>\n";
      s += "\t<th align=\"center\">state</th>\n";
      s += "\t<th align=\"center\">Target ID</th>\n";
      s += "\t<th align=\"center\">Target Names</th>\n";
      s += "\t<th align=\"center\">RA</th>\n";
      s += "\t<th align=\"center\">DEC</th>\n";
      s += "\t<th align=\"center\">Freq (MHz)</th>\n";
      s += "</tr>\n";

      beams.each do |beam|
        s += beam.to_html;
      end
      s += "</tbody></table>\n";
    end

    s += "</td></tr>\n";
    s += "</tbody></table>\n";

    s += "<hr>\n";
  
      #s += "<p>Report Created: " + @datetime + "</p>\n"; 
      s += sched.to_html + "\n";

      s += "\n<div style=\"position: absolute; top: 10px; right: 10px;\">";
      s += "<FORM>\n";
      s += "<INPUT TYPE=\"BUTTON\" VALUE=\"Refresh\" ONCLICK=\"window.location.href='/data/obsinfo'\"> ";
      s += "</FORM></div>";
      s += "<div id=\"timer\" style=\"position: absolute; top: 12px; right: 80px;\"></div>\n";
    s += "<script  type=\"text/javascript\">\n";
    s += "  stopwatch(\" \");\n";
    s += "</script>\n";

  
      #outputName
      File.open(outputName, 'w') {|f| f.write(s) } ;
      return s;
  end

end

#puts ObsReport.createReport("../../public/feeds/sonatastatus.xml",
#                            "../../public/feeds/sonataschedule.xml",
#                            "../../public/obsinfo.html") ;
