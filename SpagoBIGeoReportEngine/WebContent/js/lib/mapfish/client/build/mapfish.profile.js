dependencies = {
	layers: [
		{
			name: "../dijit/dijit.js",
			dependencies: [
				"dijit.dijit"
			]
		},
		{
			name: "../mapfish/mapfish.js",
			dependencies: [
				"mapfish.mapfish"
			]
		}

	],

	prefixes: [
		[ "dijit", "../dijit" ],
		[ "mapfish", "../mapfish" ],
	]
}