// RSA Extra Credit
// Author: Ben Kempers
// Version: 5/9/22
//
// CS4150 Extra Credit to develop an RSA encryption based off of the number manipulation and
// public/private keys from a 'normal' RSA encryption as well as utilizing tips and hints
// from class lecture.

import java.util.*;
import java.math.BigInteger;

public class RSA {

        //Main method that will only need to take in a message that you wish to encrypt. The rest of the method will output
        //the different 'answers' to the questions posed to us in the extra credit assignment page.
        public static void main(String[] args) {
        RSA rsaEncryption = new RSA();
        Scanner scan = new Scanner(System.in);
        
        //'Write a pair of functions, one for converting a text message to a single number, and the other for decoding a 
        //single number to  a text message'. Further method description is provided below.
        System.out.println("----- Question 1 -----");
        String message = scan.nextLine();
        BigInteger encodedMessage = rsaEncryption.messageToNumber(message);
        System.out.println(encodedMessage);
        String decodedMessage = rsaEncryption.numberToMessage(encodedMessage);
        System.out.println(decodedMessage);

        //'Write code that builds an RSA modulus n, and keys e and d, as shown in class.' I ran the method once which added 
        //all values to an HashMap to easily get all the values. Further method description is provided below.
        System.out.println("\n----- Question 2 -----");
        HashMap<String, BigInteger> rsaValues = rsaEncryption.RSAKeyValues(message.length());
        System.out.println("p = " + rsaValues.get("p"));
        System.out.println("q = " + rsaValues.get("q"));
        System.out.println("n = " + rsaValues.get("n"));
        System.out.println("phi = " + rsaValues.get("phi"));
        System.out.println("e = " + rsaValues.get("e"));
        System.out.println("d = " + rsaValues.get("d"));
        System.out.println(((rsaValues.get("d").multiply(rsaValues.get("e")).mod(rsaValues.get("phi"))).equals(BigInteger.ONE)));

        //'Write code to encode your message using one of the keys, then decode the message using the other key. Do it twice, 
        //once using d for the first step, the other using e for the first step.' I utilized the above couple methods and 
        //essentially outputted: Original message -> BigInteger -> Encoded Message -> Decoded Message -> BigInteger -> Original Message. 
        //Further method description is provided below.
        System.out.println("\n----- Question 3 -----");
        System.out.println("Encrypted by 'e' key, decrypted by 'd' key.");
        //Original message
        System.out.println("Original message: " + message);
        //Original message -> BigInteger
        BigInteger encodedRSAText = rsaEncryption.messageToNumber(message);
        System.out.println("Original message to base 16 unicode: " + encodedRSAText);
        //BigInteger -> Encoded message
        BigInteger encodedRSAMessage = rsaEncryption.encodeMessage(encodedRSAText, rsaValues.get("e"), rsaValues.get("n"));
        System.out.println("Message to RSA encrypted by 'e' key: " + encodedRSAMessage);
        //Encoded Message -> Decoded message
        BigInteger decodedRSAMessage = rsaEncryption.decodeMessage(encodedRSAMessage, rsaValues.get("d"), rsaValues.get("n"));
        System.out.println("Decrypted message by 'd' key: " + decodedRSAMessage);
        //Decoded message -> Original message
        String decodedRSAText = rsaEncryption.numberToMessage(decodedRSAMessage);
        System.out.println("Base 16 unicode to original message: " + decodedRSAText);

        System.out.println("\nEncrypted by 'd' key, decrypted by 'e' key.");
        //Original message
        System.out.println("Original message: " + message);
        //Original message -> BigInteger
        encodedRSAText = rsaEncryption.messageToNumber(message);
        System.out.println("Original message to base 16 unicode: " + encodedRSAText);
        //BigInteger -> Encoded message
        encodedRSAMessage = rsaEncryption.encodeMessage(encodedRSAText, rsaValues.get("d"), rsaValues.get("n"));
        System.out.println("Message to RSA encrypted by 'd' key: " + encodedRSAMessage);
        //Encoded Message -> Decoded message
        decodedRSAMessage = rsaEncryption.decodeMessage(encodedRSAMessage, rsaValues.get("e"), rsaValues.get("n"));
        System.out.println("Decrypted message by 'e' key: " + decodedRSAMessage);
        //Decoded message -> Original message
        decodedRSAText = rsaEncryption.numberToMessage(decodedRSAMessage);
        System.out.println("Base 16 unicode to original message: " + decodedRSAText);

        //'Some messages do not change when encoded, the message=cypher, then in decoding cyper=message.  
        //This is obvious if the message is 0 or 1, raising these to any power mod n doesn't change them.  
        //There are two other non-trivial messages with this property.  Do the math to find them, then prove it with a little more code and output.'
        System.out.println("----- Question 4 -----");

        scan.close();
    }

