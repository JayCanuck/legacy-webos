function ChangelogAssistant() {
	this.changelog = [
		{
			version:"v1.5.0",
			changes: [
				"Added a rearrangable Favourites screen",
				"Improved saving of large text files",
				"Scrollbar indicator added to the text editor",
				"Improved fullscreen mode for the image viewer",
				"Option to change image you're viewing with swipes left/right"
			]
		},
		{
			version:"v1.4.1",
			changes: [
				"Improved help screen for non-English languages",
				"Video tutorials now directly open into the video player"
			]
		},
		{
			version:"v1.4.0",
			changes: [
				"Renamed to \"Internalz Pro\", as \"Internalz\" is graduating to the App Catalog",
				"Much improved first-run popup screen",
				"Improved initial directory loading",
				"Default \"normal\" File Explorer font size is smaller",
				"Java-related functions removed for webOS 2.x",
				"Removed the \"About\" menu option",
				"Improved text file saving for webOS 2.x",
				"Added Youtube Internalz Tipz video tutorial section",
				"Added this change log section",
				"You can now press the enter key to execute a renaming",
				"Now supports 2-finger jump scrolling in listings",
				"Pinch-zoom gestures adjusts font size in text editor",
				"Sorting is much smoother",
				"Various bug fixes"
			]
		},
		{
			version:"v1.3.7",
			changes: [
				"Fixed minor JSON formatting error in the German translation strings.json file"
			]
		},
		{
			version:"v1.3.6",
			changes: [
				"Forward swipe refreshes the file/directory listings",
				"Fixed a bug erroneously displaying a saving label of 100% during patching",
				"Minor grammar fixes"
			]
		},
		{
			version:"v1.3.5",
			changes: [
				"Fixed filename wrapping bug affecting screen rotations",
				"Fixed issue with Palm-Dark themed Text Editor looking bad",
				"Fixed bug that prevented the Text Editor's save-dialog on close",
				"Fixed bug causing errors when you set Home Folder",
				"If the Image Viewer is in full-screen mode, the \"Share\" and \"Set As Wallpaper\" options are displayed as buttons",
				"Added \"Restart\" submenu to the File Explorer's app menu, to do Java, Luna, or Device Restarts at any time"
			]
		},
		{
			version:"v1.3.4",
			changes: [
				"Fixed issue preventing ipk file opening",
				"Improved Simplified Chinese translation (thanks to medigi8)",
				"Filename wrapping option is greatly improved"
			]
		},
		{
			version:"v1.3.3",
			changes: [
				"Added a few forgotten translations",
				"Improved German translations (thanks to Pulp)",
				"Fixed an HTML issue, on the settings screen, for Simplified Chinese users"
			]
		},
		{
			version:"v1.3.2",
			changes: [
				"Changed the Image Viewer option \"Default Handler\" to \"Register as file handler\" and added the same option for the Text Editor and Ipk Installer",
				"System default handlers are taken into consideration. If none is set, and Internalz can handle the file, Internalz will.",
				"One-time screen that prompts about registering file handlers.",
				"When downloading an image via the Image Viewer, there's now the option to reload to the downloaded file",
				"Image Viewer now has the option to set images as wallpaper",
				"Text Editor has much-improved file loading/saving with progress displayed",
				"Text Editor auto-save option has been removed until further notice, as it would fail horribly on larger files"
			]
		},
		{
			version:"v1.3.0",
			changes: [
				"Internalz is now the default handler for view-source:// URI scheme",
				"Text Editor/Patcher reworked under a new underlying system; much faster text entry on larger files",
				"New option on Settings screen for app theme: either Palm Default or Palm Dark",
				"New option on Settings screen for wrapping long file/directory names",
				"New option on Settings screen for Text Editor (and Patcher) font size",
				"New option on Settings screen to enable/disable Internalz as default image filetype handler",
				"New system of saving user settings; as a result, all settings are reset from previous versions",
				"\"Black Background\" option is gone and now the Image Viewer always uses a black background",
				"Added \"Copy to User Storage\" menu option in Text Editor, Patcher, Image Viewer, and Ipk Installer for files outside of /media/internal/",
				"Added a \"Share\" menu option to the image viewer with options for Email and MMS",
				"Added a checkbox to the Info screen for files in /media/internal/ called \"Mask from webOS\", which adds a period to the front of the filename, thus masking it from the webOS filepicker/fileindexer",
				"Added an in-editor menu option to toggle word wrap",
				"\"Install\" button always visible in Ipk Installer to allow overwriting installs and updates",
				"Improved cross-app launching compatibility",
				"Extended localization support to include Simplified Chinese and Polish",
				"Many various bug fixes"
			]
		},
		{
			version:"v1.2.0",
			changes: [
				"Any directory can be chosen as Home Folder",
				"\"Master Mode\", for complete device control",
				"Dynamic text file detection (no more preset list of text filetypes)",
				"Added black background option for Image Viewer",
				"Re-styled and robustified Text Editor",
				"Added word-wrap",
				"Added newline format options for Text Editor",
				"Added FAT32 read-only toggle in Info screen",
				"Added \"Share\" button in Info screen to share via email",
				"Added Linux permissions popup in Info screen",
				"Added Ipk Installer to install and uninstall .ipk files",
				"Added full-featured Patcher to edit, install and uninstall .patch files",
				"Internalz now registers itself as default filetype handler for the following file extensions: ipk, jpg, jpeg, png, bmp, gif, sh, mk, js, css, json, txt, log, conf, ini, c, cpp, cs, vb, h, patch, diff"
			]
		},
		{
			version:"v1.0.1",
			changes: [
				"Fixes localization issues with French and Italian devices"
			]
		},
		{
			version:"v1.0.0",
			changes: [
				"Public release!",
				"Too many changed to count between this and the old 0.3.2 alpha release",
				"It's practically a new app compared to before"
			]
		}
	];
}

ChangelogAssistant.prototype.setup = function() {
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: [
      		{label: $L("Back"), command: 'close'}
    	]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	
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

ChangelogAssistant.prototype.handleCommand = function(event) {
	if(event.type == Mojo.Event.command) {
		if(event.command == "close") {
			this.controller.stageController.popScene();
		}
	}
};

ChangelogAssistant.prototype.deactivate = function(event) {

};

ChangelogAssistant.prototype.cleanup = function(event) {
};
