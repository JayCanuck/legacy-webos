function WallpaperAssistant(params) {
	this.path = params.path;
}

WallpaperAssistant.prototype.setup = function() {
	this.controller.setupWidget("cropWallpaper",
		{
			source: this.path,
			text: $L("Set Wallpaper"),
			width: Mojo.Environment.DeviceInfo.screenWidth,
			height: Mojo.Environment.DeviceInfo.screenHeight,
			callback: this.onSelect.bind(this)
		},
		{}
	);
};

WallpaperAssistant.prototype.activate = function(event) {
	this.controller.enableFullScreenMode(true);
};

WallpaperAssistant.prototype.onSelect = function(event) {
	var params = {"target":this.path};
	if(event.scale)
		params.scale = event.scale;
	if(event.focusX)
		params.focusX = event.focusX;
	if(event.focusY)
		params.focusY = event.focusY;
	this.controller.stageController.popScene({wallpaper:params});
};

WallpaperAssistant.prototype.deactivate = function(event) {
};

WallpaperAssistant.prototype.cleanup = function(event) {
};
