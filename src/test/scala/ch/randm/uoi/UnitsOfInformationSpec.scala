package ch.randm.uoi

import org.specs2.Specification
import Unit._
import UnitsOfInformation.Implicits

class UnitsOfInformationSpec extends Specification { def is = s2"""

 This is a specification to check the implementation of the 'UnitsOfInformation' class

 The 'UnitsOfInformation' class should
   be able to build an object correctly                           $e1
   not build an object if the amount is < 0                       $e2
   not build an object if the amount is NaN                       $e3
   return the correct amount per Unit                             $e4
   return a correctly formatted String per Unit                   $e5
   return the correct Unit for an object                          $e6
   be able to create objects with the implicit class              $e7
                                                                  """

  def e1 = {
    1024.bits mustEqual 1024.0
    250000000000L.bits mustEqual 250000000000.0
    2.5.MB in b mustEqual 2.5 * 8 * math.pow(1000, 2)
    0.25.GB in b mustEqual 250 * 8 * math.pow(1000, 2)
  }

  def e2 = UnitsOfInformation(-1) must throwA[IllegalArgumentException]("Amount must be greater than or equal to zero.")

  def e3 = UnitsOfInformation(Double.NaN) must throwA[IllegalArgumentException]("Amount can not be infinite.")

  def e4 = {
    (2.5 * 8 * scala.math.pow(1000, 2) bits) in MB mustEqual 2.5
    250.MB in GB mustEqual 0.25
    UnitsOfInformation(512000, KiB) in MiB mustEqual 500
  }

  def e5 = {
    (2.51 * 8 * scala.math.pow(1000, 2) bits) format(MB, "%.1f") mustEqual "2.5 MB"
    250.MB format(GB, "%.2f") mustEqual "0.25 GB"
    2.5.MB format "%.0f" mustEqual "3 MB"
  }

  def e6 = {
    1024.bits unit b mustEqual Kibit
    250000000000L.bits unit() mustEqual GB
    2.5.MB unit() mustEqual MB
  }

  def e7 = {
    1.bits in b mustEqual 1; 1.b in b mustEqual 1
    1.kilobits in b mustEqual math.pow(1000, 1); 1.Kbit in b mustEqual math.pow(1000, 1)
    1.megabits in b mustEqual math.pow(1000, 2); 1.Mbit in b mustEqual math.pow(1000, 2)
    1.gigabits in b mustEqual math.pow(1000, 3); 1.Gbit in b mustEqual math.pow(1000, 3)

    1.bytes in b mustEqual 8; 1.B in b mustEqual 8
    1.kilobytes in b mustEqual 8 * math.pow(1000, 1); 1.KB in b mustEqual 8 * math.pow(1000, 1)
    1.megabytes in b mustEqual 8 * math.pow(1000, 2); 1.MB in b mustEqual 8 * math.pow(1000, 2)
    1.gigabytes in b mustEqual 8 * math.pow(1000, 3); 1.GB in b mustEqual 8 * math.pow(1000, 3)
  }

}
