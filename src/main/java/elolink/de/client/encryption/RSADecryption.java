package elolink.de.client.encryption;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RSADecryption {

    public static PublicKey parseAndLoadPublicKey(String rawKeyString) throws Exception {
        // Regex for Modulus and Exponent
        Pattern modulusPattern = Pattern.compile("modulus:\\s*(\\d+)");
        Pattern exponentPattern = Pattern.compile("public exponent:\\s*(\\d+)");

        Matcher modMatcher = modulusPattern.matcher(rawKeyString);
        Matcher expMatcher = exponentPattern.matcher(rawKeyString);

        if (!modMatcher.find() || !expMatcher.find()) {
            throw new IllegalArgumentException("Modulus oder Exponent nicht gefunden!");
        }

        BigInteger modulus = new BigInteger(modMatcher.group(1));
        BigInteger exponent = new BigInteger(expMatcher.group(1));

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }


    public static void main(String[] args) throws Exception {
        System.out.println(
        parseAndLoadPublicKey("Sun RSA public key, 2048 bits\\n  params: null\\n  modulus: 23310541669768867773249063893233027838229733500846344631240298123608791924303442916789026587315110978849093571207646825151179622983567227343169070149204065479963409069148769485536847416893949078741499806765352501644094650302613979737947856959415757047589690136940950703584322996220460064234648729843028777998839592354735753734486540072039657304739761998923258086718420170662577479898990456827942042172467972784230438191808288879947028120527998928751623774872400802516181044056295588730221424024686168083562837600163432060517897631590143027385178549299985713167134354631105422409217116689590096476361164862944323456531\\n  public exponent: 65537")
        );
    }
}
