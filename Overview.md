# Introduction #

My Yubico Java Client is designed as a drop-in replacement for http://code.google.com/p/yubico-java-client/, which is referenced on the [Yubico Web Service API webpage](http://www.yubico.com/developers/api/).

This one has some additional features that I could not live without.

  * Supports the complete protocol as specified on [Yubico Web Service API webpage](http://www.yubico.com/developers/api/)". On future versions, it will support every other alternative as I dive deep into the server code.
  * Generates and verifies the HMAC digital signatures.
  * Uses a precise and well known Java idiom, which can be used with any level of detail.
  * Uses [Apache Maven](http://maven.apache.org).
  * JDK 1.4 compatible (for our poor fellas on enterprise computing which are stuck).

# TODO #

  * Spring security, jsecurity integration.
  * Threadpooled for control in high demand scenarios.
  * Non-attended testing.
  * Verification in different JVM implementations (I work with SUN's JVM).