# Units of Information

[![Build Status](https://travis-ci.org/randm-ch/units-of-information.svg?branch=master)](https://travis-ci.org/randm-ch/units-of-information)

**An immutable Scala/Java class to represent units of information (file size, disk space, memory)**

## Synopsis

Storage size is the amount of Bits that a certain entity (i.e. a file) takes up in memory or on a disk.

This class provides a way for developers to abstract over [Units of Information](https://en.wikipedia.org/wiki/Units_of_information). An initial value can be created by calling the static `apply(double, Unit)` method, which will return a new `UnitsOfInformation` object, where the size is correctly calculated from the `amount` and `unit` parameters.

This class is immutable, meaning you can not change the internal state once it's set. You're only able to get the value back in the unit you want.

Because the class implements `Serializable` it can be stored to and retrieved from any kind of ORM quite easily.

While the `amount` is self explanatory, it may not be so clear what number should be passed as `unit`. Here the predefined Units come into play. Let's assume you want to represent 2.5 Kilobytes.

    // Java
    UnitsOfInformation.apply(2.5, Unit.KB)}
    // Scala (with imported implicits)
    2.5.KB

General usage examples are shown below.

## Scala

    // Create the UnitsOfInformation object
    val size = UnitsOfInformation(250, Unit.MB)
    // Read in Mebibyte
    size in MiB
    // Will print "0.25 GB"
    println(size format(GB, "%.2f"))
    // Will print "250 MB"
    println(size format "%.0f")

One practical feature of this class is that you can read the value out with it's best suited Unit.

    // Will return MB
    UnitsOfInformation(250000000L) unit
    // Will return GB
    UnitsOfInformation(2500000000L) unit

In Scala you can initialize the `UnitsOfInformation` object with the help of implicit conversions. For this to work, you have to import the implicit class `util.UnitsOfInformation.Implicits`.

    // Will print "2.5 GB"
    (2.5 GB) format "%.1f"
    // Returns 2.5
    2500.MB in GB

## Java

    // Create the UnitsOfInformation object
    UnitsOfInformation size = UnitsOfInformation.apply(250, Unit.MB);
    // Read in Mebibyte
    size.in(Unit.MiB)
    // Will print "0.25 GB"
    System.out.println(size.format(Unit.GB, "%.2f"))
    // Will print "250 MB"
    System.out.println(size.format("%.2f"))

One practical feature of this class is that you can read the value out with it's best suited Unit.

    // Will return MB
    UnitsOfInformation.apply(250000000L).unit()
    // Will return GB
    UnitsOfInformation.apply(2500000000L).unit()

## Future improvements:
 - Immutable calculation methods (add, subtract, multiply, divide, ...)
 - Helper methods (e.g. calculate free storage in percent)
 - Create managed dependency
