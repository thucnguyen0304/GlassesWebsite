package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.AppRole;
import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.UserRole;
import com.example.demo.responsitory.OrderRepository;
import com.example.demo.responsitory.UserResponsitory;
import com.example.demo.responsitory.UserRoleResponsitory;
import com.example.demo.service.CartItemService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserRoleService;
import com.example.demo.service.UserService;
import com.example.demo.utils.Utility;

@Controller
public class UserController {

	@Autowired
	private UserService service;

	@Autowired
	private UserRoleService serviceRole;
	@Autowired
	private RoleService roleSe;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserResponsitory userResponsitory;

	@Autowired
	private UserRoleResponsitory userRoleResponsitory;

	@Autowired
	private JavaMailSender mailSender;

	@RequestMapping("/listUser")
	public String viewHomePage(HttpServletRequest request, Model model, Principal principal) {
		model.addAttribute("users", userResponsitory.findAll());
		showShoppingCart(model, principal);
		return "listUser";
	}

	@RequestMapping("/new")
	public String showNewProductPage(Model model, Principal principal) {
		AppUser user = new AppUser();
		model.addAttribute("user", user);
		showShoppingCart(model, principal);
		return "new_user";
	}

	@RequestMapping("/create_account_success")
	public String verifyEmailPage() {
		return "create_account_success";
	}

	@RequestMapping(value = "/save_user", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute("user") AppUser user, RedirectAttributes re, HttpServletRequest request,
			Model model, Principal principal) throws UnsupportedEncodingException, MessagingException {
		showShoppingCart(model, principal);
		String userName = user.getUserName();
		String password = passwordEncoder.encode(user.getEncrytedPassword());
		String email = user.getEmail();
		String lastName = user.getLastName();
		String firstName = user.getLastName();
		if (service.findByEmail(email) == null) {
			if (service.findByUserName(userName) == null) {
				AppUser app_user = new AppUser(userName, password, true, email, lastName, firstName);
				service.save(app_user);
				AppUser app_user1 = service.get(app_user.getUserId());
				UserRole userRole = new UserRole();
				userRole.setAppRole(new AppRole(2L, "USER_ROLE"));
				userRole.setAppUser(
						new AppUser(app_user1.getUserId(), app_user1.getUserName(), app_user1.getEncrytedPassword(),
								true, app_user1.getEmail(), app_user1.getLastName(), app_user1.getFirstName()));
				String link = Utility.getSiteURL(request) + "/userInfo";

				serviceRole.save(userRole);
				try {
					sendEmail(email, link);
					return "redirect:/create_account_success";
				} catch (Exception e) {
					return "redirect:/userInfo";
				}

			} else {
				re.addFlashAttribute("userExist",
						"User [" + service.findByUserName(userName).getUserName() + "] is existed!!");
				return "redirect:/new";
			}
		} else {
			re.addFlashAttribute("userExist", "Email [" + email + "] is existed!!");
			return "redirect:/new";
		}

	}

	@RequestMapping(value = "/edit_user", method = RequestMethod.POST)
	public String editUser(@ModelAttribute("user") AppUser user, Model model, Principal principal) {
		AppUser user_app = service.get(user.getUserId());
		user_app.setUserName(user.getUserName());
		user_app.setEmail(user.getEmail());
		user_app.setFirstName(user.getFirstName());
		user_app.setLastName(user.getLastName());
		service.save(user_app);
		showShoppingCart(model, principal);
		return "redirect:/listUser";

	}

	@RequestMapping("/edit_user/{id}")
	public ModelAndView showEditProductPage(@PathVariable(name = "id") int id, Model model, Principal principal) {
		showShoppingCart(model, principal);
		try {
			AppUser user_app = service.get(id);
			ModelAndView mav = new ModelAndView("edit_user");
			mav.addObject("user", user_app);
			return mav;
		} catch (NoSuchElementException e) {
			ModelAndView mav1 = new ModelAndView("404");
			return mav1;
		}

	}

