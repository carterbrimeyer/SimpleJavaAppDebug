/**
 * This is a simple application that encrypts or decrypts a string inputted by a user.
 * It also supports adding an 'offset' to the encryption to act as a passcode to get proper translation.
 * Note: this program does not support special characters. Only letters or digits.
 * It will not modify special characters.
 *
 * This application is loosely based on this guide to simple encryption at this link:
 * http://practicalcryptography.com/ciphers/simple-substitution-cipher/
 *
 *
 * @author  Carter Brimeyer
 */
import java.util.Hashtable;

class helper
{
    public static String removeDuplicateChars(String input)
    {
        StringBuilder noDupeInput = new StringBuilder();
        int index = 0;
        for (int i = 0; i <= input.length() - 1; i++)
        {
            int j;
            for (j = 0; j < i; j++) {
                if (input.charAt(i) == input.charAt(j)) {
                    break;
                }
            }
            if (j == i)
            {
                noDupeInput.append(input.charAt(i));
            }
        }
        return noDupeInput.toString();
    }
}

class encrypter{
    final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    final String encryptedAlphabet = "0918273645PHQGIUMEAYLNOFDXJKRCVSTZWBphqgiumeaylnofdxjkrcvstzwb";

    private int encryptionLength;
    private Hashtable<Character, Character> encryption;
    public encrypter()
    {
        encryption =  new Hashtable<>();
        encryptionLength = alphabet.length();
        for (int i = 0; i < alphabet.length(); i++)
        {
            encryption.put(alphabet.charAt(i), encryptedAlphabet.charAt(i));
        }
    }
    public Hashtable<Character, Character> getEncryption()
    {
        return encryption;
    }
    public int length()
    {
        return encryptionLength;
    }
    public void applyOffset(String input)
    {
        String newEncAlphabet = helper.removeDuplicateChars(input.concat(encryptedAlphabet));
        for (int i = 0; i < alphabet.length(); i++)
        {
            encryption.put(alphabet.charAt(i), newEncAlphabet.charAt(i));
        }
    }
}
public class crypt {

    public static void main(String[] args) {
        encrypter enc = new encrypter();
        try
        {
            String input = "";
            for(int i = 0; i < args.length; i++)
            {
                if (args[i].equals("-offset"))
                {
                    System.out.println("crypt: Offset applied - " + parseInput(enc, "offset " + args[++i]));
                }
                else
                {
                    input += args[i] + " ";
                }
            }
            input = input.trim();
            if (checkValidInput(input))
            {
                System.out.println("crypt: " + input + " ==> "+ parseInput(enc, input));
            }
            else
            {
                System.out.println("crypt: Invalid Syntax!");
            }
        }
        catch(Exception e)
        {
            System.out.println("crypt ERROR: " + e.getMessage());
        }

    }
    public static String parseInput(encrypter enc, String input) throws Exception {
        String cmd = input.split(" ", 0)[0];
        String params = input.substring(cmd.length() + 1);
        switch(cmd)
        {
            case "encrypt":
                return encrypt(enc, params);
            case "decrypt":
                return decrypt(enc, params);
            case "offset":
                return offset(enc, params);
            default:
                throw new Exception("ILLEGAL SYNTAX");
        }
    }
    public static boolean checkValidInput(String input)
    {
        if(input == null || input.equals("") || input.equals(" "))
        {
            return false;
        }
        String cmd = input.split(" ", 0)[0];
        switch(cmd)
        {
            case "reset":
                return true;
            case "encrypt":
            case "decrypt":
            case "offset":
                break;
            default:
                return false;
        }
        return input.substring(cmd.length()).length() != 0;
    }
    public static String encrypt(encrypter enc, String input)
    {
        StringBuilder output = new StringBuilder();
        for (char ch: input.toCharArray()) {
            if(Character.isLetterOrDigit(ch))
            {
                output.append(enc.getEncryption().get(ch));
            }
            else
            {
                output.append(ch);
            }
        }
        return output.toString();
    }
    public static String decrypt(encrypter enc, String input)
    {
        StringBuilder output = new StringBuilder();
        for (char ch: input.toCharArray()) {
            if(Character.isLetterOrDigit(ch))
            {
                int i;
                for(i = 0; i < enc.length(); i++)
                {
                    if((char) enc.getEncryption().values().toArray()[i] == ch)
                    {
                        break;
                    }
                }
                output.append(enc.getEncryption().keySet().toArray()[i]);
            }
            else
            {
                output.append(ch);
            }
        }
        return output.toString();
    }
    public static String offset(encrypter enc, String input)
    {
        enc.applyOffset(helper.removeDuplicateChars(input));
        return input;
    }
}