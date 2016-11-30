# Units of Information

**An immutable Scala class to represent units of information (file size, disk space, memory)**

[![build status](https://git.randm.ch/randm/units-of-information/badges/master/build.svg)](https://git.randm.ch/randm/units-of-information/commits/master)
[![coverage report](https://git.randm.ch/randm/units-of-information/badges/master/coverage.svg)](https://pages.randm.ch/randm/units-of-information/)

## Synopsis

Storage size is the amount of Bits that a certain entity (i.e. a file) takes up in memory or on a disk.

This class provides a way for developers to abstract over [Units of Information](https://en.wikipedia.org/wiki/Units_of_information). An initial value can be created by calling the static `apply(double, Unit)` method, which will return a new `UnitsOfInformation` object, where the size is correctly calculated from the `amount` and `unit` parameters.

This class is immutable, meaning you can not change the internal state once it's set. You're only able to get the value back in the unit you want.

While the `amount` is self explanatory, it may not be so clear what number should be passed as `unit`. Here the predefined Units come into play. Let's assume you want to represent 2.5 Kilobytes.

    UnitsOfInformation(2.5, Unit.KB)
    // Or with imported implicits
    2.5.KB

## Usage

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

You can initialize the `UnitsOfInformation` object with the help of implicit conversions. For this to work, you have to import the implicit class `ch.randm.uoi.UnitsOfInformation.Implicits`.

    // Will print "2.5 GB"
    (2.5 GB) format "%.1f"
    // Returns 2.5
    2500.MB in GB

Basic calculations are supported. Since the class is immutable, these methods will create a new object that will be returned as a result.

    2.MB + 5.MB // returns 5.MB
    2.MB - 500.KB // returns 1.5.MB
    2.MB * 1.5 // returns 3.MB
    2.MB / 2 // returns 1.MB
    

## Installation

Include this library in your SBT project. For this, add

    resolvers += "randm.ch Repository" at "https://repo.randm.ch/repository/maven-releases"
      
and

    libraryDependencies += "ch.randm" %% "units-of-information" % "0.5"

## Java Support

This library will also work in your Java project, though the syntax will be different:

    // Generate a new object
    UnitsOfInformation size = UnitsOfInformation.apply(250, Unit.MB)
    // Get amount in Unit
    size.in(Unit.GB) // returns 0.25
    // Get Unit
    size.unit() // returns Unit.MB
    // Get formatted string
    size.format(Unit.GB, "%.2f") // returns "0.25 GB"
    // Calculations
    size.$plus(UnitsOfInformation.apply(20, Unit.MB))
    size.$minus(UnitsOfInformation.apply(20, Unit.MB))
    size.$times(4.0)
    size.$div(5.0)

## Future improvements:
 - Advanced calculations (pow, sqrt, mod, abs, ...)
 - Helper methods (e.g. calculate free storage in percent)
