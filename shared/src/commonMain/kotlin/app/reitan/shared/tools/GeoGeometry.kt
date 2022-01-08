package app.reitan.shared.tools

import app.reitan.shared.models.LatLon
import kotlin.math.*

/**
 * Copied from [geogeometry](https://github.com/jillesvangurp/geogeometry)
 */

const val EARTH_RADIUS_METERS = 6371000.0
const val DEGREES_TO_RADIANS = 2.0 * PI / 360.0

fun toRadians(degrees: Double): Double {
    return degrees * DEGREES_TO_RADIANS
}

/**
 * Compute the Haversine distance between the two coordinates. Haversine is
 * one of several distance calculation algorithms that exist. It is not very
 * precise in the sense that it assumes the earth is a perfect sphere, which
 * it is not. This means precision drops over larger distances. According to
 * http://en.wikipedia.org/wiki/Haversine_formula there is a 0.5% error
 * margin given the 1% difference in curvature between the equator and the
 * poles.
 *
 * @param lat1
 * the latitude in decimal degrees
 * @param long1
 * the longitude in decimal degrees
 * @param lat2
 * the latitude in decimal degrees
 * @param long2
 * the longitude in decimal degrees
 * @return the distance in meters
 */

fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
    validate(lat1, long1, false)
    validate(lat2, long2, false)

    val deltaLat = toRadians(lat2 - lat1)
    val deltaLon = toRadians(long2 - long1)

    val a =
        sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(deltaLon / 2) * sin(deltaLon / 2)

    val c = 2 * asin(sqrt(a))

    return EARTH_RADIUS_METERS * c
}

/**
 * Variation of the haversine distance method that takes an array
 * representation of a coordinate.
 *
 * @param p1
 * [latitude, longitude]
 * @param p2
 * [latitude, longitude]
 * @return the distance in meters
 */

fun distance(p1: LatLon, p2: LatLon): Double {
    return distance(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
}

/**
 * Validates coordinates. Note. because of some edge cases at the extremes that I've encountered in several data sources, I've built in
 * a small tolerance for small rounding errors that allows e.g. 180.00000000000023 to validate.
 * @param latitude latitude between -90.0 and 90.0
 * @param longitude longitude between -180.0 and 180.0
 * @param strict if false, it will allow for small rounding errors. If true, it will not.
 * @throws IllegalArgumentException if the lat or lon is out of the allowed range.
 */

private fun validate(latitude: Double, longitude: Double, strict: Boolean = false) {
    var roundedLat = latitude
    var roundedLon = longitude
    if (!strict) {
        // this gets rid of rounding errors in raw data e.g. 180.00000000000023 will validate
        roundedLat = (latitude * 1000000).roundToLong() / 1000000.0
        roundedLon = (longitude * 1000000).roundToLong() / 1000000.0
    }
    if (roundedLat < -90.0 || roundedLat > 90.0) {
        throw IllegalArgumentException("Latitude $latitude is outside legal range of -90,90")
    }
    if (roundedLon < -180.0 || roundedLon > 180.0) {
        throw IllegalArgumentException("Longitude $longitude is outside legal range of -180,180")
    }
}

/**
 * @param pointCoordinates point
 */

private fun validate(pointCoordinates: LatLon) {
    validate(pointCoordinates.latitude, pointCoordinates.longitude, false)
}
