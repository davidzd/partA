package crypto.client;

import java.math.BigInteger;
import java.util.Random;

import org.apache.log4j.Logger;

/***
 * In this class, all the candidates must implement their own
 * math and crypto functions required to solve any calculation 
 * and encryption/decryption task involved in this project.
 * 
 * @author pabloserrano
 *
 */
public class CustomCrypto {
	
	// debug logger
	private static Logger log = Logger.getLogger(CustomCrypto.class);
	
	private static Random rnd = new Random();
	
	public static BigInteger createPrivateKey(int size) {
		log.debug("You must implement this function!");
		return BigInteger.ONE;
	}

	public static BigInteger[] createDHPair(BigInteger generator, BigInteger prime, 
			BigInteger skClient) {
		BigInteger[] pair = new BigInteger[2];
		log.debug("You must implement this function!");
		pair[0] = BigInteger.ONE;
		pair[1] = BigInteger.ONE;
		return pair;
	}
	
	public static BigInteger getDHSharedKey(BigInteger pk, BigInteger sk, BigInteger prime) {
		BigInteger shared = BigInteger.ZERO;
		log.debug("You must implement this function!");
		return shared;
	}
	
	public static BigInteger modExp(BigInteger base, BigInteger exp, BigInteger modulo) {
		log.debug("You must implement this function!");
		return BigInteger.ZERO;
	}
}
