[![Build Status](https://travis-ci.org/englishtown/vertx-mod-hk2.png)](https://travis-ci.org/englishtown/vertx-mod-hk2)

# Vert.x HK2 Module
Enable Verticle and Module dependency injection using HK2.  The default Vert.x Java VerticleFactory is replaced with
com.englishtown.vertx.hk2.HK2VerticleFactory for Verticle construction.


## License
http://englishtown.mit-license.org/


## Configuration
The vertx-mod-hk2 module configuration is as follows:

```json
{
    "hk2_binder": <hk2_binder>
}
````

* `hk2_binder` -  The name of the HK2 binder class which contains the injection configuration.  Default is `"com.englishtown.vertx.hk2.BootstrapBinder"`.

NOTE: Vert.x configuration is not currently available in a VerticleFactory, so default values will be used.


To configure Vert.x to use the HK2VerticleFactory modify the langs.properties java entry like this:
`java=com.englishtown~vertx-mod-hk2~1.6.0-SNAPSHOT:com.englishtown.vertx.hk2.HK2VerticleFactory`

or set a system property when running:
`-Dvertx.langs.java=com.englishtown~vertx-mod-hk2~1.6.0-SNAPSHOT:com.englishtown.vertx.hk2.HK2VerticleFactory`


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
