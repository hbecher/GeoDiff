# GeoDiff
GeoDiff is a tool for computing the delta of two snapshots of a spatial data set.  
It also comes with a web interface (available soon) to visualize the differences directly in your browser!

We like to call it
> the Unix diff tool for geographic data - Didier DONSEZ

## Context
GeoDiff is part of the 4th year innovative project at [Polytech' Grenoble](http://www.polytech-grenoble.fr/), France.  
It is developed by three RICM (*Networks and Multimedia Communication*) students:
* AMAURIN Alexandre
* BECHER Hervé
* BROCHIER Aymeric

The project's supervisor is Nicolas Palix.  
You can find more information on
* the [main page](http://air.imag.fr/index.php/GeoDiff) of the project [FR]
* our [project progress report](http://air.imag.fr/index.php/Projets-2016-2017-GeoDiff) page [EN]

## How to Use
*Coming soon...*

## Additional Information
The GeoDiff command-line tool is written in Java, and uses
* [Gson](https://github.com/google/gson) (from Google) for manipulating JSON objects (under Apache License 2.0)
* [GeoJSON addon](https://github.com/filosganga/geogson) for Gson by user [filosganga](https://github.com/filosganga) (under Apache License 2.0)
* [JOpt Simple](https://pholser.github.io/jopt-simple/) for parsing command line options (under The MIT License)

The web interface uses [jQuery](https://jquery.com/). It embeds a delta computing module written entirely in JavaScript that can process GeoJSON files.
