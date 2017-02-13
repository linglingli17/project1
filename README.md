

Test 3. LUNWebservice - A web service to provide RESTful API to simulate storage array management 
==============================================
The web service is built using Spark 2.5.5, refer to https://groups.google.com/d/forum/sparkjava
Use RESTClient to send request to http://localhost:4567/luns

Functions
---------------
Create single LUN
```xml

Method POST 
body "size=2"

```
---------------
Create Multiple LUNs
```xml

Method POST 
body "size=2&count=10"

```
---------------
Retrive the LUN information
```xml

Method GET "http://localhost:4567/luns/:id

```
---------------
Export LUN to a host
```xml

Method PUT 
body "id=[:lunid]&hostid=[:hostid]"

```
---------------
Unexport a LUN
Method PUT 
body "id=[:lunid]"
---------------