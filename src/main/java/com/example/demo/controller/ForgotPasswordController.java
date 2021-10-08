package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.service.CartItemService;
import com.example.demo.service.CustomerNotFoundException;
import com.example.demo.service.UserService;
import com.example.demo.utils.Utility;

import net.bytebuddy.utility.RandomString;

@Controller
public class ForgotPasswordController {
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserService userService;

	@GetMapping("/forgot_password")
	public String showForgotPasswordForm(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "forgot_password_form";
	}

	public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		AppUser user = userService.findByEmail(recipientEmail);
	
		helper.setFrom("contact@leoshop.com", "Leoshop Support");
		helper.setTo(recipientEmail);

		String subject = "Here's the link to reset your password";

		String content = "<p>Hello, " + user.getUserName() + "</p>"
				+ "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + link
				+ "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password, "
				+ "or you have not made the request.</p>";

		helper.setSubject(subject);

		helper.setText(content, true);
		mailSender.send(message);

	}

	@PostMapping("/forgot_password")
	public String processForgotPassword(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		String token = RandomString.make(30);

		try {
			userService.updateResetPasswordToken(token, email);

			String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(email, resetPasswordLink);
			model.addAttribute("message", "We have sent a reset password link to your email. Please check...");

		} catch (CustomerNotFoundException ex) {
			model.addAttribute("error", ex.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "Error while sending email");
		}

		return "forgot_password_form";

	}

	@GetMapping("/reset_password")
	public String showResetPasswordForm(@Param(value = "token") String token, Model model, Principal principal) {
		showShoppingCart(model, principal);
		AppUser customer = userService.getByResetPasswordToken(token);
		model.addAttribute("token", token);

		if (customer == null) {
			model.addAttribute("message", "Invalid Token");
			return "message";
		}

		return "reset_password_form";
	}

	@PostMapping("/reset_password")
	public String processResetPassword(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");

		AppUser customer = userService.getByResetPasswordToken(token);
		model.addAttribute("title", "Reset your password");

		if (customer == null) {
			model.addAttribute("message", "Invalid Token");
		} else {
			System.out.println("User Name" + customer.getUserName());
			userService.updatePassword(customer, password);

			model.addAttribute(" ", "You have successfully changed your password.");
		}

		return "loginPage";
	}

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
}
