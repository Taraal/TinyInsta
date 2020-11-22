package package_insta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
		
			// On calcule l'écart temporel entre notre post et une date lointaine fixe (an 2300 ici). Ainsi, plus notre 
			// post est récent, plus cet écart est faible. En basant nos requêtes sur cet écart de temps, les posts les 
			// plus récents sont donc analysés les premiers, ce qui nous permet de ressortir d'abord les posts les plus 
			// récents 
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String dateInString = "2300-01-01 00:00:00.000";
			Date date = format.parse(dateInString);
			Date date_today = new Date();
			
			String date_post = String.valueOf(date.getTime()-date_today.getTime());
			while (date_post.length()<15) {
				date_post = "0"+date_post;
			}
			
			// Propriétés de chaque post
			e.setProperty("id_post", date_post + " - " + pm.owner);
			e.setProperty("owner", pm.owner);
			e.setProperty("date", new Date());
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

	
	
	@ApiMethod(name = "mypost", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> mypost(@Named("name") String name, @Nullable @Named("next") String cursorString) {

	    Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, name));
	    //text.substring(67, text.length() - 1));
	    

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
	
	

	// On crée 300 users : 200 se follow entre-eux, 50 autres se follows aussi entre-eux, et les 50 derniers ne follow
	// et ne sont follow par personne. 
	@ApiMethod(name = "NewUsers", httpMethod = HttpMethod.POST)
	public void NewUsers() {

		// Create 200 users who follow each other
		for (int i = 0; i < 200; i++) {
			Entity e = new Entity("User");
			e.setProperty("email", "f" + i + "@univ_chambery.fr");
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
				fset.add("first" + j + " " + "last" + j);
			}
			fset.remove("first" + i + " " + "last" + i);
			e.setProperty("followers", fset);
			e.setProperty("follows", fset);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);
		}
		
		// Create 50 users who follow each other
		for (int i = 200; i < 250; i++) {
			Entity e = new Entity("User", "f" + i);
			e.setProperty("email", "f" + i + "@univ_caen.fr");
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
				fset.add("first" + j + " " + "last" + j);
			}
			fset.remove("first" + i + " " + "last" + i);
			e.setProperty("followers", fset);
			e.setProperty("follows", fset);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(e);
		}
		
		// Create 50 lonely users, life can be hard
		for (int i = 250; i < 300; i++) {
			Entity e = new Entity("User", "f" + i);
			e.setProperty("email", "f" + i + "@univ_perpignan.fr");
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
		for (int i = 0; i < 300; i++) {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity e = new Entity("Post");
		
			// On calcule l'écart temporel entre notre post et une date lointaine fixe (an 2300 ici). Ainsi, plus notre 
			// post est récent, plus cet écart est faible. En basant nos requêtes sur cet écart de temps, les posts les 
			// plus récents sont donc analysés les premiers, ce qui nous permet de ressortir d'abord les posts les plus 
			// récents 
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String dateInString = "2300-01-01 00:00:00.000";
			Date date = format.parse(dateInString);
			Date date_today = new Date();
			
			String date_post = String.valueOf(date.getTime()-date_today.getTime());
			while (date_post.length()<15) {
				date_post = "0"+date_post;
			}
			
			// Propriétés de chaque post
			e.setProperty("id_post", date_post + " - " + "first" + i + " " + "last" + i);
			e.setProperty("owner", "first" + i + " " + "last" + i);
			e.setProperty("date", new Date());
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
	        compteurLikePost.setProperty("nomSousCompteur", "SC" + i);
	        compteurLikePost.setProperty("valeurCompteur", 0);
	        datastore.put(compteurLikePost);
	    }
		}
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

}