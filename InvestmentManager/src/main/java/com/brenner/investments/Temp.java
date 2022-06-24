package com.brenner.investments;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Temp {

	public static void main(String[] args) {
		
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("pass"));

	}

}
