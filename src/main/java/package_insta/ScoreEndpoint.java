package package_insta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity e = new Entity("Post");
			e.setProperty("id_post", pm.owner + new Date());
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
	        System.out.println(list_likers.remove(owner));
	        post.setProperty("likers", list_likers);
	        ds.put(post); 
		}
		
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
		
		
}