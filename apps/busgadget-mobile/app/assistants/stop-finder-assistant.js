function StopFinderAssistant() {
	this.transit = new WinnipegTransit();
}

StopFinderAssistant.prototype.setup = function() {
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
	
	this.controller.setupWidget("numberBox",
        {
            hintText: "Stop Number",
            multiline: false,
            enterSubmits: false,
            preventResize: true,
         },
         {value: ""}
    ); //modifierState: Mojo.Widget.numLock
	this.controller.setupWidget("numberButton", {}, {label : "Go"});
	this.controller.setupWidget("gpsButton", {}, {label : "Find Stops Via GPS"});
	this.controller.get("gpsResultsContainer").hide();
	this.controller.setupWidget("gpsSpinner", {spinnerSize: Mojo.Widget.spinnerSmall},
			{spinning: false});
	this.controller.get("gpsSpinContainer").hide();
	this.controller.setupWidget("gpsDrawer", {unstyled: true},{ open: false});
	this.controller.setupWidget("listGPS",
        {
            itemTemplate: "stop-finder/listItem",
            listTemplate: "stop-finder/listContainer",
            swipeToDelete: false,
            reorderable: false
         },
         this.gpsListModel = {
             listTitle: "Nearest Stops",
             items : []
         }
    );
};

StopFinderAssistant.prototype.activate = function(event) {
	this.handleNumber = this.handleNumber.bindAsEventListener(this);
	this.handleGPS = this.handleGPS.bindAsEventListener(this);
	this.handleGPSEntry = this.handleGPSEntry.bindAsEventListener(this);
	this.controller.listen("numberButton", Mojo.Event.tap, this.handleNumber);
	this.controller.listen("gpsButton", Mojo.Event.tap, this.handleGPS);
	this.controller.listen("listGPS", Mojo.Event.listTap, this.handleGPSEntry);
};

StopFinderAssistant.prototype.handleNumber = function(event) {
	var number = this.controller.get("numberBox").mojo.getValue();
	this.transit.stopQuery(number, function(response) {
			this.controller.stageController.pushScene("listings", {
				transit: this.transit,
				number: number
			});
		}.bind(this),
		function(err) {
			Error("Stop number does not exist.");
		}
	);
};

StopFinderAssistant.prototype.handleGPS = function(event) {
	this.controller.get("gpsButtonRow").removeClassName("single");
	this.controller.get("gpsButtonRow").addClassName("first");
	this.controller.get("gpsResultsContainer").show();
	this.controller.get("gpsSpinner").mojo.start();
	this.controller.get("gpsSpinContainer").show();
	this.controller.serviceRequest("palm://com.palm.location", {
		method: "getCurrentPosition",
  		parameters:{},
  		onSuccess:function(response) {
			this.latitude = response.latitude;
			this.longitude = response.longitude;
			this.transit.stopsNear(this.latitude, this.longitude, function(response) {
					this.controller.get("gpsSpinContainer").hide();
					this.controller.get("gpsSpinner").mojo.stop();
					this.gpsListModel.items = response.stops;
		         	this.controller.modelChanged(this.gpsListModel);
					this.controller.get("listGPS").mojo.invalidateItems(0);
					this.controller.get("gpsDrawer").mojo.setOpenState(true);
				}.bind(this),
				function(err) {
					if(this.gpsListModel.items.length>0) {
						this.controller.get("gpsResultsContainer").hide();
						this.controller.get("gpsButtonRow").removeClassName("first");
						this.controller.get("gpsButtonRow").addClassName("single");
					}
					this.controller.get("gpsSpinContainer").hide();
					this.controller.get("gpsSpinner").mojo.stop();
					Error(err.responseText);
				}.bind(this)
			);
		}.bind(this),
		onFailure: function(err) {
			if(this.gpsListModel.items.length>0) {
				this.controller.get("gpsResultsContainer").hide();
				this.controller.get("gpsButtonRow").removeClassName("first");
				this.controller.get("gpsButtonRow").addClassName("single");
			}
			this.controller.get("gpsSpinContainer").hide();
			this.controller.get("gpsSpinner").mojo.stop();
			Error("Failed to get GPS location.");
		}.bind(this)
	});
};

StopFinderAssistant.prototype.handleGPSEntry = function(event) {
	this.controller.stageController.pushScene("listings", {
		transit: this.transit,
		number: event.item.id
	});
};

StopFinderAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if(event.command == Mojo.Menu.prefsCmd) {
			this.controller.showDialog({
				template: 'settings/settings-popup',
				assistant: new SettingsAssistant({controller: this.controller})
			});
		} else if (event.command == Mojo.Menu.helpCmd) {
			this.controller.stageController.pushAppSupportInfoScene();
		}
	}
};

StopFinderAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("numberButton", Mojo.Event.tap, this.handleNumber);
	this.controller.stopListening("gpsButton", Mojo.Event.tap, this.handleGPS);
	this.controller.stopListening("listGPS", Mojo.Event.listTap, this.handleGPSEntry);
};

StopFinderAssistant.prototype.cleanup = function(event) {

};
