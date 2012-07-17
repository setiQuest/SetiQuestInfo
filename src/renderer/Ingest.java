/*******************************************************************************
 *
 * File:    Ingest.java
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
 * @file Ingest.java
 *
 * A class to manage ingesting the compamp data.
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

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * A class to manage ingesting the compamp data.
 *
 * @author Jon Richards - The SETI Institute - Dec 06, 2011
 *
 */
public final class Ingest
{
    private String filename = "";
    private byte[] pixelData = null;
    private boolean ingested = false;
    private Data firstData      = null;
    private String status      = "none";

    //The resulting pixelData byte array willhave these hard-coded dimensions.
    private int width = 768;
    private int height = 129;

    /**
     * Constructor.
     * Also reads in the file and processes the data into the pixelData byte array.
     * @param filename the full path name of the compamp file to ingest.
     */
    public Ingest(String filename)
    {
        this.filename = filename;
        ingest();
    }

    /**
     * Get the filename.
     * @return the filename.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Get the processed data.
     * @return the data in a byte array.
     */
    public byte[] getData()
    {
        return pixelData;
    }


    /**
     * Determine if the data in the file has been ingested.
     * @return true or false.
     * */
    public boolean isIngested()
    {
        return ingested;
    }

    //Data getters
    public double   getRfCenterFrequency() {if(getFirstData() == null) return 0.0; return getFirstData().getRfCenterFrequency();}
    public int      getHalfFrameNumber()   {if(getFirstData() == null) return 0  ; return getFirstData().getHalfFrameNumber();}
    public int      getActivityId()        {if(getFirstData() == null) return 0  ; return getFirstData().getActivityId();}
    public double   getHzPerSubband()      {if(getFirstData() == null) return 0.0; return getFirstData().getHzPerSubband();}
    public int      getStartSubbandId()    {if(getFirstData() == null) return 0  ; return getFirstData().getStartSubbandId();}
    public int      getNumberOfSubbands()  {if(getFirstData() == null) return 0  ; return getFirstData().getNumberOfSubbands();}
    public float    getOverSampling()      {if(getFirstData() == null) return 0.0f; return getFirstData().getOverSampling();}
    public int      getPolarization()      {if(getFirstData() == null) return 0  ; return getFirstData().getPolarization();}

    /**
     * Get the data from the first data record in the file.
     * This is made available because it contains information about the data.
     * @return an instance of the first data record in the file.
     */
    public Data getFirstData()
    {
        return firstData;
    }

    /**
     * Get the pixel width of the pixeldata byte array.
     * @return the width in pixels.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the pixel height of the pixeldata byte array.
     * @return the height in pixels.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Ingest the compamp file, create waterfall data array that can be
     * turned into an image file if necessary.
     */
    public boolean ingest()
    {
        Data thisData = null;
        Data lastData = null;
        int[] pixelrow = new int[width];
        pixelData = new byte[width*height];

        //Open the file
        try
        {
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(
                        new FileInputStream(filename)));

            int row = 0;
            double[] prevData = null;
            while (in != null && (in.available() >= 1))
            {
                Data d = new Data();

                //Store the first record in this.firstData
                if(firstData == null) firstData = d;

                //Read in the next data set
                d.read(in);

                //Since each data set is a half frame, wait till the second half frame before
                //actually doing any processing.
                if(row >= 1)
                {
                    double[] fftvalues = convertCoefsTo1HzFreqValues(d.getData(), prevData);
                    convertFFTValuesToPixels(fftvalues, pixelrow, (double)d.overSampling);
                    stuffIntobuff(row-1, pixelrow);
                }

                //save the more recently read in data set for use as the next lower half frame.
                prevData = d.getData();

                row++;

            }

            //This process is complete when all lines have been processed.
            if(row >= getHeight()) status = "ready";

        }
        catch (IOException ex)
        {
            status = "Read failure: " + ex.getMessage();
            return false;
        }
        catch (Exception ex)
        {
            status = "Read failure: " + ex.getMessage();
            return false;
        }

        //Set the flag saying this has been processed.
        ingested = true;

