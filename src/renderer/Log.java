/*******************************************************************************
 *
 * File:    Log.java
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
 * @file Log.java
 *
 * A class to provide logging capability.
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
 * A class to provide logging capability.
 *
 * @author Jon Richards - The SETI Institute - Dec 06, 2011
 *
 */
public class Log
{
    /**
     * Constructor.
     */
    public Log()
    {
    }

    /**
     * Write to the log file
     */
    public static void log(String text)
    {
        if(text == null) return;
        if(text.length() <= 0) return;

        Calendar cal = Calendar.getInstance();
        String filename = Utils.getLogDir() + "/rend-" + cal.get(Calendar.YEAR) + "-" +
            String.format("%02d", (cal.get(Calendar.MONTH)+1)) + "-" + 
            String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + ".txt";

        try 
        {
            File file =new File(filename);
            if(!file.exists())
            {
                file.createNewFile();
            }

            //FileWriter fw = new FileWriter(file.getName(),true);
            FileWriter fw = new FileWriter(filename,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("[" + String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" +
                    String.format("%02d", cal.get(Calendar.MINUTE)) + ":" +
                    String.format("%02d", cal.get(Calendar.SECOND)) + "] " +
                    text + "\n");
            bw.close();
            fw.close();

        } 
        catch (IOException e)
        {
        }

    } 

    /**
     * Main entry point. ised for testing.
     * @param args a string array of program arguments.
     */
    public static void main(String[] args)
    {
        Log.log("Test1");
        Log.log("Test2");
    }

}

