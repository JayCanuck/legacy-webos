function ListingsAssistant(params) {
	this.transit = params.transit;
	this.number = params.number;
}

ListingsAssistant.prototype.setup = function() {
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
	
	this.autoRefresh = Preferences.get("autoRefresh", true);
	this.refreshTimer = undefined;
	
	this.controller.setupWidget("listBuses",
        {
            itemTemplate: "listings/listItem",
            listTemplate: "listings/listContainer",
            swipeToDelete: false,
            reorderable: false
         },
         this.busesListModel = {
             listTitle: "Next Bus",
             items : []
         }
    );
	this.loadEntries();
};

ListingsAssistant.prototype.activate = function(event) {
	this.loadEntries();
	if(!this.refreshTimer && this.autoRefresh) {
		this.refreshTimer = this.controller.window.setInterval(this.loadEntries.bind(this), (60000))
	}
};

ListingsAssistant.prototype.loadEntries = function(event) {
	this.transit.stopSchedule(this.number, function(response) {
			this.busesListModel.items = response.buses;
			for(var i=0; i<this.busesListModel.items.length; i++) {
				this.busesListModel.items[i].time = this.createTimeString(
						this.busesListModel.items[i].milliseconds);
			}
			if(this.busesListModel.items.length==0) {
				this.busesListModel.items[0] = {number:"", time:"", variant:"",
						name:"No buses found"};
			}
		    this.controller.modelChanged(this.busesListModel);
			this.controller.get("listBuses").mojo.invalidateItems(0);
		}.bind(this),
		function(err) {
			this.busesListModel.items = [{number:"", time:"", variant:"",
					name:"Unable to get stop schedule"}];
			this.controller.modelChanged(this.busesListModel);
			this.controller.get("listBuses").mojo.invalidateItems(0);
			Error(err.responseText);
		}
	);
};

ListingsAssistant.prototype.createTimeString = function(milliseconds) {
	var now = new Date();
	var later = new Date(milliseconds);
	var timeString = later-now;
	timeString = Math.round(timeString/1000);
	if(timeString<60) {
		timeString += " sec";
	} else {
		timeString = Math.round(timeString/60);
		if(timeString<60) {
			timeString += " min";
		} else {
			var suffix = "am";
			var min = later.getMinutes();
			if(min<10) {
				min = "0" + min;
			}
			var hr = later.getHours();
			if(hr>12) {
				suffix = "pm";
				hr -= 12;
			}
			if(hr==0) {
				hr = "12";
			}
			timeString = hr + ":" + min + suffix;
		}
	}
	return timeString;
};

ListingsAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if(event.command == Mojo.Menu.prefsCmd) {
			if(this.refreshTimer) {
				this.controller.window.clearTimeout(this.refreshTimer);
				this.refreshTimer = undefined;
				
			}
			this.controller.showDialog({
				template: 'settings/settings-popup',
				assistant: new SettingsAssistant({controller: this.controller,
						callback:function() {
							this.autoRefresh = Preferences.get("autoRefresh", true);
							this.loadEntries();
							if(this.refreshTimer) {
								this.controller.window.clearTimeout(this.refreshTimer);
								this.refreshTimer = undefined;
							}
							if(!this.refreshTimer && this.autoRefresh) {
								this.refreshTimer = this.controller.window.setInterval(this.loadEntries.bind(this), (60000))
							}
						}.bind(this)})
			});
		} else if (event.command == Mojo.Menu.helpCmd) {
			this.controller.stageController.pushAppSupportInfoScene();
		}
	}
};

ListingsAssistant.prototype.deactivate = function(event) {
	if(this.refreshTimer) {
		this.controller.window.clearTimeout(this.refreshTimer);
		this.refreshTimer = undefined;
	}
};

ListingsAssistant.prototype.cleanup = function(event) {
};
