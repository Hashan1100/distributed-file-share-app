package com.uom.dist.node.api;

import com.uom.dist.node.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class NetworkNodeController {

	@Autowired
	private RoutingService routingService;

	@RequestMapping("/routing")
	public String index() {
		return routingService.getRoutingTableValues();
	}
}