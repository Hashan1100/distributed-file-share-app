package com.uom.dist.node.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NetworkNodeController {

	@RequestMapping("/find")
	public String index() {
			return "File found returning the file";
	}

}