/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.common.data

/**
  * Response
  *
  * @author Yuriy Stul
  */
case class Response(status:Int, data:Option[Data], errorMessage:Option[String])