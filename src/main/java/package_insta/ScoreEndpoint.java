package package_insta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

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
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.com.google.protobuf.Duration;
import com.google.appengine.repackaged.org.joda.time.Interval;
import com.google.appengine.api.datastore.TransactionOptions;


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
	public Entity postMessage(PostMessage pm) throws ParseException {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity e = new Entity("Post");
		
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date_today = new Date();			
			String date_post = format.format(date_today);
			
			// Propriétés de chaque post
			e.setProperty("id_post", date_post + " - " + pm.owner);
			e.setProperty("owner", pm.owner);
			e.setProperty("date", date_post);
			e.setProperty("url", pm.url);
			e.setProperty("body", pm.body);
			ArrayList<String> list_likers = new ArrayList<String>();
			list_likers.add("");
			e.setProperty("likers", list_likers);
			long like = 0;
			e.setProperty("likec", like);
			datastore.put(e);
					
		Key clePost = datastore.put(e);
	    Long idPostCree = clePost.getId();
		    
	    // Create the like counter
	    for (int i = 1; i <= 10; i++){

	        Entity compteurLikePost = new Entity("GestionCompteur");
	        compteurLikePost.setProperty("idPost", idPostCree);
	        compteurLikePost.setProperty("nomSousCompteur", "SC" + i);
	        compteurLikePost.setProperty("valeurCompteur", 0);
	        datastore.put(compteurLikePost);
	    }	    
	    
		return e;		
	}
	
	
	
	@ApiMethod(name = "getUserByName", path = "/myApi/v1/getUserByName", httpMethod = HttpMethod.GET)
    public List<Entity> getUserByName(@Named("inputBar") String inputBar) {
		ArrayList<String> ListNameQuery = new ArrayList<String>();
		ArrayList<Entity> FinalListUsers = new ArrayList<Entity>();
		
		
		
		String regex = "[!._,@? ] {}~&()|^;/%$";
		StringTokenizer str = new StringTokenizer(inputBar,regex);
		
		while(str.hasMoreTokens()) {
			ListNameQuery.add(str.nextToken().toLowerCase());
	    }
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		for (int i=0;i<ListNameQuery.size();i++) {
			//System.out.println("motQuery :");
			//System.out.println(ListNameQuery.get(i));
			
			Query q1 = new Query("User").setFilter(new FilterPredicate("nom", FilterOperator.EQUAL, ListNameQuery.get(i)));
			PreparedQuery pq1 = datastore.prepare(q1);
			
			Query q2 = new Query("User").setFilter(new FilterPredicate("prenom", FilterOperator.EQUAL, ListNameQuery.get(i)));
			PreparedQuery pq2 = datastore.prepare(q2);
			
			List<Entity> resultNom = pq1.asList(FetchOptions.Builder.withDefaults());
			List<Entity> resultPrenom = pq2.asList(FetchOptions.Builder.withDefaults());
			
			for (int j=0;j<resultNom.size();j++) {
				FinalListUsers.add(resultNom.get(j));
			}
			
			for (int j=0;j<resultPrenom.size();j++) {
				FinalListUsers.add(resultPrenom.get(j));
			}
			
			
		}
		
		//Suppression des doublons
		Set<Entity> mySet = new HashSet<Entity>(FinalListUsers);
	    List<Entity> FinalListUsersUnique = new ArrayList<Entity>(mySet);
	 
	    /*System.out.println("------------------------------");
		for(int k=0;k<FinalListUsersUnique.size();k++) {
			System.out.println(FinalListUsersUnique.size());
			System.out.println(FinalListUsersUnique.get(k).getProperty("userName"));
		}
		
		System.out.println("------------------------------");*/
		
		return FinalListUsersUnique;
    }
	
	
	
	@ApiMethod(name = "ajouterLike", path = "/myApi/v1/ajouterLike", httpMethod = HttpMethod.POST)
    public void ajouterLike(@Named("idPost") String idPost, @Named("id_post") String id_post, @Named("owner") String owner) {

		// Sélection du post en question
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Filter filterPost = new Query.FilterPredicate("id_post", Query.FilterOperator.EQUAL, id_post);  
        Query query3 = new Query("Post").setFilter(filterPost);
        Entity post = ds.prepare(query3).asSingleEntity();
        
        // Liste des users ayant déjà liké le post
        List<String> list_likers = (List<String>)post.getProperty("likers");
        		
        if (!(list_likers.contains(owner))) {  
		
			// Choix random d'un sous-compteur de likes du post
	        int nbCompteurs = 10;
	        int compteurChoisi = (int)(Math.random() * nbCompteurs + 1);
	        String nomSousCompteurChoisi = "SC" + compteurChoisi;
	        	        
	        // Conversion de l'id en long pour la suite
	        long idPost2 = Long.parseLong(idPost);
	
	        // Sélection du sous-compteur choisi
	        Filter filterID = new Query.FilterPredicate("idPost", Query.FilterOperator.EQUAL, idPost2);
	        Filter filterCPT = new Query.FilterPredicate("nomSousCompteur", Query.FilterOperator.EQUAL, nomSousCompteurChoisi);
	        CompositeFilter filterFus = CompositeFilterOperator.and(filterID, filterCPT);
	        Query query = new Query("GestionCompteur").setFilter(filterFus);
	        Entity gestLikesPost = ds.prepare(query).asSingleEntity();
	
	        // +1 sur sous-compteur choisi
	        Long tmpLike = (Long)gestLikesPost.getProperty("valeurCompteur");
	        gestLikesPost.setProperty("valeurCompteur", tmpLike + 1);
	        ds.put(gestLikesPost);
	
	        // Sélection de l'ensemble des sous-compteurs du post
	        Query query2 = new Query("GestionCompteur").setFilter(filterID);
	        PreparedQuery pq = ds.prepare(query2);
	        List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
	
	        long totalLikesPost = 0;
	
	        // Somme des sous-compteurs
	        for (Entity r : result){
	            totalLikesPost += (Long)(r.getProperty("valeurCompteur"));
	        }
	             	
	        // Actualisation du compteur global de likes
	        post.setProperty("likec", totalLikesPost);
	        
	        // Ajout du user à la liste des likers 
	        list_likers.add(owner);
	        post.setProperty("likers", list_likers);
	        ds.put(post); 
		}
		
    }
	
	@ApiMethod(name = "supprimerLike", path = "/myApi/v1/supprimerLike", httpMethod = HttpMethod.POST)
    public void supprimerLike(@Named("idPost") String idPost, @Named("id_post") String id_post, @Named("owner") String owner) {

		// Sélection du post en question
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Filter filterPost = new Query.FilterPredicate("id_post", Query.FilterOperator.EQUAL, id_post);  
        Query query3 = new Query("Post").setFilter(filterPost);
        Entity post = ds.prepare(query3).asSingleEntity();
        
        // Liste des users ayant déjà liké le post
        List<String> list_likers = (List<String>)post.getProperty("likers");
        		
        if (list_likers.contains(owner)) {  
		
			// Choix random d'un sous-compteur de likes du post
	        int nbCompteurs = 10;
	        int compteurChoisi = (int)(Math.random() * nbCompteurs + 1);
	        String nomSousCompteurChoisi = "SC" + compteurChoisi;
	        	        
	        // Conversion de l'id en long pour la suite
	        long idPost2 = Long.parseLong(idPost);
	
	        // Sélection du sous-compteur choisi
	        Filter filterID = new Query.FilterPredicate("idPost", Query.FilterOperator.EQUAL, idPost2);
	        Filter filterCPT = new Query.FilterPredicate("nomSousCompteur", Query.FilterOperator.EQUAL, nomSousCompteurChoisi);
	        CompositeFilter filterFus = CompositeFilterOperator.and(filterID, filterCPT);
	        Query query = new Query("GestionCompteur").setFilter(filterFus);
	        Entity gestLikesPost = ds.prepare(query).asSingleEntity();
	
	        // -1 sur sous-compteur choisi
	        Long tmpLike = (Long)gestLikesPost.getProperty("valeurCompteur");
	        gestLikesPost.setProperty("valeurCompteur", tmpLike - 1);
	        ds.put(gestLikesPost);
	
	        // Sélection de l'ensemble des sous-compteurs du post
	        Query query2 = new Query("GestionCompteur").setFilter(filterID);
	        PreparedQuery pq = ds.prepare(query2);
	        List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
	
	        long totalLikesPost = 0;
	
	        // Somme des sous-compteurs
	        for (Entity r : result){
	            totalLikesPost += (Long)(r.getProperty("valeurCompteur"));
	        }
	             	
	        // Actualisation du compteur global de likes
	        post.setProperty("likec", totalLikesPost);
	        
	        // Ajout du user à la liste des likers 
	        list_likers.remove(owner);
	        post.setProperty("likers", list_likers);
	        ds.put(post); 
		}
		
    }
	
	
	@ApiMethod(name = "followerPost", path="/myApi/v1/getposts/{email}", httpMethod = HttpMethod.GET)
	public ArrayList followerPost(@Named("email") String email){
		
		Query q = new Query("User").setFilter(new FilterPredicate("email", FilterOperator.EQUAL, email));
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		
		
		Entity user = result.get(0);
		System.out.println((String)user.getProperty("email"));
		
		ArrayList<Entity> posts = new ArrayList<Entity>();
		
		for(String followEmail: (ArrayList<String>)user.getProperty("follows")) {

			System.out.println("LOL");
			System.out.println(followEmail);
			Query nq = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, followEmail));

			
			PreparedQuery npq = datastore.prepare(nq);
			List<Entity> userPosts = npq.asList(FetchOptions.Builder.withDefaults());
			posts.addAll(userPosts);
		}
			
		try {
		Collections.sort(posts, (o1, o2) -> ((Date)o1.getProperty("date")).compareTo(((Date)o2.getProperty("date"))));
		}catch(ClassCastException e) {
			System.out.println(e.getMessage());
		}
		
		Collections.reverse(posts);
		System.out.println(posts);

		return posts;
		
	}
	
	
	@ApiMethod(name = "mypost", path = "/myApi/v1/mypost", httpMethod = HttpMethod.GET)
	public List<Entity>  mypost(@Named("name") String name, @Nullable @Named("next") String cursorString) {

		//Filter filterUser = new Query.FilterPredicate("id_post".substring(19, "id_post".length())), Query.FilterOperator.EQUAL, user_id);  
        //Query query = new Query("User").setFilter(filterUser);
		
		
		/*DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		
		Filter filterKey = new Query.FilterPredicate("key", FilterOperator.GREATER_THAN, name);  
        Query query = new Query("Post_key").setFilter(filterKey);
        Entity post = datastore.prepare(query).asSingleEntity();
		
		
		Query q =
                new Query("RetrievePost")
                	.setFilter(new FilterPredicate("__key__" , FilterOperator.GREATER_THAN, KeyFactory.createKey("RetrievePost", pseudo+"_")));
		*/
		
		//Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, name));
	    
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filterUser = new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, name);  
        Query query = new Query("User").setFilter(filterUser);
        Entity user = datastore.prepare(query).asSingleEntity();
        
        ArrayList<String> list_follows = (ArrayList<String>)user.getProperty("follows");
        
        if (list_follows == null){
            throw new NullPointerException("No follow found");
        }
		

		
		Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.IN, list_follows));
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
        
        
	    //Filter filterPost = new Query.FilterPredicate("owner", Query.FilterOperator.IN, list_follows);  
        /*
        Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.IN, list_follows));
        q.addSort("date", SortDirection.DESCENDING);*/
        
        
        return result;
	    //Query q = new Query("Post").setFilter(filterPost);
	    //q.addSort("date", SortDirection.DESCENDING);	    

		
		
		
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
	    
	    
		
		
		
		//DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    
       /* DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        
		PreparedQuery pq = ds.prepare(q);
	    
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);
	    
	    if (cursorString != null) {
	    	fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}
	    
	    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	    cursorString = results.getCursor().toWebSafeString();
	    
	    return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();*/
	    
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
	
	

	

	
	
	/* Finds a user in the datastore by its email
	 * Crappy method as it fails if the result is null 
	 * TODO : try/catch the null result, return null if no user is found 
	 * 
	 * @param email : email of the requested user
	 * @return Entity : the requested user
	 */
	static public Entity getUserByEmail(@Named("email") String email) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("User").setFilter(new FilterPredicate("email", FilterOperator.EQUAL, email));
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		System.out.println(result);
		Entity user = result.get(0);
		return user;
	}
	
	@ApiMethod(name = "getUser", path = "user/{email}", httpMethod = HttpMethod.GET)
	public Object getUser(@Named("email") String email) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("User").setFilter(new FilterPredicate("email", FilterOperator.EQUAL, email));
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		
		Entity user = new Entity("User");
		user.setProperty("key", result.get(0).getProperty("key"));
		user.setProperty("email", result.get(0).getProperty("email"));
		return user;
	}
	
	
	
	/* Makes userA follow userB
	 * @param emailA : email property of userA
	 * @param emailB : email property of userB
	 * @return boolean : True if successful, False otherwise
	 */
	@ApiMethod(name= "follow", path = "follow/{emailA}/{emailB}", httpMethod = HttpMethod.PUT)
	public void follow(@Named("emailA") String emailA, @Named("emailB") String emailB) {
		
		Entity userA = getUserByEmail(emailA);
		Entity userB = getUserByEmail(emailB);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		
		ArrayList<String> follows = (ArrayList<String>)userA.getProperty("follows");
		if (!follows.contains(emailB)) {
			follows.add(emailB);
			userA.setProperty("follows", follows);
			datastore.put(userA);
		}
		
		ArrayList<String> followers = (ArrayList<String>) userB.getProperty("followers");
		if (!followers.contains(emailA)) {
			followers.add(emailA);
			userB.setProperty("followers", followers);
			datastore.put(userB);
		}
		txn.commit();
		
	}
	
	
	
	/* Makes userA unfollow userB
	 * 
	 */
	@ApiMethod(name = "unfollow", path = "follow/{emailA}/{emailB}", httpMethod = HttpMethod.DELETE)
	public void unfollow(@Named("emailA") String emailA, @Named("emailB") String emailB) {
	
		Entity userA = getUserByEmail(emailA);
		Entity userB = getUserByEmail(emailB);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		ArrayList<String> follows = (ArrayList<String>)userA.getProperty("follows");
		follows.remove(emailB);
		userA.setProperty("follows", follows);
		datastore.put(userA);
		
		ArrayList<String> followers = (ArrayList<String>) userB.getProperty("followers");
		followers.remove(emailA);
		userB.setProperty("followers", followers);
		datastore.put(userB);
		
		txn.commit();
		
	}
	
	
	
