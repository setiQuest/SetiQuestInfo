/*******************************************************************************
 *
 * File:    Props.java
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
 * @file Props.java
 *
 * A class to manage program properties
 *
 * Project: SetiQuestInfo
 * <BR>
 * Version: 1.0
 * <BR>
 * @author Jon Richards (current maintainer)
 */

package setiquest.renderer;

//import java.util.Properties;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 * Class to manage properties.
 *
 * @author Jon Richards - The SETI Institute - March 29, 2012
 *
 */
public class Props extends Properties
{
    private String propertiesFilename = "";

    /**
     * Constructor.
     */
    public Props(String propertiesFilename)
    {
        super();
        this.propertiesFilename = propertiesFilename;
    }

    /**
     * Get a property from the properties file.
     * @param name the name of the property.
     * @return the value of the property. null if does not exist.
     */
    public String get(String name)
    {
        //try retrieve data from file
        try 
        {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesFilename));

            //Get the value, if it exists 
            if(props.getProperty(name) !=null)
            {
                return props.getProperty(name);
            }
        }
        catch (Exception ex)
        {
            Log.log("Could not retrieve value of " + name + " from " + propertiesFilename);
        }

        return null;

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

