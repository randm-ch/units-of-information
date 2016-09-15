package util

import Unit._

/** Storage size is the amount of Bits that a certain entity (i.e. a file) takes up in memory or on a disk.
  *
  * This class provides a way for developers to abstract over
  * [[https://en.wikipedia.org/wiki/Units_of_information Units of Information]]. An initial value can be created by
  * calling the static {{{apply(double, Unit)}}} method, which will return a new `UnitsOfInformation` object, where the
  * size is correctly calculated from the `amount` and `unit` parameters.
  *
  * This class is immutable, meaning you can not change the internal state once it's set. You're only able to get the
  * value back in the unit you want.
  *
  * While the `amount` is self explanatory, it may not be so clear what number should be passed as `unit`. Here the predefined Units come into play. Let's assume you want to represent 2.5 Kilobytes.
  *
  * {{{
  *   // Java
  *   UnitsOfInformation.apply(2.5, Unit.KB)}
  *   // Scala (with imported implicits)
  *   2.5.KB
  * }}}
  *
  * General usage examples are shown below.
  *
  * {{{
  *   // Create the UnitsOfInformation object
  *   val size = UnitsOfInformation(250, Unit.MB)
  *   // Read in Mebibyte
  *   size in MiB
  *   // Will print "0.25 GB"
  *   println(size format(GB, "#.##"))
  *   // Will print "250 MB"
  *   println(size format "#")
  * }}}
  *
  * One practical feature of this class is that you can read the value out with it's best suited Unit.
  *
  * {{{
  *   // Will return MB
  *   UnitsOfInformation(250000000L) unit
  *   // Will return GB
  *   UnitsOfInformation(2500000000L) unit
  * }}}
  *
  * In Scala you can initialize the `UnitsOfInformation` object with the help of implicit conversions. For this to work, you have to import the implicit class `util.UnitsOfInformation.Implicits`.
  *
  * {{{
  *   // Will print "2.5 GB"
  *   (2.5 GB) format "#.#"
  *   // Returns 2.5
  *   2500.MB in GB
  * }}}
  */
class UnitsOfInformation(val size: BigInt) {

  import UnitsOfInformation._

  if(size < 0)
    throw new IllegalArgumentException("Amount must be greater than or equal to zero.")

  /** @see [[unit(unit: Unit): Unit]]
    */
  def unit(): Unit = unit(B)

  /** Find out and return the Unit best suited to represent the internal value by dividing the `size` by the Unit's
    * value and checking if the result is greater than one, starting with the biggest Unit.
    *
    * The number system (decimal or binary) is determined automatically.
    *
    * {{{
    *   // Create the UnitsOfInformation object
    *   val size1 = UnitsOfInformation(250000000000L)
    *   // Return the best Byte Unit (in this case, `Unit.GB`)
    *   size1.unit
    *   // Return the best Bit Unit (in this case, `Unit.GiB`)
    *   size1.unit(b)
    * }}}
    *
    * @return The Unit best suited to represent this instance's value
    */
  def unit(unit: Unit): Unit = {
    // Find out if we're using a decimal or binary Unit. This is determined by dividing the Bit count by 1024 (BIN)
    // and multiplying that number with 100. If the result is a natural number that means that `size` can be
    // represented with two decimal places in the binary system, which is good enough. Otherwise we use decimal
    val system = (BigDecimal(size) / Decimal * 100).isWhole match {
      case true => Decimal
      case false => (BigDecimal(size) / Binary * 100).isWhole match {
        case true => Binary
        case false => Decimal
      }
    }

    val units = UnitsOfInformation.getUnit(system, unit.multiplier).filter(size / _.value >= 1).sortBy(_.exponent).reverse
    units.headOption match {
      case Some(x) => x
      case None => unit
    }
  }

  /**
    * Returns the internal value in the requested Unit.
    *
    * {{{
    *   // Create the UnitsOfInformation object
    *   val size = UnitsOfInformation(250, Unit.MB)
    *   // Read in Mebibyte
    *   size.in(MiB)
    * }}}
    *
    * @param unit The requested Unit of information
    * @return A `double` value representing the object's `size` in the requested Unit of information
    */
  def in(unit: Unit): Double = (BigDecimal(size) / BigDecimal(unit.value)).toDouble

  /**
    * @see [[format]]
    */
  def format(formatting: String): String = format(unit(), formatting)

  /**
    * This is a convenience method to return a formatted string, which automatically appends the Unit's abbreviation.
    * Internally the default Scala sting formatter is used, please refer to that documentation if you want to find out
    * what String to pass to the `format` argument.
    *
    * {{{
    *   // Create the UnitsOfInformation object
    *   UnitsOfInformation size = UnitsOfInformation.apply(250, Unit.MB);
    *   // Print in Gigabyte (will print "0.25 GB")
    *   System.out.println(size.format(Unit.GB, "%.2f"));
    *   // Print in Gigabyte (will print "0.3 GB")
    *   System.out.println(size.format(Unit.GB, "%.1f"));
    *   // Will print "250 MB"
    *   System.out.println(size.format("%f"));
    * }}}
    *
    * @param unit       The requested Unit of information
    * @param formatting The format in which to format the `double` value
    * @return A String with format `format + " " + unit.name()`
    */
  def format(unit: Unit, formatting: String): String =
    formatting.format(in(unit)) + " " + unit.name()

