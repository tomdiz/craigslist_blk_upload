
import java.util.Date;
import java.util.LinkedList;

/**
 * 
 * 
 * @author ehaojii
 *
 */
public class RssChannel {

	protected ItemAuth authInfo;
	protected LinkedList<String> items = new LinkedList<String>();
	
	
	public ItemAuth getAuth() {
		return authInfo;
	}
	public void setAuth(ItemAuth auth) {
		this.authInfo = auth;
	}
	public LinkedList<String> getItemList() {
		return items;
	}
	public void setItemList(LinkedList<String> itemList) {
		this.items = itemList;
	}
}
