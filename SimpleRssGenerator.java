
import java.io.*;
import java.lang.reflect.Field;
import java.util.LinkedList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.jdom.CDATA;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class SimpleRssGenerator {
	
	public static void generate(Rss rss, OutputStream out) throws IllegalArgumentException, IllegalAccessException, IOException
	{
		Element rssElement = SimpleRssGenerator.processRss(rss);
		Document doc = new Document(rssElement);
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		xmlOutputter.output(doc, out);
	}
	
	private static Element processRss(Rss rss) throws IllegalArgumentException, IllegalAccessException
	{
		Namespace namespace = Namespace.getNamespace("http://purl.org/rss/1.0/");
		Namespace clNamespace = Namespace.getNamespace("cl", "http://www.craigslist.org/about/cl-bulk-ns/1.0");
		Namespace rdfNamespace = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		Element rssRootElement = new Element("RDF", rdfNamespace);
		//rssRootElement.setAttribute("version", rss.version);
		rssRootElement.addNamespaceDeclaration(namespace);
		rssRootElement.addNamespaceDeclaration(clNamespace);
		rssRootElement.addNamespaceDeclaration(rdfNamespace);
		Element channelElement = processRssChannel(rss.channel);
		rssRootElement.addContent(channelElement);

		LinkedList<?> list = ((LinkedList<?>) rss.items);
		if (list.size() > 0) {
			if (list.get(0).getClass() == RssItem.class) {
						
				Element e = new Element("item");
				for (Object object : list) {
					RssItem spaceInfo = (RssItem) object;
					e.setAttribute("about", (String)spaceInfo.about, rdfNamespace);

					Element titleElement = new Element("title");
					titleElement.setText((String)spaceInfo.title);
					e.addContent(titleElement);
					Element descriptionElement = new Element("description");
					descriptionElement.addContent((CDATA)spaceInfo.description);
					e.addContent(descriptionElement);
					
					Element categoryElement = new Element("category", clNamespace);
					categoryElement.setText("off");
					e.addContent(categoryElement);

					Element priceElement = new Element("housingInfo", clNamespace);
					priceElement.setAttribute("price", (String)spaceInfo.price);
					priceElement.setAttribute("bedrooms", "0");
					priceElement.setAttribute("sqft", "0");
					e.addContent(priceElement);

					Element areaElement = new Element("area", clNamespace);
					areaElement.setText("sfo");
					e.addContent(areaElement);

					Element replyEmailElement = new Element("replyEmail", clNamespace);
					replyEmailElement.setText("concierge@peerspace.com");
					replyEmailElement.setAttribute("privacy", "C");
					e.addContent(replyEmailElement);
					
					// images
					//<cl:image position="[0-23]"> setText("encode64")
					try {
						int cnt = 1;
						LinkedList<String> imageFileList = spaceInfo.getItemList();
						for (String filename: imageFileList ) {
					
							Element imageElement = new Element("replyEmail", clNamespace);
							imageElement.setText(new String(Base64.encodeBase64(FileUtils.readFileToByteArray(new File(filename)))));
							imageElement.setAttribute("position", new String("" + cnt));
							cnt++;
							e.addContent(imageElement);
						}
					} catch (IOException exception) {
						exception.printStackTrace();
					}

				}

				rssRootElement.addContent(e);
			}
		}
		
		return rssRootElement;
	}
	
	private static Element processRssChannel(RssChannel rssChannel)
			throws IllegalArgumentException, IllegalAccessException {
		Element channelElement = new Element("channel");
		//System.out.println(rssChannel.getClass());
		Field[] fileds = rssChannel.getClass().getDeclaredFields();
		for (Field field : fileds) {

			Object o = field.get(rssChannel);
			if (o != null) {

				if (field.getType() == ItemAuth.class) {

					Namespace clNamespace = Namespace.getNamespace("cl", "http://www.craigslist.org/about/cl-bulk-ns/1.0");
					Element e = new Element("auth", clNamespace);
					e.setText("auth");
					e.setAttribute("username", ((ItemAuth) o).username);
					e.setAttribute("password", ((ItemAuth) o).password);
					e.setAttribute("accountID", ((ItemAuth) o).accountID);

					channelElement.addContent(e);
				} else if (field.getType() == LinkedList.class) {

					LinkedList<?> list = ((LinkedList<?>) o);
					if (list.size() > 0) {
						if (list.get(0).getClass() == String.class) {

							Element e = new Element("items");
							for (Object object : list) {
								Namespace rdfNamespace = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
								Element ee = new Element("li", rdfNamespace);
								ee.setAttribute("resource", (String)object, rdfNamespace);
								e.addContent(ee);
							}

							channelElement.addContent(e);
						}
					}

				}
			}
		}

		return channelElement;
	}
}
