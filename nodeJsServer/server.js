/*
 * Instead of using Apache for html testing, implementing following in nodeJs.
 * Ofcourse it does not have any security measures as Apache or other stuff,
 * but serves purpose for learning html, javascript and other things.
 */
var express = require('express');
var http = require("http");
var app = express();
var path = require('path'); 

app.get('*', (req, res) => {
    console.log(req.params[0]);
    var file = '.' + req.params[0];
    var fs = require('fs');
    if (fs.existsSync(file)) {
        res.sendfile(file);
    } else {
        res.send("File not found!");
    }
});

http.createServer(app).listen(80); // serve HTTP directly

