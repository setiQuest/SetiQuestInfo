/*******************************************************************************
 *
 * File:    SetiquestRenderedData.java
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
 * @file SetiquestRenderedData.java
 *
 * A class to contain the data to send to the server.
 *
 * Project: SetiQuestInfo
 * <BR>
 * Version: 1.0
 * <BR>
 * @author Jon Richards (current maintainer)
 */
package setiquest.renderer;

import java.io.Serializable;
import java.io.FileReader;
import java.io.BufferedReader;
import java.text.*;
import java.util.*;

/**
 * A class to contain the data to send to the server.
 */
public class SetiquestRenderedData  implements Serializable
{

    private int _activityId;
    private int _pol;
    private int _bitPix;
    private int _width;
    private int _height;
    private double _centerFreqMhz;
    private float _bandwidthMhz;
    private long _startTimeNanos;
    private long _endTimeNanos;
    private Beam[] _beam;
    private int _followupId;
    private String _rendering = "";

    /**
     * Constructor.
     */
    public SetiquestRenderedData()
    {
    }

    /**
     * Package up all the information for one set of JSON/BSON data to be 
     * sent to the server.
     * @param activityId the activity Id.
     * @param pol the pol.
     * @param bitPix bits per pixel.
     * @param width in picesl.
     * @param height in pixels.
     * @param centerFreqMhz the center frequency in MHz.
     * @param bandwidthMhz the bandwidth of the data in MHz.
     * @param date the date of the data. Like "2012-01-13"
     * @param time the time of the data. Like "07-05-18"
     * @param beam1TargetId the target Id of beam 1.
     * @param beam2TargetId the target Id of beam 2.
     * @param beam3TargetId the target Id of beam 3.
     * @param beam an array of beam data.
     */
    public SetiquestRenderedData(int activityId, int pol, int bitPix,
            int width, int height, double centerFreqMhz, float bandwidthMhz,
            String date, String time,
            int beam1TargetId, int beam2TargetId, int beam3TargetId,
            Beam[] beam, int followupId)
    {
        _activityId = activityId;
        _pol = pol;
        _bitPix = bitPix;
        _width = width;
        _height = height;
        _centerFreqMhz = centerFreqMhz;
        _bandwidthMhz = bandwidthMhz;
        _beam = beam;
        _followupId = followupId;
        _rendering = Utils.getRendererVersion();

        //Convert the date and time
        //Date:        2012-01-13
        //Time:        07-05-18

        try
        {
            java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'");
            // explicitly set timezone of input if needed
            df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
            java.util.Date d = df.parse(date + "T" + time + "Z");

            //JR - June 21, 2012 - Found out the time for the data if about 2.5 minutes
            //behind what it should be. That is because the time of the file name if the start of the
            //activity - not the start of collection. So i am adjusting roughly here.
            //_startTimeNanos = (new Long(d.getTime()) - 46500) *1000000 ;
            //_endTimeNanos = _startTimeNanos + 93*1000000000;
            _startTimeNanos = getDataCollectionStartTime(activityId);
            Log.log("Start time nanos read from activity data collection start time file: " + _startTimeNanos);
            if(_startTimeNanos <= 0)
            {
                Log.log("Calculating _startTimeNanos, value in file was bad.");
                _startTimeNanos = (new Long(d.getTime()) + 150000) *1000000 ;
            }
            _endTimeNanos = _startTimeNanos + 94*1000000000; //An obs is 94 seconds
        }
        catch (Exception ex)
        {
            System.err.println("SetiquestRenderedData exception: " + ex.getMessage());
        }

    }

    /**
     * Get the activity Id.
     * @return the activity Id.
     */
    public int getActivityId() { return _activityId; }

    /**
     * Set the activity Id.
     * @param activityId the activity Id.
     */
    public void setActivityId(int activityId) { _activityId = activityId; }

    /**
     * Get the Pol.
     * @return the pol.
     */
    public int getPol() { return _pol; }

    /**
     * Set the Pol.
     * @param pol the pol.
     */
    public void setPol(int pol) { _pol = pol; }

    /**
     * Get the bits per pixel.
     * @return the bits per pixel.
     */
    public int getBitPix() { return _bitPix; }

    /**
     * Set the bits per pixel.
     * @param bitPix the bits per pixel.
     */
    public void setBitPix(int bitPix) { _bitPix = bitPix; }

    /**
     * Get the width of the data.
     * @return the width of the data.
     */
    public int getWidth() { return _width; }

    /**
     * Set the width of the data.
     * @param width the width of the data.
     */
    public void setWidth(int width) { _width = width; }

    /**
     * Get the height of the data.
     * @return the width of the data.
     */
    public int getHeight() { return _height; }

    /**
     * Set the height of the data.
     * @param height the width of the data.
     */
    public void setHeight(int height) { _height = height; }

