enyo.kind({
	name: "ComicPageViewer",
	kind: "ImageView",
	defaultKind: "ComicPage",
	events: {
		onPageError: ""
	},
	create: function() {
		this.inherited(arguments);
	},
	snapFinish: function() {
		this.inherited(arguments);
		//this.pageChange();
	},
	centerSrcChanged: function(newValue, oldValue) {
		this.inherited(arguments);
		//if(newValue!=oldValue && oldValue!=undefined) {
			//this.pageChange();
		//}
	},
	pageChange: function() {
		enyo.error("PageChange event");
		var zoomlockStatus = enyo.application.prefs.get("zoomLock");
		var center = this.fetchView("center");
		if(center && enyo.ImageView.initZoom && zoomlockStatus) {
			center.setZoom(enyo.ImageView.initZoom);
		}
		var left = this.fetchView("left");
		if(left && enyo.ImageView.initZoom && zoomlockStatus) {
			left.setZoom(enyo.ImageView.initZoom);
		}
		var right = this.fetchView("right");
		if(right && enyo.ImageView.initZoom && zoomlockStatus) {
			right.setZoom(enyo.ImageView.initZoom);
		}
		this.outputPageZooms();
	},
	outputPageZooms: function() {
		var center = this.fetchView("center");
		var centerZoom = "unknown";
		if(center) {
			centerZoom = center.getZoom();
		}
		var left = this.fetchView("left");
		var leftZoom = "unknown";
		if(left) {
			leftZoom = left.getZoom();
		}
		var right = this.fetchView("right");
		var rightZoom = "unknown";
		if(right) {
			rightZoom = right.getZoom();
		}
		var initZoom = enyo.ImageView.initZoom || "unknown";
		enyo.error("i:" + initZoom + " l:" + leftZoom + " c:" + centerZoom + " r:" + rightZoom);
	},
	newView: function(inViewHolder, inInfo, inRender) {
		var view = "center";
		if(inViewHolder == this.$.left) {
			view = "left";
		}else if(inViewHolder == this.$.right) {
			view = "right";
		}
		this.inherited(arguments);
		var newViewObj = this.fetchView(view);
		if(newViewObj) {
			//enyo.error("Creating view: " + view + " with image " + newViewObj.$.image.getSrc());
			newViewObj.imgErrCallback = this.doPageError.bind(this);
			newViewObj.zoomCallback = function(inSender, inZoomValue) {
				enyo.ImageView.initZoom = inZoomValue;
				//enyo.error("zoom event: " + inZoomValue);
				var zoomlockStatus = enyo.application.prefs.get("zoomLock");
				var center = this.fetchView("center");
				if(center && enyo.ImageView.initZoom && zoomlockStatus) {
					center.setZoom(enyo.ImageView.initZoom);
				}
				var left = this.fetchView("left");
				if(left && enyo.ImageView.initZoom && zoomlockStatus) {
					left.setZoom(enyo.ImageView.initZoom);
				}
				var right = this.fetchView("right");
				if(right && enyo.ImageView.initZoom && zoomlockStatus) {
					right.setZoom(enyo.ImageView.initZoom);
				}
			}.bind(this);
		}
	}
});