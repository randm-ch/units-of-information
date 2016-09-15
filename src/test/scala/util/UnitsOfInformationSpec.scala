package util

import org.specs2.Specification
import util.Unit._
import util.UnitsOfInformation.Implicits

class UnitsOfInformationSpec extends Specification { def is = s2"""

 This is a specification to check the 'UnitsOfInformation' class, especially the scala wrapper and implicits

 The 'UnitsOfInformation' class should
   be able to build an object correctly                           $e1
   not build an object if the amount is < 0                       $e2
   return the correct amount per Unit                             $e3
   return a correctly formatted String per Unit                   $e4
   return the correct Unit for an object                          $e5
                                                                  """

  def e1 = {
    1024.bits mustEqual 1024.0
    250000000000L.bits mustEqual 250000000000.0
    2.5.MB in b mustEqual 2.5 * 8 * scala.math.pow(1000, 2)
    0.25.GB in b mustEqual 250 * 8 * scala.math.pow(1000, 2)
  }

  def e2 = UnitsOfInformation(-1) must throwA[IllegalArgumentException]

  def e3 = {
    (2.5 * 8 * scala.math.pow(1000, 2) bits) in MB mustEqual 2.5
    250.MB in GB mustEqual 0.25
  }

  def e4 = {
    (2.51 * 8 * scala.math.pow(1000, 2) bits) format(MB, "%.1f") mustEqual "2.5 MB"
    250.MB format(GB, "%.2f") mustEqual "0.25 GB"
    2.5.MB format "%.0f" mustEqual "3 MB"
  }

  def e5 = {
    1024.bits unit b mustEqual Kibit
    250000000000L.bits unit() mustEqual GB
    2.5.MB unit() mustEqual MB
  }

}
