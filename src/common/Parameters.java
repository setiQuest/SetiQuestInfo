/*******************************************************************************

 File:    Parameters.java
 Project: SetiQuestInfo
 Authors: Jon Richards. April 2012

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

package setiquest.common;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * The class that allows easy access to name/value pairs in a
 * property file.
 *
 * @author Jon Richards - The SETI Institute - April, 2012
 *
 */
public class Parameters
{
	private String parameterFileName = "";
	private Properties properties = null;

	/**
	 * Constructor.
	 * @param parameterFileName the full path name of the parameter file.
	 */
	public Parameters(String parameterFileName)
	{
		this.parameterFileName = parameterFileName;
		properties = new Properties();

		InputStream is = null;
		try 
		{
			is = new FileInputStream( this.parameterFileName );
			properties.load( is );
		} 
		catch( IOException e ) 
		{
		} 
		finally 
		{
			if( null != is ) 
			{
				try 
				{ 
					is.close(); 
				} 
				catch( IOException e ) 
				{ 
				}
			}
		}
	}
	
	/**
	 * Get the value of a parameter.
         * @param name the name of the parameter.
         * @return the value of the parameter. Empty string if it does not exist.
         */
	public String getParam(String name)
	{
		return properties.getProperty(name, "");
	}

        /**
         * Set a parameter value, save it to the properties file.
         * @param name the name of the parameter.
         * @param value the value of the parameter.
         * @return true if the value was stored, false if there was an error.
         */
	public boolean setParam(String name, String value)
	{
		OutputStream os = null;
		try 
		{
		        properties.setProperty(name, value);
			os = new FileOutputStream( parameterFileName );
			properties.store(os, null);
		} 
		catch( IOException e ) 
		{
			return false;
		} 
		finally 
		{
			if( null != os ) 
			{
				try 
				{ 
					os.close(); 
				} 
				catch( IOException e ) 
				{ 
				}
			}
			return true;
		}
	}

	/**
	 * Main entry point for the program. Used for testing.
	 *
	 * @param cmdLine the command line arguments
	 */
	public static void main(String[] cmdLine)
	{
		//To run this test:
		//java -classpath ${CLASSPATH}:${LIB_DIR} setiquest.common.Parameters
		Parameters p = new Parameters(System.getenv("PROJ_HOME") + "/properties/common_test.properties");
		p.setParam("test", "test value");
		p.setParam("test2", "test value 2.2");
		System.out.println(p.getParam("test"));
		System.out.println(p.getParam("test2"));
	}


}

