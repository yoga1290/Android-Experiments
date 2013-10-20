package yoga1290.printk;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.IterationTag;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ShadySocialNetwork 
{
	private static String AndroidSecret="";
	private static void checkin(String userID,String venueID) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity user=datastore.get(KeyFactory.createKey("shady_users", userID));
		
		if(user.getProperty("current_venue")!=null)
			datastore.get(KeyFactory.createKey("shady_venues", user.getProperty("current_venue")+"")).removeProperty(userID);
		
		user.setProperty("current_venues",venueID);
		datastore.put(user);
		
		Entity venue=null;
		try
		{
			venue=datastore.get(KeyFactory.createKey("shady_venues", venueID));
			Integer cnt=(Integer)venue.getProperty("cnt");
			
			
			venue.setProperty(userID,new Date().getTime());
			datastore.put(venue);
		}catch(Exception e)
		{
			venue=new Entity(KeyFactory.createKey("shady_venues", venueID));
			venue.setProperty(userID, new Date().getTime());
			datastore.put(venue);
		}
	}
	private static String nowHere(String venueID) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		return new JSONObject(	datastore.get(KeyFactory.createKey("shady_venues",venueID)).getProperties()	).toString();
	}
	private static String newUser(String userID) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try{
			Entity user=datastore.get(KeyFactory.createKey("shady_users", userID));
			return "FOUND";
		}
		catch(Exception e){
			Entity user=new Entity(KeyFactory.createKey("shady_users",userID));
			datastore.put(user);
		}
		
		return ":)";
	}
	public static String getPost(int postid,String venueId) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity post=datastore.get(KeyFactory.createKey(KeyFactory.createKey("shady_venues", venueId), "shady_post", postid));
		return new JSONObject(post.getProperties()).toString();
	}
	
	
	private static void setField(String userID,String fieldID,String value) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity user=datastore.get(KeyFactory.createKey("shady_users", userID));
		user.setProperty(fieldID,value);
		datastore.put(user);
	}
	private static String whoisHere(String venueID) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity venue=datastore.get(KeyFactory.createKey("shady_venues", venueID));
		return new JSONObject(venue.getProperties()).toString();
	}
	
	private static String getUserinfo(String userID) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity user=datastore.get(KeyFactory.createKey("shady_users", userID));
		return new JSONObject(user.getProperties()).toString();
	}
	
	
	public static void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		try{
				if(req.getRequestURI().equals("/shady/checkin"))
				{
					checkin(		req.getParameter("userID"),	req.getParameter("venueID"));
					resp.getWriter().println(":)");
				}
				else if(req.getRequestURI().equals("/shady/setField"))
				{					
					setField(req.getParameter("userID"), req.getParameter("field"), req.getParameter("value"));	
					resp.getWriter().println(":)");
				}
				else if(req.getRequestURI().equals("/shady/profile"))
					resp.getWriter().println(getUserinfo(req.getParameter("userID")));
				else if(req.getRequestURI().equals("/shady/newUser"))
					resp.getWriter().println(newUser(req.getParameter("userID")));
				
				else if(req.getRequestURI().equals("/shady/herenow"))
					resp.getWriter().println(nowHere(req.getParameter("venueID")));
				else if(req.getRequestURI().equals("/shady/post"))
				{
					//TODO 
					//req.getParameter("venueID")
					//req.getParameter("offset")
					resp.getWriter().println(new JSONObject().put("venueID", req.getParameter("offset")).put("", "").toString());
				}
				
		}catch(Exception e){e.printStackTrace();
			try{resp.getWriter().println(e.getMessage());}catch(Exception e2){}
		}
	}
	
	public static void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		try
		{
		
			if(req.getRequestURI().equals("/shady/post"))
			{
				String res="";
				InputStream in=req.getInputStream();
				
				int i,j=0,p=0,imgsz=0;
				if(req.getParameter("imgsz")!=null)
					imgsz=Integer.parseInt(req.getParameter("imgsz"));
				byte buff[]=new byte[100],bin[]=new byte[imgsz];
				
				
				while((i=in.read(buff))>0)
				{

//					for(j=0;j<i && j<imgsz;j++)
//						bin[p+j]=buff[j];
//					
//					p+=i;
//					imgsz-=i;
//					if(j<i)
						res+=new String(buff,0,i);
				}
				
				resp.getWriter().println(">>"+res+"<<");
				
				if(res.length()==0)
					res="{}";
				JSONObject json=new JSONObject(res);
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				long postid=1;
				Entity cnt;
				try
				{
					cnt=DatastoreServiceFactory.getDatastoreService().get(KeyFactory.createKey("shady_post","cnt"));
					postid=(Long) cnt.getProperty("cnt");
					
					cnt.setProperty("cnt",postid+1);
					datastore.put(cnt);
					
				}catch(Exception e)
				{
					cnt=new Entity(KeyFactory.createKey("shady_post","cnt"));
//					resp.getWriter().println(e.getMessage());
					cnt.setProperty("cnt",postid+1);
					datastore.put(cnt);
				}
				
				
				
				Entity post=new Entity(	"shady_post", postid,	KeyFactory.createKey("shady_venues", json.getString("venueId")) );
				
//				cnt.setProperty("cnt",postid+1);
//				datastore.put(cnt);
				
				String k;
				Iterator<String> K=json.keys();
				
				while(K.hasNext())
				{
					k=K.next();
					System.out.println(k+":"+json.getString(k));
					post.setProperty(k,json.getString(k));
				}
				//array of bytes
//				post.setProperty("data", bin);
				datastore.put(post);
				resp.getWriter().println("saved as "+json.put("postid", postid).toString()+"<<");
			}
		}catch(Exception e){
			try
			{
				resp.getWriter().println(e.getMessage());
			}catch(Exception e2)
			{
				
			}
		}
	}
}
