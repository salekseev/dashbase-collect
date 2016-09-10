package io.dashbase.collector;

import java.io.File;

import org.graylog2.syslog4j.server.SyslogServer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class DashbaseCollectorCmdLineArgs {

    @Option(name = "-s", aliases = { "--sink" }, usage = "sink configuration file", required = true)
    public File sinkConfigFile = null;

    @Option(name = "-p", aliases = { "--port" }, usage = "http port or syslog port, default 4567")
    public int port = 4567;

    @Option(name = "--syslog", usage = "start collector listen to syslog, default false")
    public boolean useSyslogServer = false;

    @Option(name = "--syslog-host", usage = "syslog host, default 127.0.0.1")
    public String syslogHost = "127.0.0.1";

    @Option(name = "--syslog-port", usage = "syslog port, default 32376")
    public int syslogPort = 32376;

    @Option(name = "--syslog-protocol", usage = "syslog protocol, must be UDP or TCP, default UDP")
    public String syslogProtocol = "UDP";

    private final CmdLineParser parser;

    DashbaseCollectorCmdLineArgs(String... args) throws CmdLineException {
        parser = new CmdLineParser(this);
        parse(args);
        validate();
    }

    private void parse(String... args) throws CmdLineException {
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("invalid argument: " + e.getMessage());
            parser.printUsage(System.err);
            throw e;
        }
    }

    private void validate() throws CmdLineException {
        if (sinkConfigFile == null) {
            throw new CmdLineException(
                  parser,
                  new RuntimeException("Sink configuration file does not exist"));
        }

        if (useSyslogServer && !SyslogServer.exists(syslogProtocol)) {
            throw new CmdLineException(
                  parser,
                  new RuntimeException("Unknown syslog protocol " + syslogProtocol));
        }
    }
}
