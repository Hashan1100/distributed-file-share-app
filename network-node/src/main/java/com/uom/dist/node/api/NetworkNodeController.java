package com.uom.dist.node.api;

import com.uom.dist.node.service.DownloadService;
import com.uom.dist.node.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class NetworkNodeController {

	@Autowired
	private RoutingService routingService;

	@Autowired
	private DownloadService downloadService;

	@RequestMapping("/routing")
	public String index() {
		return routingService.getRoutingTableValues();
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(@RequestParam("fileName") String fileName) {
		return downloadService.getResponse(fileName.replaceAll("^\"|\"$", ""));
	}
}