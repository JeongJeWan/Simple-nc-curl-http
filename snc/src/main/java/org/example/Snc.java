package org.example;

import org.apache.commons.cli.*;

public class Snc {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("l")
                .hasArg()
                .argName("port")
                .desc("서버 모드로 동작, 입력 받은 포트로 listen")
                .build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("l")) {
                new MyServerSocket(cmd.getOptionValue("l"));
            } else {
                new ClientSocket(args[0], args[1]);
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }

    }
}
