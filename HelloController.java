package ro.bcr.authserver.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class HelloController {

	private ClientDetailsService clientDetailsService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView welcomePage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Custom Login Form");
		model.addObject("message", "This is welcome page!");
		model.setViewName("hello");
		return model;
	}

	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "BCR Authentication test form");
		model.addObject("message", "This is protected page!");
		model.setViewName("admin");

		return model;
	}

	//Spring Security see this :
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			HttpSession session) {
		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid username and password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}

		SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		String clientName = "";
		if (savedRequest != null) {
			Map<String,String[]> map = savedRequest.getParameterMap();
			if(map.get("client_id") != null && map.get("client_id").length>0) {

				try {
					String query = "SELECT client_name FROM OAUTH_CLIENT_DETAILS WHERE client_id='" + map.get("client_id")[0] + "'";
					clientName = this.jdbcTemplate.queryForObject(query, String.class);
				} catch (SQLWarningException e) {
					e.printStackTrace();
				}


				System.out.println("CLIENT ID - LogIn = " + map.get("client_id")[0]);
				model.addObject("clientName", clientName);
			}
		}

		model.setViewName("login");

		return model;
	}

	//Spring Security see this :
	@RequestMapping(value = "/client", method = RequestMethod.GET)
	public ModelAndView client(
		@RequestParam(value = "code", required = false) String code) {

		ModelAndView model = new ModelAndView();
		if (code != null) {
			model.addObject("authCode", code);
		}

		model.setViewName("client");

		return model;
	}

	@Autowired
	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}
	
}