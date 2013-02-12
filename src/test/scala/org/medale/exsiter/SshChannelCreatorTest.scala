package org.medale.exsiter

import com.jcraft.jsch.JSchException
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.io.File

@RunWith(classOf[JUnitRunner])
class SshChannelCreatorTest extends FunSuite {

  test("getSessionForCurrentConfigurationSettings with valid dir/file") {
    val configFileLocation = System.getProperty("user.home") + "/.exsiter/application.conf"
    val configFile = new File(configFileLocation)
    val session = SshChannelCreator.getSessionForCurrentConfigurationSettings(configFile)
    assert(session != None)
  }

  test("getSessionForCurrentConfigurationSettings with invalid file") {
    val configFileLocation = "src/test/resources/testConfigs/badApplication.conf"
    val configFile = new File(configFileLocation)
    intercept[JSchException] {
      SshChannelCreator.getSessionForCurrentConfigurationSettings(configFile)
    }
  }

  test("initializeSession with valid dir/file") {
    val configFileLocation = System.getProperty("user.home") + "/.exsiter/application.conf"
    val configFile = new File(configFileLocation)
    val channelCreator = new SshChannelCreator(configFile)
    channelCreator.initializeSession
    val execChannel = channelCreator.getChannelExec
  }
}