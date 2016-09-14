package scalautil

import util.UnitsOfInformation.Unit

object UnitsOfInformation {

  def apply(amount: Double) = util.UnitsOfInformation.of(amount)

  def apply(amount: Double, unit: Unit) = util.UnitsOfInformation.of(amount, unit)

  implicit class Implicits(amount: Double) {

    def bits: util.UnitsOfInformation = UnitsOfInformation(amount)
    def b: util.UnitsOfInformation = UnitsOfInformation(amount)
    def bytes: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.B)
    def B: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.B)
    def kilobits: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.Kbit)
    def Kbit: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.Kbit)
    def kilobytes: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.KB)
    def KB: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.KB)
    def megabits: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.Mbit)
    def Mbit: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.Mbit)
    def megabytes: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.MB)
    def MB: util.UnitsOfInformation = util.UnitsOfInformation.of(amount, Unit.MB)
    def gigabits: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.Gbit)
    def Gbit: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.Gbit)
    def gigabytes: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.GB)
    def GB: util.UnitsOfInformation = UnitsOfInformation(amount, Unit.GB)

  }

}
