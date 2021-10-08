package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.responsitory.CartItemReponsitory;
import com.example.demo.responsitory.OrderRepository;
import com.example.demo.responsitory.OrderStatusRepository;
import com.example.demo.service.CartItemService;
import com.example.demo.service.UserService;
import com.example.demo.utils.Utility;

@Controller
public class ShoppingCartController {

	@Autowired
	private CartItemService cartService;

	@Autowired
	private CartItemReponsitory cartRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private JavaMailSender mailSender;

	@GetMapping("/cart")
	public String showShoppingCart(Model model, Principal principal) {
		AppUser customer = userService.findByUserName(principal.getName());
		if (customer != null) {
			List<CartItem> cartItems = cartService.listCartItems(customer);
			model.addAttribute("cartSize", cartItems.size());
			model.addAttribute("cartItems", cartItems);

			return "shopping_cart";
		} else {
			return "loginPage";

		}
	}

	@GetMapping("/cart/delete/{id}")
	public String deteleCart(Model model, Principal principal, @PathVariable("id") Integer id) {
		if (principal != null) {
			cartService.deleteCart(id);
			return "redirect:/cart";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/order_list")
	public String orderListOfUser(Principal principal, Model model) {
		showShoppingCart1(model, principal);
		AppUser customer = userService.findByUserName(principal.getName());
		List<Order> orders = orderRepo.findByCustomer(customer);
		model.addAttribute("orders", orders);
		return "order_list";
	}

	@RequestMapping(value = "/cart/checkout", method = RequestMethod.GET)
	public String checkoutCart(Model model, Principal principal, @RequestParam("address") String address,
			@RequestParam("phone") String phone, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		AppUser customer = userService.findByUserName(principal.getName());
		Date createDate = new Date();
		List<CartItem> cartItem = cartRepo.findByCustomer(customer);

		if (customer != null) {
			double total = 0.0;
			for (CartItem cartItem2 : cartItem) {
				Order order = new Order();
				order.setProduct(cartItem2.getProduct());
				order.setCustomer(customer);
				order.setQuantity(cartItem2.getQuantity());
				order.setTotalPrice(cartItem2.getSubtotal());
				order.setOrderStatus("Đang chờ xử lí");
				order.setPhoneNumber(phone);
				order.setCreateDate(createDate);
				order.setAddress(address);
				orderRepo.save(order);
				cartRepo.delete(cartItem2);
				total += cartItem2.getSubtotal();
			}

			model.addAttribute("totalPrice", total);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom("contact@leoshop.com", "Leoshop Support");
			helper.setTo(customer.getEmail());

			try {
				String subject = "Please confirm your order.";
				String link = Utility.getSiteURL(request);
				String content = "<p>Hello, " + customer.getLastName() + " " + customer.getFirstName() + "</p>"
						+ "<p>You have placed your order at $" + total + " and we will contact you via your phone "
						+ phone + " to deliver your order within the next few days.</p>" + "<p>Have a good day!!!!</p>"
						+ "<p><a href=\"" + link + "\">Continue shopping >></a></p>";

				helper.setSubject(subject);
				helper.setText(content, true);
				mailSender.send(message);
				return "redirect:/order_list";
			} catch (Exception e) {
				return "redirect:/order_list";
			}

		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/confirmInfo")
	public String confirmInfo(Principal principal, Model model) {
		showShoppingCart1(model, principal);
		return "confirmInfo";
	}

	@GetMapping("/order_manage")
	public String orderAllList(Principal principal, Model model) {
		showShoppingCart1(model, principal);
		List<Order> orders = orderRepo.findAll();
		if (principal != null) {
			model.addAttribute("orders_list", orders);
			return "order_manage";
		} else {
			return "loginPage";
		}

	}

	@Autowired
	private OrderStatusRepository orderStatusRepo;

	@GetMapping("/list_order_status")
	public String listOrderStatus(Model model) {
		List<OrderStatus> orderStatus = orderStatusRepo.findAll();
		model.addAttribute("list_order_status", orderStatus);
		return "list_order_status";
	}

	@GetMapping("/order_status")
	public String addOrderStatus() {
		return "order_status";
	}

	@PostMapping("/order_status_process")
	public String addOrderStatusProcess(@RequestParam("nameStatus") String nameStatus) {
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setNameStatus(nameStatus);
		orderStatusRepo.save(orderStatus);
		return "redirect:/list_order_status";
	}

	@PostMapping("/order_status_edit_process")
	public String editOrderStatusProcess(@ModelAttribute("orderStatus") OrderStatus orderStatus) {
		Optional<OrderStatus> orderStatusOptional = orderStatusRepo.findById(orderStatus.getId());
		OrderStatus orderStatusEntity = orderStatusOptional.get();
		orderStatusEntity.setNameStatus(orderStatus.getNameStatus());
		orderStatusRepo.save(orderStatus);
		return "redirect:/list_order_status";
	}

	@GetMapping("/order_status_edit/{id}")
	public String editOrderStatus(@PathVariable("id") Long id, Model model) {
		Optional<OrderStatus> orderStatusOptional = orderStatusRepo.findById(id);
		OrderStatus orderStatusEntity = orderStatusOptional.get();
		model.addAttribute("orderStatus", orderStatusEntity);
		return "order_status_edit";
	}

	@GetMapping("/order_status_delete/{id}")
	public String deleteOrderStatus(@PathVariable("id") Long id) {
		Optional<OrderStatus> orderStatusOptional = orderStatusRepo.findById(id);
		OrderStatus orderStatus = orderStatusOptional.get();
		orderStatusRepo.delete(orderStatus);
		return "redirect:/list_order_status";
	}

	@GetMapping("/edit/order/{id}")
	public ModelAndView editOrderProcess(@PathVariable("id") Long id, Principal principal, Model model) {
		showShoppingCart1(model, principal);

		List<OrderStatus> listOrderStatuss = orderStatusRepo.findAll();
		List<String> listOrderStatus = new ArrayList<String>();
		for (OrderStatus orderStatus : listOrderStatuss) {
			listOrderStatus.add(orderStatus.getNameStatus());
		}
		model.addAttribute("listOrderStatus", listOrderStatus);

		ModelAndView mav;
		Optional<Order> order = orderRepo.findById(id);
		if (principal != null) {
			if (!order.isPresent()) {
				mav = new ModelAndView("404");
				return mav;
			} else {
				mav = new ModelAndView("edit_order");
				mav.addObject("orderModel", order);
				return mav;
			}
		} else {
			mav = new ModelAndView("loginPage");
			return mav;
		}
	}

	@PostMapping("/edit/order")
	public String editAdminOrder(@ModelAttribute("orderModel") Order orderFrom, Principal principal, Model model) {

		Order order = orderRepo.findById(orderFrom.getId()).get();
		if (principal != null) {
			order.setOrderStatus(orderFrom.getOrderStatus());
			orderRepo.save(order);
			return "redirect:/order_manage";
		} else {
			return "loginPage";
		}
	}

	@GetMapping("/delete/order/{id}")
	public String deleteOrder(@PathVariable("id") Long id, Principal principal, Model model) {
		Order order = orderRepo.findById(id).get();
		if (principal != null) {
			orderRepo.delete(order);
			return "redirect:/order_manage";
		} else {
			return "loginPage";
		}
	}

	@GetMapping("/cancel/order/{id}")
	public String cancelOrder(@PathVariable("id") Long id, Principal principal, Model model, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		Order order = orderRepo.findById(id).get();
		if (principal != null) {
			if (order.getOrderStatus().equals("Đã hủy")) {
				return "redirect:/order_list";
			}else {
				order.setOrderStatus("Đã hủy");
				orderRepo.save(order);
				// start send mail cancel order
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message);

				helper.setFrom("contact@leoshop.com", "Leoshop Support");
				String customerEmail = userService.findByUserName(principal.getName()).getEmail();
				helper.setTo(customerEmail);
				String subject = "Confirm cancellation of your order.";
				String link = Utility.getSiteURL(request);
				String content = "<p>Hello, " + principal.getName() + "</p>" + "You canceled your order on "
						+ order.getCreateDate() + ".</p>" + "<p>Have a good day!!!!</p>" + "<p><a href=\"" + link
						+ "\">Continue shopping >></a></p>";

				helper.setSubject(subject);
				helper.setText(content, true);
				mailSender.send(message);
				// end send mail cancel order
				return "redirect:/order_list";
			}
			
		} else {
			return "loginPage";
		}
	}

	public String showShoppingCart1(Model model, Principal principal) {
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