    /**
     * Empty class object constructor
     */
    public RSA() {
    }

    /**
     * Given a message of some length, this method turns that message into a BigIntger that's made up of hex values 
     * of base 16 for that string message. This is done by iterating over the message's characters, making the character 
     * into the representing hex value, and adding that to the outputted BigInteger.
     * @param message
     * @return BigInteger that represents the hex decimal value in base 16 of message.
     */
    public BigInteger messageToNumber(String message){
        String hexMessage = "";

        for(char letter : message.toCharArray()){
            hexMessage += Integer.toHexString(letter);
        }

        BigInteger messageValue = new BigInteger(hexMessage, 16);
        return messageValue;
    }

    /**
     * Given a BigInteger (base 16 hex decimal) representation of some string, outputs the corresponding message 
     * in readable String form. This is done by taking the BigInteger number and turning it into a string by base 16 value. 
     * The string is then iterated over and parsing the string and turning it into an integer by (i, i+1).
     * @param number
     * @return String representation of the base 16 hex decimal value of message
     */
    public String numberToMessage(BigInteger number){
        String bigIntegerMessage = number.toString(16);
        String message = "";
        for(int i = 0; i < bigIntegerMessage.length(); i += 2){
            String string = bigIntegerMessage.substring(i, (i + 2));
            int decimal = Integer.parseInt(string, 16);
            message = message + (char)decimal;
        }
        return message;
    }

    /**
     * Generates the RSA values based off of the class lecture information that was given to us and return a HashMap of (value, RSA number).
     * @param messageLength
     * @return HashMap of string value (p, q, n, phi, e, d), and their corresponding values.
     */
    public HashMap<String, BigInteger> RSAKeyValues(int messageLength){
        HashMap<String, BigInteger> rsaValues = new HashMap<String, BigInteger>();
        Random rand = new Random();

        //p prime value (distinct and large 10^20).
        BigInteger p = BigInteger.probablePrime(500, rand);
        rsaValues.put("p", p);

        //q prime value (distinct and large 10^20).
        BigInteger q = BigInteger.probablePrime(500, rand);
        rsaValues.put("q", q);

        //n equal to p * q.
        BigInteger n = p.multiply(q);
        rsaValues.put("n", n);

        //phi equal to ((p-1) * (q-1)).
        BigInteger phi = (p.subtract(new BigInteger("1"))).multiply(q.subtract(new BigInteger("1")));
        rsaValues.put("phi", phi);

        //e equal to (1 < e < phi) and coprime with phi.
        BigInteger e = BigInteger.probablePrime(50, rand);
        while(!e.gcd(phi).equals(BigInteger.ONE))
            e = BigInteger.probablePrime(50, rand);
        rsaValues.put("e", e);
        
        //d equal to e^-1 mod phi. Also - d * e = 1 mod phi.
        BigInteger d = e.modInverse(phi);
        rsaValues.put("d", d);

        return rsaValues;
    }

    /**
     * Encodes a BigInteger message by RSA instructions from class. cypher = message^key mod n.
     * @param message
     * @param key
     * @param n
     * @return Encoded message by BigInteger key.
     */
    public BigInteger encodeMessage(BigInteger message, BigInteger key, BigInteger n){
        return message.modPow(key, n);
    }

    /**
     * Decodes the BigInteger cypher by RSA instructions from class. message = cypher^key mod n.
     * @param cypher
     * @param key
     * @param n
     * @return Decoded message by BigInteger key.
     */
    public BigInteger decodeMessage(BigInteger cypher, BigInteger key, BigInteger n){
        return cypher.modPow(key, n);
    }
}