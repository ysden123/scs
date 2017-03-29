/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import com.stulsoft.scs.common.data.Data

/**
  * Response
  *
  * @author Yuriy Stul
  */
case class Response(status:Int, data:Option[Data], errorMessage:Option[String])