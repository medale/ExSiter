package org.medale.exsiter

import com.jcraft.jsch.JSchException
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.io.File

@RunWith(classOf[JUnitRunner])
class SshFileListerTest extends FunSuite {

  test("Get full listing") {
    val configFileLocation = System.getProperty("user.home") + "/.exsiter/application.conf"
    val configFile = new File(configFileLocation)
    val channelCreator = new SshChannelCreator(configFile)
    channelCreator.initializeSession
    channelCreator.openSession
    val sshFileLister = new SshFileLister()
    sshFileLister.channelCreator = channelCreator
    val completeFileListing = sshFileLister.getCompleteFileListing
    println(completeFileListing)
    channelCreator.closeSession
  }
}