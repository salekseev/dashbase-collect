package io.dashbase.collector.source;

import java.net.SocketAddress;
import java.util.Date;

import org.graylog2.syslog4j.server.SyslogServer;
import org.graylog2.syslog4j.server.SyslogServerConfigIF;
import org.graylog2.syslog4j.server.SyslogServerEventHandlerIF;
import org.graylog2.syslog4j.server.SyslogServerEventIF;
import org.graylog2.syslog4j.server.SyslogServerIF;
import org.graylog2.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.graylog2.syslog4j.util.SyslogUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import io.dashbase.collector.DashbaseCollectorCmdLineArgs;
import io.dashbase.collector.sink.CollectorSink;

public class CollectorSyslogSource implements CollectorSource {
    private static final Logger logger = LoggerFactory.getLogger(CollectorSyslogSource.class);

    private static final String DEFAULT_SYSLOG_SINK_TOPIC = "syslog";

    private static class CollectorSinkSyslogEventHandler
          implements SyslogServerSessionEventHandlerIF {

        private final CollectorSink sink;
        private final String topic;

        CollectorSinkSyslogEventHandler(CollectorSink sink, String topic) {
            this.sink = sink;
            this.topic = topic;
        }

        @Override
        public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
            return null;
        }

        @Override
        public void event(
              Object session,
              SyslogServerIF syslogServer,
              SocketAddress socketAddress,
              SyslogServerEventIF event) {
            String date = (event.getDate() == null ? new Date() : event.getDate()).toString();
            String facility = SyslogUtility.getFacilityString(event.getFacility());
            String level = SyslogUtility.getLevelString(event.getLevel());

            try {
                sink.add(
                      topic,
                      ImmutableMap.of("date", date, "facility", facility, "level", level),
                      event.getMessage().getBytes(),
                      false);
            } catch (Exception e) {
                logger.error("Exception while adding event to sink", e);
            }
        }

        @Override
        public void exception(
              Object session,
              SyslogServerIF syslogServer,
              SocketAddress socketAddress,
              Exception exception) {
        }

        @Override
        public void sessionClosed(
              Object session,
              SyslogServerIF syslogServer,
              SocketAddress socketAddress,
              boolean timeout) {
        }

        @Override
        public void initialize(SyslogServerIF syslogServer) {
        }

        @Override
        public void destroy(SyslogServerIF syslogServer) {
        }
    }
    @Inject
    private CollectorSink sink;

    @Inject
    private DashbaseCollectorCmdLineArgs cmdLineArgs;

    @Override
    public void start() {
        final SyslogServerIF syslogServer = SyslogServer.getInstance(cmdLineArgs.syslogProtocol);

        final SyslogServerConfigIF syslogServerConfig = syslogServer.getConfig();
        syslogServerConfig.setHost(cmdLineArgs.syslogHost);
        syslogServerConfig.setPort(cmdLineArgs.syslogPort);

        SyslogServerEventHandlerIF eventHandler =
              new CollectorSinkSyslogEventHandler(sink, DEFAULT_SYSLOG_SINK_TOPIC);
        syslogServerConfig.addEventHandler(eventHandler);

        final SyslogServerIF threadedInstance =
              SyslogServer.getThreadedInstance(cmdLineArgs.syslogProtocol);

        try {
            threadedInstance.getThread().join();
        } catch (InterruptedException e) {
            logger.warn("Interrupted while joining syslog server thread", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void shutdown() {
        logger.info("Syslog collector shut down");
    }
}
