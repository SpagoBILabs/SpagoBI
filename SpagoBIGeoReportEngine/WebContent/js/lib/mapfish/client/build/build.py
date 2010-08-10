#!/usr/bin/env python

# 
# Copyright (C) 2007-2008  Camptocamp
#  
# This file is part of MapFish Client
#  
# MapFish Client is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#  
# MapFish Client is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#  
# You should have received a copy of the GNU General Public License
# along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
#

#
# Some code taken from the OpenLayers code base
#
# Copyright (c) 2006-2007 MetaCarta, Inc., published under the Clear BSD
# license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
# full text of the license.
#

import sys
sys.path.append("./")

import getopt

import jsmin, mergejs

#
# Define config vars
#

configDictOpenLayers = {
    'OpenLayers.js':                '../mfbase/openlayers/lib',
    'OpenLayers':                   '../mfbase/openlayers/lib',
    'Rico':                         '../mfbase/openlayers/lib',
    'GoogleGears':                  '../mfbase/openlayers/lib'
}

configDictMapFish = {
    'SingleFile.js':                '../mfbase/mapfish',
    'MapFish.js':                   '../mfbase/mapfish',
    'widgets':                      '../mfbase/mapfish',
    'core':                         '../mfbase/mapfish',
    'lang':                         '../mfbase/mapfish'
}


#
# Parse command args
#

def usage():
    sys.stderr.write("""
Usage: build.py [OPTION]...
    -c --config configfile      Specify configuration file (default value: mapfish-widgets.cfg)
    -o --output outputfile      Specify output file (default value: MapFish.js)
    -d --debug                  Don't compress the files
    -m --mfonly                 MapFish only build (OpenLayers files not included)
"""
    )

try:
    opts, args = getopt.getopt(sys.argv[1:], "c:o:dm", ["config=", "output=", "debug", "mfonly"])
except getopt.GetoptError:
    usage()
    sys.exit(1)

configFilename = "mapfish-widgets.cfg"
outputFilename = "MapFish.js"
debug = False
mfOnly = False
for o, a in opts:
    if o in ("-c", "--config"):
        configFilename = a
        if not configFilename.endswith(".cfg"):
            configFilename = a + ".cfg"
    if o in ("-o", "--output"):
        outputFilename = a
    if o in ("-d", "--debug"):
        debug = True
    if o in ("-m", "--mfonly"):
        mfOnly = True

#
# Get file list
#

configDictGlobal = {}
configDictGlobal.update(configDictOpenLayers)
configDictGlobal.update(configDictMapFish)

(files, order) = mergejs.getFiles(configDictGlobal, configFilename)

# rebuild the file list and order list, based on whether a MapFish-only
# build is to be done or not
if not mfOnly:
    newfiles = files
    neworder = order
else:
    newfiles = {}
    neworder = []
    for f in files:
        keep = False
        for k in configDictMapFish:
            if f.startswith(k):
                keep = True
                break
        if keep:
            newfiles[f] = files[f]
    for o in order:
        keep = False
        for k in configDictMapFish:
            if o.startswith(k):
                keep = True
                break
        if keep:
            neworder.append(o)

#
# Merge files
#
print "Merging libraries."
merged = mergejs.run(newfiles, neworder)

#
# Compress files
#
if debug:
    minimized = merged
else:
    print "Compressing."
    minimized = jsmin.jsmin(merged)

#
# Add license
#
print "Adding license file."
minimized = file("license.txt").read() + minimized

#
# Print to output file
#
print "Writing to %s." % outputFilename
file(outputFilename, "w").write(minimized)

print "Done."
