DriveDesktopClient
==================

#java helper Library provide Modal part for google drive tree node structure & file operation on it like.
- upload new file/folder
- Trash/delete file
- copy file
- download file from server
- Update/touch file metadata

also its easy to authorize this library to use user google drive i.e you just have to set up this library it will take care
of get authorization from User.

#Using this you can replicate google drive UI structure & perform above mention operation on it in your java application.

#build pre requisite
* java 7 sdk 
* maven build environment

#compiling instruction
* download zip file & Unzip it
* perform maven install i.e "mvn install" from command line

#using instruction 
*javaDocs are available under Build_jar/javadoc folder
* minimum code required to set up ths library
* DriveDesktopClient.CLIENT_ID = "YOUR CLIENT ID";
* DriveDesktopClient.CLIENT_SECRET = "YOUR CLIENT SECRET";
* DriveDesktopClient.APPLICATION_NAME = "YOUR APPLICATION NAME";
* if (DriveDesktopClient.setUpGDrive()){
	//Set Up your Application
  }
  
#sample 
* UI project which is using this library available under sample repository 
* sample project's executabel jar available under "dist" folder so backup it before clean build this sample project
* sample project is Netbeans project 