  override def toString: String = "UnitsOfInformation{" + format(unit(), "#.##") + "}"

}

/** Companion object of the [[UnitsOfInformation]] class, holding the static values and methods.
  */
object UnitsOfInformation {

  /** Floating point precision, used when dividing [[BigDecimal]] numbers to round the result, if necessary. */
  private val Precision: Int = 5

  /** The default value for all the parts of a Unit. */
  val Default: Int = 1

  // There are two ways of calculating units of information: decimal and binary
  val Decimal: Int = 1000
  val Binary: Int = 1024

  // We implement the storage size magnitudes up until x^8, x being either `Decimal` or `Binary`
  val Kilo: Int = 1
  val Mega: Int = 2
  val Giga: Int = 3
  val Tera: Int = 4
  val Peta: Int = 5
  val Exa: Int = 6
  val Zetta: Int = 7
  val Yotta: Int = 8

  /** A list of all the available Units */
  val units: List[Unit] = Unit.values().toList

  /** @see [[apply(amount:Double, unit:Unit)]]
    */
  def apply(amount: Double): UnitsOfInformation = apply(amount, b)

  /** Factory method for the UnitsOfInformation class, to have a more sophisticated API.
    *
    * {{{
    *   // Create a UnitsOfInformation object representing 2.5 MB
    *   UnitsOfInformation(2.5, Unit.MB);
    *   // Create a UnitsOfInformation object representing 1024 Bit
    *   UnitsOfInformation(1024);
    * }}}
    *
    * @param amount The amount of Units to represent (must be greater than zero)
    * @param unit   Unit to be used to calculate the real value of the instance
    * @return An object that can be used to calculate any unit of information from the internal value
    * @throws IllegalArgumentException If the `amount` is infinite or a fraction of a Bit
    */
  def apply(amount: Double, unit: Unit): UnitsOfInformation = {
    if(amount.isInfinite || amount.isNaN)
      throw new IllegalArgumentException("Amount can not be infinite.")

    // Recursive inner function to map the `amount` and `unit` in case the `amount` is not an integer. This is
    // required because we want to make sure that `amount` and `unit` combined doesn't result in a fraction of a
    // Bit. In that case, we would throw an `IllegalArgumentException`
    def inner(d: Double, u: Unit): UnitsOfInformation =
      if(d isWhole)
        new UnitsOfInformation(u.value * d.longValue())
      else
        // Get the next Unit where the exponent is one lower
        getUnit(u.system, u.multiplier, u.exponent - 1) match {
          case Some(x) => inner(d * u.system, x)
          case None => throw new IllegalArgumentException("Amount cannot be a fraction of a Bit.")
        }

    inner(amount, unit)
  }

  /** Returns a List of Units, filtered by the conditions passed as arguments.
    *
    * @param system     The system to filter by
    * @param multiplier The multiplier to filter by
    * @return A filtered List of Units
    */
  private def getUnit(system: Int, multiplier: Int): List[Unit] =
    units
      .filter(_.system == system) // Get all Units with the defined number system
      .filter(_.multiplier == multiplier) // Get all Units with the defined Bit multiplier

  /** Returns an Optional of Unit, filtered by the conditions passed as arguments.
    *
    * @param system     The system to filter by
    * @param multiplier The multiplier to filter by
    * @param exponent   The exponent to filter by
    * @return An Option of Unit if the conditions hold, or an empty one
    */
  private def getUnit(system: Int, multiplier: Int, exponent: Int): Option[Unit] =
    getUnit(system, multiplier).find(_.exponent == exponent) // Get Units that have the defined exponent

  /** Allows implicit conversions of Double values to `UnitsOfInformation` objects.
    *
    * {{{
    *   1 bit // Equal to UnitsOfInformation(1)
    *   3 MB // Equal to UnitsOfInformation(3, MB)
    *   2.6 gigabytes // Equal to UnitsOfInformation(2.6, GB)
    * }}}
    *
    * @param amount The amount that should be converted to UnitsOfInformation
    */
  implicit class Implicits(amount: Double) {

    def bits = apply(amount)
    def b = apply(amount)
    def bytes = apply(amount, Unit.B)
    def B = apply(amount, Unit.B)
    def kilobits = apply(amount, Unit.Kbit)
    def Kbit = apply(amount, Unit.Kbit)
    def kilobytes = apply(amount, Unit.KB)
    def KB = apply(amount, Unit.KB)
    def megabits = apply(amount, Unit.Mbit)
    def Mbit = apply(amount, Unit.Mbit)
    def megabytes = apply(amount, Unit.MB)
    def MB = apply(amount, Unit.MB)
    def gigabits = apply(amount, Unit.Gbit)
    def Gbit = apply(amount, Unit.Gbit)
    def gigabytes = apply(amount, Unit.GB)
    def GB = apply(amount, Unit.GB)

  }

}
