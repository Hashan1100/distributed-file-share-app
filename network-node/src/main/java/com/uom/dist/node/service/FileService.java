package com.uom.dist.node.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.uom.dist.node.service.shell.Shell;
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
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Autowired
    private Shell shell;

    @Value("${udp.receiver.port}")
    private int udpPort;

    @Value("${server.port}")
    private int tcpPort;

    @Value("${udp.receiver.url}")
    private String udpUrl;

    @Value("${node.username}")
    private String nodeUserName;

    @Autowired
    private Node node;

    @Value("${search.request.cache.validity.time}")
    private Long cacheValidityTime;

    @Value("${search.request.cache.size}")
    private Long cacheSize;

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private Map<String, String> searchStatusCodeMap = Map.of(
            "0", "no matching results. Searched key is not in key table",
            "9999", "failure due to node unreachable",
            "9998", "some other error."
    );

    private static final int NODE_UNREACHABLE = 9999;
    private static final int ERROR = 9998;

    @PostConstruct
    public void init() {
        this.loadingCache = CacheBuilder
                .newBuilder()
                .maximumSize(cacheSize)
                .expireAfterAccess(cacheValidityTime, TimeUnit.SECONDS)
                .build(
                        new CacheLoader<String, SearchRequest>() {
                            public SearchRequest load(String message) throws Exception {
                                Protocol decode = protocolFactory.decode(message);
                                return (SearchRequest) decode;
                            }
                        });
    }

    public boolean isFileAvailable(String fileName) {
        logger.debug("Checking availability of a file with file name [{}]", fileName);
        return fileList.contains(fileName);
    }

    public List<String> findFile(String fileName) throws Exception {
        if (fileName == null) {
            throw new Exception("File name is null");
        }
        logger.debug("Finding files with for the file name: [{}]", fileName);
        List<String> files = fileList
                .stream()
                .filter(Objects::nonNull)
                .filter(fileElement ->
                        fileElement.toLowerCase()
                                .contains(fileName.toLowerCase().replaceAll("^\"|\"$", "")))
                .map(file -> "\"" + file + "\"")
                .collect(Collectors.toList());
        logger.debug("Files found: [{}] in node: [{}]", files, nodeUserName);
        return files;
    }

    public void handleSearchResponse(SearchResponse searchResponse) {
        logger.debug("Search response arrived: [{}]", new Gson().toJson(searchResponse));
        if (searchResponse.getNoOfFiles() > 0) {
            logger.debug("Files found: [{}]", searchResponse.getFileNames());
            searchResponse.getFileNames().forEach(name -> {
                logger.debug("File URL: [{}]", searchResponse.getIp() + ":" + searchResponse.getPort() + "/download?fileName=" + name);
                shell.printNoPrompt("File URL: http://" + searchResponse.getIp() + ":" + searchResponse.getPort() + "/download?fileName=" + name);
            });
            shell.print("Hops: [" + searchResponse.getHops() + "]\n" +
                    "Timestamp: [" + Instant.now().toEpochMilli() + "]");
        } else if (searchResponse.getNoOfFiles() == NODE_UNREACHABLE) {
            logger.debug("Node was not reachable [{}]", searchResponse.serialize());
        } else if (searchResponse.getNoOfFiles() == ERROR) {
            logger.debug("Error occurred while searching [{}] [{}]",
                    searchResponse.serialize(), searchStatusCodeMap.get(searchResponse.getNoOfFiles() + ""));
        } else {
            logger.debug("Files not found: [{}] [{}]",
                    searchResponse.getFileNames(), searchStatusCodeMap.get(searchResponse.getNoOfFiles() + ""));
        }
    }

    public void search(SearchRequest searchRequest) throws Exception {
        try {
            SearchRequest request = loadingCache.getIfPresent(searchRequest.cacheKey());
            if (request == null) {
                loadingCache.put(searchRequest.cacheKey(), searchRequest);
                List<String> files = fileService.findFile(searchRequest.getFileName());
                if (!files.isEmpty()) {
                    SearchResponse searchResponse = new SearchResponse(files.size(), udpUrl, tcpPort + "",
                            searchRequest.getHops() + 1, files);
                    logger.debug("Files found sending search response to origin node url: [{}], port: [{}]",
                            searchRequest.getIpAddress(), searchRequest.getPort());
                    logger.debug("HOPS_COUNT :[{}] file name: [{}] node: [{}]",
                            searchRequest.getHops() + 1, searchRequest.getFileName(), nodeUserName);
                    node.send(searchResponse, searchRequest.getIpAddress(), Integer.parseInt(searchRequest.getPort()));
                } else {
                    logger.debug("Files not found propagating the search request to neighbour nodes");
                }
                routingService.sendSearchRequests(searchRequest);
            } else {
                logger.debug("Search request: [{}] already in the cache, So assuming request has already arrived " +
                        "in this node. ignoring the request", request.serialize());
            }
        } catch (IOException ex) {
            SearchResponse searchResponse = new SearchResponse(NODE_UNREACHABLE, udpUrl, tcpPort + "",
                    searchRequest.getHops() + 1, "");
            node.send(searchResponse, searchRequest.getIpAddress(), Integer.parseInt(searchRequest.getPort()));
        } catch (Exception e) {
            logger.error("Error while fetching from the cache ", e);
            SearchResponse searchResponse = new SearchResponse(ERROR, udpUrl, tcpPort + "",
                    searchRequest.getHops() + 1, "");
            node.send(searchResponse, searchRequest.getIpAddress(), Integer.parseInt(searchRequest.getPort()));
            throw new Exception("Error while fetching from the cache");
        }
    }

    public void handleSearchCommand(String fileName) throws Exception {
        SearchRequest searchRequest = new SearchRequest(udpUrl, udpPort + "", "\"" + fileName + "\"", 0);
        search(searchRequest);
    }
}
