enyo.kind({
	name: "ComicPage",
	kind: "ViewImage",
	imgErrCallback: undefined,
	zoomCallback: undefined,
	create: function() {
		this.inherited(arguments);
		this.setMaxZoomRatio(2.5);
	},
	imageError: function() {
		this.inherited(arguments);
		if(this.imgErrCallback) {
			this.imgErrCallback(this.$.image.getSrc());
		}
	},
	/*zoomChanged: function() {
		this.inherited(arguments);
		if(this.zoomCallback && this._imageWidth>1) {
			this.zoomCallback(this, this.zoom);
		}
	},*/
	/*updateZoomPosition: function() {
		this.inherited(arguments);
		if(this.zoomCallback && this._imageWidth>1) {
			this.zoomCallback(this, this.zoom);
		}
	},*/
	adjustSize: function() {
		var w = this._imageWidth = this.bufferImage.width;
		var h = this._imageHeight = this.bufferImage.height;
		var b = this._boxSize = enyo.fetchControlSize(this);
		if (this.autoSize) {
			var ar = w / h;
			var n = this.node;
			w = b.w;
			h = b.h;
			if (h * ar > w) {
				h = w / ar;
			} else {
				w = h * ar;
			}
		}
		this.minZoom = w / this._imageWidth;
		this.maxZoomRatioChanged();
		if(enyo.ImageView.initZoom!=undefined  && this._imageWidth>1 &&
				enyo.application.prefs.get("zoomLock")) {
			this.setZoom(enyo.ImageView.initZoom);
		} else {
			if(enyo.application.prefs.get("viewMode")=="auto") {
				this.setZoom(this.minZoom);
				//enyo.error("min zoom: " + this.minZoom);
			} else if(enyo.application.prefs.get("viewMode")=="width") {
				this.$.image.applyStyle("width", "100%");
				this.$.image.applyStyle("height", "auto");
				this.setZoom(this.$.image.hasNode().offsetWidth / this._imageWidth);
			} else if(enyo.application.prefs.get("viewMode")=="height") {
				this.$.image.applyStyle("height", "100%");
				this.$.image.applyStyle("width", "auto");
				this.setZoom(this.$.image.hasNode().offsetWidth / this._imageWidth);
			}
		}
	},
});