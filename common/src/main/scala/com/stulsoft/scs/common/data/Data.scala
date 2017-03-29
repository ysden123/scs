/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.common.data

/**
  * Holds a data object details
  *
  * @param key   the key
  * @param value the value
  * @param ttl   the TTL (maximum life time)
  * @author Yuriy Stul
  */
case class Data(key: String, value: String, ttl: Long)