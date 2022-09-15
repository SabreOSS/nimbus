/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.utils.properties;

import java.util.Properties;

import com.eviware.soapui.model.project.Project;

/**
 * Class that has the logic to process system properties starting with NIMBUS_ string and making them available as soapui project properties
 *
 * @author Kiran Chitivolu
 */

public class ProcessSystemProperties {

	private ProcessSystemProperties() {

	}
	public static void processNimbusSystemProperties(Project soapUIProject) {
		Properties systemProperties = System.getProperties();
		for(Object key : systemProperties.keySet()) {
			if(key.toString().contains("NIMBUS_")) {
				soapUIProject.setPropertyValue(key.toString(), systemProperties.getProperty(key.toString()));
			}
		}		
	}
}
