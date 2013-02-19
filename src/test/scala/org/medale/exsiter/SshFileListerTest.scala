package org.medale.exsiter

import com.jcraft.jsch.JSchException
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.io.File
import java.util.Date

@RunWith(classOf[JUnitRunner])
class SshFileListerTest extends FunSuite {

  ignore("Get full listing") {
    val configFileLocation = System.getProperty("user.home") + "/.exsiter/application.conf"
    val configFile = new File(configFileLocation)
    val channelCreator = new SshChannelCreator(configFile)
    channelCreator.initializeSession
    channelCreator.openSession
    val sshFileLister = new SshFileLister()
    sshFileLister.channelCreator = channelCreator
    val listings = sshFileLister.getCompleteListingIterator
    assert(listings.size > 0)
    channelCreator.closeSession
  }

  test("Test getListingEntry with empty line") {
    val line = """"""
    val result = SshFileLister.getListingEntry(line)
    assert(result == None)
  }

  test("test getListingEntry sunny day") {
    val line = "-rw-r--r--,961361,1348255810.0000000000,./web/firstFriday/BizHead-.jpg"
    val result = SshFileLister.getListingEntry(line)
    result match {
      case Some(entry) => {
        assert(entry.permissions === "-rw-r--r--")
        assert(entry.sizeInBytes === 961361)
        assert(entry.lastModTime === 1348255810000L)
        assert(entry.parentDir === "./web/firstFriday/")
        assert(entry.fileName === "BizHead-.jpg")
      }
      case None => fail
    }
  }
}