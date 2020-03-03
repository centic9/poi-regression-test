package org.dstadler.commoncrawl.datalayer;

import org.apache.derby.drda.NetworkServerControl;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for running a database in-process.
 *
 * It will ensure that the database is started
 */
public final class DatabaseStarter {
    private static final Logger log = LoggerFactory.make();

	private static final int DEFAULT_PORT = 11527;

	public static final String DERBY_PORT_NUMBER = "derby.drda.portNumber";
	public static final String DERBY_HOST = "derby.drda.host";
    //public static final String DERBY_LOGGER_METHOD = "derby.stream.error.method";
    public static final String DERBY_SYSTEM_HOME = "derby.system.home";

	private DbmsProcedure proc;
	private boolean started = false;

	/**
	 * Ensure that the internal database is started and keep
	 * the database running until the process exists.
	 *
	 * @param port The port where the database should be started.
	 * @return A {@link Runnable} which is executed as shutdown hook,
	 *         it is returned for better testability.
	 */
	public static Runnable ensureDatabase(int port) {
		final DatabaseStarter db = new DatabaseStarter();
		db.start(port);

        Thread hook = new Thread("Shutdown hook") {
			@Override
			public void run() {
				// do a println here as logging might already be stopped
                System.out.println("Stopping database if started manually here."); //NOPMD
		        db.stop();
			}
        };
        Runtime.getRuntime().addShutdownHook(hook);

        // return the hook for better testability
        return hook;
	}

	public boolean start() {
		return start(DEFAULT_PORT);
	}

	public boolean start(int port) {
		proc = new DbmsProcedure(port);

		// don't start it twice if it is already running
		if(!proc.isRunning()) {
			log.info("Starting database procedure");

			proc.setConsoleWriter(new PrintWriter(System.out));

			started = true;
			return proc.run();
		}

		return true;
	}

	public boolean stop() {
		if(started) {
			log.info("Stopping database procedure");

			started = false;
			return proc.stop();
		}

		return true;
	}

	/**
	 * Start and stop the Derby Database Management System
	 */
	private static class DbmsProcedure {
	    private NetworkServerControl dbServer = null; // NOPMD
	    private PrintWriter consoleWriter = null;
        private final int dbServerPort;

	    public DbmsProcedure(int port) {
	    	this.dbServerPort = port;
	        System.setProperty(DERBY_PORT_NUMBER, Integer.toString(dbServerPort));
	        System.setProperty(DERBY_HOST, "localhost");
	        //System.setProperty(DERBY_LOGGER_METHOD, DerbyLogger.getLogMethod());
	        System.setProperty(DERBY_SYSTEM_HOME, /*Directories.getExistingDatabaseDir().getAbsolutePath()*/ ".");

	        /* This does not work on a Linux VirtualBox, somehow we do always get the port as taken!
	        if(SocketUtils.isPortAvailable(dbServerPort, null)) {
	        	log.warning(TextUtils.merge("Unable to create database server controller for ''{0}:{1,number,#}'' (<address>:<port>), port is already taken.", dbServerAddress, dbServerPort));
	        	return;
	        }*/

	        try {
	            dbServer = new NetworkServerControl();
	        } catch (Exception e) {
	            log.log(Level.WARNING, "Unable to create database server controller for ''localhost:" + dbServerPort + "'' (<address>:<port>).", e);
	        }
		}

		/**
	     * @param consoleWriter The {@link PrintWriter} to which server console will be output. Console output will be disabled if <code>null</code> is passed in.
	     */
	    public void setConsoleWriter(PrintWriter consoleWriter) {
	        this.consoleWriter = consoleWriter;
	    }

	    public boolean run() {
	        log.info("Starting internal database management system on host: '" + "localhost" + "' and port: '" + dbServerPort + "'");

	        try {
	            dbServer.start(consoleWriter);

	            for(int i = 0;i < 10;i++) {
	            	if(isRunning()) {
	            		break;
	            	}
	            	Thread.sleep(1000);
	            }

	            return isRunning();
	        } catch (Exception e) {
	            log.log(Level.SEVERE, "Unable to start embedded database (Derby) on host: '" + "localhost" +
	                    "' and port: '" + dbServerPort + "' timed out.", e);
	            return false;
	        }
	    }

	    public boolean stop() {
	        if (dbServer == null) {
	            log.info("Database management system actually not running, so it cannot be stopped.");
	            return true;
	        }

	        try {
	            dbServer.shutdown();
	        } catch (Exception e) {
	            log.log(Level.WARNING, "Unable to stop database management system.", e);
	            return false;
	        }

	        return true;
	    }

	    /**
	     * Check if the database is running.
	     * @return <code>true</code> if the database is running or <code>false</code> otherwise
	     */
	    public boolean isRunning() {
	        if (dbServer == null) {
	            return false;
	        }

	        try {
	            dbServer.ping();
	            return true;
	        } catch (@SuppressWarnings("unused") Exception e) {
	            return false;
	        }
	    }
	}
}
