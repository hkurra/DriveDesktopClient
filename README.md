DriveDesktopClient
==================

**Java helper library provides Modal part for google drive tree node structure and file operations on it like -**

- Upload a new file/folder
- Trash/delete a file
- Copy a file
- Download a file from server
- Update/touch a file metadata

Also it is easy to authorize user's google drive using this library i.e you just have to set up this library and it will take care
of the authorization by itself.

Besides, using this library you can replicate google drive UI structure & perform above mention operation on it in your java application.

###Build pre-requisite
* java 7 sdk 
* maven build environment

###Compiling instructions
* download zip file & Unzip it
* perform maven install using "mvn install" from command line

###Using instruction 
* javaDocs are available under Build_jar/javadoc folder
* minimum code required to set up ths library
```
* DriveDesktopClient.CLIENT_ID = "YOUR CLIENT ID";
* DriveDesktopClient.CLIENT_SECRET = "YOUR CLIENT SECRET";
* DriveDesktopClient.APPLICATION_NAME = "YOUR APPLICATION NAME";

* if (DriveDesktopClient.setUpGDrive()){
	//Set Up your Application
  }
  ```
  
#Sample 
* UI project which is using this library available under sample repository 
* sample project's executabel jar available under "dist" folder so backup it before clean build this sample project
* sample project is Netbeans project 
