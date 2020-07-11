package com.uom.dist.protocol.service;

import com.uom.dist.protocol.*;
import com.uom.dist.protocol.Protocol.COMMAND;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ProtocolFactoryImpl implements ProtocolFactory {
    @Override
    public Protocol decode(String message) throws Exception {
        if (message != null) {
            String[] values = message.split(" ");
            List<String> valueList = Arrays.asList(values);
            if (valueList.size() < 1) {
                throw new Exception("Invalid message");
            }
            String type;
            if (valueList.size() == 1) {
                type = valueList.get(0).replace("\n", "");
            } else {
                type = valueList.get(1);
            }
            COMMAND command = Protocol.COMMAND.valueOf(type);
            try {
                switch (command) {
                    case REG: {
                        return new RegisterRequest(valueList);
                    }
                    case REGOK: {
                        return new RegisterResponse(valueList);
                    }
                    case UNREG: {
                        return new UnRegisterRequest(valueList);
                    }
                    case UNROK: {
                        return new UnRegisterResponse(valueList);
                    }
                    case JOIN: {
                        return new JoinRequest(valueList);
                    }
                    case JOINOK: {
                        return new JoinResponse(valueList);
                    }
                    case LEAVE: {
                        return new LeaveRequest(valueList);
                    }
                    case LEAVEOK: {
                        return new LeaveResponse(valueList);
                    }
                    case SER: {
                        String fileName = getFileNameForSearchRequest(message);
                        String[] listToStringArray = StringUtils.delimitedListToStringArray(message, " ", fileName + " ");
                        List<String> stringList = Arrays.stream(listToStringArray)
                                .filter(s -> !s.isEmpty() || !s.isBlank())
                                .collect(Collectors.toList());
                        return new SearchRequest(stringList, fileName);
                    }
                    case SEROK: {
                        List<String> fileList = getFileNamesForSearchResponse(message);
                        List<String> listWithoutNames = valueList.subList(0, 6);
                        return new SearchResponse(listWithoutNames, fileList);
                    }
                    case ERROR: {
                        return new ErrorResponse(valueList);
                    }
                    case PRINT: {
                        return new PrintResponse(valueList);
                    }
                    default: {
                        throw new Exception("Unsupported command");
                    }
                }
            } catch (NumberFormatException e) {
                throw new Exception("Cannot covert numbers " + e.getMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new Exception("Message format is incorrect " + e.getMessage());
            } catch (Exception e) {
                throw new Exception("Unknown error occurred " + e.getMessage());
            }
        } else {
            throw new Exception("Message can't be null");
        }
    }

    private String getFileNameForSearchRequest(String message) {
        Matcher m = getMatcher(message);
        if(m.find()) {
            return m.group(0);
        } else {
            return "";
        }
    }

    private Matcher getMatcher(String message) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        return p.matcher(message);
    }

    private List<String> getFileNamesForSearchResponse(String message) {
        ArrayList<String> nameList = new ArrayList<>();
        Matcher m = getMatcher(message);
        while (m.find()) {
            nameList.add(m.group(0));
        }
        return nameList;
    }

    @Override
    public String encode(Protocol protocol) {
        return protocol.serialize();
    }
}
