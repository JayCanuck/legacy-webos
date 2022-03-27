function ChangelogAssistant() {
	this.changelog = [
		{
			version:"v1.0.0",
			changes: [
				"Initial release",
			]
		}
	];
}

ChangelogAssistant.prototype.setup = function() {
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, {
		visible: true,
    	items: [{label: "Back", command:"back"}]
	});
	
	this.controller.get("changelogTitle").innerHTML = $L("Change Log");
	var log = "";
	for(var i=0; i<this.changelog.length; i++) {
		log += Mojo.View.render({object:{version:this.changelog[i].version},
				template: "changelog/header"});
		log += "<ul>";
		for(var j=0; j<this.changelog[i].changes.length; j++) {
			log += "<li>" + this.changelog[i].changes[j] + "</li>";
		}
		log += "</ul>";
	}
	log += "<br>";
	this.controller.get("changelog").innerHTML = log;
	this.controller.get("footer").innerHTML = Mojo.Controller.appInfo.copyright;
};

ChangelogAssistant.prototype.activate = function(event) {
	
};

ChangelogAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if(event.command=="back") {
			this.controller.stageController.popScene();
		}
	}
};

ChangelogAssistant.prototype.deactivate = function(event) {

};

ChangelogAssistant.prototype.cleanup = function(event) {
};
