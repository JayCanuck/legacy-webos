function ChangelogAssistant() {
	this.changelog = [
		{
			version:"v1.1.0",
			changes: [
				"Added a rearrangable Favourites screen",
				"Improved saving of large text files",
				"Scrollbar indicator added to the text editor",
				"Improved fullscreen mode for the image viewer",
				"Option to change image you're viewing with swipes left/right"
			]
		},
		{
			version:"v1.0.1",
			changes: [
				"Fixed Simplified Chinese help screen translation",
				"Improved help screen for non-English languages",
				"Video tutorials now directly open into the video player"
			]
		},
		{
			version:"v1.0.0",
			changes: [
				"Public App Catalog release!",
				"A homebrew graduate; the homebrew app has been renamed to Internalz Pro",
				"Enhanced user-friendly interface centered around /media/internal/, aka the USB Drive"
			]
		}
	];
}

ChangelogAssistant.prototype.setup = function() {
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

ChangelogAssistant.prototype.deactivate = function(event) {

};

ChangelogAssistant.prototype.cleanup = function(event) {
};
