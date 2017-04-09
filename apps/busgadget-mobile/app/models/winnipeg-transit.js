function WinnipegTransit() {
	this.requestId = 0;
    this.requests = {};
	this.inCleanup = false;
};

WinnipegTransit.prototype.stopsNear = function(latitude, longitude, onSuccess,
		onFailure) {
	var params = {lat:latitude, lon:longitude, distance:250};
	this.doRequest(this.API_URL + "stops", params, function(response) {
		var results = {stops:[]};
		var stops = response.responseXML.getElementsByTagName("stop");
		for(var i=0; i<stops.length; i++) {
			var curr = {};
			var keys = stops[i].getElementsByTagName("key");
			for(var j=0; j<keys.length; j++) {
				if(keys[j].parentNode.tagName=="stop") {
					curr.id = parseInt(keys[j].textContent);
					break;
				}
			}
			var names = stops[i].getElementsByTagName("name");
			for(var j=0; j<names.length; j++) {
				if(names[j].parentNode.tagName=="stop") {
					curr.name = names[j].textContent;
					break;
				}
			}
			curr.latitude = parseFloat(stops[i]
					.getElementsByTagName("latitude")[0].textContent);
			curr.longitude = parseFloat(stops[i]
					.getElementsByTagName("longitude")[0].textContent);
			var distance = stops[i].getElementsByTagName("direct")[0];
			curr.distance = distance.textContent + distance.getAttribute("unit");
			results.stops[i] = curr;
		}
		if(onSuccess) {
			onSuccess(results);
		}
	}, onFailure);
};

WinnipegTransit.prototype.stopQuery = function(number, onSuccess, onFailure) {
	this.doRequest(this.API_URL + "stops/" + number, {}, function(response) {
		var results = {};
		var keys = response.responseXML.getElementsByTagName("key");
		var tags = "";
		for(var i=0; i<keys.length; i++) {
			tags += keys[i].parentNode.tagName;
			if(keys[i].parentNode.tagName=="stop") {
				results.id = parseInt(keys[i].textContent);
				break;
			}
		}
		
		var names = response.responseXML.getElementsByTagName("name");
		for(var i=0; i<names.length; i++) {
			if(names[i].parentNode.tagName=="stop") {
				results.name = names[i].textContent;
				break;
			}
		}
		results.latitude = parseFloat(response.responseXML
				.getElementsByTagName("latitude")[0].textContent);
		results.longitude = parseFloat(response.responseXML
				.getElementsByTagName("longitude")[0].textContent);
		if(onSuccess) {
			onSuccess(results);
		}
	}, onFailure);
};

WinnipegTransit.prototype.stopSchedule = function(number, onSuccess, onFailure) {
	var params = {};
	/*if(maxTime!=2) {
		var now = new Date();
		now.setHours(now.getHours()+maxTime);
		var hr = now.getHours();
		if(hr<10) {
			hr = "0" + hr;
		}
		var min = now.getMinutes();
		if(min<10) {
			min = "0" + min;
		}
		params["end"] = hr + ":" + min;
	}*/
	this.doRequest(this.API_URL + "stops/" + number + "/schedule", params,
			function(response) {
		var results = {buses:[]};
		var stops = response.responseXML.getElementsByTagName("route-schedule");
		for(var i=0; i<stops.length; i++) {
			var routeNode = stops[i].getElementsByTagName("route")[0];
			var routeNumber = routeNode.getElementsByTagName("number")[0].textContent;
			var routeName = routeNode.getElementsByTagName("name")[0].textContent;
			var schedules = stops[i].getElementsByTagName("scheduled-stop");
			for(var j=0; j<schedules.length; j++) {
				var curr = {number:routeNumber, name:routeName};
				var arrival = schedules[j].getElementsByTagName("arrival")[0];
				var estimated = arrival.getElementsByTagName("estimated")[0];
				curr.milliseconds = new Date(estimated.textContent).getTime();
				var variantNode = schedules[j].getElementsByTagName("variant")[0];
				var names = variantNode.getElementsByTagName("name");
				for(var k=0; k<names.length; k++) {
					if(names[k].parentNode.tagName=="variant") {
						curr.variant = names[k].textContent;
						curr.variant = curr.variant.replace(curr.name, "");
						curr.variant = curr.variant.trim().capitalize();
						break;
					}
				}
				results.buses.push(curr);
			}
		}
		if(results.buses.length>1) {
			results.buses.sort(function(a,b) {
				return a.milliseconds - b.milliseconds;
			});
		}
		if(onSuccess) {
			onSuccess(results);
		}
	}, onFailure);
};

WinnipegTransit.prototype.doRequest = function(url, params, onSuccess, onFailure) {
	this.requestId++;
	var parameters = params || {};
	parameters["api-key"] = this.API_KEY;
	this.requests[this.requestId] = new Ajax.Request(url, {
		method: "GET",
		parameters: parameters,
		contentType: "application/xml",
		onSuccess: function(response) {
			if(response.responseXML!=null) {
				if(onSuccess) {
					onSuccess(response);
				}
			} else {
				if(onFailure) {
					onFailure(response);
				}
			}
		},
		onFailure: onFailure || Mojo.doNothing,
		onComplete: this.onComplete.bind(this, this.requestId)
	});
	return this.requests[this.requestId];
};

WinnipegTransit.prototype.onComplete = function(response, id) {
	delete this.requests[id];
	if(this.inCleanup && this.allCompleted()) {
		delete this;
	}
};

WinnipegTransit.prototype.allCompleted = function() {
	var completed = true;
	for(var curr in this.requests) {
		if(curr != undefined) {
			completed = false;
			break;
		}
	}
	return completed;
};

WinnipegTransit.prototype.cleanup = function(str) {
	this.inCleanup = true;
	if(this.allCompleted()) {
		delete this;
	}
};

WinnipegTransit.prototype.API_KEY = "8Wimj2zyFjXx9Nc4Zab1";
WinnipegTransit.prototype.API_URL = "http://api.winnipegtransit.com/";
