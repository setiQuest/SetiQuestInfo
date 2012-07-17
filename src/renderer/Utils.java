/*******************************************************************************
 *
 * File:    Utils.java
 * Project: SetiQuestInfo
 * Authors: Jon Richards - The SETI Institute
 *
 * Copyright 2012 The SETI Institute
 *
 * SetiQuestInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SetiQuestInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SetiQuestInfo.  If not, see<http://www.gnu.org/licenses/>.
 *
 * Implementers of this code are requested to include the caption
 * "Licensed through SETI" with a link to setiQuest.org.
 *
 * For alternate licensing arrangements, please contact
 * The SETI Institute at www.seti.org or setiquest.org. 
 *
 *******************************************************************************/


/**
 * @file Utils.java
 *
 * A class contain helpfil utilities.
 *
 * Project: SetiQuestInfo
 * <BR>
 * Version: 1.0
 * <BR>
 * @author Jon Richards (current maintainer)
 */

package setiquest.renderer;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.*;

import setiquest.common.Parameters;


/**
 * The class that contains various methods used for various things.
 *
 * @author Jon Richards - The SETI Institute - Feb 26, 2012
 *
 */
public class Utils
{
    private static Parameters params = 
        new Parameters(System.getenv("PROJ_HOME") + "/properties/renderer.properties");
    /**
     * Constructor.
     */
    public Utils()
    {
        Log.log("Properties file = " + System.getenv("PROJ_HOME") + "/properties/renderer.properties");
    }

    /**
     * Specify a properties file other than the default.
     * @param filename the parameters file, full path.
     */
    public static void setParamFileName(String filename)
    {
        Log.log("Setting Properties file = " + filename);
        params = new Parameters(filename);
    }

    /**
     * Get the URL to send BSON files to when SonATA is observing.
     * @return the URL of the offline_subjects.
     */
    public static String getSubjectsURL()
    {
        return params.getParam("subjectsurl");
    }

    /**
     * Get the URL to send BSON files to when SonATA is not observing.
     * @return the URL of the offline_subjects.
     */
    public static String getOfflineSubjectsURL()
    {
        return params.getParam("offlineSubjectsurl");
    }

    /**
     * Get the URL to send BSON files to when SonATA is not observing.
     * @return the URL of the status URL.
     */
    public static String getStatusURL()
    {
        return params.getParam("statusurl");
    }

    /**
     * Get the full path to the log directory.
     * @return the full path to the log directory.
     */
    public static String getLogDir()
    {
        return params.getParam("logdir");
    }

    /**
     * Get the data directory.
     * This is the directory where all data resides for processing.
     * @return the full path of the data directory.
     */
    public static String getDataDir()
    {
        return params.getParam("datadir");
    }

    /**
     * Get the bson data directory.
     * @return the full path of the bson data directory.
     */
    public static String getBsonDataDir()
    {
        return params.getParam("bsondatadir");
    }

    /**
     * Get the renderer version
     * @return the renderer version
     */
    public static String getRendererVersion()
    {
        return params.getParam("rendererversion");
    }

    /**
     * Create a String of all the properties.
     * @return a string of all the properties.
     */
    public static String propertiesToString()
    {
        return "datadir=" + getDataDir() + "\n" + 
            "bsondatadir=" + getBsonDataDir() + "\n" +
            "logdir=" + getLogDir() + "\n" + 
            "statusurl=" + getStatusURL() + "\n" + 
            "offlineSubjectsurl=" + getOfflineSubjectsURL() + "\n" +
            "subjectsurl=" + getSubjectsURL() + "\n" +
            "rendering=" + getRendererVersion();
    }


