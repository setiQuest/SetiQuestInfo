#!/usr/local/rvm/bin/ruby -w

puts "<?xml version=\"1.0\" encoding=\"ISO8859-1\" ?>\n";
puts "<info>\n";
file = File.new("../public/feeds/kep365.csv", "r")
while (line = file.gets)
  parts = line.split(",");

  puts "  <uptime>\n";
  puts "    <risedate>" + parts[0] + "</risedate>\n";
  puts "    <risetime>" + parts[1] + "</risetime>\n";
  puts "    <setdate>" + parts[2] + "</setdate>\n";
  puts "    <settime>" + parts[3].chomp + "</settime>\n";
  puts "  </uptime>\n";
end
file.close
puts "</info>";