//////////////////////////////////////////// BENCHMARK \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	
	// On crée un user avec 10 followers, un avec 100 followers et un dernier avec 500 followers
	
	@ApiMethod(name = "UserTest", path = "/myApi/v1/UserTest", httpMethod = HttpMethod.POST)
	public void UserTest() {

		// User 10 followers
		Entity e = new Entity("User");
		e.setProperty("email", "u10@test.fr");		
		e.setProperty("nom", "u10");
		e.setProperty("prenom", "u10");
		e.setProperty("userName", "u10 u10");
		// Create followers and follows
		ArrayList<String> fset = new ArrayList<String>();
		fset.add("");
		for (int j = 1; j <= 10; j++) {
			fset.add("f" + j + "@test.fr");
		}
		e.setProperty("followers", fset);
		// Send user to datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);
	
		// User 100 followers
		Entity f = new Entity("User");
		f.setProperty("email", "u100@test.fr");		
		f.setProperty("nom", "u100");
		f.setProperty("prenom", "u100");
		f.setProperty("userName", "u100 u100");
		// Create followers and follows
		ArrayList<String> fset2 = new ArrayList<String>();
		fset2.add("");
		for (int j = 1; j <= 100; j++) {
			fset2.add("f" + j + "@test.fr");
		}
		f.setProperty("followers", fset2);
		// Send user to datastore
		datastore.put(f);
		
		// User 500 followers
		Entity g = new Entity("User");
		g.setProperty("email", "u500@test.fr");		
		g.setProperty("nom", "u500");
		g.setProperty("prenom", "u500");
		g.setProperty("userName", "u500 u500");
		// Create followers and follows
		ArrayList<String> fset3 = new ArrayList<String>();
		fset3.add("");
		for (int j = 1; j <= 500; j++) {
			fset3.add("f" + j + "@test.fr");
		}
		g.setProperty("followers", fset3);
		// Send user to datastore
		datastore.put(g);
	}
	

	
	// On calcule le temps nécessaire pour créer un post avec chacun des 3 comptes test
	
	@ApiMethod(name = "PostTest", path = "/myApi/v1/PostTest", httpMethod = HttpMethod.POST)
	public void PostTest() {
	
	String userTest = "u500@test.fr";	
	
	long startTime = 0;
    long delta = 0;
    long endTime = 0;
		
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	for(int j=1 ; j<=30 ; j++) {
	 
		startTime = System.currentTimeMillis();
		
		Entity e = new Entity("Post");
	
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date_today = new Date();
		String date_post = format.format(date_today);
		
		// Propriétés de chaque post
		e.setProperty("id_post", date_post + " - " + userTest);
		e.setProperty("owner", userTest);
		e.setProperty("date", date_post);
		e.setProperty("url", "http://test.com");
		e.setProperty("body", "Ceci est un test. Restez tous calmes.");
		ArrayList<String> list_likers = new ArrayList<String>();
		list_likers.add("");
		e.setProperty("likers", list_likers);
		long like = 0;
		e.setProperty("likec", like);
		datastore.put(e);
				
		Key clePost = datastore.put(e);
	    Long idPostCree = clePost.getId();
		    
	    // Create the like counter
	    for (int i = 1; i <= 10; i++){
	
	        Entity compteurLikePost = new Entity("GestionCompteur");
	        compteurLikePost.setProperty("idPost", idPostCree);
	        compteurLikePost.setProperty("nomSousCompteur", "SC" + i);
	        compteurLikePost.setProperty("valeurCompteur", 0);
	        datastore.put(compteurLikePost);
	    }
    
	    endTime = System.currentTimeMillis();
        delta += endTime-startTime;
	}
	
	Entity result = new Entity("ResultPost");
	result.setProperty("user", userTest);
	result.setProperty("time", delta/30);
	datastore.put(result);
	}
	
	

	// On calcule le max de likes en 1 seconde, sur un post choisi complètement aléatoirement
	
	@ApiMethod(name = "LikeTest", path = "/myApi/v1/LikeTest", httpMethod = HttpMethod.POST)
    public void LikeTest(@Named("idPost") String idPost, @Named("id_post") String id_post) {
	
	long startTime = 0;
    long delta = 0;
    long endTime = 0;
		
	// Sélection du post en question
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Filter filterPost = new Query.FilterPredicate("id_post", Query.FilterOperator.EQUAL, id_post);  
    Query query3 = new Query("Post").setFilter(filterPost);
    Entity post = ds.prepare(query3).asSingleEntity();
	
    for(int i=1 ; i<=300 ; i++) {
    	
    	startTime = System.currentTimeMillis();
    
		// Choix random d'un sous-compteur de likes du post
	    int nbCompteurs = 10;
	    int compteurChoisi = (int)(Math.random() * nbCompteurs + 1);
	    String nomSousCompteurChoisi = "SC" + compteurChoisi;
	    	        
	    // Conversion de l'id en long pour la suite
	    long idPost2 = Long.parseLong(idPost);
	
	    // Sélection du sous-compteur choisi
	    Filter filterID = new Query.FilterPredicate("idPost", Query.FilterOperator.EQUAL, idPost2);
	    Filter filterCPT = new Query.FilterPredicate("nomSousCompteur", Query.FilterOperator.EQUAL, nomSousCompteurChoisi);
	    CompositeFilter filterFus = CompositeFilterOperator.and(filterID, filterCPT);
	    Query query = new Query("GestionCompteur").setFilter(filterFus);
	    Entity gestLikesPost = ds.prepare(query).asSingleEntity();
	
	    // +1 sur sous-compteur choisi
	    Long tmpLike = (Long)gestLikesPost.getProperty("valeurCompteur");
	    gestLikesPost.setProperty("valeurCompteur", tmpLike + 1);
	    ds.put(gestLikesPost);
    	
    	endTime = System.currentTimeMillis();
        delta += endTime-startTime;
	}
    
    // Sélection de l'ensemble des sous-compteurs du post
    long idPost2 = Long.parseLong(idPost);
    Filter filterID = new Query.FilterPredicate("idPost", Query.FilterOperator.EQUAL, idPost2);
    Query query2 = new Query("GestionCompteur").setFilter(filterID);
    PreparedQuery pq = ds.prepare(query2);
    List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());

    long totalLikesPost = 0;

    // Somme des sous-compteurs
    for (Entity r : result){
        totalLikesPost += (Long)(r.getProperty("valeurCompteur"));
    }
         	
    // Actualisation du compteur global de likes
    post.setProperty("likec", totalLikesPost);
    
    double likes = ((double)totalLikesPost/delta)*1000;
    
	Entity res = new Entity("ResultLikes");
	res.setProperty("nb_likes", likes);
	ds.put(res);
	 
	}
	
	