	@Autowired
	private OrderRepository orderRepo;

	@RequestMapping("/delete_user/{id}")
	public String deleteProduct(@PathVariable(name = "id") Long id) {
		AppUser user = service.get(id);
		UserRole role = serviceRole.findByUserId(user);
		List<Order> order = orderRepo.findByCustomer(user);
		for (Order order2 : order) {
			orderRepo.delete(order2);
		}
		service.delete(id);
		serviceRole.delete(role.getId());

		return "redirect:/listUser";
	}

	// role user
	@RequestMapping("/listRole")
	public String viewListRole(HttpServletRequest request, Model model, Principal principal) {
		model.addAttribute("roles", userRoleResponsitory.findAll());
		showShoppingCart(model, principal);
		System.out.println(userResponsitory.findAll());
		return "listRole";
	}

	@RequestMapping("/add_role")
	public String addRole(Model model, Principal principal) {
		UserRole userRole = new UserRole();
		model.addAttribute("userRole", userRole);
		showShoppingCart(model, principal);
		return "add_role";
	}

	@RequestMapping(value = "/add_role_process", method = RequestMethod.POST)
	public String saveProduct(@ModelAttribute("userRole") UserRole userRole, Model model, Principal principal) {
		serviceRole.save(userRole);
		showShoppingCart(model, principal);
		return "redirect:/listRole";
	}

	@RequestMapping("/edit_role/{id}")
	public ModelAndView showEditRolePage(@PathVariable(name = "id") int id, Model model, Principal principal) {
		ModelAndView mav = new ModelAndView("edit_role");
		UserRole userRole = serviceRole.get(id);
		mav.addObject("userRole", userRole);
		showShoppingCart(model, principal);

		return mav;
	}

	// role
	@RequestMapping("/list_role")
	public String listRole(Model model, Principal principal) {
		List<AppRole> listRole1 = roleSe.listAll();
		model.addAttribute("listRole1", listRole1);
		showShoppingCart(model, principal);
		return "list_role";
	}

	@RequestMapping("/new_role1")
	public String showNewRolePage(Model model, Principal principal) {
		AppRole rolee = new AppRole();
		model.addAttribute("rolee", rolee);
		showShoppingCart(model, principal);
		return "new_role1";
	}

	@RequestMapping(value = "/save_role", method = RequestMethod.POST)
	public String saveProduct(@ModelAttribute("role1") AppRole role) {
		roleSe.save(role);
		return "redirect:/list_role";
	}

	@RequestMapping("/edit_role1/{id}")
	public ModelAndView showEditRolePage1(@PathVariable(name = "id") int id, Model model, Principal principal) {
		ModelAndView mav = new ModelAndView("edit_role1");
		AppRole role = roleSe.get(id);
		showShoppingCart(model, principal);
		mav.addObject("role1", role);

		return mav;
	}

	@RequestMapping("/delete_role1/{id}")
	public String deleteProduct(@PathVariable(name = "id") int id) {
		roleSe.delete(id);
		return "redirect:/list_role";
	}
	// role

	@Autowired
	private UserService userService;

	@Autowired
	private CartItemService cartService;

	public String showShoppingCart(Model model, Principal principal) {
		if (principal != null) {
			AppUser customer = userService.findByUserName(principal.getName());
			List<CartItem> cartItems = cartService.listCartItems(customer);
			model.addAttribute("cartItems", cartItems);
		} else {
			return "loginPage";
		}
		return null;
	}

	public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		AppUser user = userService.findByEmail(recipientEmail);

		helper.setFrom("contact@leoshop.com", "Leoshop Support");
		helper.setTo(recipientEmail);

		String subject = "You have successfully registered for an account.";

		String content = "<p>Hello, " + user.getUserName() + "</p>"
				+ "<p>Click on the link below to go to the login page:</p>" + "<p><a href=\"" + link
				+ "\">Login now to here</a></p>";

		helper.setSubject(subject);

		helper.setText(content, true);
		mailSender.send(message);
	}

}
