package ro.bcr.authserver.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import ro.bcr.authserver.Model.Scope;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Controller for retrieving the model for and displaying the confirmation page
 * for access to a protected resource.
 *
 * @author Ryan Heaton
 */
@Controller
@SessionAttributes(types = AuthorizationRequest.class)
public class AccessConfirmationController {

  private ClientDetailsService clientDetailsService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @RequestMapping("/oauth/confirm_access")
  public ModelAndView getAccessConfirmation(@ModelAttribute AuthorizationRequest clientAuth) throws Exception {
    ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());

    System.out.println("CLIENT ID="+client.getClientId());
    if (clientAuth.getScope() != null) {
		for (String c : clientAuth.getScope()) {
			System.out.println("ARE "+c);
		}
	}
    String clientName = "";
    try {
        String query = "SELECT client_name FROM OAUTH_CLIENT_DETAILS WHERE client_id='" + client.getClientId() + "'";
        clientName = this.jdbcTemplate.queryForObject(query, String.class);
    } catch (SQLWarningException e) {
          e.printStackTrace();
    }

    // -----------------------
    String sql = "SELECT * FROM OAUTH_SCOPES_TL WHERE client_id='" + client.getClientId() + "' ORDER BY language ";
    List<Scope> scopes = new ArrayList<Scope>();
    JSONObject list = new JSONObject();
    JSONArray en = new JSONArray();
    JSONArray ro = new JSONArray();

    List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
    for(Map row:rows) {
      JSONObject obj = new JSONObject();
      obj.put("clientID", (String)row.get("client_id"));
      obj.put("scope", (String)row.get("scope"));
      obj.put("scope_text", (String)row.get("scope_text"));
      if( row.get("language").equals("en") ) {
        en.add(obj);
      } else {
        ro.add(obj);
      }



      Scope scope = new Scope();
      scope.setClientID((String)row.get("client_id"));
      scope.setLanguage((String)row.get("language"));
      scope.setScope((String)row.get("scope"));
      scope.setScope_text((String)row.get("scope_text"));
      scopes.add(scope);
    }
    // -----------------------
    list.put("en",en);
    list.put("ro",ro);

    System.out.println("SCOPES: " + scopes.toString());
    System.out.println("JSON: " + list.toJSONString());


    TreeMap<String, Object> model = new TreeMap<String, Object>();
    model.put("auth_request", clientAuth);
    model.put("client", client);
    model.put("clientName",clientName);
    model.put("scopes",list);
    return new ModelAndView("access_confirmation", model);
  }

  @Autowired
  public void setClientDetailsService(ClientDetailsService clientDetailsService) {
    this.clientDetailsService = clientDetailsService;
  }
}