#<a name="top"></a>SpagoBI enabling Fiware

* [Introduction](#introduction)
* [GEi overall description](#gei-overall-description)
* [Build and Install](#build-and-install)
* [Running](#running)
* [REST API Example](#rest-api-example)
* [API Reference Documentation](#api-reference-documentation)
* [SpagoBI User manuals](#spagobi-user-manuals)
* [Advanced topics](#advanced-topics)
* [License](#license)
		  
## Introduction

This is the code repository for the SpagoBI project related to [Fiware](www.fiware.org).

Check also the [FIWARE Catalogue entry for SpagoBI](http://catalogue.fiware.org/enablers/data-visualization-spagobi)

Any feedback on this documentation is highly welcome, including bugs, typos
or things you think should be included but aren't. You can use [github issues](https://github.com/EngineeringSPA/SpagoBI-Fiware/issues) to provide feedback.

You can find the User & Programmer's Manual and the Administration Guide on [readthedocs.org](http://spagobi-fiware.readthedocs.org)

## GEi overall description

SpagoBI is a Business Intelligence Free Platform which uses many FOSS tools as analytical engines. It integrates them in an infrastructure which offers a cross-operativeness and a consistent vision between Report,OLAP,Data Mining,Dashboard and over the DWH.

If this is your first contact with SpagoBI, it is highly recommended to have a look to the brief [Demo](http://demo.spagobi.org/Demo/index.html#).

## Build and Install

Build and Install documentation for SpagoBI can be found at [the corresponding section of the Admin Manual](doc/admin/README.md).

## Running

How to run SpagoBI can be found at [the corresponding section of the Admin Manual](doc/admin/README.md).

[Top](#top)

## REST API Example

In order to retrieve information about a specified DataSet (*RestMetersValues* in this example): 

``` 
curl http://<spagobi-url>/SpagoBI/restful-services/2.0/datasets/RestMetersValues --header '<authentication header>' | python -mjson.tool

{
    "active": true,
    "categoryId": 150,
    "commonInfo": {
        "organization": null,
        "sbiVersionDe": null,
        "sbiVersionIn": "4.0",
        "sbiVersionUp": null,
        "timeDe": null,
        "timeIn": 1441025411000,
        "timeUp": null,
        "userDe": null,
        "userIn": "biadmin",
        "userUp": null
    },
    "configuration": {
        "restAddress": "http://192.168.2.137:1026/v1/queryContext",
        "restDirectlyJSONAttributes": "false",
        "restFetchSize": "limit",
        "restHttpMethod": "Post",
        "restJsonPathAttributes": [
            {
                "jsonPathType": "string",
                "jsonPathValue": "pros3",
                "name": "id"
            },
            {
                "jsonPathType": "double",
                "jsonPathValue": "$.contextResponses[?(@.contextElement.id==pros3_Meter)].contextElement.attributes[?(@.name==upstreamActivePower)].value",
                "name": "upstreamActivePower"
            },
           ...
``` 

[Top](#top)

## API Reference Documentation

* [SpagoBI REST](http://docs.spagobi.apiary.io/#) (Apiary)
* [SpagoBI Javascript SDK](doc/user/JS/README.md)

[Top](#top)

## SpagoBI User manuals

* [SpagoBI NGSI](doc/user/NGSI/README.md)
* [SpagoBI CKAN](doc/user/CKAN/README.md) 

[Top](#top)

## Advanced topics

* [Installation and Administration manual](doc/admin/README.md)
* [Docker Container-based deployment](docker/README.md)

[Top](#top)

## License

You can find the license of SpagoBI [here](http://www.spagobi.org/homepage/opensource/license/).

[Top](#top)