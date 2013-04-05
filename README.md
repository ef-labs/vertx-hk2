# Vert.x HK2 Module

Provides a VerticleFactory that uses HK2 for dependency injection.


## Configuration

The vertx-mod-hk2 module configuration is as follows:

```json
{
    "vertx_service_locator_name": <vertx_service_locator_name>,
    "hk2_binder": <hk2_binder>
}
````

* `vertx_service_locator_name` - The service locator name to use.  Default is `"vertx.service.locator"`
* `hk2_binder` -  The name of the HK2 binder class which contains the configuration.  Default is `"com.englishtown.vertx.hk2.BootstrapBinder"`.
