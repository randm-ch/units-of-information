package ch.randm.uoi;

import static ch.randm.uoi.UnitsOfInformation.*;

import scala.math.BigDecimal;
import scala.math.BigInt;

/**
 * Each enum variable represents one type of unit of information.
 */
public enum Unit {

    b("Bit", 1),
    B("Byte", 8),

    // Decimal Bit units (International system of Units, SI)
    Kbit("Kilobit", Decimal(), Kilo()),
    Mbit("Megabit", Decimal(), Mega()),
    Gbit("Gigabit", Decimal(), Giga()),
    Tbit("Terabit", Decimal(), Tera()),
    Pbit("Petabit", Decimal(), Peta()),
    Ebit("Exabit", Decimal(), Exa()),
    Zbit("Zettabit", Decimal(), Zetta()),
    Ybit("Yettabit", Decimal(), Yotta()),

    // Binary Bit units (ISO/IEC 80000)
    Kibit("Kibibit", Binary(), Kilo()),
    Mibit("Mebibit", Binary(), Mega()),
    Gibit("Gibibit", Binary(), Giga()),
    Tibit("Tebibit", Binary(), Tera()),
    Pibit("Pebibit", Binary(), Peta()),
    Eibit("Exbibit", Binary(), Exa()),
    Zibit("Zebibit", Binary(), Zetta()),
    Yibit("Yobibit", Binary(), Yotta()),

    // Decimal Byte units (Metric system)
    KB("Kilobyte", Decimal(), Kilo(), B.multiplier),
    MB("Megabyte", Decimal(), Mega(), B.multiplier),
    GB("Gigabyte", Decimal(), Giga(), B.multiplier),
    TB("Terabyte", Decimal(), Tera(), B.multiplier),
    PB("Petabyte", Decimal(), Peta(), B.multiplier),
    EB("Exabyte", Decimal(), Exa(), B.multiplier),
    ZB("Zettabyte", Decimal(), Zetta(), B.multiplier),
    YB("Yottabyte", Decimal(), Yotta(), B.multiplier),

    // Binary Byte units (ISO/IEC 80000)
    KiB("Kibibyte", Binary(), Kilo(), B.multiplier),
    MiB("Mebibyte", Binary(), Mega(), B.multiplier),
    GiB("Gibibyte", Binary(), Giga(), B.multiplier),
    TiB("Tebibyte", Binary(), Tera(), B.multiplier),
    PiB("Pebibyte", Binary(), Peta(), B.multiplier),
    EiB("Exbibyte", Binary(), Exa(), B.multiplier),
    ZiB("Zebibyte", Binary(), Zetta(), B.multiplier),
    YiB("Yobibyte", Binary(), Yotta(), B.multiplier);

    final String name;

    final BigInt value;

    final int system;

    final int multiplier;

    final int exponent;

    /**
     * @see Unit(String, int, int, int)
     */
    Unit(String name, int multiplier) {
        this(name, Default(), Default(), multiplier);
    }

    /**
     * @see Unit(String, int, int, int)
     */
    Unit(String name, int system, int exponent) {
        this(name, system, exponent, Default());
    }

    /**
     * Don't use this constructor directly unless you really need to, use the predefined enum variables instead.
     *
     * The value is calculated as {@code multiplier * (system ^ exponent)}
     *
     * @param name The long, human readable name of the Unit
     * @param system The number system, use `Decimal()` (decimal, 10-based) or `Binary()` (binary, 2-based) here
     * @param exponent The system is raised to the power of this value to calculate the Unit value
     * @param multiplier Set this to `B.value` if this unit is calculated in Bytes
     */
    Unit(String name, int system, int exponent, int multiplier) {
        this.name = name;
        this.system = system;
        this.exponent = exponent;
        this.multiplier = multiplier;
        // Calculate the Bit value
        value = BigDecimal.valueOf(multiplier).$times(BigDecimal.valueOf(Math.pow(system, exponent))).toBigInt();
    }

    @Override
    public String toString() {
        return "Unit{name='" + name + '\'' +
                ", system=" + system +
                ", multiplier=" + multiplier +
                ", exponent=" + exponent +
                '}';
    }

}
