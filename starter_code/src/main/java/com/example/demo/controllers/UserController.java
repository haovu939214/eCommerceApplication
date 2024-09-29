package com.example.demo.controllers;


import net.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger logs = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logs.info("Find user with Id: {}", id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logs.info("Find user with name: {}", username);
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws Exception {
		logs.info("Begin create user with name: {}", createUserRequest.getUsername());
		User user = userRepository.findByUsername(createUserRequest.getUsername());
		if (user != null) {
			logs.error("Username {} exist", createUserRequest.getUsername());
			throw new Exception("Username is exist");
		}
		if(createUserRequest.getPassword().length() < 7) {
			logs.error("Password less 7 characters");
			throw new Exception("Password less more 7 characters");
		}

		if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			logs.error("Confirm password not mapping");
			throw new Exception("Confirm password not mapping");
		}
		User user1 = new User();
		user1.setUsername(createUserRequest.getUsername());
		user1.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		Cart cart = new Cart();
		cartRepository.save(cart);
		user1.setCart(cart);
		userRepository.save(user1);
		logs.info("Created user {} OK", createUserRequest.getUsername());
		return ResponseEntity.ok(user1);
	}
	
}
