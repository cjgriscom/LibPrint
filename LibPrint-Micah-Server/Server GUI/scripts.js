var req = new XMLHttpRequest();
var url = "http://chandler.io/LibPrint/RequestHandler?request=listQueue";

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
//http://stackoverflow.com/questions/5180382/convert-json-data-to-a-html-table
//var myList = [//...
  //{ "name": "abc", "age": 50 },
  //{ "age": "25", "hobby": "swimming" },
 // { "name": "xyz", "hobby": "programming" }
//];

//$(document).ready(function() {
 //           $("#refresh").click(function(event){
  //             $('#temp').load('http://chandler.io/LibPrint/RequestHandler?request=listQueue');
   //         });
    //     });
function clearTable(elementID)
{
    document.getElementById(elementID).innerHTML = "";
}
$(document).ready(function() {
			$("#refresh").click(function(event){
			$.getJSON('http://chandler.io/LibPrint/RequestHandler?request=listQueue', function(jd) {
				$('#queue').empty();
				buildHtmlTable(queue, jd.queue);
			});
			
			$.getJSON('http://chandler.io/LibPrint/RequestHandler?request=listHistory', function(jd) {
				$('#hist').empty();
				buildHtmlTable(hist, jd.history);
			});
		});
	});
$(document).ready(function() {
	$("#highlight").click(function(event){
    var frm = $('.htaccess');
    var dat = JSON.stringify(frm.serializeArray());

    alert("I am about to POST this:\n\n" + dat);

	$.post(
		 frm.attr("action"),
         dat,
         function(data) {
           alert("Response: " + data);
         }
       );
     });
});
// Builds the HTML Table out of myList.
function buildHtmlTable(selector, myList) {
	
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