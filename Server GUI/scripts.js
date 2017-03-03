var req = new XMLHttpRequest();
var url = "http://chandler.io/LibPrint/RequestHandler?request=listQueue";
var myList;

function callOtherDomain() {
	if ('withCredentials' in req) {
    req.open('GET', url, true);
    // Just like regular ol' XHR
    req.onreadystatechange = function() {
        if (req.readyState === 4) {
            if (req.status >= 200 && req.status < 400) {
                buildHtmlTable('#excelDataTable');
            } else {
                // Handle error case
            }
        }
    };
    req.send();
}
}



// Builds the HTML Table out of myList.
function buildHtmlTable(selector) {
	//myList = $.getJSON("http://chandler.io/LibPrint/RequestHandler?request=listQueue");
	var columns = addAllColumnHeaders(myList, selector);

	for (var i = 0; i < myList.length; i++) {
		var row$ = $('<tr/>');
		for (var colIndex = 0; colIndex < columns.length; colIndex++) {
			var cellValue = myList[i][columns[colIndex]];
			if (cellValue == null) cellValue = "";
			row$.append($('<td/>').html(cellValue));
		}
		$(selector).append(row$);
	}
}


// Adds a header row to the table and returns the set of columns.
// Need to do union of keys from all records as some records may not contain
// all records.
function addAllColumnHeaders(myList, selector) {
	var columnSet = [];
	var headerTr$ = $('<tr/>');

	for (var i = 0; i < myList.length; i++) {
		var rowHash = myList[i];
		for (var key in rowHash) {
			if ($.inArray(key, columnSet) == -1) {
			columnSet.push(key);
			headerTr$.append($('<th/>').html(key));
			}
		}
	}
	$(selector).append(headerTr$);
	return columnSet;
}