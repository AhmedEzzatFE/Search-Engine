# MySQL Date for Javascript
extends javascript's Date object with four new methods designed to assist with handling MySQL Timestamps.

## Examples
toMysqlDate & toMysqlUTCDate methods:
```javascript
var date = new Date();
date.toMysqlDate();         // Outputs: '2016-08-28 22:45:37' (String)
date.toMysqlUTCDate();      // Outputs: '2016-08-28 19:45:37' (String)
```

fromMysqlDate & fromMysqlUTCDate methods:
```javascript
Date.fromMysqlDate('1979-03-28 23:45:37');       // Outputs: (Date object)
Date.fromMysqlUTCDate('2013-03-28 11:11:52');    // Outputs: (Date object)
```

## Installtion

#### Installation Using NPM
```
npm install mysqldate
```
Usage:
```javascript
require('mysqldate');
```


#### Installation Using Bower
```
bower install mysqldate
```
Insert before your scripts:
```html
<script src="bower_components/mysqldate/mysqldate.js" type="text/javascript"></script>
```
