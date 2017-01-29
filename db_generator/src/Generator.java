import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;


public class Generator {
	
	public static void main(String[] args) throws Exception{
		Schema schema = new Schema(1008, "com.yoggo.dleandroidclient.database");
		
		addNews(schema);
		addCommentaries(schema);
		addCategories(schema);
		addGroups(schema);
		
		new DaoGenerator().generateAll(schema, "src-gen");
	}
	
	private static void addNews(Schema schema){
		Entity news = schema.addEntity("News");
		news.addIdProperty().primaryKey();
		news.addStringProperty("Title");
		news.addStringProperty("ShortStory");
		news.addStringProperty("FullStory");
		news.addStringProperty("Date");
		news.addStringProperty("Author");
		news.addStringProperty("NewsRead");
		news.addStringProperty("Rating");
		news.addStringProperty("CommNum");
		news.addStringProperty("UserId");
		news.addStringProperty("Category");
		news.addByteArrayProperty("Image");
	}
	
	private static void addCommentaries(Schema schema){
		Entity commentaries = schema.addEntity("Commentaries");
		commentaries.addIdProperty().primaryKey();
		commentaries.addLongProperty("NewsId");
		commentaries.addStringProperty("Content");
		commentaries.addStringProperty("Author");
		commentaries.addStringProperty("Date");
		commentaries.addStringProperty("UserId");
	}
	
	private static void addCategories(Schema schema){
		Entity categories = schema.addEntity("Categories");
		categories.addIdProperty().primaryKey();
		categories.addStringProperty("Name");
		categories.addStringProperty("AltName");
		categories.addStringProperty("ParentId");
	}
	
	private static void addGroups(Schema schema){
		Entity groups = schema.addEntity("Groups");
		groups.addIdProperty().primaryKey();
		groups.addStringProperty("GroupName");
		groups.addBooleanProperty("AllowAddNews");
		groups.addBooleanProperty("AllowAddCommentary");
		groups.addBooleanProperty("AllowEditCommentary");
		groups.addBooleanProperty("AllowDeleteCommentary");
		groups.addBooleanProperty("AllowEditAllCommentary");
		groups.addBooleanProperty("AllowDeleteAllCommentary");
		groups.addBooleanProperty("AdminCategories");
		groups.addStringProperty("CatAllowAddNews");
		
	}
}














