/*******************************************************************************
 *
 * File:    JPeg.java
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
 * @file JPeg.java
 *
 * A class to convert the byte array of waterfall data to a JPeg image.
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

import java.awt.image.*;
import com.sun.image.codec.jpeg.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Class that converts bytes to jpeg
 *
 * @author Jon Richards - The SETI Institute - jan 27, 2012
 *
 */
public class Jpeg
{
    /**
     * Constructor.
     */
    public Jpeg()
    {
    }

    /**
     * Convert a byte array to a jpeg.
     * @param raw the raw byte array of data.
     * @param width the width of the image.
     * @param height the height of the data.
     * @param filename the full path filename of the output image.
     * @throws IOException if there is a problem writing the data to a jpeg file.
     */
    public static void byteArrayToJpeg(byte[] raw, int width, int height, String filename)
        throws java.io.IOException
        {
            BufferedImage bi = new BufferedImage(width,
                    height, BufferedImage.TYPE_BYTE_GRAY);

            WritableRaster raster = bi.getRaster();

            // Put the pixels on the raster, using values between 0 and 255.
            for(int h=0;h<height;h++)
                for(int w=0;w<width;w++)
                {
                    int value = raw[w + width*h];
                    raster.setSample(w,h,0,value); 
                }
            // Store the image using the PNG format.
            ImageIO.write(bi,"JPEG",new File(filename));

        }

    /**
     * Convert a compamp file to jpeg.
     * @param compampFile the full path filename of a compamp file.
     * @param outFile the full path name of the resulting jpeg file.
     */
    public static void fileToJpeg(String compampFile, String outFile)
    {
        FileInfo fi = new FileInfo(compampFile);

        try
        {
            byte[] p = fi.getData();
            Jpeg.byteArrayToJpeg(correctPixels(p), 768, 129, outFile);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    /**
     * Correct the pixels in the image. This method is menat to be tweaked so you can get
     * the best JPEG image possible.
     * @param data the input data to correct.
     * @return a byte array of the corrected pixel values.
     */
    public static byte[] correctPixels(byte[] data)
    {
        double[] tempdata = new double[data.length];
        byte[] outdata = new byte[data.length];

        double max = 0;
        double min = 999999999;
        double val = 0;
        double val2 = 0;
        for(int i = 0; i<data.length; i++)
        {
            val = (double)data[i];
            val = Math.log(val)*255.0/Math.log(255.0);
            if(val < min) min = val;
            if(val > max) max = val;
        }

        for(int i = 0; i<data.length; i++)
        {
            val = (double)data[i];
            val = Math.log(val)*255.0/Math.log(255.0);
            outdata[i] = (byte)(((val - min)*255.0)/(max-min));
            //outdata[i] = (byte)(val);
        }

        return outdata;

    }

    /**
     * Main entry point.
     */
    public static void main(String[] cmdLine)
    {
    }


}

