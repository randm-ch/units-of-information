package util;

import org.junit.Test;

import static util.UnitsOfInformation.Unit.*;
import static org.junit.Assert.*;

/**
 * Tests the {@link UnitsOfInformation} class.
 */
public class UnitsOfInformationTest {

    @Test
    public void testOf() {
        UnitsOfInformation size1 = UnitsOfInformation.of(1024);
        assertEquals("The internal state after creation with int value must be correct", 1024.0, size1.in(b), 0.0);

        UnitsOfInformation size2 = UnitsOfInformation.of(250000000000L);
        assertEquals("The internal state after creation with long value must be correct", 250000000000.0, size2.in(b), 0.0);

        UnitsOfInformation size3 = UnitsOfInformation.of(2.5, MB);
        assertEquals("The internal state after creation with Unit value must be correct", 2.5 * 8 * Math.pow(1000, 2), size3.in(b), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfWithZero() {
        UnitsOfInformation.of(0);
    }

    @Test
    public void testIn() {
        UnitsOfInformation size1 = UnitsOfInformation.of(1024);
        assertEquals("in(b) must return the exact internal value", 1024.0, size1.in(b), 0.0);

        UnitsOfInformation size2 = UnitsOfInformation.of(2.5 * 8 * Math.pow(1000, 2));
        assertEquals("in(MB) must return the correct value", 2.5, size2.in(MB), 0.0);

        UnitsOfInformation size3 = UnitsOfInformation.of(250, MB);
        assertEquals("in(GB) must return the correct value", 0.25, size3.in(GB), 0.0);
    }

    @Test
    public void testFormat() {
        UnitsOfInformation size1 = UnitsOfInformation.of(2.51 * 8 * Math.pow(1000, 2));
        assertEquals("format(MB, \"%.1f\") must return the correctly formatted string", "2.5 MB", size1.format(MB, "#.#"));

        UnitsOfInformation size2 = UnitsOfInformation.of(250, MB);
        assertEquals("format(GB, \"%.2f\") must return the correctly formatted string", "0.25 GB", size2.format(GB, "#.##"));

        UnitsOfInformation size3 = UnitsOfInformation.of(2.5, MB);
        assertEquals("format(MB, \"%d\") must return the correctly rounded and formatted string", "3 MB", size3.format(MB, "#"));
    }

    @Test
    public void testUnit() {
        UnitsOfInformation size1 = UnitsOfInformation.of(1024);
        assertEquals("unit() must return the best suiting Unit", Kibit, size1.unit(b));

        UnitsOfInformation size2 = UnitsOfInformation.of(250000000000L);
        assertEquals("unit() must return the best suiting Unit", GB, size2.unit());

        UnitsOfInformation size3 = UnitsOfInformation.of(2.5, MB);
        assertEquals("unit() must return the best suiting Unit", MB, size3.unit());
    }

}
