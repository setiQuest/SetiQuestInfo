###!/usr/bin/env ruby

require 'rubygems'
require 'active_support/core_ext'
require 'json'

def convertTojson(infile, outfile)
  json_str = Hash.from_xml(File.open(infile).read).to_json
  File.open(outfile, 'w') {|f| f.write(json_str) } ;
#  return json_str;
end

#puts convertTojson("../../public/feeds/obsreport.xml", "../../public/feeds/obsreport.json")
