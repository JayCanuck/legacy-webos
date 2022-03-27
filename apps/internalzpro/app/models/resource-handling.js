function ResourceHandling() {
	this.requests = [];
	this.ext = {};
	this.defaults = [];
	this.appId = Mojo.Controller.appInfo.id;
}

ResourceHandling.prototype.set = function(viewsource, images, text, ipk, onSuccess,
		onFailure) {
	this.viewsource = viewsource;
	this.callback = onSuccess;
	if(images) { this.ext = Object.extend(this.ext, ResourceHandling.image); }
	if(text) { this.ext = Object.extend(this.ext, ResourceHandling.text); }
	if(ipk) { this.ext = Object.extend(this.ext, ResourceHandling.ipk); }
	
	this.requests[this.requests.length] = new Mojo.Service.Request(ResourceHandling.id, {
		method: "dumpMimeTable",
		parameters: {},
		onSuccess: function(response) {
			for(var i=0; i<response.resources.length; i++) {
				if(response.resources[i].handlers && response.resources[i].handlers.primary
						&& response.resources[i].handlers.primary.appId == this.appId) {
					this.defaults[this.defaults.length] = response.resources[i].handlers.primary.extension;
				}
			}
			this.requests[this.requests.length] = new Mojo.Service.Request(ResourceHandling.id, {
					method: "removeHandlersForAppId",
					parameters: {appId:"ca.canucksoftware.internalz"},
					onSuccess: function(response2) {
						for(var resource in this.ext) {
							this.requests[this.requests.length] = new Mojo.Service.Request(ResourceHandling.id, {
								method: "addResourceHandler",
								parameters: this.ext[resource],
								onSuccess: this._setDefaults.bind(this, resource),
								onFailure: this._setDefaults.bind(this, resource)
							});
						}
						if(this.viewsource) {
							this.requests[this.requests.length] = new Mojo.Service.Request(ResourceHandling.id, {
								method: "addRedirectHandler",
								parameters: {
									appId: "ca.canucksoftware.internalz",
									urlPattern:"^view-source://",
									schemeForm:true
								}
							});
						}
					}.bind(this),
					onFailure: onFailure
				});
		}.bind(this),
		onFailure: onFailure
	});
};

ResourceHandling.prototype._setDefaults = function(extension, response) {
	delete this.ext[extension];
	if(Object.isEmpty(this.ext)) {
		this.requests[this.requests.length] = new Mojo.Service.Request(ResourceHandling.id, {
			method: "dumpMimeTable",
			parameters: {},
			onSuccess: function(response2) {
				for(var i=0; i<response2.resources.length; i++) {
					if(response2.resources[i].handlers && response2.resources[i].handlers.primary
							&& response2.resources[i].handlers.primary.extension) {
						var extension = response2.resources[i].handlers.primary.extension;
						if(this.defaults.indexOf(extension)>=0) { //was default before handler wipe
							if(response2.resources[i].handlers.primary.appId != this.appId) {
								var alternatives = response2.resources[i].handlers.alternates;
								for(var alt=0; alt<alternatives.length; alt++) {
									this.requests[this.requests.length] = new Mojo.Service.Request(ResourceHandling.id, {
										method: "swapResource",
										parameters: {
											mimeType: response2.resources[i].mimeType,
											index: alternatives[alt].index
										}
									});
								}
							}
						}
					}
				}
				this.callback({returnValue:true});
			},
			onFailure: function(err) {
				this.callback({returnValue:true});
			}.bind(this)
		});
	}
};


ResourceHandling.image = {
	jpg:{extension:"jpg", mimeType:"image/jpeg", shouldDownload:false},
	jpeg:{extension:"jpeg", mimeType:"image/jpeg", shouldDownload:false},
	png:{extension:"png", mimeType:"image/png", shouldDownload:false},
	bmp:{extension:"bmp", mimeType:"image/bmp", shouldDownload:false},
	gif:{extension:"gif", mimeType:"image/gif", shouldDownload:false}
};

ResourceHandling.text = {
	sh:{extension:"sh", mimeType:"application/x-sh", shouldDownload:true},
	mk:{extension:"mk", mimeType:"text/plain", shouldDownload:true},
	js:{extension:"js", mimeType:"text/javascript", shouldDownload:true},
	css:{extension:"css", mimeType:"text/css", shouldDownload:true},
	json:{extension:"json", mimeType:"application/json", shouldDownload:true},
	txt:{extension:"txt", mimeType:"application/txt", shouldDownload:true},
	"text.plain":{extension:"text.plain", mimeType:"text/plain", shouldDownload:true},
	log:{extension:"log", mimeType:"text/plain", shouldDownload:true},
	conf:{extension:"conf", mimeType:"text/plain", shouldDownload:true},
	ini:{extension:"ini", mimeType:"text/plain", shouldDownload:true},
	c:{extension:"c", mimeType:"text/plain", shouldDownload:true},
	cpp:{extension:"cpp", mimeType:"text/plain", shouldDownload:true},
	cs:{extension:"cs", mimeType:"text/plain", shouldDownload:true},
	vb:{extension:"vb", mimeType:"text/plain", shouldDownload:true},
	h:{extension:"h", mimeType:"text/plain", shouldDownload:true},
	java:{extension:"java", mimeType:"text/plain", shouldDownload:true},
	patch:{extension:"patch", mimeType:"text/x-patch", shouldDownload:true},
	diff:{extension:"diff", mimeType:"text/x-diff", shouldDownload:true}
};

ResourceHandling.ipk = {
	ipk:{extension:"ipk", mimeType:"application/vnd.webos.ipk"}
};

ResourceHandling.id = "palm://com.palm.applicationManager";
