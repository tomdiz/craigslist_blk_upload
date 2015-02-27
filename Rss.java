
import java.util.LinkedList;

public class Rss {

	RssChannel channel;
	protected LinkedList<RssItem> items = new LinkedList<RssItem>();
	
	public RssChannel getChannel() {
		return channel;
	}
	public void setChannel(RssChannel channel) {
		this.channel = channel;
	}
	public LinkedList<RssItem> getItemList() {
		return items;
	}
	public void setItemList(LinkedList<RssItem> itemList) {
		this.items = itemList;
	}
}
