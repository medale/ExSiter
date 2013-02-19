package org.medale.exsiter

import org.apache.commons.lang3.builder.ToStringBuilder

class ListingEntry(val permissions: String, val sizeInBytes: Int, val lastModTime: Long,
  val parentDir: String, val fileName: String) {

  override def toString(): String = {
    ToStringBuilder.reflectionToString(this)
  }
}