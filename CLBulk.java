import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import org.jdom.CDATA;

public class CLBulk {

	private static String readAll(Reader rd) throws IOException {
    	StringBuilder sb = new StringBuilder();
    	int cp;
    	while ((cp = rd.read()) != -1) {
      		sb.append((char) cp);
    	}
    	return sb.toString();
  	}

  	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    	InputStream is = new URL(url).openStream();
    	try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      		String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		JSONObject jsonObject = readJsonFromUrl("https://api.somewhere.com/api/v1/car_data?limit=200");
		//System.out.println(json.toString());

		// Above URL returns 200 cars objects
		JSONArray carsJSONArray = (JSONArray)jsonObject.get("cars");

		// First create array - Needed for item names in CL channel data
		// In this example I use the car object database ID as it's name.
		// In this code it was a UDID string.
		List<String> carIDItemNames = new ArrayList<String>();
		int cnt = 0;
		for (String aboutcarId: args) {

			for (int i = 0; i < carsJSONArray.length(); i++)
			{
				JSONObject carObject = carsJSONArray.getJSONObject(i);
				String carIDString = (String)carObject.get("car_id");
				if (aboutcarId.equals(carIDString))
				{
					carIDItemNames.add(carIDString);
					cnt++;
				}
			}
		}

		// Now let's process our car objects to upload to CL
		cnt = 0;
		for (String carId: args) {

			for (int i = 0; i < carsJSONArray.length(); i++)
			{
				JSONObject carObject = carsJSONArray.getJSONObject(i);
				String carIDString = (String)carObject.get("car_id");
				//System.out.println("car_id = " + carIDString);

				if (carId.equals(carIDString))
				{
					System.out.println();
					System.out.println("==================================================");
					System.out.println();
					System.out.println("car_id: " + carIDString);
				
					// Create RSS object of this car
					// Make sure all data there we need first
					if (carObject.has("title") == false)
					{
						System.out.println("car is missing title");
						return;
					}
					if (carObject.has("features") == false)
					{
						System.out.println("car is missing features");
						return;
					}
					if (carObject.has("price") == false)
					{
						System.out.println("car is missing price");
						return;
					}
					if (carObject.has("images") == false)
					{
						System.out.println("car is missing images");
						return;
					}

					String carTitleString = (String)carObject.get("title");
					String carFeaturesString = (String)carObject.get("features");
					System.out.println("Title: " + carTitleString);
					System.out.println("Features: " + carFeaturesString);

					int carHourlyRate = 0;
					JSONArray carPriceArray = (JSONArray)carObject.get("price");
					for (int j = 0; j < carPriceArray.length(); j++)
					{
						JSONObject carPriceObject = carPriceArray.getJSONObject(j);
						String carPriceType = (String)carPriceObject.get("type");
						if (carPriceType.equals("HOURLY_RATE"))
						{
							// FYI: value in "cents" not dollars
							carHourlyRate = (int)carPriceObject.get("value");
							carHourlyRate = carHourlyRate / 100;
						}
					}
					
					System.out.println("Hourly Rate: $" + carHourlyRate + " per hour");

					JSONArray carImagesArray = (JSONArray)carObject.get("images");
					System.out.println("carImagesArray length: " + carImagesArray.length());
					String imageUrls[] = new String[carImagesArray.length()];
					System.out.println("imageUrls length: " + imageUrls.length);
					for (int k = 0; k < imageUrls.length; k++)
					{
						imageUrls[k] = carImagesArray.getString(k);
					}

					for (int x = 0; x < imageUrls.length; x++)
						System.out.println("car Image URL[" + x + "]: " + imageUrls[x]);

					System.out.println();
					System.out.println("==================================================");
					System.out.println();
					
					// Generate RSS to upload to Craigslist
					Rss rss = new Rss();

	                RssChannel rssChannel = new RssChannel();
    	            
    	            ItemAuth auth = new ItemAuth();
    	            auth.setUsername("test@test.com");
    	            auth.setPassword("test");
    	            auth.setAccountID("14");
    	            rssChannel.setAuth(auth);
					
					LinkedList<String> aboutList = rssChannel.getItemList();
					for (int z = 0; z < carIDItemNames.size(); z++) {
						aboutList.add(carIDItemNames.get(z));
					}
					rssChannel.setItemList(aboutList);

	                rss.setChannel(rssChannel);

					// Loop here for each car
    	            RssItem item = new RssItem();
    	            
    	            String endString = "We'd love to work with you! You can book our car now using Peercar (<a href=\"www.Peercar.com)\">link www.Peercar.com)</a>www.Peercar.com) or shoot us a quick email with info about what you are planning";
    	            CDATA description = new CDATA(carFeaturesString + "\n\n" + endString);
    	            item.setDescription(description);
    	            String priceString = new String("" + carHourlyRate);
    	            item.setPrice(priceString);
					item.setTitle(carTitleString);
    	            item.setAbout(carIDItemNames.get(cnt));
    	            cnt++;
					
					LinkedList<String> imageFileList = item.getItemList();
					for (int z = 0; z < imageUrls.length; z++) {
					
						String destinationFile = carId + "_" + z +".jpg";

						saveImage(imageUrls[z], destinationFile);
						System.out.println("Downloading image from URL " + imageUrls[z] + " to file " + destinationFile);
						imageFileList.add(destinationFile);
					}
					item.setItemList(imageFileList);

					LinkedList<RssItem> items = rss.getItemList();
					items.add(item);
					rss.setItemList(items);
					

    	            OutputStream output = new OutputStream()
    				{
        				private StringBuilder string = new StringBuilder();
        				@Override
        				public void write(int b) throws IOException {
            				this.string.append((char) b );
        				}

        				public String toString(){
            				return this.string.toString();
        				}
    				};    	                

	               try {
	               
                        //SimpleRssGenerator.generate(rss, new FileOutputStream("cl_rss.xml"));	
    	                SimpleRssGenerator.generate(rss, output);
						//System.out.println("Downloading image from URL " + output);

        	        } catch (IllegalArgumentException e) {
            	        e.printStackTrace();
					} catch (IllegalAccessException e) {
            	        e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// Upload new car to craigslist now that RSS ready
					// This use validate right now, but can be changed here to do posts.
        			URL url = new URL("https://post.craigslist.org/bulk-rss/validate");
        			HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
   				 	httpConn.setUseCaches(false);
			        httpConn.setDoOutput(true); // indicates POST method
 			       	httpConn.setDoInput(true);
    				httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

					DataOutputStream severOutput = new DataOutputStream(httpConn.getOutputStream());  
					severOutput.writeBytes(output.toString());
					severOutput.close();

	               try {
	               
					InputStream input = new DataInputStream(httpConn.getInputStream()); 
					for (int c = input.read(); c != -1; c = input.read()) 
						System.out.print((char)c);
					input.close();
					
					// Handle the response from CL
/*
					// Turn to XML to parse
					// <item> == Array of items and want to check these elements below
					// <postingManageURL> == URL of post content
					// <postedStatus> == POSTED
					StringWriter writer = new StringWriter();
        			IOUtils.copy(input, writer, "UTF-8");
        			String xmlString = writer.toString();
        			System.out.println(xmlString);

					// Parse XML string
					
					// Show use the results. For example, could use JavaMail here and send the
					// business person an email with public CL links for them to check and status
					// returned form CL.
*/
					} catch (IOException serverException) {
						serverException.printStackTrace();
						serverException.toString();
					}

					System.out.println("Resp Code:" + httpConn.getResponseCode()); 
					System.out.println("Resp Error Stream:" + httpConn.getErrorStream()); 
					System.out.println("Resp Message:" + httpConn.getResponseMessage()); 
				}
			}		
		}	
	}
}

