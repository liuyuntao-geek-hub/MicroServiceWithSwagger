package com.deloitte.demo.util

/**
  * Created by yuntliu on 11/8/2017.
  */

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.salt.RandomSaltGenerator

class EncryptionUtil {
  private val KEY = "8yewsfAasdF2sD6c4ASasdX17urfs20POASD"

  private val ALGORITHM = "PBEWithMD5AndDES"

  private val ITERATIONS = 500

  /**
    * Encrypts a given String
    * @param aValueToBeEncrypted
    * @return the encrypted String
    */
  def encrypt(aValueToBeEncrypted: String): String = {
    val enc = new StandardPBEStringEncryptor()
    enc.setAlgorithm(ALGORITHM)
    enc.setPassword(KEY)
    enc.setKeyObtentionIterations(ITERATIONS)
    enc.setSaltGenerator(new RandomSaltGenerator())
    enc.encrypt(aValueToBeEncrypted)
  }

  /**
    * Decrypts a given String
    * @param aEncryptedString
    * @return the decrypted String
    */
  def decrypt(aEncryptedString: String): String = {
    val enc = new StandardPBEStringEncryptor()
    enc.setAlgorithm(ALGORITHM)
    enc.setPassword(KEY)
    enc.setKeyObtentionIterations(ITERATIONS)
    enc.setSaltGenerator(new RandomSaltGenerator())
    enc.decrypt(aEncryptedString)
  }
}
object EncryptionUtil {

  private val _instance = new EncryptionUtil()

  def instance() = _instance

}