Test 1. Reverse list per K nodes
==============================================
Usage: python reverselist.py -i [input list, string] -k [k node,integer]
example 1, the normal case, the K is less than the length of the list 
```xml

python reverselist.py -i 1234567 -k 3
input list= 1234567, K= 3
----------------------
the input list is:1->2->3->4->5->6->7
----------------------
the output list is:3->2->1->6->5->4->7


```
---------------
example 2, the K equals the lenght of the input list, the whole list is reversed
```xml

reverselist.py -i 1234567 -k 7

input list= 1234567, K= 7
----------------------
the input list is:1->2->3->4->5->6->7
----------------------
the output list is:7->6->5->4->3->2->1

```
---------------
example 3, if K node is 0 or 1, the list keep unchanged
```xml

reverselist.py -i 1234567 -k 1
input list= 1234567, K= 1
----------------------
the input list is:1->2->3->4->5->6->7
----------------------
the output list is:1->2->3->4->5->6->7


```
Test 3. LUNWebservice - A web service to provide RESTful API to simulate storage array management 
==============================================
The web service is built using Spark 2.5.5, refer to https://groups.google.com/d/forum/sparkjava
Use RESTClient to send request to http://localhost:4567/luns

Functions
---------------
Query the current number of LUNs for different type: free, exported, and unexported

Example for get the number of "free" type LUN
```xml

http://localhost:4567/luns
Method GET 
body "type=free"

```
---------------

Create single LUN

```xml

http://localhost:4567/lun
Method POST 
body "size=2"

```
---------------
Create Multiple LUNs
```xml

http://localhost:4567/luns
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
```xml

Method PUT 
body "id=[:lunid]"

```
---------------