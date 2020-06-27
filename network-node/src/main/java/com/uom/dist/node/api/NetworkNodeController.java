package com.uom.dist.node.api;

import com.uom.dist.node.connector.UDPMessageSender;
import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.service.ProtocolFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class NetworkNodeController {

	@Autowired
	private ProtocolFactory protocolFactory;

	@Autowired
	private UDPMessageSender udpMessageSender;

	@RequestMapping("/find")
	public String index(@RequestParam("message") String message) throws Exception {
		Protocol decode = protocolFactory.decode(message);
		udpMessageSender.send(decode, 10001, "127.0.0.1");
		return decode.serialize();
	}
}