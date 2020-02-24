package com.ppb.dk.time

import org.junit.{Assert, Test}

class TimeStoreTest {

  private val testClass = new TimeStore

  @Test
  def testAdd(): Unit = {
    Assert.assertEquals(0, size)

    testClass.add("a")
    Assert.assertEquals(1, size)

    testClass.add("b")
    Assert.assertEquals(2, size)
  }

  @Test
  def testClear(): Unit = {
    testClass.add("a")
    assert(size > 0)

    testClass.clear()
    Assert.assertEquals(size, 0)
  }

  private def size: Int = testClass.listAll.size
}
