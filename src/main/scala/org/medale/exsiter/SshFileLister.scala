package org.medale.exsiter

import java.io.IOException
import java.io.InputStream
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSchException
import scala.io.Source

/**
 * %M file permissions %s file size in bytes %T@ last mod time seconds since 1970 %p file name
 *
 * find . -type f -printf '%M,%s,%T@,%p\n'
 *
 * -rw-r--r--,961361,1348255810.0000000000,./web/firstFriday/BizHead-.jpg
 * -rw-r--r--,647927,1348255791.0000000000,./web/firstFriday/BizHead-0161.jpg
 *
 */
class SshFileLister {

  var channelCreator: SshChannelCreator = _

  def getListingEntries(): List[ListingEntry] = {
    val listings = getCompleteListingIterator
    getListingEntries(listings)
  }

  def getCompleteListingIterator(): List[String] = {
    var listings: List[String] = null
    try {
      val channelExec = channelCreator.getChannelExec();
      channelExec.setCommand("find . -type f -printf '%M,%s,%T@,%p\n'")
      channelExec.connect();
      val commandInputStream = channelExec.getInputStream
      val completeListingIterator = Source.fromInputStream(commandInputStream).getLines()
      listings = completeListingIterator.toList
      channelExec.disconnect()
    } catch {
      case e: Exception =>
        {
          val errMsg = "Unable to get directory listing due to " + e;
          throw new JSchException(errMsg)
        }
    }
    listings
  }

  def getListingEntries(listings: List[String]): List[ListingEntry] = {
    var listingEntries: List[ListingEntry] = Nil
    listings.foreach(line => {
      val entryOption = SshFileLister.getListingEntry(line)
      entryOption match {
        case Some(entry) => listingEntries = entry :: listingEntries
        case None => println("Unable to process entry " + line)
      }
    })
    listingEntries
  }
}

object SshFileLister {
  //-rw-r--r--,961361,1348255810.0000000000,./web/firstFriday/BizHead-.jpg
  val LISTING_LINE_REGEX = """(.*?),(.*?),(.*?),(.*?)""".r

  def getListingEntry(line: String): Option[ListingEntry] = {
    var entry: ListingEntry = null
    try {
      val SshFileLister.LISTING_LINE_REGEX(permissions, sizeInBytes, rawLastModTime, filePath) = line
      var lastModTime = getLastModTime(rawLastModTime)
      val (parentDir, fileName) = getParentDirAndFileName(filePath)
      entry = new ListingEntry(permissions, sizeInBytes.toInt, lastModTime, parentDir, fileName)
    } catch {
      //ignore scala.MatchError or conversion error and create None option
      case e: MatchError => println("Unable to parse line >>" + line + "<<")
    }
    if (entry != null) Some(entry) else None
  }

  def getLastModTime(rawLastModTime: String): Long = {
    val dotSplits = rawLastModTime.split("\\.")
    dotSplits(0).toLong * 1000 //need milliseconds
  }

  def getParentDirAndFileName(filePath: String): (String, String) = {
    val lastForwardSlashIndex = filePath.lastIndexOf("/")
    val parentDir = filePath.substring(0, lastForwardSlashIndex + 1)
    val fileName = filePath.substring(lastForwardSlashIndex + 1)
    return (parentDir, fileName)
  }
}