//////////////////////////////////////////// BONUS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	
	// On crée 300 users : 200 se follow entre-eux, 50 autres se follows aussi entre-eux, et les 50 derniers ne follow
	// et ne sont follow par personne. 
	@ApiMethod(name = "NewUsers", httpMethod = HttpMethod.POST)
	public void NewUsers() {

		// Create 200 users who follow each other
		for (int i = 0; i < 200; i++) {
			Entity e = new Entity("User");
			e.setProperty("email", "f" + i + "@test.fr");
			//ArrayList<String> empty_L = new ArrayList<String>();
			//empty_L.add("");
			//e.setProperty("followers", empty_L);
			//e.setProperty("follows", empty_L);			
			e.setProperty("nom", "last" + i);
			e.setProperty("prenom", "first" + i);
			e.setProperty("userName", "first" + i + " " + "last" + i);

			// Create followers and follows
			ArrayList<String> fset = new ArrayList<String>();
			fset.add("");
			for (int j = 0; j < 200; j++) {
				fset.add("f" + j + "@test.fr");
			}
			fset.remove("f" + i + "@test.fr");
			e.setProperty("followers", fset);
			e.setProperty("follows", fset);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);
		}
		
		// Create 50 users who follow each other
		for (int i = 200; i < 250; i++) {
			Entity e = new Entity("User");
			e.setProperty("email", "f" + i + "@test.fr");
			//ArrayList<String> empty_L = new ArrayList<String>();
			//empty_L.add("");
			//e.setProperty("followers", empty_L);
			//e.setProperty("follows", empty_L);			
			e.setProperty("nom", "last" + i);
			e.setProperty("prenom", "first" + i);
			e.setProperty("userName", "first" + i + " " + "last" + i);

			// Create followers and follows
			ArrayList<String> fset = new ArrayList<String>();
			fset.add("");
			for (int j = 200; j < 250; j++) {
				fset.add("f" + j + "@test.fr");
			}
			fset.remove("f" + i + "@test.fr");
			e.setProperty("followers", fset);
			e.setProperty("follows", fset);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);
		}
		
		// Create 50 lonely users, life can be hard
		for (int i = 250; i < 300; i++) {
			Entity e = new Entity("User");
			e.setProperty("email", "f" + i + "@test.fr");
			//ArrayList<String> empty_L = new ArrayList<String>();
			//empty_L.add("");
			//e.setProperty("followers", empty_L);
			//e.setProperty("follows", empty_L);			
			e.setProperty("nom", "last" + i);
			e.setProperty("prenom", "first" + i);
			e.setProperty("userName", "first" + i + " " + "last" + i);

			// Create 0 followers and 0 follows (sad reacts only)
			ArrayList<String> fset = new ArrayList<String>();
			fset.add("");
			e.setProperty("followers", fset);
			e.setProperty("follows", fset);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);
		}		
	}
	
	
	
	// On crée 300 posts, un pour chacun de nos 300 users fictifs. Pour cela, on reprend exactement le même code que 
	// pour la fonction de création de post
	@ApiMethod(name = "NewPosts", httpMethod = HttpMethod.POST)
	public void NewPosts() throws ParseException {
		for (int i = 1; i <= 500; i++) {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity e = new Entity("Post");
		
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date_today = new Date();			
			String date_post = format.format(date_today);
			
			// Propriétés de chaque post
			e.setProperty("id_post", date_post + " - " + "f" + i + "@test.fr");
			e.setProperty("owner", "f" + i + "@test.fr");
			e.setProperty("date", date_post);
			e.setProperty("url", "http://placehold.it/120x120&text=image" + i);
			e.setProperty("body", "Hi, my name is first" + i + ", I like potatoes.");
			ArrayList<String> list_likers = new ArrayList<String>();
			list_likers.add("");
			e.setProperty("likers", list_likers);
			long like = 0;
			e.setProperty("likec", like);
			datastore.put(e);
					
		Key clePost = datastore.put(e);
	    Long idPostCree = clePost.getId();
		    
	    // Create the like counter
	    for (int j = 1; j <= 10; j++){

	        Entity compteurLikePost = new Entity("GestionCompteur");
	        compteurLikePost.setProperty("idPost", idPostCree);
	        compteurLikePost.setProperty("nomSousCompteur", "SC" + j);
	        compteurLikePost.setProperty("valeurCompteur", 0);
	        datastore.put(compteurLikePost);
	    }
		}
	}
	
	
	
	// On ajoute des follows et followers fictifs (faux comptes précédemment créés) à un de nos comptes pour faire des tests
	@ApiMethod(name = "NewFollows", path = "/myApi/v1/NewFollows", httpMethod = HttpMethod.POST)
	public void NewFollows(@Named("user_id") String user_id) {
		// Sélection du user
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filterUser = new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, user_id);  
        Query query = new Query("User").setFilter(filterUser);
        Entity user = ds.prepare(query).asSingleEntity();
        
        // Liste des follows et followers
        ArrayList<String> list_follows = new ArrayList<String>();
        ArrayList<String> list_followers = new ArrayList<String>();
        
        // Ajout de follows + followers (5 dans ce qui ont 200 follows, 5 dans ceux qui en ont 50, et 5 dans ceux 
        // qui en ont 0)
        for(int i = 1; i <= 5; i++){
        	list_follows.add("f" + i + "@test.fr");
        	list_followers.add("f" + i + "@test.fr");
        }
        for(int j = 200; j <= 205; j++){
        	list_follows.add("f" + j + "@test.fr");
        	list_followers.add("f" + j + "@test.fr");
        }
        for(int k = 250; k <= 255; k++){
        	list_follows.add("f" + k + "@test.fr");
        	list_followers.add("f" + k + "@test.fr");
        }
        user.setProperty("follows", list_follows);
        user.setProperty("followers", list_followers);
        ds.put(user);
        
        
        // Puis on ajoute le user dans les listes de follows et de followers des comptes fictifs
        for(int i = 1; i <= 5; i++){
	        DatastoreService datastore1 = DatastoreServiceFactory.getDatastoreService();
	        Filter filterUs1 = new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, "f" + i + "@test.fr");  
	        Query query1 = new Query("User").setFilter(filterUs1);
	        Entity f = ds.prepare(query1).asSingleEntity();
	        
	        // Liste des follows et followers
	        ArrayList<String> list_follows1 = (ArrayList<String>)f.getProperty("follows");
	        ArrayList<String> list_followers1 = (ArrayList<String>)f.getProperty("followers");
	        
	        // Ajout du user en questio)n aux listes
	        list_follows1.add(user_id);
        	list_followers1.add(user_id);
        	
        	f.setProperty("follows", list_follows1);
            f.setProperty("followers", list_followers1);
        	
        	datastore1.put(f);
        }
        for(int j = 200; j <= 205; j++){
	        DatastoreService datastore2 = DatastoreServiceFactory.getDatastoreService();
	        Filter filterUs1 = new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, "f" + j + "@test.fr");  
	        Query query1 = new Query("User").setFilter(filterUs1);
	        Entity f = ds.prepare(query1).asSingleEntity();
	        
	        // Liste des follows et followers
	        ArrayList<String> list_follows1 = (ArrayList<String>)f.getProperty("follows");
	        ArrayList<String> list_followers1 = (ArrayList<String>)f.getProperty("followers");
	        
	        // Ajout du user en question aux listes
	        list_follows1.add(user_id);
        	list_followers1.add(user_id);
        	
        	f.setProperty("follows", list_follows1);
            f.setProperty("followers", list_followers1);
        	
        	datastore2.put(f);
        }
        for(int k = 250; k <= 255; k++){
	        DatastoreService datastore2 = DatastoreServiceFactory.getDatastoreService();
	        Filter filterUs1 = new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, "f" + k + "@test.fr");  
	        Query query1 = new Query("User").setFilter(filterUs1);
	        Entity f = ds.prepare(query1).asSingleEntity();
	        
	        // Liste des follows et followers
	        ArrayList<String> list_follows1 = (ArrayList<String>)f.getProperty("follows");
	        ArrayList<String> list_followers1 = (ArrayList<String>)f.getProperty("followers");
	        
	        // Ajout du user en question aux listes
	        list_follows1.add(user_id);
        	list_followers1.add(user_id);
        	
        	f.setProperty("follows", list_follows1);
            f.setProperty("followers", list_followers1);
        	
        	datastore2.put(f);
        }
	}
	

	
	// On accède aux 10, 100, 500 derniers posts que le user follow	
	@ApiMethod(name = "last10Posts", path = "/myApi/v1/last10Posts",httpMethod = HttpMethod.GET)
	public Entity last10Posts() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		int nbMeasures = 30;
		long time = 0;
		
		
		for(int i=0;i<nbMeasures;i++) {
			long startTime = System.currentTimeMillis();
			
			
			Query q = new Query("Post").addSort("date", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			
			List<Entity> posts = pq.asList(FetchOptions.Builder.withLimit(10));
			
			long endTime = System.currentTimeMillis();
			
			 time = time + (endTime - startTime);
			
		}
		
		long finalTime = time / nbMeasures;
		
		Entity e = new Entity("TimeMeasured");
		e.setProperty("timeMilliSec", finalTime);
		
		return e;
		
	}
	
	@ApiMethod(name = "last100Posts", path = "/myApi/v1/last100Posts",httpMethod = HttpMethod.GET)
	public Entity last100Posts() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		int nbMeasures = 30;
		long time = 0;
		
		
		for(int i=0;i<nbMeasures;i++) {
			long startTime = System.currentTimeMillis();
			
			
			Query q = new Query("Post").addSort("date", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			
			List<Entity> posts = pq.asList(FetchOptions.Builder.withLimit(100));
			
			long endTime = System.currentTimeMillis();
			
			 time = time + (endTime - startTime);
			
		}
		
		long finalTime = time / nbMeasures;
		
		Entity e = new Entity("TimeMeasured");
		e.setProperty("timeMilliSec", finalTime);
		
		return e;
		
	}
	
	@ApiMethod(name = "last500Posts", path = "/myApi/v1/last500Posts",httpMethod = HttpMethod.GET)
	public Entity last500Posts() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		int nbMeasures = 30;
		long time = 0;
		
		
		for(int i=0;i<nbMeasures;i++) {
			long startTime = System.currentTimeMillis();
			
			Query q = new Query("Post").addSort("date", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			
			List<Entity> posts = pq.asList(FetchOptions.Builder.withLimit(500));
			
			long endTime = System.currentTimeMillis();
			
			 time = time + (endTime - startTime);
			
		}
		
		long finalTime = time / nbMeasures;
		
		Entity e = new Entity("TimeMeasured");
		e.setProperty("timeMilliSec", finalTime);
		
		return e;
		
	}
	
}