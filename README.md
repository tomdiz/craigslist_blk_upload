craigslist_blk_upload
=====================

This code can download JSON from a URL and process it into a CL RSS feed to validate or post to Craigslist.

This is code I wrote at a hackathon in 1 day. I hope it can help someone looking to bulk upload
data to Craigslist. It can use some cleanup and I know that. All examples I found out there 
were Perl based so I thought this would be a fun exercise. This example was written against 
specific data but can easily be changed using the file explanation below. It is not the 
best Java code. This example uploads cars and there hourly rates to use them.

This repo contains code to help you with bulk uploading of Craigslist Posts using Craigslist
RSS-XML. This code is written in Java and uses the Simple RSS Generator found at 
(https://code.google.com/p/rss-generator/). I have made chages so that it generates the
Craigslist RSS format including namespaces. The basic class will need to be updated to 
support your data you want to upload. I download data from a server in JSON format, parse,
and create the Craigslist RSS XML. I also asynchronously download images to encode64 from 
a CDN.

THIS EXMAPLE WILL FAIL WITH A HTTP 403 ERROR - FORBIDDEN ACCESS
Also, this example uses validation and can be changed to posting by switching the URL.

You need to get a bulk upload account from CL to use this code.

See these 2 URLs:

http://www.craigslist.org/about/bulk_posting_interface
http://www.craigslist.org/about/ctd


## Getting Started
 You will need the following JAR files to run this example:
 
 1) org.json.jar
 2) jdom-1.1.3.jar
 3) commons-codec-1.10.jar
 4) commons-io-2.4.jar

### Compiling and Running this code

Compile:

javac -classpath ./:./jdom-1.1.3.jar:./org.json.jar:./commons-codec-1.10.jar:./commons-io-2.4.jar CLBulk.java

Run:

java -classpath "./:org.json.jar:./:jdom-1.1.3.jar:./org.json.jar:./commons-codec-1.10.jar:./commons-io-2.4.jar" CLBulk


## Files and there Jobs

Here is a list of all the files used in this project and what they do.

### Main File

* CLBulk.java - CLBulk

This is the main class that contains main(). The first part of this file will download
car objects in JSON format a parse out the data it needs to put on CL. This data is for the
Title, description, body and payment info fields. IT also pulls the image URLs form the
backend to pull from the CDN. These will eventually be download to files and read in, then 
base64 encoded and added to the RSS XML.

### RSS_Generator Files I changed to Support CL RSS using the 3 namespaces that are needed.

* SimpleRssGenerator.java

This works with jdom to build the actual CL RSS fee

* Rss.java

This is the start of the CL RSS feed class

* RssChannel.java

This class supports the Channel of the CL RSS feed

* RssItem.java

This class supports the general CL ad title, description, etc of the CL RSS feed

* ItemAuth.java

This class is the Channel Auth element found in the CL RSS feed

