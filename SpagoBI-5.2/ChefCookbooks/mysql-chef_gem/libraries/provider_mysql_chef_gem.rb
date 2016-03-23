class Chef
  class Provider
    class MysqlChefGem < Chef::Provider::LWRPBase
      use_inline_resources if defined?(use_inline_resources)

      def whyrun_supported?
        true
      end

      action :install do
        # We're going to need compilers to build the gem's native
        # extentions.
        recipe_eval do
          run_context.include_recipe 'build-essential::default'
        end

        directory "#{Chef::Config[:file_cache_path]}/connector" do
          action :create
        end

        remote_file ::File.basename(new_resource.connectors_url) do
          path "#{Chef::Config[:file_cache_path]}/mysql-connector.tar.gz"
          source new_resource.connectors_url
          checksum new_resource.connectors_checksum
          notifies :run, 'bash[unpack connector]', :immediately
          action :create
        end

        bash 'unpack connector' do
          code <<-EOF
          tar xzf #{Chef::Config[:file_cache_path]}/mysql-connector.tar.gz \
          -C #{Chef::Config[:file_cache_path]}/connector \
          --strip-components 1
          EOF
          action :nothing
        end

        gem_package 'mysql' do
          gem_binary RbConfig::CONFIG['bindir'] + '/gem'
          version new_resource.gem_version
          options "-- --with-mysql-dir=#{Chef::Config[:file_cache_path]}/connector"
          action :install
        end
      end

      action :remove do
        gem_package 'mysql' do
          gem_binary RbConfig::CONFIG['bindir'] + '/gem'
          action :remove
        end
      end
    end
  end
end
