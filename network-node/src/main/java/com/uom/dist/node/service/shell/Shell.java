package com.uom.dist.node.service.shell;

import com.uom.dist.node.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Shell {
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    @Autowired
    private FileService fileService;

    @Autowired
    private RoutingService routingService;

    @Value("${node.username}")
    private String nodeUserName;

    @Autowired
    private LeaveNodeService leaveNodeService;

    @Autowired
    private UnregisterService unregisterService;

    @Autowired
    private RegisterService registerService;

    private enum  COMMAND {search, unreg, print, exit, reg, leave, help}

    private String prompt = "node Shell>";

    private String helpString = "" +
            "search <filename>    search for a file" +
            "\nunreg                unregister from the bootstrap server" +
            "\nprint                print routing table data" +
            "\nexit                 exit from the console" +
            "\nreg                  register to the bootstrap server" +
            "\nleave                leave from the network" +
            "\nhelp                 get command information";

    @PostConstruct
    public void init() {
        executor.submit(this::runTerminal);
    }

    public void runTerminal() {
        Scanner scan = new Scanner(System.in);
        System.out.println("To get command information enter command help");
        prompt = "node "+ nodeUserName +" Shell>";
        System.out.print(prompt);
        while (true) {
            String userInput = scan.nextLine();
            String[] split = userInput.split(" ", 2);
            try {
                String result = handleCommand(split);
                if (COMMAND.exit.name().equals(result)) {
                    break;
                }
                System.out.print(result);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command");
            }
            catch (Exception e) {
                System.out.println("Error occurred : " + e.getMessage());
            }
            System.out.print("\n"+ prompt);
        }
        System.out.print("\nShell exited");
    }

    public String handleCommand(String[] commandValuePair) throws Exception {
        if (commandValuePair.length < 1) {
            return "invalid entry";
        }
        String commandStr = commandValuePair[0];
        COMMAND command = COMMAND.valueOf(commandStr);

        String value = null;

        if (commandValuePair.length == 2) {
            value = commandValuePair[1];
        }

        switch (command) {
            case search: {
                if (value == null) {
                    return command + " must contain a value";
                } else {
                    fileService.handleSearchCommand(value);
                    return "";
                }
            }
            case print: return routingService.getRoutingTableValues();
            case leave: {
                leaveNodeService.sendLeaveRequest();
                return "";
            }
            case unreg: {
                unregisterService.unregister();
                return "";
            }
            case reg: registerService.register();
            case exit: return command.name();
            case help: return helpString;
            default: return "invalid command";
        }
    }

    public void print(String entry) {
        System.out.println("\n" + entry);
        System.out.print(prompt);
    }

}
