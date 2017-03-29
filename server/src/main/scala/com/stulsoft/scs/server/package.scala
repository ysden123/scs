/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs

import com.stulsoft.scs.server.DataTable
import slick.lifted.TableQuery

/**
  * @author Yuriy Stul
  */
package object server {
  val dataTable: TableQuery[DataTable] = TableQuery[DataTable]
}
