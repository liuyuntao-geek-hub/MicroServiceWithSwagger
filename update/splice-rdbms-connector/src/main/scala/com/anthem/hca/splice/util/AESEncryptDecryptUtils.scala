package com.anthem.hca.splice.util

import java.io.{ File, FileInputStream, IOException }
import java.security._
import java.security.cert.CertificateException
import java.util.Scanner

import com.typesafe.config.{ Config, ConfigFactory }
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import org.apache.commons.codec.binary.Base64

/**
 * %JAVA_HOME%/keytool -genseckey -keystore /data/cdr_dev/keystore/aes-keystore.jck -storetype jceks -storepass mystorepass -keyalg AES -keysize 256 -alias jceksaes -keypass mykeypass
 *
 */

object AESEncryptDecryptUtils {

  def getKeyFromKeyStore(keystoreLocation: String, keystorePass: String, alias: String, keyPass: String): Key = try {
    val keystoreStream = new FileInputStream(keystoreLocation)
    val keystore = KeyStore.getInstance("JCEKS")
    keystore.load(keystoreStream, keystorePass.toCharArray)

    if (!keystore.containsAlias(alias)) throw new RuntimeException("Alias for key not found")
    val key = keystore.getKey(alias, keyPass.toCharArray)
    key
  } catch {
    case e @ (_: UnrecoverableKeyException | _: KeyStoreException | _: CertificateException | _: IOException | _: NoSuchAlgorithmException) =>
      throw new RuntimeException(e)
  }

  def encrypt(value: String, config: Config): String = {

    val keystoreLocation = config.getString("src.aes.keystoreLocation")
    val keystorePass = config.getString("src.aes.keystorePass")
    val alias = config.getString("src.aes.alias")
    val keyPass = config.getString("src.aes.keyPass")

    val initvector: String = "RandomInitVector"
    val iv: IvParameterSpec = new IvParameterSpec(initvector.getBytes("UTF-8"))
    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    val skeyspec = getKeyFromKeyStore(keystoreLocation, keystorePass, alias, keyPass)
    cipher.init(Cipher.ENCRYPT_MODE, skeyspec, iv)

    return Base64.encodeBase64String(cipher.doFinal(value.getBytes()))
  }

  def decrypt(encrypted_value: String, keystoreLocation: String, keystorePass: String, alias: String, keyPass: String): String = {

    val initvector: String = "RandomInitVector"
    val iv: IvParameterSpec = new IvParameterSpec(initvector.getBytes("UTF-8"))
    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val skeyspec = getKeyFromKeyStore(keystoreLocation, keystorePass, alias, keyPass)
    cipher.init(Cipher.DECRYPT_MODE, skeyspec, iv)
    val decrypted_value: String = new String(cipher.doFinal(Base64.decodeBase64(encrypted_value)))
    return (decrypted_value)
  }

  def main(args: Array[String]) {
    require(
      args != null && args.length == 1,
      """This program needs exactly 1 argument.
        | 1. conf path""".stripMargin)
    val Array(confPath) = args
    val config = ConfigFactory.parseFile(new File(confPath))
    val scanner: Scanner = new Scanner(System.in);
    System.out.println("Enter the string to be encrypted ");
    val temp: String = scanner.next();
    val encrypted_value = encrypt(temp, config)
    println("Encrypted value " + encrypted_value)
    //    val decrypted_value = decrypt(encrypted_value,config)
    //    println( "decrypted value " + decrypted_value )
  }

}
