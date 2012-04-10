/*******************************************************************************

File:    Data.java
Project: SetiQuestInfo
Authors: Jon Richards - The SETI Institute

Copyright 2012 The SETI Institute

SetiQuestInfo is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SetiQuestInfo is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SetiQuestInfo.  If not, see<http://www.gnu.org/licenses/>.

Implementers of this code are requested to include the caption
"Licensed through SETI" with a link to setiQuest.org.

For alternate licensing arrangements, please contact
The SETI Institute at www.seti.org or setiquest.org. 

 *******************************************************************************/


/**
 * @file Data.java
 *
 * A class to contain one record of compamp data.
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
 * Class to contain one line of header and data.
 * @author Jon Richards - The SETI Institute - jan 12, 2012
 */
public class Data
{
    public double   rfCenterFrequency = 0.0;
    public int      halfFrameNumber   = 0;
    public int      activityId        = 0;
    public double   hzPerSubband      = 0;
    public int      startSubbandId    = 0;
    public int      numberOfSubbands  = 0;
    public float    overSampling      = 0;
    public int      polarization      = 0;
    double[] coef		   = null;

    //Usually the data is 1 subband, but can be more than one.
    //As a sanity check limit the max number of subbands to 16.
    int maxSubbands = 16;  // sanity check

    /**
     * Constructor.
     */
    public Data()
    {
    }

    //Getter methods

    public double   getRfCenterFrequency() {return rfCenterFrequency;}
    public int      getHalfFrameNumber()   {return halfFrameNumber;}
    public int      getActivityId()        {return activityId;}
    public double   getHzPerSubband()      {return hzPerSubband;}
    public int      getStartSubbandId()    {return startSubbandId;}
    public int      getNumberOfSubbands()  {return numberOfSubbands;}
    public float    getOverSampling()      {return overSampling;}
    public int      getPolarization()      {return polarization;}

    /**
     * Read one record that consists of the header data and the data.
     */
    public void read(DataInputStream in) throws Exception
    {
        readHeader(in);
        readData(in);
    }

    /**
     * Get the actual real/im data.
     * @return an array of doubles. Real/Im pairs.
     */
    public double[] getData()
    {
        return coef;
    }

    /**
     * Read the header from the file.
     * @throws Exception if there is a problem reading.
     */
    private void readHeader(DataInputStream in) throws Exception
    {

        try
        {
            // read the header
            rfCenterFrequency = in.readDouble();
            halfFrameNumber = in.readInt();
            activityId = in.readInt();
            hzPerSubband = in.readDouble();
            startSubbandId = in.readInt();
            numberOfSubbands = in.readInt();
            overSampling = in.readFloat();
            polarization = in.readInt();

            // validate length of compamp value array
            if (numberOfSubbands < 1 || numberOfSubbands > maxSubbands)
            {
                throw new Exception("Header read error: " + 
                        "subbands value " + numberOfSubbands +
                        " out of range.  Must be 1 - " + maxSubbands);
            }
        }
        catch (java.io.IOException ex)
        {
            throw new Exception("Header read exception: " + ex.getMessage());
        }
    }

    /**
     * Read the data from the file.
     * @throws Exception if there is a problem reading.
     */
    private void readData(DataInputStream in) throws Exception
    {	
        try
        {
            int MAX_SUBBAND_BINS_PER_1KHZ_HALF_FRAME = 512;

            int[] rawCoef = new int[MAX_SUBBAND_BINS_PER_1KHZ_HALF_FRAME];
            for (int i=0; i<MAX_SUBBAND_BINS_PER_1KHZ_HALF_FRAME; ++i)
            {
                rawCoef[i] = in.readUnsignedByte();
            }

            coef = new double[rawCoef.length * 2];

            int outIndex = 0;
            for (int i=0; i<rawCoef.length; ++i)
            {
                // coef's are:
                // RRRRIIII (4 bits real, 4 bits imaginary in 2's complement)

                int realValue = (rawCoef[i] & 0x000000f0) >> 4;
                if ((realValue & 0x00000008) != 0)
                    realValue |= 0xfffffff0;  // sign extend

                int imagValue = rawCoef[i] & 0x0000000f;
                if ((imagValue & 0x00000008) != 0)
                    imagValue |= 0xfffffff0;  // sign extend

                coef[outIndex++] = realValue;
                coef[outIndex++] = imagValue;
            }
        }
        catch (java.io.IOException ex)
        {
            throw new Exception("Data read exception: " + ex.getMessage());
        }
    }

    /**
     * Convert the polarization value in the header to a string.
     *
     * @param pol the polarization number from the header.
     * @return  R, L, B, M, or ?
     */
    private String polIntToString(int pol)
    {
        String value;
        switch (pol)
        {
            case 0: value ="R";  // right circular
                    break;
            case 1: value = "L"; // left circular
                    break;
            case 2: value = "B"; // both
                    break;
            case 3: value = "M"; // mixed
                    break;
            default: value = "?";
                     break;
        }

        return value;
    }

    /**
     * Create a string representation of this instance.
     * @return a string representation of this instance.
     */
    public String toString()
    {
        return "rfcenterFreq " + rfCenterFrequency + " MHz," +
            " halfFrameNumber " + halfFrameNumber + "," +
            " activityId " + activityId + "," +
            " hzPerSubband " + hzPerSubband + "," +
            " startSubbandId " + startSubbandId + "," +
            " #subbands " +  numberOfSubbands + "," +
            " overSampling " + overSampling + "," +
            " pol " + polIntToString(polarization) + "," + coef.length;

    }
}

