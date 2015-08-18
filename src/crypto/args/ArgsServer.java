package crypto.args;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

/***
 * This bean represents the arguments received by the Server application.
 * Please, refer to args4j for more details and documentation.
 * 
 * Remember to extend this class adding all the additional parameters that
 * you need. 
 * 
 * @author pabloserrano
 *
 */
public class ArgsServer {
	// private logger for debug only
	private static Logger log = Logger.getLogger(ArgsServer.class);

	@Option(name="-port", usage="Local port to listen incomming connections", required=true)
	public int Port;
	
	public void showArgs() {
		log.debug("Parameters for ArgsServer class:");
		log.debug("Port: [" + Port + "]");
	}
}
