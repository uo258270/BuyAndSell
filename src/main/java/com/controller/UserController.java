package com.controller;

import java.security.Principal;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import org.springframework.stereotype.Controller;
import com.entity.UserEntity;
import com.service.impl.UserServiceImpl;
import com.validators.SignUpFormValidator;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	
	@Autowired
	private HttpSession httpSession;

	@Autowired
	private UserServiceImpl usersService;

	@Autowired
	private SignUpFormValidator signUpFormValidator;
	
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model model) {
		model.addAttribute("user", new UserEntity());
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(@Validated UserEntity user, BindingResult result) throws Exception {
		signUpFormValidator.validate(user, result);
		if (result.hasErrors()) {
			return "signup";
		}

		user.setRole(usersService.getRoles()[0]);
		usersService.addUser(user);
		usersService.autoLogin(user.getEmail(), user.getPasswordConfirm());
		return "redirect:home";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		return "login";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(Model model, Principal principal) throws Exception {
		UserEntity user = usersService.findByEmail(principal.getName());
	//	httpSession.setAttribute("money", user.getMoney());
		httpSession.setAttribute("email", user.getEmail());
		
		return "home";
	}

	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public String list(Model model) {
		
		model.addAttribute("usersList", usersService.getStandardUsers());
		return "user/list";
	}

	@RequestMapping(value = "/user/delete", method = RequestMethod.POST)
	public String delete(Model model, @RequestParam Map<String, String> deletedUsers) throws Exception {

		for (Map.Entry<String, String> entry : deletedUsers.entrySet()) {
			usersService.deleteUser(usersService.findByEmail(entry.getValue()).getUserId());
		}

		
		model.addAttribute("usersList", usersService.getStandardUsers());

		return "user/list";
	}
}
