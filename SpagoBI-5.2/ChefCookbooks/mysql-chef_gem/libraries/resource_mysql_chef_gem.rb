require 'chef/resource/lwrp_base'

class Chef
  class Resource
    class MysqlChefGem < Chef::Resource::LWRPBase
      self.resource_name = :mysql_chef_gem
      actions :install, :remove
      default_action :install

      attribute :mysql_chef_gem_name, kind_of: String, name_attribute: true, required: true
      attribute :gem_version, kind_of: String, default: '2.9.1'
      attribute :connectors_url, kind_of: String, default: 'http://dev.mysql.com/get/Downloads/Connector-C/mysql-connector-c-6.1.5-linux-glibc2.5-x86_64.tar.gz'
      attribute :connectors_checksum, kind_of: String, default: '38dea02ea8593359037aef7df7df3d388c9baac3604635f398bae9e1e8eaa4d2'
    end
  end
end
