[![Build Status](https://travis-ci.org/englishtown/vertx-mod-hk2.png)](https://travis-ci.org/englishtown/vertx-mod-hk2)

# Vert.x HK2 Module
Enable Verticle dependency injection using HK2.  Deploy your verticle with the `java-hk2:` prefix to use the `HK2VerticleFactory`.


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
