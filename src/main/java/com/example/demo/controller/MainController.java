package com.example.demo.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.responsitory.ProductResponsitory;
import com.example.demo.service.CartItemService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import com.example.demo.utils.WebUtils;

@Controller
public class MainController {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductResponsitory productRepo;
	@Autowired
	private UserService userService;

	@Autowired
	private CartItemService cartService;

	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
	public String welcomePage(Model model, Principal principal) {

		List<Product> products = productService.listProduct();
		Collections.sort(products, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		List<Product> product = products.stream().limit(9).collect(Collectors.toList());
		model.addAttribute("products", product);
		showShoppingCart(model, principal);
		return "welcomePage";

	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage(Model model) {

		return "loginPage";
	}

	@RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
	public String logoutSuccessfulPage(Model model) {
		model.addAttribute("title", "Logout");
		return "logoutSuccessfulPage";
	}

	@RequestMapping(value = "/edit_userInfo", method = RequestMethod.POST)
	public String editUserInfo(Model model, Principal principal, @ModelAttribute("appuser") AppUser appuser) {

		String userName = principal.getName();
		AppUser user = userService.findByUserName(userName);
		user.setEmail(appuser.getEmail());
		user.setFirstName(appuser.getFirstName());
		user.setLastName(appuser.getLastName());
		user.setUserName(appuser.getUserName());
		userService.save(user);
		showShoppingCart(model, principal);
		return "redirect:/";
	}

	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	public String userInfo(Model model, Principal principal, @ModelAttribute("appuser") AppUser appuser) {

		String userName = principal.getName();
		AppUser user = userService.findByUserName(userName);
		ModelAndView mav = new ModelAndView();
		mav.addObject("app_user", user);
		model.addAttribute("userInfo", user);
		showShoppingCart(model, principal);
		return "userInfoPage";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String accessDenied(Model model, Principal principal) {

		if (principal != null) {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();

			String userInfo = WebUtils.toString(loginedUser);

			model.addAttribute("userInfo", userInfo);

			String message = "Hi, " + principal.getName() //
					+ "<br> You do not have permission to access this page!";
			model.addAttribute("message", message);

		}
		showShoppingCart(model, principal);
		return "403Page";
	}

	@RequestMapping(value = "/404", method = RequestMethod.GET)
	public String notFoundPage(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "404";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String aboutPage(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "about";
	}

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String contactPage(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "contact";
	}

	@RequestMapping(value = "/delivery", method = RequestMethod.GET)
	public String deliveryPage(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "delivery";
	}

	@RequestMapping(value = "/mens", method = RequestMethod.GET)
	public String mensPage(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "mens";
	}

	@RequestMapping(value = "/other", method = RequestMethod.GET)
	public String otherPage(Model model, HttpServletRequest request, Principal principal) {

		int page = 0;
		int size = 9;
		if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
			page = Integer.parseInt(request.getParameter("page")) - 1;
		}

		if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
			size = Integer.parseInt(request.getParameter("size"));
		}
		model.addAttribute("products", productRepo.findAll(PageRequest.of(page, size)));

		Page<Product> products = productRepo.findAll(PageRequest.of(page, size));
		List<Product> list = products.getContent();
		List<List<Product>> subProducts = ListUtils.partition(list, 3);

		model.addAttribute("products", subProducts);
		model.addAttribute("product", products);
		showShoppingCart(model, principal);
		return "other";
	}

	@RequestMapping(value = "/womens", method = RequestMethod.GET)
	public String womensPage(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "womens";
	}

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
}
