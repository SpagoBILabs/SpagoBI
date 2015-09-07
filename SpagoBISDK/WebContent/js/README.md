# Javascript SpagoBI API

SpagoBI SDK contains a javascript API that helps users to embed parts of SpagoBI Suite inside a web page or to retrieve informations about datasets and documents.

## Same-origin policy
Because of this policy web browsers don't let a script in a web page to retrieve data from a url if this url has a different origin respect of the web page. The origin is different if the URI schema, host name or port are different. For more informations about the same-origin policy please refer to https://en.wikipedia.org/wiki/Same-origin_policy.

That means that if you use SpagoBI SDK in a different origin from the SpagoBI server the browser will not let your script to communicate with SpagoBI.

There are two types of functions in SpagoBI SDK:
1. iFrame injection: they inject a SpagoBI page inside an iFrame
2. REST services: they use REST services of SpagoBI to retrieve some kind of informations

For the first type of function same-origin policy is not a problem. For the second one it is.
In order to solve the problem SpagoBI SDK use two different approaches:
1. jsonp (see https://en.wikipedia.org/wiki/JSONP for more details)
2. CORS (see https://en.wikipedia.org/wiki/Cross-origin_resource_sharing for more details)

The jsonp version of these functions are inside the Sbi.sdk.api namespace together with the functions of the first type (iFrame injection). The CORS version of the same functions (the ones based on REST services) are in the Sbi.sdk.cors.api namespace and have same names as jsonp counterpart. 

For example, injectDocument(config) is a function of the first type and can be found in Sbi.sdk.api namespace while getDataSetList(config) is of the second type and can be found in Sbi.sdk.api (jsonp version) namespace as well as Sbi.sdk.cors.api (CORS version).

## Jsonp vs CORS
New api that gives the same functionalities of these functions were developed using CORS instead of jsonp.
There are three main advantages on using CORS over jsonp:
<ul>
<li>all the methods are available while in jsonp only GET request can be done;</li>
<li>if an error occurs it is possible to manage it with CORS, while in jsonp it is only possible to set a timeout;</li>
<li>jsonp has security problems (see later for an example).</li>
</ul>

However, jsonp is supported by all browser while CORS doesn't work properly in Internet Explorer 8 and 9
(in IE 7 and earlier versions is not supported at all).

If you use the version with jsonp please take note about this security problem:
the authentication is made with a GET request, so users credentials are sent as query parameters.
It would be better to not use "authenticate" function (in that way, the user should already logged in
in order to use the api).

## Authentication
In order to use SpagoBI SDK functions the user has to be authenticated. If the user is already authenticated in SpagoBI the SDK use cockies to retrieve the session and all functions can be used.

If the user is not already authenticated he has to log in. There is the authenticate function for that. As for functions based on REST services, the authenticate function is also available in both Sbi.sdk.api and Sbi.sdk.cors.api namespaces.

If you use the version with jsonp please take note about this security problem: the authentication is made with a GET request, so users credentials are sent as query parameters. It would be better to not use "authenticate" function (in that way, the user should already logged in in order to use the API)

For the functions that use REST services it is also available Basic Authentication. If the user is not authenticated the browser will show a pop up asking for user credentials the first time. Than the credentials will be saved in the cache of the browser and they will be used for all other REST services. The functions based on REST services and CORS give also the possibility to specify the credentials that will be set automatically in the (Basic Authentication) header of the request.