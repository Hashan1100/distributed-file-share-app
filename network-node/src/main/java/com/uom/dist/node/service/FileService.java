package com.uom.dist.node.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.SearchRequest;
import com.uom.dist.protocol.SearchResponse;
import com.uom.dist.protocol.service.ProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class FileService {

    @Value("${file.list}")
    private List<String> fileList;

    private LoadingCache<String, SearchRequest> loadingCache;

    @Autowired
    private ProtocolFactory protocolFactory;

    @Autowired
    private FileService fileService;

    @Autowired
    private RoutingService routingService;

    @Value("${udp.receiver.port}")
    private int udpPort;

    @Value("${udp.receiver.url}")
    private String udpUrl;

    @Autowired
    private Node node;

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @PostConstruct
    public void init(){
        this.loadingCache = CacheBuilder
                .newBuilder()
                .maximumSize(100)
                .expireAfterAccess(2, TimeUnit.SECONDS)
                .build(
                        new CacheLoader<String, SearchRequest>() {
                            public SearchRequest load(String message) throws Exception {
                                Protocol decode = protocolFactory.decode(message);
                                return (SearchRequest) decode;
                            }
                        });
    }

    public List<String> findFile(String fileName) {
        logger.debug("Finding files with for the file name: [{}]", fileName);
        List<String> files = fileList
                .stream()
                .filter(fileElement -> Objects.equals(fileElement, fileName))
                .collect(Collectors.toList());
        logger.debug("Files found: [{}]", files);
        return files;
    }

    public void handleSearchResponse(SearchResponse searchResponse) {
        logger.debug("Search response arrived: [{}]", new Gson().toJson(searchResponse));
        if (searchResponse.getNoOfFiles() > 0) {
            logger.debug("Files found: [{}]", searchResponse.getFileNames());
        } else {
            logger.debug("Files not found: [{}]", searchResponse.getFileNames());
        }
    }

    public void search(SearchRequest searchRequest) throws Exception {
        try {
            SearchRequest request = loadingCache.get(searchRequest.serialize());
            if (request == null) {
                loadingCache.put(searchRequest.serialize(), searchRequest);
                List<String> files = fileService.findFile(searchRequest.getFileName());
                if (!files.isEmpty()) {
                    SearchResponse searchResponse = new SearchResponse(files.size(), udpUrl, udpPort + "",
                            searchRequest.getHops() + 1, files);
                    logger.debug("Search response: [{}]", new Gson().toJson(searchResponse));
                    logger.debug("Files found sending search response to origin node url: [{}], port: [{}]",
                            searchRequest.getIpAddress(), searchRequest.getPort());
                    node.send(searchResponse, searchRequest.getIpAddress(), Integer.parseInt(searchRequest.getPort()));
                } else {
                    logger.debug("Files not found propagating the search request to naibour nodes");
                    routingService.sendSearchRequests(searchRequest);
                }
            } else {
                logger.debug("Search request: [{}] already in the cache, So assuming request has already arrived " +
                        "in this node. ignoring the request", request.serialize());
            }
        } catch (ExecutionException e) {
            logger.error("Error while fetching from the cache ", e);
            throw new Exception("Error while fetching from the cache");
        }
    }
}
