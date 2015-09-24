# What is SpagoBI?

This is the official repository of SpagoBI. SpagoBI is a Business Intelligence Free Platform which uses many FOSS tools as analytical engines. It integrates them in an infrastructure which offers a cross-operativeness and a consistent vision between Report,OLAP,Data Mining,Dashboard and over the DWH. 

> [spagobi.org](https://www.spagobi.org)

# Run SpagoBI

## Run SpagoBI All In One

All in One image contains many demo  example with the DB included. SpagoBI is started in this way:

```console
$ docker run --name spagobi -d engineeringspa/spagobi:all-in-one
2656735e7ce42c30ae1b284d05e05565b3dbc62fa0118279b31c479b7b0e2cfa
```

## Run SpagoBI Minimal

The minimal image contains only these engines:

* SpagoBI Birt Report Engine
* SpagoBI Cockpit Engine 
* SpagoBI Qbe Engine

and it runs with an external MySQL Docker container. It doesn't contain examples like the All In One version, it's like an empty installation.

Firstly run the MySQL container for spagobi:

```console
$ docker run --name spagobidb -e MYSQL_USER=spagobiuser -e MYSQL_PASSWORD=spagobipassword -e MYSQL_DATABASE=spagobidb -e MYSQL_ROOT_PASSWORD=spagobirootpassword -d mysql:5.5
3347e0fb5e0f8e0b4e9a7ee32f5f72b8009ab4bbc018647b96d5a451142d8e9e
```

You can use whatever you want for user and password properties. Then run SpagoBI and link it to the previous MySQL container:

```console
$ docker run --link spagobidb:db --name spagobiminimal -d engineeringspa/spagobi:minimal
c24b00a79cfd05bf45e4b30fecb0857298f5bc3133b8f468a3be36a796f0c287
```

# Use SpagoBI

Get the IP of container (use ```spagobiminal``` instead of ```spagobi``` if you ran the minimal image before) :

```console
$ docker inspect --format '{{ .NetworkSettings.IPAddress }}' spagobi
172.17.0.43
```

Open SpagoBI on your browser at url (use your container-ip): 

> container-ip:8080/SpagoBI

It's necessary to test it through the container, so without mapping the port to the host.

If you run the host with a Virtual Machine (for example in a Mac environment) then you can route the traffic directly to the container from you localhost using route command:

```console
$ sudo route -n add 172.17.0.0/16 ip-of-host-Virtual-Machine
```

# License

View license information [here](https://www.spagobi.org/homepage/opensource/license/) for the software contained in this image.