        //If it got this far - success!
        return true;
    }

    //row is 0 based.
    /**
     * Stuff the latest data row into the larger pixelData waterfall data array.
     * @param row the row number, 0 based.
     * @param data the byte array to stuff into the pixelData array.
     */
    private void stuffIntobuff(int row, int[] data)
    {
        int bufferWidth = getWidth();
        byte[] buf = getData();
        for(int i = 0; i<bufferWidth; i++)
        {
            buf[i+(row*bufferWidth)] = (byte)data[i];
        }
    }


    /**
     * Get a String reprenestation of this class instance.
     * @return a huan readable string.
     */
    public String toString()
    {
        String result = filename + "\n";
        result += getFirstData().toString();

        //Used for debugging the pixel values. 
        //Commented out fir now.
        /*
           if(isIngested())
           {
           result += "Data: " + pixelData[0] + "," + pixelData[width*height/2] + "," + pixelData[(width*height)-1] + "\n";
           for(int i = 1; i<768; i+=76) result += " " + pixelData[10*width + i];
           result += "\n";
           for(int i = 1; i<768; i+=76) result += " " + pixelData[50*width + i];
           result += "\n";
           for(int i = 1; i<768; i+=76) result += " " + pixelData[120*width + i];
           result += "\n";
           }
           */
        return result;
    }

    /**
     * Convert the 1Hz complex amplitude values to frequency values.
     * This pieces together the last half frame and the current half
     * frame to operate on one full frame. Pipelined.
     *
     * @param newCoef the latest half frame.
     * @return an array of frequency values. Sonce this is pipelined, the
     * first time there is no half frame of coeffiecients stored so a
     * zeroed array is returned.
     */
    synchronized public double[] convertCoefsTo1HzFreqValues(double newCoef[], double prevCoef[])
    {
        // arrays are twice as big as the MAX_SUBBAND_BINS consts
        // because the real/imag values have been unpacked into
        // adjacent array elements.

        int MAX_SUBBAND_BINS_PER_1KHZ_HALF_FRAME = 512;

        // must be able to hold two 1KHz arrays
        double[] combinedCoef =
            new double[2 * 2 * MAX_SUBBAND_BINS_PER_1KHZ_HALF_FRAME];

        // If first time or a reset has occurred
        if (prevCoef == null) // first time or reset
        {
            prevCoef = new double[2 * MAX_SUBBAND_BINS_PER_1KHZ_HALF_FRAME];

            // Copy the new coefficients into the last half frame array
            for (int i=0; i<newCoef.length; ++i)
            {
                prevCoef[i] = newCoef[i];
            }

            //Zero out the array to return.
            for (int i=0; i<combinedCoef.length; ++i)
            {
                combinedCoef[i] = 0;
            }

            return combinedCoef;  // return empty array the first time
        }

        // copy the previous coefs
        for (int i=0; i<prevCoef.length; ++i)
        {
            combinedCoef[i] = prevCoef[i];
        }

        // add the new coefs on to the end of the last ones
        for (int i=0; i<newCoef.length; ++i)
        {
            combinedCoef[i+2*MAX_SUBBAND_BINS_PER_1KHZ_HALF_FRAME] = newCoef[i];
        }

        // convert them
        convertCoefsToFreqValues(combinedCoef);

        // save the most recent data for next time
        for (int i=0; i<newCoef.length; ++i)
        {
            prevCoef[i] = newCoef[i];
        }

        return combinedCoef;

    }

    /**
     * Convert the FFT values to pixel values.
     *
     * @param coef the array of FFTp coeffieients.
     * @param pixbuff the output pixels.
     */
    public void convertFFTValuesToPixels(double coef[], int[] pixbuff, double oversamplingPct)
    {

	int maxPixelValue = 255;

        /* Scale is fixed. Beamformer maintains average level nominally at 1 in 
         * compamp data. Previous scale calculation worked out to fixed value
         * of 10.408. This should be chosen to provide the desired linear range
         * in terms of average noise power and the resolution of the display. 
         */

        double scale = 15;

        // zero output buffer in case it's only partially filled
        for (int i=0; i<pixbuff.length; ++i)
        {
            pixbuff[i] = 0;
        }

        // See the todo
        int pixIndex = 0;
        int discard = (int) (coef.length * oversamplingPct);
        int ofs = discard / 2;
        int length = coef.length - discard;
        //System.out.println("pixbuff length = " + pixbuff.length + ", Coef.length=" + coef.length);
        //System.out.println("ofs = " + ofs + " length = " + length);


        int pixelShift = (int)(width/2) - (int)(length/4);

        //System.out.println("Shift=" + pixelShift);

        // convert to power (sum of the squares)
        for (int i=0; i<length; i+=2)
        {

            double realValue = coef[ofs+i];
            double imagValue = coef[ofs+i+1];

            double power = (realValue * realValue +
                    imagValue * imagValue); // keep power a double
            int pixel = (int) (power * scale + 0.5); //0.5 makes cast a round.
            if (pixel > maxPixelValue)
                pixel = maxPixelValue;

            int index = pixelShift +  pixIndex++;
            if(index < pixbuff.length)
            {
                pixbuff[index] = pixel;
            }
        }
    }

    /**
     * Convert the complex amplitude time samples to the frequency domain.
     *
     * Time sample order is real, imag in adjacent array elements
     *
     * @param coef the array of complex amplitude time samples.
     * The return values are placed back into the coef[] array.
     */
    private void convertCoefsToFreqValues(double coef[])
    {

        //The number of coefficients for the FFT
        int transformLength = coef.length / 2;  // N real, imag pairs

        // Perform the FFT. Values are placed back in coef[].
        DoubleFFT_1D fft = new DoubleFFT_1D(transformLength);
        fft.complexForward(coef);

        // Swap the left/right halves of the output array
        // to get the negative freqs into the proper place.
        for (int i = 0; i < transformLength; ++i)
        {
            double temp = coef[i + transformLength];
            coef[i+transformLength] = coef[i];
            coef[i] = temp;
        }

        
        // Normalize by the nominal mean square value of noise.
        //    Time data is already normalized, so just scale the fft data.
        // Try to correct for the noise floor suppression caused by upstream
        //    quantization clipping and AGC due to strong RFI.
        // Clip and then normalize by the mean square value of unclipped values.
        // FFT I and Q noise samples should be Gaussian with variance 0.5
        double norm; //nominal normalization
        double clipsq = 4.0 * transformLength; //4 x variance
        double sumnoclip = 0;
        double numnoclip = 0;
        // get sum of squared un-clipped valued
        for (int i=0; i<coef.length; ++i)
        {
            double temp = coef[i] * coef[i];
            if ( temp < clipsq ) {
                sumnoclip += temp;
                numnoclip += 1.0;
            }
        }
        // If 25% samples unclipped, use calculated value otherwise use nominal.
        // Multiplier inside sqrt was determined empirically to get scaling
        // normalized to get FFT mean power = 1 for noise.
        if ( numnoclip > (double) coef.length * 0.75 ) {
            norm = java.lang.Math.sqrt( 2.3 * sumnoclip / numnoclip );
        } else {
            norm = java.lang.Math.sqrt( transformLength );
        }
        
        for (int i=0; i<coef.length; ++i)
        {
            coef[i] /= norm;            

            //Trimming/clipping is not necessary. Let the imaging process do it.
        }

    }

}

