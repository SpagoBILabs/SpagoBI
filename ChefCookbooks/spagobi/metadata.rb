name             'Spagobi'
maintainer       'Spagobi'
maintainer_email 'Spagobi'
license          'All rights reserved'
description      'Installs/Configures spagobi'
long_description 'Installs/Configures spagobi'
version          '1.0.0'

depends 'mysql', '~> 6.0'
depends 'database', '<= 2.3.1'
depends 'java'

supports 'ubuntu', '>= 12.04'
supports 'debian', '>= 7.0'

#centos in progress: problems with images space and random chef fails
#supports 'centos', '>= 6.0'