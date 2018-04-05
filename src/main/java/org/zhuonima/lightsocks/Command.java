package org.zhuonima.lightsocks;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Command {

    private static final Logger logger = LoggerFactory.getLogger(Command.class);

    private final String[] args;

    private final Options options = new Options();

    public Command(String[] args) {
        this.args = args;
        this.options.addOption("h", "help", false, "show help. ");
        this.options.addOption("s", "server", false, "start lightsocks remote server. ");
        this.options.addOption("c", "client", false, "start lightsocks local server. ");
    }

    public void parse() {
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                help();
            } else if (cmd.hasOption("s")){
                startServer();
            } else if (cmd.hasOption("c")) {
                String args[] = cmd.getArgs();
                if (args.length <= 0) {
                    throw new RuntimeException();
                }
                String password = args[0];
                logger.info("use password: {}", password);
                startClient(password);

            } else {
                help();
            }

        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            help();
        }
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("lightsocks", options);
        System.exit(0);
    }

    private void startServer()  {
        try {
            Server server = new Server(new InetSocketAddress(8806));
            server.serve();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void startClient(String password) {
        InetSocketAddress local = new InetSocketAddress(9999);
        InetSocketAddress remote = new InetSocketAddress(9090);
        try {
            Client client = new Client(password, local, remote);
            client.listen();
            logger.info("Start Client. ");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
