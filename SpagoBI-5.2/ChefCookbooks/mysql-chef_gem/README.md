Mysql chef_gem Cookbook
=======================

The Mysql chef_gem is a library cookbook that provides an LWRP for use
in recipes. It provides a wrapper around `chef_gem` called
`mysql_chef_gem`, that eases the installation process, collecting the
prerequisites and side-stepping the compilation phase arms race.

Scope
-----
This cookbook is concerned with the installation of the `mysql`
Rubygem into Chef's gem path. Installation into other Ruby
environments, or installation of related gems such as `mysql2`

Requirements
------------
* Chef 11 or higher
* Ruby 1.9 (preferably from the Chef full-stack installer)
 
Usage
-----
Place a dependency on the mysql cookbook in your cookbook's  metadata.rb
```ruby
depends 'mysql-chef_gem', '~> 1.0'
```

Then, in a recipe:

```ruby
mysql_chef_gem 'default' do
  action [:install]
end

Resources Overview
------------------
### mysql_chef_gem

The `mysql_chef_gem` resource the build dependencies and installation
of the `mysql` rubygem into Chef's Ruby environment


#### Example
```ruby
mysql_chef_gem 'default' do
  gem_version '2.9.1'
  connectors_url 'http://internal.computers.biz/mysql-connector-c-6.1.5-linux-glibc2.5-x86_64.tar.gz'
  connectors_checksum '38dea02ea8593359037aef7df7df3d388c9baac3604635f398bae9e1e8eaa4d2'
  action :install
end
```

#### Parameters
- `gem_version` - The version of the `mysql` Rubygem to install into
  the Chef environment. Defaults to '2.9.1'
- `connectors_url` - URL of a tarball containing pre-compiled MySQL
  connector libraries  
- `connectors_checksum` - sha256sum of the `connectors_url` tarball

#### Actions
- `:install` - Build and install the gem into the Chef environment
- `:remove` - Delete the gem from the Chef environment

License & Authors
-----------------
- Author:: Sean OMeara (<sean@chef.io>)

```text
Copyright:: 2009-2014 Chef Software, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
