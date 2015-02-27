import java.util.Date;
import org.jdom.CDATA;
import java.util.LinkedList;

public class RssItem {

	String title;
	String about;
	CDATA description;
	String area;
	String price;
	//    <cl:housingInfo price="1450"
    //                bedrooms="0"
    //                sqft="600"/>
	
	String subarea;
	//sfo	SF bay area	eby	east bay area
	//sfo	SF bay area	nby	north bay / marin
	//sfo	SF bay area	pen	peninsula
	//sfo	SF bay area	sby	south bay area
	//sfo	SF bay area	scz	santa cruz co
	//sfo	SF bay area	sfc	city of san francisco

	String neighborhood;	

	protected LinkedList<String> items = new LinkedList<String>();

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public CDATA getDescription() {
		return description;
	}
	public void setDescription(CDATA description) {
		this.description = description;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSubArea() {
		return subarea;
	}
	public void setSubArea(String subarea) {
		this.subarea = subarea;
	}
	public String getNeighborhood() {
		return neighborhood;
	}
	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}
	public LinkedList<String> getItemList() {
		return items;
	}
	public void setItemList(LinkedList<String> itemList) {
		this.items = itemList;
	}
}