    /**
     * Get the stat of observations by querying the base post URL.
     * @return true if active.
     */
    public static boolean getObsState()
    {

        String responseString = "";
        HttpClient httpclient = null;

        try
        {
            httpclient = new DefaultHttpClient();

            // Prepare a request object
            HttpGet httpget = new HttpGet(getStatusURL());

            // Execute the request
            HttpResponse response = httpclient.execute(httpget);

            // Examine the response status
            //System.out.println(response.getStatusLine());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();

            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            if (entity != null) 
            {
                InputStream instream = entity.getContent();
                try 
                {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(instream));
                    responseString = reader.readLine();
                    //System.out.println("|" + reader.readLine() + "|");

                } 
                catch (IOException ex) 
                {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;

                } 
                catch (RuntimeException ex) 
                {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection and release it back to the connection manager.
                    httpget.abort();
                    throw ex;

                } 
                finally 
                {
                    // Closing the input stream will trigger connection release
                    instream.close();
                }

                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpclient.getConnectionManager().shutdown();
            }
        }
        catch (Exception ex)
        {
        }

        if(responseString.equals("active")) return true;
        else return false;

    }

    /**
     * Remove old BSON files.
     * @param bsonDir the full path directory containing the son data files.
     * @param maxDays the maximum days a bson file should live.
     */
    public static int purgeBsonDir(String bsonDir, long maxDays)
    {
        File directory = new File(bsonDir);  
        int count = 0;

        if(directory.exists())
        {
            File[] listFiles = directory.listFiles();              
            long purgeTime = System.currentTimeMillis() - (maxDays * 24 * 60 * 60 * 1000);  

            for(File listFile : listFiles) 
            {  
                if(listFile.lastModified() < purgeTime) 
                {
                    if(!listFile.delete()) 
                    {  
                        Log.log("Unable to delete file: " + listFile);  
                    }  
                    else
                    {
                        count++;
                    }
                }  
            }  
        } 
        else 
        {  
            Log.log("Files were not deleted, directory " + bsonDir + " does'nt exist!");  
            System.out.println("Files were not deleted, directory " + bsonDir + " does'nt exist!");  
        }  

        return count;
    }  

    /**
     * Remove old compamp files.
     * @param dir the full path directory containing the files.
     * @param maxSecs the maximum seconds a file should live.
     */
    public static int purgeOldCompampFiles(String dir, long maxSecs)
    {
        File directory = new File(dir);  
        int count = 0;

        Log.log("Purging from " + dir);

        if(directory.exists())
        {
            File[] listFiles = directory.listFiles();              
            long purgeTime = System.currentTimeMillis() - (maxSecs * 1000);  

            for(File listFile : listFiles) 
            {  
                Log.log("Purging: " + listFile.getName());
                if((listFile.getName().contains(".requested.R.compamp") ||
                            listFile.getName().contains(".requested.L.compamp"))&& 
                        listFile.lastModified() < purgeTime) 
                {
                    if(!listFile.delete()) 
                    {  
                        Log.log("Unable to delete file: " + listFile);  
                    }  
                    else
                    {
                        Log.log("Deleted ol compamp file: " + listFile + ", " + listFile.lastModified() + "," + purgeTime);
                        count++;
                    }
                }  
            }  
        } 
        else 
        {  
            Log.log("Files were not deleted, directory " + dir + " does'nt exist!");  
            System.out.println("Files were not deleted, directory " + dir + " does'nt exist!");  
        }  

        return count;
    }  

    /**
     * Get a random file from a directory.
     * @param dir the directory to search in.
     * @return a file name full path.
     */
    public static String getRandomFile(String dir)
    {
        File directory = new File(dir);  
        int count = 0;
        String randomFileName = "";

        if(directory.exists())
        {
            File[] listFiles = directory.listFiles();              
            int numFiles = listFiles.length;
            Random rand = new Random(System.currentTimeMillis());
            int index = rand.nextInt(numFiles - 1);
            randomFileName = listFiles[index].getAbsolutePath();

        }

        return randomFileName;
    }

    /**
     * Send a random BSON file.
     * @return the response from the web server.
     */
    public static String sendRandomBsonFile()
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getOfflineSubjectsURL());

        String filename = getRandomFile(getBsonDataDir());		
        File f = new File(filename);
        if(!f.exists())
        {
            Log.log("sendRandonBsonFile(): File \"" + filename + "\" does not exists.");
            return "sendRandonBsonFile(): File \"" + filename + "\" does not exists.";
        }

        //Parse info from the name.
        //Old file = act1207.1.5.bson = actId, pol, subchannel
        //New file name: act1207.1.0.5.bson = actid, pol, obsType, subchannel
        int actId = 0;
        int obsId = 0;
        int pol = 0;
        String subchannel = "0";
        String shortName = f.getName();
        String[] parts = shortName.split("\\.");
        if(parts.length == 4) //Old name type
        {
            actId = Integer.parseInt(parts[0].substring(3));
            obsId = 0; //Default to this.
            pol = Integer.parseInt(parts[1]);
            subchannel = parts[2];
        }
        else if(parts.length == 5) //New name type
        {
            actId = Integer.parseInt(parts[0].substring(3));
            pol = Integer.parseInt(parts[1]);
            obsId = Integer.parseInt(parts[2]);
            subchannel = parts[3];
        }
        else
        {
            Log.log("sendRandonBsonFile(): bad bson file name: " + shortName);
            return "sendRandonBsonFile(): bad bson file name: " + shortName;
        }

        Log.log("Sending " + filename + ", Act:" + actId + ", Obs type:" + obsId + ", Pol" + pol + ", subchannel:" + subchannel );

        FileBody bin = new FileBody(new File(filename));
        try
        {
            StringBody comment = new StringBody("Filename: " + filename);

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file", bin);
            reqEntity.addPart("type", new StringBody("data/bson"));
            reqEntity.addPart("subject[activity_id]", new StringBody("" + actId));
            reqEntity.addPart("subject[observation_id]", new StringBody("" + obsId));
            reqEntity.addPart("subject[pol]", new StringBody("" + pol));
            reqEntity.addPart("subject[subchannel]", new StringBody("" + subchannel));
            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            //Log.log(response.toString());

            String sendResult = "Sending: " + shortName + "\n";
            sendResult += "subject[activity_id]: " + actId + "\n";
            sendResult += "subject[observation_id]: " + obsId + "\n";
            sendResult += "subject[pol]: " + pol + "\n";
            sendResult += "subject[subchannel]: " + subchannel + "\n";
            sendResult += response.toString() + "|\n";

            return sendResult;

        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        return "undefined";
    }

    /**
     * Send a BSON file.
     * @param filename the full path to the BSON file.
     * @param actId the activity Id.
     * @param obsId the observation Id.
     * @param pol the POL of thedata.
     * @param subchannel the subchannel of the data.
     */
    public static String sendBSONFile(String filename, int actId, int obsId, int pol, String subchannel)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Utils.getSubjectsURL());

        Log.log("Sending " + filename + ", Act:" + actId + ", Obs type:" + obsId + ", Pol" + pol + ", subchannel:" + 
                subchannel );

        FileBody bin = new FileBody(new File(filename));
        try
        {
            StringBody comment = new StringBody("Filename: " + filename);

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file", bin);
            reqEntity.addPart("type", new StringBody("data/bson"));
            reqEntity.addPart("subject[activity_id]", new StringBody("" + actId));
            reqEntity.addPart("subject[observation_id]", new StringBody("" + obsId));
            reqEntity.addPart("subject[pol]", new StringBody("" + pol));
            reqEntity.addPart("subject[subchannel]", new StringBody("" + subchannel));
            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            Log.log(response.toString());
            return response.toString();

            /*
               sendResult  = "subject[activity_id]: " + actId + "\n";
               sendResult += "subject[observation_id]: " + obsId + "\n";
               sendResult += "subject[pol]: " + pol + "\n";
               sendResult += "subject[subchannel]: " + subchannel + "\n";
               sendResult += response.toString() + "|\n";
               */

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }


    /**
     * Main entry point for the program. Used for testing.
     *
     * @param cmdLine the command line arguments
     */
    public static void main(String[] cmdLine)
    {
    }


}

