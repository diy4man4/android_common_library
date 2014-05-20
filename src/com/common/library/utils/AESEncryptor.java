package com.common.library.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptor {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final int CACHE_SIZE = 1024;
    
    /**
     * Generate random secret key.<p>
     * 
     * @return random secret key
     * @throws Exception
     */
    public static String getSecretKey() throws Exception {
        return getSecretKey(null);
    }
    
    /**
     * Generate secret key with given seed.</p>
     * 
     * @param seed
     * @return secret key
     * @throws Exception
     */
    public static String getSecretKey(String seed) {
		try {
			KeyGenerator keyGenerator;
			keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			SecureRandom secureRandom;
	        if (seed != null && !"".equals(seed)) {
	            secureRandom = new SecureRandom(seed.getBytes());
	        } else {
	            secureRandom = new SecureRandom();
	        }
	        keyGenerator.init(KEY_SIZE, secureRandom); 
	        SecretKey secretKey = keyGenerator.generateKey(); 
	        return Base64Utils.encode(secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * Encrypt string
     * 
     * @param secret key
     * @param clear string
     * @return hex string
     */
    public static String encrypt(String key, String clear) {
		try {
			Key k = toKey(Base64Utils.decode(key));
			byte[] raw = k.getEncoded(); 
	        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM); 
	        Cipher cipher = Cipher.getInstance(ALGORITHM); 
	        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	        byte[] bytes = cipher.doFinal(clear.getBytes());
	        return toHex(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
	/**
	 * Encrypt bytes
	 * 
	 * @param key
	 *            secret key
	 * @param data
	 *            data to encrypt
	 * @return encrypte bytes
	 */
    public static byte[] encrypt(String key, byte[] data) {
		try {
			Key k = toKey(Base64Utils.decode(key));
			byte[] raw = k.getEncoded(); 
	        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM); 
	        Cipher cipher = Cipher.getInstance(ALGORITHM); 
	        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	        return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
	/**
	 * Encrypt file.
	 * <p>
	 * 
	 * @param key
	 *            secret key
	 * @param src
	 *            file to encrypt
	 * @param des
	 *            output file of encrypted
	 * @return true when encrypt successfully, otherwise return false
	 */
	public static boolean encryptFile(String key, File src, File des) {
		InputStream in = null;
		OutputStream out = null;
		CipherInputStream cin = null;

		try {
			if (src.exists() && src.isFile()) {
				if (!des.getParentFile().exists()) {
					des.getParentFile().mkdirs();
				}

				if (!des.exists()) {
					des.createNewFile();
				}

				in = new FileInputStream(src);
				out = new FileOutputStream(des);

				Key k = toKey(Base64Utils.decode(key));
				byte[] raw = k.getEncoded();

				SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
				Cipher cipher = Cipher.getInstance(ALGORITHM);
				cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
				cin = new CipherInputStream(in, cipher);

				byte[] cache = new byte[CACHE_SIZE];
				int len = 0;

				while ((len = cin.read(cache)) != -1) {
					out.write(cache, 0, len);
					out.flush();
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (cin != null) {
					cin.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
	/**
	 * Decrypt file. </p>
	 * 
	 * @param key
	 *            secret key
	 * @param hexStr
	 *            encoded string
	 * @return clear string which has been decoded
	 */
    public static String decrypt(String key, String hexStr) {
		try {
			Key k = toKey(Base64Utils.decode(key));
			byte[] raw = k.getEncoded(); 
	        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM); 
	        Cipher cipher = Cipher.getInstance(ALGORITHM); 
	        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
	        byte[] enc = toByte(hexStr);
	        return new String(cipher.doFinal(enc));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
	/**
	 * Decrypt bytes
	 * 
	 * @param key
	 *            secret key
	 * @param data
	 *            data to decrypt
	 * @return bytes decrypted
	 */
    public static byte[] decrypt(String key, byte[] data) {
		try {
			Key k = toKey(Base64Utils.decode(key));
			byte[] raw = k.getEncoded(); 
	        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM); 
	        Cipher cipher = Cipher.getInstance(ALGORITHM); 
	        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
	        return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * <p>
     * 文件解密
     * </p>
     * 
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws true when decrypt file successfully, otherwise return false
     */
    public static boolean decryptFile(String key, File src, File des){
    	FileInputStream in = null;
        FileOutputStream out = null;
        CipherOutputStream cout = null;
        
        try{
        	if (src.exists() && src.isFile()) {
                if (!des.getParentFile().exists()) {
                    des.getParentFile().mkdirs();
                }
                
                if(!des.exists()){
                	des.createNewFile();
                }
                
                in = new FileInputStream(src);
                out = new FileOutputStream(des);
                
                Key k = toKey(Base64Utils.decode(key));
                byte[] raw = k.getEncoded(); 
                
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM); 
                Cipher cipher = Cipher.getInstance(ALGORITHM); 
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                cout = new CipherOutputStream(out, cipher);
                
                byte[] cache = new byte[CACHE_SIZE];
                int len = 0;
                
                while ((len = in.read(cache)) != -1) {
                    cout.write(cache, 0, len);
                    cout.flush();
                }
            }
        	return true;
        }catch(IOException e){
        	e.printStackTrace();
        	return false;
        } catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
        	try {
        		if(cout != null){
        			cout.close();
        		}
        		if(out != null){
        			out.close();
        		}
        		if(in != null){
        			in.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    /**
     * Transform bytes key to secret key.
     * </p>
     * 
     * @param bytes key
     * @return secret key
     */
    private static Key toKey(byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }
    
    private static String toHex(byte[] buf) {
		if (buf == null) {
			return "";
		}
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}
    
    private static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
		return result;
	}
    
    private final static String HEX = "0123456789ABCDEF";

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}
}
