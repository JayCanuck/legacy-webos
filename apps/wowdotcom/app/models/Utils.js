function Utils() {
}

Utils.mobilizeURL = function(url) {
	var val = url;
	if(val.indexOf("http://wow.com/photos")==-1) {
		val = val.replace("http://wow.com", "http://i.wow.com");
	}
	if(val.indexOf("http://www.wow.com/photos")==-1) {
		val = val.replace("http://www.wow.com", "http://i.wow.com");
	}
	if(val.indexOf("http://wowinsider.com/photos")==-1) {
		val = val.replace("http://wowinsider.com", "http://i.wow.com");
	}
	if(val.indexOf("http://www.wowinsider.com/photos")==-1) {
		val = val.replace("http://www.wowinsider.com", "http://i.wow.com");
	}
	val = val.replace("http://i.wowinsider.com", "http://i.wow.com");
	return val;
};
