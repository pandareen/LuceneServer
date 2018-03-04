package com.luceneserver.suggest.core;

import java.io.Serializable;

public class UserObject implements Serializable{

	private String username;
	private String email;
	private long repositoryCode;
	private long status;
	private String firstName;
	private String lastName;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getRepositoryCode() {
		return repositoryCode;
	}

	public void setRepositoryCode(long repositoryCode) {
		this.repositoryCode = repositoryCode;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
