function Error(msg) {
	if(msg != undefined) {
		if(msg.length > 0) {
			msg = msg.replace(/\n/g, " ");
			Mojo.Controller.errorDialog($L(msg));
		}
	}
};

function MsgBox(scene, msg, title, onChoose) {
	scene.controller.showAlertDialog({
		onChoose: onChoose || Mojo.doNothing,
		title: title || $L("Information"),
		message: msg,
		choices: [ {label: $L("OK"),value: ""} ],
		allowHTMLMessage: true
	});
};

function getFileName(path) {
	var name = path;
	var index = -1;
	index = name.lastIndexOf("/");
	if(index > -1) {
		name = name.substring(index+1);
	}
	index = name.lastIndexOf("\\");
	if(index > -1) {
		name = name.substring(index+1);
	}
	return name;
};

function getFileExt(path) {
	var ext = ""; //test.abc
	var index = -1;
	index = path.lastIndexOf(".");
	if(index > 0 && path.length>index+1) { //to avoid extensionless hidden files
		ext = path.substring(index+1);
	}
	return ext;
};

function getFileDir(path) {
	var name = path;
	var index = -1;
	name = name.replace(/\\/g, "/");
	index = name.lastIndexOf("/");
	if(index > -1  && index != name.length-1) {
		name = name.substring(0, index);
	}
	return name;
};

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
};
String.prototype.ltrim = function() {
	return this.replace(/^\s+/,"");
};
String.prototype.rtrim = function() {
	return this.replace(/\s+$/,"");
};
String.prototype.startsWith = function(str) {
	return (this.indexOf(str) == 0);
};
String.prototype.endsWith = function(str) {
	var lastIndex = this.lastIndexOf(str);
    return (lastIndex != -1) && (lastIndex + str.length == this.length);
};

Object.isEmpty = function(obj) {
	for(var prop in obj) {
        if(obj.hasOwnProperty(prop))
            return false;
    }
    return true;
};

Element.prototype.hasClass = function(className) {
	var pattern = new RegExp('(^|\\s)' + className + '(\\s|$)'); //use this regexp
	return pattern.test(this.className); //to check for the class
};

Array.prototype.jsonSort = function(field, reverse, primer) {
	if(this.length>0) {
		var lowercase = function(string) { return string.toLowerCase(); };
		reverse = (reverse) ? -1 : 1;
		if(!primer) {
			var type = typeof this[0][field];
			if(type == "number") {
				primer = parseFloat;
			} else if(type == "string") {
				primer = lowercase;
			}
		}
		this.sort(function(a, b) {
			a = a[field];
			b = b[field];
			if (typeof(primer) != 'undefined'){
				a = primer(a);
				b = primer(b);
			}
			if (a<b) return reverse * -1;
			if (a>b) return reverse * 1;
			return 0;
		});
	}
};


Array.prototype.partition = function(begin, end, pivot) {
	var piv=this[pivot];
	this.swap(pivot, end-1);
	var store=begin;
	var ix;
	for(ix=begin; ix<end-1; ++ix) {
		if(this[ix]<=piv) {
			this.swap(store, ix);
			++store;
		}
	}
	this.swap(end-1, store);
	return store;
};

Array.prototype.swap = function(a, b) {
	var tmp=this[a];
	this[a]=this[b];
	this[b]=tmp;
}

Array.prototype.quicksort = function() {
	var array = this;
	var qsort = function(begin, end) {
		if(end-1>begin) {
			var pivot=begin+Math.floor(Math.random()*(end-begin));
			pivot=array.partition(array, begin, end, pivot);
			qsort(array, begin, pivot);
			qsort(array, pivot+1, end);
		}
	};
	qsort(0, this.length);
}

Array.prototype.quicksort2 = function(compareFunction){
	var defaultCompare = function(a, b) {
		if(a < b) {
    		return -1;
  		} else if (a > b) {
    		return 1;
  		} else {
    		return 0;
		}
	};
	
	var sort = function(a, l, r){
		var i=l, j=r, w, x=a[int((l+r)/2)];
		do {
			while(compareFunction(a[i], x) < 0) i++;
			while(compareFunction(x, a[j]) < 0) j--;
			if(i<=j){
				w = a[i];
				a[i++] = a[j];
				a[j--] = w;
			} 
		} while(i <= j);
		if(l < j) sort(a, l,j, compareFunction);
		if(i < r) sort(a, i, r, compareFunction); 
	};
	sort(this, 0, this.length-1, compareFunction);
}


/*unused*/
function jsonArraySort(field, reverse, primer) {
	reverse = (reverse) ? -1 : 1;
	return function(a, b) {
		a = a[field];
		b = b[field];
		if (typeof(primer) != 'undefined'){
			a = primer(a);
			b = primer(b);
		}
		if (a<b) return reverse * -1;
		if (a>b) return reverse * 1;
		return 0;
	};
};

Function.prototype.subclassOf = function(SuperClass) {
	this.prototype = new SuperClass();
	this.prototype.constructor = this;
};
