# spagobi-cookbook

This cookbook containes the recipe for install, uninstall, start and stop SpagoBI application.

## Supported Platforms

* Ubuntu >= 12.04

## Attributes

Check `metadata.rb` to know the applicable attributes. 

## Usage

For SpagoBI installation include `spagobi` in your node's `run_list`:

```json
{
  "run_list": [
    "recipe[spagobi::1.0.2_install]"
  ]
}
```

## Authors

Author:: Giorgio Federici (giorgio.federici@eng.it)
