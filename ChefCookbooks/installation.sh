#!/bin/bash
#SpagoBI VM installation script

sudo chef-client -o 'recipe[spagobi::1.0.2_install]'
