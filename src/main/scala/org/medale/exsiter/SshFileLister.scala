package org.medale.exsiter

import java.io.IOException
import java.io.InputStream
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSchException
import scala.io.Source

/**
 * %M file permissions %s file size in bytes %T@ last mod time %p file name
 *
 * find . -type f -printf '%M|%s|%T@|%p\n'
 */
class SshFileLister {

  var channelCreator: SshChannelCreator = _

  def getCompleteFileListing(): String = {
    var completeListing : String = null
    try {
       val channelExec = channelCreator.getChannelExec();
      channelExec.setCommand("find . -type f -printf '%M|%s|%T@|%p\n'")
      channelExec.connect();
      val commandInputStream = channelExec.getInputStream
      completeListing = Source.fromInputStream(commandInputStream).getLines().mkString("\n")
      channelExec.disconnect();
    } catch {
      case e : Exception => {
    	  val errMsg = "Unable to get directory listing due to " + e;
    	  throw new JSchException(errMsg)
      }
    }
    return completeListing
  }
}