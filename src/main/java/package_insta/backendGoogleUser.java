package package_insta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


@WebServlet(urlPatterns = {"/checkGoogleUser"})
public class backendGoogleUser extends HttpServlet {
	
	    @Override
	    protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	    	resp.setContentType("text/html");

	        try {
	            String idToken = req.getParameter("id_token");
	            
	            
	            GoogleIdToken.Payload payLoad = IdTokenVerifier.getPayload(idToken);
	            String name = (String) payLoad.get("name");
	            String email = payLoad.getEmail();
	            boolean emailVerified = Boolean.valueOf(payLoad.getEmailVerified());	            
	            String pictureUrl = (String) payLoad.get("picture");
	            String locale = (String) payLoad.get("locale");
	            String nom = (String) payLoad.get("family_name");
	            String prenom = (String) payLoad.get("given_name");

	            
	            
	            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	            Query q = new Query("User").setFilter(new FilterPredicate("email", FilterOperator.EQUAL, email));

	    		PreparedQuery pq = datastore.prepare(q);
	    		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
	    		
	    		//User doesn't exist in db -> add it to db
	    		if(result.isEmpty()) {
	    			Entity e = new Entity("User");
	    			e.setProperty("userName", name);
	    			e.setProperty("nom", nom);
	    			e.setProperty("prenom", prenom);
	    			e.setProperty("email", email);
					e.setProperty("subscribers", new ArrayList<String>());
					e.setProperty("subscriptions", new ArrayList<String>());
	    			datastore.put(e);
	    			
	    			req.getSession().setAttribute("email", email);
		            resp.sendRedirect(req.getContextPath() + "/post.html");
	    		}
	    		
	    		//User does exist 
	    		else {
	    			
	    			req.getSession().setAttribute("email", email);
		            resp.sendRedirect(req.getContextPath() + "/post.html");
	    		}
	            
	            
	            
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
	

		
		
}