    /**
     * Get the center frequeny of the data in MHz.
     * @return the center frequeny of the data in MHz.
     */
    public double getCenterFreqMhz() { return _centerFreqMhz; }

    /**
     * Set the center frequeny of the data in MHz.
     * @param centerFreqMhz the center frequeny of the data in MHz.
     */
    public void setCenterFreqMhz(double centerFreqMhz) { _centerFreqMhz = centerFreqMhz; }

    /**
     * Get the bandwidth of the data in MHz.
     * @return the bandwidth of the data in MHz.
     */
    public float getBandwidthMhz() { return _bandwidthMhz; }

    /**
     * Set the bandwidth of the data in MHz.
     * @param bandwidthMhz the bandwidth of the data in MHz.
     */
    public void setBandwidthMhz(float bandwidthMhz) { _bandwidthMhz = bandwidthMhz; }

    /**
     * Get the start time of the data in nano seconds.
     * @return the start time of the data in nano seconds.
     */
    public long getStartTimeNanos() { return _startTimeNanos; }

    /**
     * Set the start time of the data in nano seconds.
     * @param startTimeNanos the start time of the data in nano seconds.
     */
    public void setStartTimeNanos(long startTimeNanos) { _startTimeNanos = startTimeNanos; }

    /**
     * Get the end time of the data in nano seconds.
     * @return the end time of the data in nano seconds.
     */
    public long getEndTimeNanos() { return _endTimeNanos; }

    /**
     * Set the end time of the data in nano seconds.
     * @param endTimeNanos the end time of the data in nano seconds.
     */
    public void setEndTimeNanos(long endTimeNanos) { _endTimeNanos = endTimeNanos; }

    /**
     * Get the array of beam data.
     * @return the array of beam data.
     */
    public Beam[] getBeam() { return _beam; }

    /**
     * Set the array of beam data.
     * @param beam the array of beam data.
     */
    public void setBeam(Beam[] beam) { _beam = beam; }

    /**
     * Get the followup Id.
     * @return the followup Id.
     */
    public int getFollowupId() { return _followupId; }

    /**
     * Set the followup Id.
     * @param followupId the followup Id.
     */
    public void setFollowupId(int followupId) { _followupId = followupId; }

    /**
     * Get the rendering version.
     * @return the rendering version. This is rendererversion in teh properties file.
     */
    public String getRendering() { return _rendering; }

    /**
     * Set the rendering version.
     * @param rendering the rendering version. 
     */
    public void setRendering(String rendering) { _rendering = rendering; }


    /**
     * Class to contain the data for 1 beam.
     */
    public class Beam
    {
        private int _beam = 0;
        private int _target = 0;
        private byte[] _data = null;

        /**
         * Constructor.
         */
        public Beam(int beam, int targetId, byte[] data)
        {
            this._beam = beam;
            this._target = targetId;
            this._data = data;
            setBeam(beam);
            setTarget(targetId);
            setData(data);
        }

        /**
         * Get the beam number.
         * @return the beam number.
         */
        public int getBeam() { return _beam; }

        /**
         * Set the beam data.
         * @param beam the beam number
         */
        public void setBeam(int beam) { _beam = beam; }

        /**
         * Get the target Id.
         * @return the target Id.
         */
        public int getTarget() { return _target; }

        /**
         * Set the target Id.
         * @param target the target Id.
         */
        public void setTarget(int target) { _target = target; }

        /**
         * Get the data.
         * @return the byte array containg the beam data.
         */
        public byte[] getData() { return _data; };

        /**
         * Set the beam data.
         * @param data the data for this beam.
         */
        public void setData(byte[] data) { _data = data; }
    }

    public static long getDataCollectionStartTime(int activityId)
    {

        String dateTimeString = "";
        try
        {
            //Try to open the file and read the date time string
            FileReader fr = new FileReader(Utils.getDataDir() + "/setiquest-activity-time-" + activityId + ".txt");

            Log.log("Reading: " + Utils.getDataDir() + "/setiquest-activity-time-" + activityId + ".txt");
            BufferedReader br = new BufferedReader(fr); 
            dateTimeString = br.readLine();
            dateTimeString = dateTimeString.replace(" ", "T") + "Z";
            Log.log("Start time for activity " + activityId + " data collection is: " + dateTimeString);
            fr.close(); 

            if(dateTimeString.length() <= 0) return -1;

            java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
            java.util.Date d = df.parse(dateTimeString);

            return (new Long(d.getTime())) *1000000 ;
        }
        catch (Exception ex)
        {
            Log.log(ex.getMessage() + " : " + dateTimeString);
            return -1;
        }

    }

    /**
     * Main entry point, for testing.
     * @param args string array of arguments.
     */
    public static void main(String[] args) 
    {
        System.out.println(SetiquestRenderedData.getDataCollectionStartTime(7241));
    }

}
