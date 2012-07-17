/*******************************************************************************
 *
 * File:    FileInfo.java
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
 *  *******************************************************************************/


/**
 * @file FileInfo.java
 *
 * A class to contain the information about one compamp file.
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

/**
 * Class to contain the information about one compamnp file.
 */
public class FileInfo
{
    String filename = null;
    boolean filenameValid = false;
    String status = "none";
    byte[]   pixelData         = null;

    //File info
    public String date         = "";
    public String time         = "";
    public String activityId   = "";
    public String activityType = "";
    public String dxNum        = "";
    public String pol          = "";
    public String subchannel   = "";
    public String targetId     = "";
    public String sigId     = "0";
    public boolean isDX    = false;
    Ingest ingest = null;

    /**
     * Constructor.
     * The filename is parsed and the comapm data is ingested.
     * @param name the full path name of the compamp file.
     */
    public FileInfo(String name)
    {
        this.filename = name;
        status = "none";
        filenameValid = parse();
        ingest = new Ingest(this.filename);
        ingest.ingest();
    }

    /**
     * Get the filename.
     * @return the filename, full path.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Get the compamp data read from this compamp file.
     * @return byte array of data.
     */
    public byte[] getData()
    {
        return ingest.getData();
    }

    /**
     * Get the activity Id.
     * @return the activity Id as an integer.
     */
    public int getActivityId()
    {
        try
        {
            return Integer.parseInt(activityId);
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    /**
     * Get the target Id.
     * @return the target Id as an integer.
     */
    public int getTargetId()
    {
        try
        {
            return Integer.parseInt(targetId);
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    /**
     * Determine if this is a Dx, not a Zx.
     * @return true if is a Dx.
     */
    public boolean isDX()
    {
        return isDX;
    }

    /**
     * Get the subchannel.
     * @return the subchannel.
     */
    public String getSubchannel()
    {
        return subchannel;
    }

    /**
     * Parse the file name to extract identification information of rhtis file.
     * Note: Files are named thus:
     *   2012-01-13_15-16-51_UTC.act8382.target.zx1900.L.compamp
     * @return true if parsed properly/
     */
    private boolean parse()
    {
        //The filename should be a full path. We are only interested
        //in the last part.
        Log.log("PARSE: " + filename);
        int index = filename.lastIndexOf("/");
        if(index <= 0) return false;
        index++; //Skip over the leading "/";

        //Should have something like:
        //  2012-01-13_15-16-51_UTC.act8382.target.zx1900.L.compamp
        String[] majorParts = filename.substring(index).split("_");
        if(majorParts.length != 3) return false;

        //The date is the first one
        date = majorParts[0];

        //The time is the next one.
        time = majorParts[1];

        //The third part is the other information, separated by "."
        String[] minorParts = majorParts[2].split("\\.");
        //There should be 6 parts for a DX, 9 for ZX
        //Is this a DX?
        if(majorParts[2].contains(".dx")) isDX = true;

        Log.log("ISDX: " + isDX);

        //UTC.act7581.target.zx2900.R.9999.compamp
        if(isDX)
        {
            if(minorParts.length != 7) return false;

            //Get the activity Id
            if(!minorParts[1].startsWith("act")) return false;
            activityId = minorParts[1].substring(3);

            //Get the avtivity Type
            activityType = minorParts[2];

            //Get the dx (zx) number
            if(!minorParts[3].startsWith("dx") && !minorParts[3].startsWith("zx"))
                return false;
            dxNum = minorParts[3].substring(2);

            //Get the pol
            pol = minorParts[4];
            if(pol.length() != 1) return false;

            //Get the targetId
            targetId = minorParts[5];
            if(targetId.contains("compamp"))
            {
                targetId = "0";
            }
        }
        //UTC.act10457.target.zx2900.tg160130.sbch1014.requested.id999.L.compamp
        else //ZX
        {
            Log.log("PARSING ZX");
            if(minorParts.length < 9) return false;

            //Get the activity Id
            if(!minorParts[1].startsWith("act")) return false;
            activityId = minorParts[1].substring(3);

            //Get the avtivity Type
            activityType = minorParts[2];

            //Get the dx (zx) number
            if(!minorParts[3].startsWith("dx") && !minorParts[3].startsWith("zx"))
                return false;
            dxNum = minorParts[3].substring(2);

            //Get the targetId
            if(!minorParts[4].startsWith("tg")) return false;
            targetId = minorParts[4].substring(2);

            //Get the subchannel
            Log.log("PARSING ZX - subchannel=" + minorParts[5]);
            if(!minorParts[5].startsWith("sbch")) return false;
            subchannel = minorParts[5].substring(4);
            Log.log("PARSING ZX - subchannel=" + subchannel);

            //Get the follup id. May not exist, so be careful and check the name 
            //starts with "id".
            if(minorParts[7].startsWith("id"))
            {
                sigId = minorParts[7].substring(2);
                Log.log("ID = " + sigId);
            }

            //Get the pol, should always be the last part before ".compamp".
            pol = minorParts[minorParts.length-2];
            if(pol.length() != 1) return false;

        }

        return true;

    }


    /**
     * Move this file to another location.
     * @param dir the directory to move the file to.
     */
    public void backup(String dir)
    {
        File d = new File(dir + "/" + date);
        d.mkdir();

        File f = new File(this.filename);
        int index= filename.lastIndexOf("/");
        String newName = dir + "/" + date + filename.substring(index);
        File fTo = new File(newName);
        f.renameTo(fTo);

    }

    /**
     * Return the state: is the filename a valid one, was it 
     * parsed correctly?
     */
    public boolean isValid()
    {
        return filenameValid;
    }

    /**
     * Return if the file has been ingested.
     */
    public boolean isIngested()
    {
        return ingest.isIngested();
    }

    /**
     * Get the first data record of this compamp file.
     * @return the Data instance of the first header/data combo.
     */
    public Data getFirstData()
    {
        return ingest.getFirstData();
    }

    /**
     * Match, should be in same Group.
     */
    public boolean match(FileInfo fi)
    {
        if(!isValid()) parse();
        if(!isValid()) return false;
        if(fi.parse() == false) return false;
        if(!date.equals(fi.date)) return false;
        if(!time.equals(fi.time)) return false;
        if(!activityId.equals(fi.activityId)) return false;
        if(!activityType.equals(fi.activityType)) return false;
        if(!pol.equals(fi.pol)) return false;

        if(!isDX)
        {
            if(!subchannel.equals(fi.subchannel)) return false;
        }
        return true;
    }

    /**
     * Get a string representation.
     * @return a string representation.
     */
    public String toString()
    {
        String result = filename + "\n";
        result += "Filename is valid = " + isValid() + "\n";
        result += "Is ingested: " + isIngested() + "\n";
        result += "Beam:        " + getBeam() + "\n";
        result += "Date:        " + date + "\n";
        result += "Time:        " + time + "\n";
        result += "Activity:    " + activityId + "\n";
        result += "Type:        " + activityType + "\n";
        result += "Dx:          " + dxNum + "\n";
        result += "Pol:         " + pol + "\n";

        return result;
    }

    /**
     * From the Dx number, get the beam.
     * @return the beam number. 0 if there is a problem determining the beam number.
     */
    public int getBeam()
    {
        if(!isValid()) return 0;
        if(dxNum.startsWith("1")) return 1;
        else if(dxNum.startsWith("2")) return 2;
        else if(dxNum.startsWith("3")) return 3;
        return 0;
    }

}
