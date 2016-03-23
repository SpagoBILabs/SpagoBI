name             'spagobi'
maintainer       'Spagobi Labs'
maintainer_email 'giorgio.federici@eng.it'
license          'All rights reserved'
description      'Installs/Configures spagobi'
long_description 'Installs/Configures spagobi'
version          '1.0.2'

depends 'mysql', '~> 6.0'
depends 'database', '<= 2.3.1'
depends 'java', '>= 1.29.0'

supports 'ubuntu', '>= 12.04'
#supports 'debian', '>= 7.0'

#centos in progress: problems with images space and random chef fails
#supports 'centos', '>= 6.0'