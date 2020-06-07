package com.uom.dist.bootstrap.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class BootstrapController {

	@RequestMapping("/connect")
	public String index() {
			return "Greetings from Spring Boot!";
	}

}