package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class CreateUserRequest {

	@JsonProperty
	@NotEmpty(message = "Username must not be null or blank")
	private String username;

	@JsonProperty
	@NotEmpty(message = "Password must not be null or blank")
	private String password;

	@JsonProperty
	@NotEmpty(message = "Confirm password must not be null or blank")
	private String confirmPassword;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
