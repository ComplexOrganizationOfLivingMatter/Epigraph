# EpiGraph

## Installation onto ImageJ/FIJI

1. Download the [jar file](http://bit.ly/2RoVdXt)
2. Go to Fiji>Plugins>Install PlugIn...
3. Select the downloaded jar file.
4. Ta ta! You can use EpiGraph!!

## Clone project

If you want to test the code in your computer, follow these steps:

1st, import the maven project found on this project

2nd, import to the eclipse project the eclipse-preferences.

3rd, install as an eclipse plugin: 
- Windowbuilder
- Eclipse java development tools
- Swing Designer


4th If you have any trouble with the Epigraph package:
Change the package to lower case.

5th Try to run the application as java application. It should be working.

6th Try to run the application as Maven build. All by default.

7th If you have any trouble with Maven not founding the JDK:
Go to preferences and check if it's selected the JDK, not the JRE.
If you don't have installed the JDK, install it.

## Install dependency

Add this dependency to the POM file of your proyect:

	<dependency>
  		<groupId>es.escudero</groupId>
		<artifactId>Epigraph_</artifactId>
		<version>1.0.2</version>
	</dependency>
	
Also add this repository:

	<repository>
		<id>Fiji-plugins</id>
	        <url>https://raw.github.com/ComplexOrganizationOfLivingMatter/Fiji-plugins/master/</url>
	</repository>

