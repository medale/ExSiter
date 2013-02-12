package org.medale.exsiter

import com.jcraft.jsch.{ Channel, ChannelExec, ChannelShell, JSch, JSchException, Session }
import com.typesafe.config.ConfigFactory
import java.io.File

class SshChannelCreator(val configFile: File) {

  private var session: Session = _

  def initializeSession() {
    val sessionOption = SshChannelCreator.getSessionForCurrentConfigurationSettings(configFile)
    sessionOption match {
      case Some(session) => this.session = session
      case None => throw new RuntimeException("Unable to initialize session.")
    }
  }

  def openSession() {
    if (session != null) {
      if (session.isConnected()) {
        session.disconnect();
      }
      session.connect();
    } else {
      val errMsg = "Session must be successfully initialized before opening."
      throw new JSchException(errMsg);
    }
  }

  def getChannelExec(): ChannelExec = {
    getChannel(SshChannelCreator.EXEC_CHANNEL_TYPE).asInstanceOf[ChannelExec]
  }

  def getChannelShell(): ChannelShell = {
    getChannel(SshChannelCreator.SHELL_CHANNEL_TYPE).asInstanceOf[ChannelShell]
  }

  def getChannel(channelType: String): Channel = {
    var channel: Channel = null
    if (isOpenSession()) {
      channel = session.openChannel(channelType)
    } else {
      val errMsg = "JSch session must be open to get channel."
      throw new JSchException(errMsg);
    }
    channel
  }

  def closeSession() {
    if (isOpenSession()) {
      session.disconnect();
    }
  }

  def isOpenSession(): Boolean = {
    session != null && session.isConnected()
  }

}

object SshChannelCreator {

  val EXEC_CHANNEL_TYPE = "exec"
  val SHELL_CHANNEL_TYPE = "shell"

  def getSessionForCurrentConfigurationSettings(configFile: File): Option[Session] = {
    val conf = ConfigFactory.parseFile(configFile).resolve()
    val privateKeyLocation = conf.getString("privateKeyLocation")
    val privateKeyPassphrase = conf.getString("privateKeyPassphrase")
    val knownHostsLocation = conf.getString("knownHostsLocation")
    val username = conf.getString("username")
    val hostname = conf.getString("hostname")
    val jsch = new JSch();
    jsch.addIdentity(privateKeyLocation, privateKeyPassphrase);
    jsch.setKnownHosts(knownHostsLocation);
    val session = jsch.getSession(username, hostname)
    if (session == null) None else Some(session)
  }

}