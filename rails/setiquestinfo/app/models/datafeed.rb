#require 'rubygems'
require 'openssl'
require 'digest/sha1'
require 'createobsreport.rb'
require 'createweatherreport.rb'
require 'tojson.rb'
require 'base64'
require 'net/http'
require 'RMagick'

class Datafeed

	attr_accessor :type,:data,:datatype,:webcam_num,:result
	
  	def encrypt(string, key)
    		Base64.encode64(aes(key, string)).gsub /\s/, ''
  	end

  	def decrypt(string, key)
    		aes_decrypt(key, Base64.decode64(string))
  	end

  	def aes(key,string)
    		cipher = OpenSSL::Cipher::Cipher.new("aes-256-cbc")
    		cipher.encrypt
    		cipher.key = Digest::SHA256.hexdigest(key)
    		cipher.iv = initialization_vector = cipher.random_iv
    		cipher_text = cipher.update(string)
    		cipher_text << cipher.final
    		return initialization_vector + cipher_text
  	end

  	def aes_decrypt(key, encrypted)
    		cipher = OpenSSL::Cipher::Cipher.new("aes-128-cbc")
    		cipher.decrypt
    		cipher.key =  Digest::SHA1.digest(key)[0, 16];   
    		cipher.iv = encrypted.slice!(0,16)
    		d = cipher.update(encrypted)
    		d << cipher.final
  	end

	def process


		if(@type == "sonata")
			begin
				decrypted=decrypt(@data.gsub!(" ", "+"), "FF123");
                        	if(@datatype == "xml")
					File.open("public/feeds/sonatastatus.xml", 'w') {|f| f.write(decrypted) }
                                        ObsReport.createReport("public/feeds/sonatastatus.xml",
                                                               "public/feeds/sonataschedule.xml",
                                                               "public/obsinfo.html") ;
					@result="OK"
                  	      	elsif(@datatype=="json")
					File.open("public/feeds/sonatastatus.json", 'w') {|f| f.write(decrypted) }
					@result="OK"
				else
					@result="ERROR1"
				end
			rescue
				@result="ERROR2"
			end

		elsif(@type == "schedule")
			begin
				#decrypted=decrypt(@data.gsub!(" ", "+"), "FF123");
				decrypted=decrypt(@data, "FF123");
                        	if(@datatype == "xml")
					File.open("public/feeds/sonataschedule.xml", 'w') {|f| f.write(decrypted) }
                                        ObsReport.createReport("public/feeds/sonatastatus.xml",
                                                               "public/feeds/sonataschedule.xml",
                                                               "public/obsinfo.html") ;
                                        convertTojson("public/feeds/sonataschedule.xml",
                                                      "public/feeds/sonataschedule.json");
					@result="OK"
				else
					@result="ERROR3: bad \"datatype\"";
				end
			rescue
				@result="ERROR4"
			end

		elsif(@type == "obsreport")
			begin
				decrypted=decrypt(@data, "FF123");
                        	if(@datatype == "xml")
					File.open("public/feeds/obsreport.xml", 'w') {|f| f.write(decrypted) }
					@result="OK"
                                        convertTojson("public/feeds/obsreport.xml",
                                                      "public/feeds/obsreport.json");
                  	      	elsif(@datatype=="json")
					File.open("public/feeds/obsreport.json", 'w') {|f| f.write(decrypted) }
					@result="OK"
				else
					@result="ERROR5"
				end
			rescue
				@result="ERROR6"
			end

		elsif(@type == "weather")
			begin
				decrypted=decrypt(@data, "FF123");
                        	if(@datatype == "xml")
					File.open("public/feeds/weather.xml", 'w') {|f| f.write(decrypted) }
					@result="OK"
                                        convertTojson("public/feeds/weather.xml",
                                                      "public/feeds/weather.json");
				WeatherReport.createReport("public/feeds/weather.xml",
                            				"public/weather.html") ;
				else
					@result="ERROR7"
				end
			rescue
				@result="ERROR8"
			end

		elsif(@type == "pic")
			decrypted=decrypt(@data, "FF123");
 			picData = Base64.decode64(decrypted);
			File.open("public/feeds/webcam" + @webcam_num + ".jpg", 'wb') {|f| f.write(picData) }

			#Image.resize("public/feeds/webcam" + @webcam_num + ".jpg",
                        #             "public/feeds/webcam" + @webcam_num + "-256.jpg",
                        #             256, 256);
			img = Magick::Image.read("public/feeds/webcam" + @webcam_num + ".jpg").first

                        fract = 256.0/704.0;
                        w = (704.0*fract).to_i;
                        h = (480.0*fract).to_i;
                        thumb = img.resize(w, h);
                        thumb.write("public/feeds/webcam" + @webcam_num + "-256.jpg");
                        fract = 320.0/704.0;
                        w = (704.0*fract).to_i;
                        h = (480.0*fract).to_i;
                        thumb = img.resize(w, h);
                        thumb.write("public/feeds/webcam" + @webcam_num + "-320.jpg");
                        fract = 480.0/704.0;
                        w = (704.0*fract).to_i;
                        h = (480.0*fract).to_i;
                        thumb = img.resize(w, h);
                        thumb.write("public/feeds/webcam" + @webcam_num + "-480.jpg");
			@result="OK"
			@result="OK"
 
          
		else
			@result="ERROR9"
		end

	end

end 
