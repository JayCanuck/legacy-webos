Date.prototype.getNextResetDate = function() {
	var weekVal = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
	var week = Preferences.get("week", {sun:false, mon:false, tue:false, wed:false,
			thu:false, fri:false, sat:false});
	var time = new Date(Preferences.get("time", {ms:new Date().getTime()}).ms);
	time.setMinutes(time.getMinutes()-1);
	time.setDate(this.getDate());
	time.setMonth(this.getMonth());
	time.setFullYear(this.getFullYear());
	if(this.getTime() >= time.getTime()) {
		this.setDate(this.getDate()+1);
	}
	var currDate = this.getDate();
	var found = false;
	//Mojo.Controller.errorDialog(week["sun"] + " - " + weekVal[this.getDay()]);
	for(var i=0; i<weekVal.length; i++) {
		this.setDate(currDate + i);
		if(week[weekVal[this.getDay()]]==true) {
			this.setHours(time.getHours());
			this.setMinutes(time.getMinutes());
			this.setSeconds(time.getSeconds());
			found = true;
			return this;
		}
	}
	return null;
}

Date.prototype.getUTCDateTimeString = function() {
	var date = this;
	var dd = this.getUTCDate();
	if(dd<10)
		dd = '0' + dd;
	var mm = this.getUTCMonth() + 1;
	if(mm<10)
		mm = '0' + mm;
	var yyyy = this.getUTCFullYear();
	var hrs = this.getUTCHours();
	if(hrs<10)
		hrs = '0' + hrs;
	var min = this.getUTCMinutes();
	if(min<10)
		min = '0' + min;
	var sec = this.getUTCSeconds();
	if(sec<10)
		sec = '0' + sec;
	return mm + "/" + dd + "/" + yyyy + " " + hrs + ":" + min + ":" + sec;
}

Date.prototype.getDateTimeString = function() {
	var date = this;
	var dd = this.getDate();
	if(dd<10)
		dd = '0' + dd;
	var mm = this.getMonth() + 1;
	if(mm<10)
		mm = '0' + mm;
	var yyyy = this.getFullYear();
	var hrs = this.getHours();
	if(hrs<10)
		hrs = '0' + hrs;
	var min = this.getMinutes();
	if(min<10)
		min = '0' + min;
	var sec = this.getSeconds();
	if(sec<10)
		sec = '0' + sec;
	return mm + "/" + dd + "/" + yyyy + " " + hrs + ":" + min + ":" + sec;
}
