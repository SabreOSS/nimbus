<!--
  MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 -->

## _NIMBUS_

![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)

## Don't forget to give a :star: to make the project popular.

NIMBUS is a functional test automation framework built on top of SOAPUI that adds below PRO like capabilities to SOAPUI community edition
* Data driven capability
* Testcase tagging
* Extent Reports Integration
    * HTML Reports for analysis
    * MongoDB Integration for test results storage and analytics


## Getting Started

### Prerequisites

* Make sure you have a [GitHub account](https://github.com/signup/free).
* [Oracle JDK 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html)
* [Maven (version 3.0.5 or later)](http://maven.apache.org/)
* [Git (version 1.8 or later)](http://git-scm.com)
* Install IDE of your choice

### Project setup

#### Clone

To clone NIMBUS please use command below inside the folder of your choice:

```bash
git clone https://github.com/SabreOSS/nimbus.git
```

#### Build

To build NIMBUS please use command below on the folder where you have cloned NIMBUS

```bash
 mvn clean install
```
#### Launch

To Launch SOAPUI with NIMBUS framework please use command below from the folder where you have cloned NIMBUS repo, after running build commands

```bash
 mvn exec:exec -Pstart
```
This will open SOAPUI and loads NIMBUS SOAPUI listeners

#### Executing SOAPUI Tests

* Create a new [SOAPUI Project](https://www.soapui.org/docs/soapui-projects/) or Import any existing project
* Launch TestRunner
* Provide all required details in TestRunner window and click on Launch
  * Refer SOAPUI Documentation for details on [TestRunner](https://www.soapui.org/docs/test-automation/running-functional-tests/)

#### Get the latest changes

To get the latest source code changes for NIMBUS use command below on the folder where you have cloned NIMBUS and then execute Build and Launch steps:

```bash
git pull
```

### Data Driven Tests

* Add below test case properties to the test case which you want to convert into a data driven test case
  * _NimbusDataDrivenFlag_ # This will take true or false, true means it is a data drive test and false means it is not
  * _NimbusDataSourcePath_ # This will take the value of the data source path,excel file which contains the test data for the test case.
    * Only excel data sources are supported.
    * Below columns are mandatory in the data source excel
      * _Nimbus_TestCase_ # Name of the test case
      * _Nimbus_Execution_Flag_ # Is execution required
        * _Y_ # Needed
        * _N_ # Skip
  * Add any additional columns as required by your test case
* All columns in the excel sheet will be presented as test case properties during run time. Parameterize your test case using these test case properties.
  * context.expand('${#TestCase#*****}' when used in groovy code
  * ${#TestCase#*****} when used in requests
* Please refer testcase "DataDrivenTestCase" form the sample provided under samples folder

### Tagging

* Add _NimbusTags_ test case property to an existing test case and specify ; separated tags to identify this test case while executing
  * eg : @Tag1;@Tag2
* In SoapUI right click on the test case that you have tagged and select "Launch TestRunner" option
* Select the test case and test suite you want to execute from Basic Tab
* Go to Properties tab and under System Properties specify the tags you want to execute
  * eg : NimbusTags=@Tag1
* Click on Launch Button. This will execute only the test cases that are tagged as @Tag1 and skip all other test cases.
* Please refer testcases  "TaggedTestCase" and "TaggedDataDrivenTestCase" from the sample provided under samples folder

### HTML Reports

* NIMBUS integrates extent reports into SOAPUI. When you execute SOAPUI test cases via TestRunner, html report will be generated inside reports folder.
  * Please execute the sample provided via TestRunner window to view reports
* If you want to write results to a MongoDB (first set up [Extent Reports KLOV](https://www.extentreports.com/docs/versions/5/klov/index.html)) for storage and analytical purposes you can do that by passing these system parameters while executing the test cases
    * _nimbus.klov_ # set this value to true if results needs to be pushed to MongoDB
    * _nimbus.klov.mongodb.uri_ # MongoDB Conenction URI in the form  mongodb://{host}:{port} or mongodb://{user}:{password}@{host}:{port} incase credentials are required
    * _nimbus.klov.url_ # KLOV URL in the form http://{host}:{port}
* Refer below steps on how to pass system parameters while executing test cases
  * In SoapUI right click on the test case that you have tagged and select "Launch TestRunner" option
  * Go to Properties tab and under System Properties specify the system parameters as space/new-line seperated name-value pairs
    * eg : nimbus.klov=true nimbus.klov.mongodb.uri=mongodb://localhost:27017 nimbus.klov.url=http://localhost:7777

### Setting Debug Configuration

* Open the Run/Debug Configuration 
* Click on the + button
* Select Remote
* Enter a name as "_ossNimbus_"
* Enter the port as "_9876_"
* Click on the Apply and OK Button


### Debugging Capability

* As we all know debugging is a powerful tool, which lets us to find the bugs a lot faster by providing an insight into the internal operations of a program
* NIMBUS provides the Capability to debug the code while using the SOAPUI
* Process to start a debugger from the SoapUI
    * Select the sampleDebugging test suite in the SoapUI
    * Right click the test suite
    * Click on the _Launch TestRunner_
    * Go to Basic Tab and Click on the Browse button to select the TestRunner Path
    * Path to be selected is "_execute\debug_" folder 
    * Click on the _Launch_ button
    * In the SoapUI TestRunner Popup window you might see a message _Listening for transport dt_socket at address: 9876_
    
### Next Feature

* Selenium Integration with SOAPUI
    

## Contributing

We accept pull request via _GitHub_. Here are some guidelines which will make applying PRs easier for us:

* No tabs. Please use spaces for indentation.
* Respect the code style.
* Create minimal diffs - disable on save actions like reformat source code or organize imports.
  If you feel the source code should be reformatted create a separate PR for this change.

See [CONTRIBUTING](CONTRIBUTING.md) document for more details.

## JDK support policy
Currently NIMBUS supports Oracle JDK 12:

## License

Copyright 2022 Sabre GLBL Inc.

Output artifacts are under the [MIT license](LICENSE).
