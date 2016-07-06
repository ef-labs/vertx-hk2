# Vert.x HK2 Extension
Enable Verticle dependency injection using HK2.  Deploy your verticle with the `java-hk2:` prefix to use the `HK2VerticleFactory`.

[![Build Status](http://img.shields.io/travis/ef-labs/vertx-hk2.svg?maxAge=2592000&style=flat-square)](https://travis-ci.org/ef-labs/vertx-hk2)
[![Maven Central](https://img.shields.io/maven-central/v/com.englishtown.vertx/vertx-hk2.svg?maxAge=2592000&style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.englishtown.vertx/vertx-hk2/)

## License
http://englishtown.mit-license.org/


## Configuration
The vertx-mod-hk2 module configuration is as follows:

```json
{
    "hk2_binder": <hk2_binder>
}
````

* `hk2_binder` -  The name of the HK2 binder class which contains the injection configuration.  Default is `"com.englishtown.vertx.hk2.BootstrapBinder"`.  You can also provide an array of binder classes.



## Example

```java
package com.englishtown.vertx.hk2;

import com.englishtown.configuration.ConfigValueManager;
import com.englishtown.configuration.OtherBinder1;
import com.englishtown.configuration.OtherBinder2;
import com.englishtown.configuration.impl.PropertiesConfigValueManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class BootstrapBinder extends AbstractBinder {

    @Override
    protected void configure() {

        // Configure bindings
        bind(PropertiesConfigValueManager.class).to(ConfigValueManager.class).in(Singleton.class);

        // Install other binders
        install(new OtherBinder1(), new OtherBinder2());

    }

}
```
