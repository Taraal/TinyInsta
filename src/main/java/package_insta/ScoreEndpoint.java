package package_insta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;

@Api(name = "myApi",
     version = "v1",
     audiences = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
  	 clientIds = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )

public class ScoreEndpoint {

	@ApiMethod(name = "postMessage", httpMethod = HttpMethod.POST)
	public Entity postMessage(PostMessage pm) {

		Entity e = new Entity("Post"); // quelle est la clef ?? non specifié -> clef automatique
		e.setProperty("owner", pm.owner);
		e.setProperty("url", pm.url);
		e.setProperty("body", pm.body);
		e.setProperty("likec", 0);
		e.setProperty("date", new Date());
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Key> mycounter = new ArrayList<>(); 
		for (int i=0;i<20;i++) {
			Entity x = new Entity("Count");
			mycounter.add(x.getKey());
			x.setProperty("val", 0);
			datastore.put(x);
		}
		
		e.setProperty("like_array", mycounter);
		

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = ds.beginTransaction();
		ds.put(e);
		txn.commit();
		return e;
	}

	@ApiMethod(name = "mypost", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> mypost(@Named("name") String name, @Nullable @Named("next") String cursorString) {

	    Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, name));

	    // https://cloud.google.com/appengine/docs/standard/python/datastore/projectionqueries#Indexes_for_projections
	    //q.addProjection(new PropertyProjection("body", String.class));
	    //q.addProjection(new PropertyProjection("date", java.util.Date.class));
	    //q.addProjection(new PropertyProjection("likec", Integer.class));
	    //q.addProjection(new PropertyProjection("url", String.class));

	    // looks like a good idea but...
	    // generate a DataStoreNeedIndexException -> 
	    // require compositeIndex on owner + date
	    // Explosion combinatoire.
	    // q.addSort("date", SortDirection.DESCENDING);
	    
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    PreparedQuery pq = datastore.prepare(q);
	    
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);
	    
	    if (cursorString != null) {
		fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}
	    
	    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	    cursorString = results.getCursor().toWebSafeString();
	    
	    return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	    
	}
    
	@ApiMethod(name = "getPost",
		   httpMethod = ApiMethod.HttpMethod.GET)
	public CollectionResponse<Entity> getPost(User user, @Nullable @Named("next") String cursorString)
			throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}

		Query q = new Query("Post").
		    setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, user.getEmail()));

		// Multiple projection require a composite index
		// owner is automatically projected...
		// q.addProjection(new PropertyProjection("body", String.class));
		// q.addProjection(new PropertyProjection("date", java.util.Date.class));
		// q.addProjection(new PropertyProjection("likec", Integer.class));
		// q.addProjection(new PropertyProjection("url", String.class));

		// looks like a good idea but...
		// require a composite index
		// - kind: Post
		//  properties:
		//  - name: owner
		//  - name: date
		//    direction: desc

		// q.addSort("date", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);

		if (cursorString != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}

		QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		cursorString = results.getCursor().toWebSafeString();

		return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	}

	@ApiMethod(name = "postMsg", httpMethod = HttpMethod.POST)
	public Entity postMsg(User user, PostMessage pm) throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}

		Entity e = new Entity("Post", Long.MAX_VALUE-(new Date()).getTime()+":"+user.getEmail());
		e.setProperty("owner", user.getEmail());
		e.setProperty("url", pm.url);
		e.setProperty("body", pm.body);
		e.setProperty("likec", 0);
		e.setProperty("date", new Date());

///		Solution pour pas projeter les listes
//		Entity pi = new Entity("PostIndex", e.getKey());
//		HashSet<String> rec=new HashSet<String>();
//		pi.setProperty("receivers",rec);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
//		datastore.put(pi);
		txn.commit();
		return e;
	}

	
	// Retourne une erreur 404 quand appelée par post.html, ligne 122
	@ApiMethod(name = "likePost", httpMethod = HttpMethod.POST)
	public Entity likePost(@Named("postId") String postId) throws EntityNotFoundException{

		// Récupération du post
		Key postKey = KeyFactory.stringToKey(postId);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity post = datastore.get(postKey);

		Thread thread = new Thread();
		Random random = new Random();

		thread = ThreadManager.createThreadForCurrentRequest(new Runnable()  {
				public void run() {
					DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
					Transaction txn = ds.beginTransaction();
						try {
							// Récup du like_array et choix aléatoire d'un compteur
							int randomc = random.nextInt(((ArrayList<Key>) post.getProperty("like_array")).size());
							Entity c = ds.get(((ArrayList<Key>) post.getProperty("like_array")).get(randomc));
							
							// Un truc du genre : String id_counter = c.key.id pour vraiment récupérer l'id du counter 
							// qui nous intéresse
							Key id_counter = c.getKey();
							Entity counter = datastore.get(id_counter);
							Long v=(Long)counter.getProperty("val");
							
							//Long v=(Long)c.getProperty("val");
							
							// UN SLEEP DE CONTENTION
							Thread.sleep(100);
							// Incrémentation de la valeur et envoi sur le datastore
							
							counter.setProperty("val", v+1);
							//c.setProperty("val", v+1);
							
							ds.put(counter);
							//ds.put(c);
							txn.commit();
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (EntityNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (txn.isActive()) {
							    txn.rollback();
							  }
						}
					}
			});
			thread.start();
		

		
			try {
				thread.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		
		try {
			// Count pas encore fait (com de Sylouan)
			Long count=(long) 0;
			for (Key e : (ArrayList<Key>) post.getProperty("like_array")) {
				//response.getWriter().print((long)datastore.get(e.getKey()).getProperty("val"));
				//response.getWriter().print(datastore.get(e.getKey()));
				
				// Là encore je pense que c'est pas le compteur qui est sélectionné dans e, je pense qu'il faut d'abord
				// fait un truc du genre id = e.key.id et après un get(id).getProperty("val")
				Key id_counter2 = e.getKey();
				Entity counter = datastore.get(id_counter);
				Long v=(Long)counter.getProperty("val");
				
				
				count+=(long)datastore.get(e).getProperty("val");
			} 
			
			//response.getWriter().print("final value:"+count);
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return post;

		
		
	}
		
		
}

