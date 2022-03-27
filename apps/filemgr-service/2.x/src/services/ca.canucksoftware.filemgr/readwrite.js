function ReadWrite(message, file) {
	this.message = message;
	this.file = file;
};

ReadWrite.prototype._respond = function(response) {
	this.message.respond(JSON.stringify(response));
};

ReadWrite.prototype._error = function(text) {
	this.message.respond(JSON.stringify({errorCode:"ERROR", errorText:text}));
};

ReadWrite.prototype.write = function(text) {
	this.content = text;
	this.flag = "w";
	this._doWrite();
};

ReadWrite.prototype.append = function(text) {
	this.content = text;
	this.flag = "a";
	this._doWrite();
};

ReadWrite.prototype._doWrite = function() {
	this.fs = require("fs");
	try {
		this.stream = this.fs.createWriteStream(this.file, {flags:this.flag});
		this.stream.write(this.content);
		this.stream.end();
		this._respond({returnValue:true});
	} catch(e) {
		this._error("Unable to write to file.");
	}
};

ReadWrite.prototype.read = function(options, isSubscribed) {
	this.options = options;
	this.subscribe = isSubscribed;
	this.fs = require("fs");
	this.fs.readFile(this.file, "utf8", function(err, data) {
		if(!err) {
			if(typeof data != "string") { //object
				if(data.length==0) { //should have been an empty string
					data = "";
				} else {
					this._error("Unable to read file.")
					//this._respond({data:data});
					return;
				}
			}
			if(this.subscribe) { //full read multiple calls
				while(data.length>200000) {
					var chunk = data.substring(0, 199999);
					this._respond({data:chunk, completed:false});
					data = data.substring(199999);
				}
				this._respond({data:data, completed:true});
			} else if(this.options.offset && this.options.length) { //partial read, 1 call
				this._respond({data:data.substr(this.options.offset, this.options.length)});
			} else { //full read, 1 call
				this._respond({data:data});
			}
		} else {
			this._error("Unable to read file.")
		}
	}.bind(this));
};


ReadWrite.prototype.readNONFUNCTIONING = function(options, isSubscribed) {
	this.fs = require("fs");
	this.content = "";
	this.opt = {};
	var self = this;
	
	this.message.respond(JSON.stringify({testStep:4}));
	this.stream = this.fs.createReadStream(this.file);
	this.stream.setEncoding("utf8");
	this.message.respond(JSON.stringify({testStep:5}));
	this.stream.on("data", function(data) {
		self.content += data;
		self._respond({testStep:6});
		if(content.length>=200000 && isSubscribed) {
			var currContent = content;
			self._respond({data:content, completed:false});
			content = "";
		}
	});
	this.stream.on("error", function(err) {
		self._error("Unable to read file.")
	});
	this.stream.on("end", function() {
		var reply = {data:content};
		if(isSubscribed) {
			reply.completed = true;
		}
		self._respond(reply);
	});
};
