function ListviewAssistant() {
}

ListviewAssistant.prototype.setup = function() {
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: [
			Mojo.Menu.editItem,
			{label: "Preferences", command:Mojo.Menu.prefsCmd},
			{label: "Help", command:Mojo.Menu.helpCmd}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	
	var cmdMenuModel = {
		items: [
			{}, {}, {icon: "refresh", command: "refresh"}
		]
	};
	this.controller.setupWidget(Mojo.Menu.commandMenu, {menuClass:"no-fade"},
			cmdMenuModel);
	
	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'},
			{spinning:true});
			
	this.controller.get("listNear").hide();
	
	var listAttrs = {
		listTemplate:'listview/listContainer', 
		itemTemplate:'listview/listItem',
		swipeToDelete:false,
		autoconfirmDelete:false
	};
	this.listModel = {
		listTitle: "Nearest",
		items: []
	};
	this.controller.setupWidget("listNear", listAttrs, this.listModel);
	this.ajaxRequest = new Ajax.Request(Mojo.appPath + "data.json", {
		method: "GET",
		evalJSON: "force",
		onSuccess: function(response){
			this.locations = response.responseJSON.placemark;
			this.determineNearest();
		}.bind(this),
		onFailure: function(err) {
			Error("Unable to load Tim Hortons data");
		}
	});
	
}

ListviewAssistant.prototype.activate = function(event) {
	this.handleTap = this.handleTap.bindAsEventListener(this);
	this.controller.listen("listNear", Mojo.Event.listTap, this.handleTap);
};

ListviewAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if(event.command == Mojo.Menu.prefsCmd) {
			this.controller.showDialog({
				template: 'settings/settings-popup',
				assistant: new SettingsAssistant({controller: this.controller,
						callback:this.determineNearest.bind(this)})
			});
		} else if (event.command == Mojo.Menu.helpCmd) {
			this.controller.stageController.pushAppSupportInfoScene();
		} else if (event.command == "refresh") {
			this.controller.get('spinLoading').mojo.start();
			this.controller.get('loadingScrim').show();
			this.determineNearest();
		}
	}
};

ListviewAssistant.prototype.determineNearest = function() {
	this.controller.serviceRequest("palm://com.palm.location", {
		method: "getCurrentPosition",
  		parameters:{},
  		onSuccess:function(response) {
			var useMetric = Preferences.get("useMetric", true);
			var numEntries = Preferences.get("numEntries", 25);
			this.longitude = response.longitude;
			this.latitude = response.latitude;
			var unit = "km";
			if(!useMetric) {
				unit = "mi";
			}
			var items = new Array();
			for(var i=0; i<this.locations.length; i++) {
				var curr = this.locations[i];
				curr.precise = this.calcDistance(this.longitude, this.latitude,
						curr.longitude, curr.latitude, useMetric);
				curr.distance = curr.precise.toFixed(2);
				curr.unit = unit;
				if(curr.dt.length>0) {
					curr.dt = curr.dt.replace("(", "").replace(")", "")
							.replace(" ", "<br>");
				}
				if(items.length<numEntries) {
					items.push(curr);
				} else {
					var greatest = 0;
					for(var j=0; j<items.length; j++) {
						if(items[j].precise > items[greatest].precise) {
							greatest = j;
						}
					}
					if(curr.precise < items[greatest].precise) {
						items[greatest] = curr;
					}
				}
			}
			if(items.length>1) {
				items.sort(function(a,b) {return a.precise - b.precise;});
			}
			this.listModel.items = items;
			this.controller.get("listNear").show();
			this.controller.getSceneScroller().mojo.revealTop();
			this.controller.modelChanged(this.listModel);
			this.controller.get('spinLoading').mojo.stop();
			this.controller.get('loadingScrim').hide();
		}.bind(this),
  		onFailure: function(err) {
			Mojo.Controller.errorDialog("Error reading current GPS location." +
					err.errorText);
			this.calcCallback([]);
		}.bind(this)
	});
};

ListviewAssistant.prototype.calcDistance = function(lon1, lat1, lon2, lat2, metric){
	var R = 6371; // km
	var dLat = (lat2-lat1).toRad();
	var dLon = (lon2-lon1).toRad(); 
	var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		Math.cos(lat1.toRad()) * Math.cos(lat2.toRad()) * 
		Math.sin(dLon/2) * Math.sin(dLon/2); 
	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	var d = R * c;
	if(!metric) {
		d = d/1.609344;
	}
	return d;
};

ListviewAssistant.prototype.handleTap = function(event) {
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "open",
		parameters: {
			target:"maploc:" + event.item.latitude + "," + event.item.longitude
			/*id: "com.palm.app.maps",
			params: {
				query: "http://maps.google.com/?q=(Tim%20Hortons)@"
						+ event.item.latitude + "," + event.item.longitude,
        	}*/
		}
	});
}

ListviewAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("listNear", Mojo.Event.listTap, this.handleTap);
}

ListviewAssistant.prototype.cleanup = function(event) {
}
