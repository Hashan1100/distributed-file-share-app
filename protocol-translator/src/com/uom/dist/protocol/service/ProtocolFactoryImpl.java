package com.uom.dist.protocol.service;

import com.uom.dist.protocol.*;
import com.uom.dist.protocol.Protocol.COMMAND;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ProtocolFactoryImpl implements ProtocolFactory {
    @Override
    public Protocol decode(String message) throws Exception {
        if (message != null) {
            String[] values = message.split(" ");
            List<String> valueList = Arrays.asList(values);
            if (valueList.size() <= 1) {
                throw new Exception("Invalid message");
            }

            String type = valueList.get(1);
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
                        return new SearchRequest(valueList);
                    }
                    case SEROK: {
                        return new SearchResponse(valueList);
                    }
                    case ERROR: {
                        return new ErrorResponse(valueList);
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

    @Override
    public String encode(Protocol protocol) {
        return protocol.serialize();
    }
}
