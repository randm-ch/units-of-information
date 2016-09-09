package util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Storage size is the amount of Bits that a certain entity (i.e. a file) takes up in memory or on a disk.
 *
 * This class provides a way for developers to abstract over
 * <a href="https://en.wikipedia.org/wiki/Units_of_information">Units of Information</a>. An initial value can be
 * created by calling the static {@link #of(double, Unit) of} method, which will return a new UnitsOfInformation object, where
 * the size is correctly calculated from the `amount` and `unit` parameters.
 *
 * While the `amount` is self explanatory, it may not be so clear what number should be passed as `unit`. Here the
 * predefined Units come into play. Let's assume you want to represent 2.5 Kilobytes, here's the code for that:
 *
 * {@code UnitsOfInformation.of(2.5, Unit.KB)}
 *
 * This class is immutable, meaning you can not change the internal state once it's set. You're only able to get the
 * value back in the unit you want.
 *
 * {@code
 * // Create the UnitsOfInformation object
 * UnitsOfInformation size = UnitsOfInformation.of(250, Unit.MB);
 * // Read in Mebibyte
 * double mebibyte = size.in(Unit.MiB)
 * // Print in Gigabyte (will print "0.25 GB")
 * System.out.println(size.format(Unit.GB, "%.2f"))
 * }
 *
 * One practical feature of this class is that you can read the value out with it's best suited Unit.
 *
 * {@code
 * // Create the UnitsOfInformation object
 * UnitsOfInformation size = UnitsOfInformation.of(250000000);
 * // Will print "250 MB"
 * System.out.println(size.format(size.unit(), "%.2f"))
 * // Create the UnitsOfInformation object
 * UnitsOfInformation size = UnitsOfInformation.of(2500000000);
 * // Will print "2.5 GB"
 * System.out.println(size.format(size.unit(), "%.2f"))
 * }
 */
public class UnitsOfInformation implements Serializable {

    /**
     * Floating point precision, used when dividing {@link BigDecimal} numbers to round the result, if necessary.
     */
    private static final int PRECISION = 5;

    // There are two ways of calculating units of information: Decimal and Binary
    private static final int NONE = 1;
    private static final int DEC = 1000;
    private static final int BIN = 1024;

    // We implement the storage size magnitudes up until x^8, x being either `DEC` or `BIN`
    private static final int KILO = 1;
    private static final int MEGA = 2;
    private static final int GIGA = 3;
    private static final int TERA = 4;
    private static final int PETA = 5;
    private static final int EXA = 6;
    private static final int ZETTA = 7;
    private static final int YOTTA = 8;

    /**
     * Each enum variable represents one type of unit of information.
     */
    public enum Unit {

        b("Bit", 1),
        B("Byte", 8),

        // Decimal Bit units (International system of Units, SI)
        Kbit("Kilobit", DEC, KILO),
        Mbit("Megabit", DEC, MEGA),
        Gbit("Gigabit", DEC, GIGA),
        Tbit("Terabit", DEC, TERA),
        Pbit("Petabit", DEC, PETA),
        Ebit("Exabit", DEC, EXA),
        Zbit("Zettabit", DEC, ZETTA),
        Ybit("Yettabit", DEC, YOTTA),

        // Binary Bit units (ISO/IEC 80000)
        Kibit("Kibibit", BIN, KILO),
        Mibit("Mebibit", BIN, MEGA),
        Gibit("Gibibit", BIN, GIGA),
        Tibit("Tebibit", BIN, TERA),
        Pibit("Pebibit", BIN, PETA),
        Eibit("Exbibit", BIN, EXA),
        Zibit("Zebibit", BIN, ZETTA),
        Yibit("Yobibit", BIN, YOTTA),

        // Decimal Byte units (Metric system)
        KB("Kilobyte", DEC, KILO, Unit.B.multiplier),
        MB("Megabyte", DEC, MEGA, Unit.B.multiplier),
        GB("Gigabyte", DEC, GIGA, Unit.B.multiplier),
        TB("Terabyte", DEC, TERA, Unit.B.multiplier),
        PB("Petabyte", DEC, PETA, Unit.B.multiplier),
        EB("Exabyte", DEC, EXA, Unit.B.multiplier),
        ZB("Zettabyte", DEC, ZETTA, Unit.B.multiplier),
        YB("Yottabyte", DEC, YOTTA, Unit.B.multiplier),

        // Binary Byte units (ISO/IEC 80000)
        KiB("Kibibyte", BIN, KILO, Unit.B.multiplier),
        MiB("Mebibyte", BIN, MEGA, Unit.B.multiplier),
        GiB("Gibibyte", BIN, GIGA, Unit.B.multiplier),
        TiB("Tebibyte", BIN, TERA, Unit.B.multiplier),
        PiB("Pebibyte", BIN, PETA, Unit.B.multiplier),
        EiB("Exbibyte", BIN, EXA, Unit.B.multiplier),
        ZiB("Zebibyte", BIN, ZETTA, Unit.B.multiplier),
        YiB("Yobibyte", BIN, YOTTA, Unit.B.multiplier);

        private final String name;

        private final BigInteger value;

        private final int system;

        private final int multiplier;

        private final int exponent;

        /**
         * @see Unit(String, int, int, int)
         */
        Unit(String name, int multiplier) {
            this(name, NONE, 1, multiplier);
        }

        /**
         * @see Unit(String, int, int, int)
         */
        Unit(String name, int system, int exponent) {
            this(name, system, exponent, 1);
        }

        /**
         * Don't use this constructor directly unless you really need to, use the predefined enum variables instead.
         *
         * The value is calculated as {@code multiplier * (system ^ exponent)}
         *
         * @param name The long, human readable name of the Unit
         * @param system The number system to use, use `DEC` (decimal, 10-based) or `BIN` (binary, 2-based) here
         * @param exponent The system is raised to the power of this value to calculate the Unit value
         * @param multiplier Set this to `B.value` if this unit is calculated in Bytes
         */
        Unit(String name, int system, int exponent, int multiplier) {
            this.name = name;
            this.system = system;
            this.exponent = exponent;
            this.multiplier = multiplier;
            // Calculate the Bit value
            value = new BigDecimal(multiplier).multiply(new BigDecimal(Math.pow(system, exponent))).toBigIntegerExact();
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

    private static List<Unit> units = Arrays.asList(Unit.class.getEnumConstants());

    /**
     * @see #of(double, Unit)
     */
    public static UnitsOfInformation of(double amount) {
        return UnitsOfInformation.of(amount, Unit.b);
    }

    /**
     * Factory method for the UnitsOfInformation class, to have a more sophisticated API.
     *
     * {@code
     * // Create a UnitsOfInformation object representing 2500 KB
     * UnitsOfInformation size = UnitsOfInformation.of(2.5, Unit.MB);
     * // Create a UnitsOfInformation object representing 1024 Bit
     * UnitsOfInformation size = UnitsOfInformation.of(1024);
     * }
     *
     * @param amount The amount of Units to represent (must be greater than zero)
     * @param unit Unit to be used to calculate the real value of the instance
     * @return An object that can be used to calculate any unit of information from the internal value
     * @throws IllegalArgumentException If the `amount` is infinite or a fraction of a Bit
     */
    public static UnitsOfInformation of(double amount, Unit unit) {
        if(Double.isInfinite(amount)) throw new IllegalArgumentException("Amount can not be infinite.");

        // Recursive inner function to map the `amount` and `unit` in case the `amount` is not an integer. This is
        // required because we want to make sure the `amount` argument does not divide the `unit` so much that it ends
        // up being a fraction of a Bit. In that case, we would throw an `IllegalArgumentException`
        Recursive<BiFunction<Double, Unit, UnitsOfInformation>> inner = new Recursive<>();
        inner.func = (Double d, Unit u) -> {
            if(isInteger(d)) {
                return new UnitsOfInformation(BigInteger.valueOf(d.longValue()), u);
            } else {
                // Get the next Unit where the exponent is one lower
                Unit next = getUnit(u.system, u.multiplier, u.exponent - 1)
                        .orElseThrow(() -> new IllegalArgumentException("Amount cannot be a fraction of a Bit."));

                return inner.func.apply(d * u.system, next);
            }
        };

        return inner.func.apply(amount, unit);
    }

    private static Stream<Unit> getUnit(final int system, final int multiplier) {
        return units.stream()
                .filter(e -> e.system == system) // Get all Units with the defined number system
                .filter(e -> e.multiplier == multiplier); // Get all Units with the defined Bit multiplier
    }

    private static Optional<Unit> getUnit(final int system, final int multiplier, final int exponent) {
        return getUnit(system, multiplier)
                .filter(e -> e.exponent == exponent) // Get Units that have the defined exponent
                .findFirst();
    }

    /**
     * @param number The double value to check
     * @return `true` if the passed `number` can be represented as an int, `false` otherwise
     */
    private static boolean isInteger(double number) {
        return number == Math.floor(number);
    }

    private final BigInteger size;

    /**
     * @param amount The amount of Units to represent (must be greater than zero)
     * @param unit Unit to be used to calculate the real value of the instance
     * @throws IllegalArgumentException If the `amount` is not greater than zero
     */
    private UnitsOfInformation(BigInteger amount, Unit unit) {
        if(amount.compareTo(BigInteger.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        this.size = amount.multiply(unit.value);
    }

    /**
     * @see #unit(Unit)
     */
    public Unit unit() {
        return unit(Unit.B);
    }

    /**
     * Will find out which Unit is best suited to represent the internal value by dividing the value by the Unit's value
     * and checking if the result is greater than one, starting with the biggest Unit.
     *
     * {@code
     * // Create the UnitsOfInformation object
     * UnitsOfInformation size1 = UnitsOfInformation.of(250000000000L);
     * // Return the best Byte Unit (in this case, `Unit.GB`)
     * Unit unit = size1.unit();
     * // Return the best Bit Unit (in this case, `Unit.GiB`)
     * Unit unit = size1.unit(Unit.b);
     * }
     *
     * @return The Unit best suited to represent this instance's value
     */
    public Unit unit(Unit unit) {
        // Find out if we're using a decimal or binary Unit. This is determined by dividing the Bit count by 1024 (BIN)
        // and multiplying that number with 100. If the result is a natural number that means that `size` can be
        // represented with two decimal places in the binary system, which is good enough. Otherwise we use decimal
        boolean decimalOk = isInteger(divide(DEC).multiply(BigDecimal.valueOf(100)).doubleValue());
        boolean binaryOk = isInteger(divide(BIN).multiply(BigDecimal.valueOf(100)).doubleValue());
        final int system = (!decimalOk && !binaryOk) ? DEC : (decimalOk && binaryOk) ? DEC : (decimalOk) ? DEC : BIN;

        for(Unit current : getUnit(system, unit.multiplier).sorted((u1, u2) -> Integer.compare(u2.exponent, u1.exponent)).collect(Collectors.toList())) {
            BigDecimal unitValue = divide(current);
            if(unitValue.compareTo(BigDecimal.ONE) >= 0) {
                return current;
            }
        }

        return unit;
    }

    /**
     * Whenever you want to convert this instance to a new unit of information, use this method.
     *
     * {@code
     * // Create the UnitsOfInformation object
     * UnitsOfInformation size = UnitsOfInformation.of(250, Unit.MB);
     * // Read in Mebibyte
     * double mebibyte = size.in(Unit.MiB)
     * }
     *
     * @param unit The requested Unit of information
     * @return A `double` value representing the UnitsOfInformation's value in the requested Unit of information
     */
    public double in(Unit unit) {
        return divide(unit).doubleValue();
    }

    /**
     * @see #divide(BigInteger)
     */
    private BigDecimal divide(Unit unit) {
        return divide(unit.value);
    }

    /**
     * @see #divide(BigInteger)
     */
    private BigDecimal divide(int number) {
        return divide(BigInteger.valueOf(number));
    }

    /**
     * Divides this object's `size` by the passed {@link BigInteger}.
     *
     * @param divisor The divisor
     * @return The result of the division
     */
    private BigDecimal divide(BigInteger divisor) {
        return new BigDecimal(size).divide(new BigDecimal(divisor), PRECISION, RoundingMode.HALF_UP);
    }

    /**
     * This is a convenience method to return a formatted string, which automatically appends the Unit's abbreviation.
     * Internally the {@link DecimalFormat} class is used, please refer to that documentation if you want to find out
     * what String to pass to the `format` argument.
     *
     * {@code
     * // Create the UnitsOfInformation object
     * UnitsOfInformation size = UnitsOfInformation.of(250, Unit.MB);
     * // Print in Gigabyte (will print "0.25 GB")
     * System.out.println(size.format(Unit.GB, "#.##"));
     * // Print in Gigabyte (will print "0.3 GB")
     * System.out.println(size.format(Unit.GB, "#.#"));
     * }
     *
     * @param unit The requested Unit of information
     * @param format The format in which to format the `double` value
     * @return A String with format `format + " " + unit.name()`
     */
    public String format(Unit unit, String format) {
        DecimalFormat formatter = new DecimalFormat(format);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return formatter.format(in(unit)) + " " + unit.name();
    }

    @Override
    public String toString() {
        return "UnitsOfInformation{" + format(unit(), "#.##") + "}";
    }

    /**
     * Helper class to enable recursive lambda function calls, used in {@link #of(double, Unit)}.
     *
     * @param <I> Functional interface type
     */
    private static class Recursive<I> {
        I func;
    }

}
