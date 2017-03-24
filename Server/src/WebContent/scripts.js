var domain = "chandler.io";

function refreshTables() {
	$.getJSON('http://'+domain+'/LibPrint/RequestHandler?request=listQueue', function(jd) {
		$('#queue').empty();
		buildHtmlTable(queue, jd.queue, true);
	});
		
	$.getJSON('http://'+domain+'/LibPrint/RequestHandler?request=listHistory', function(jd) {
		$('#hist').empty();
		buildHtmlTable(hist, jd.history, false);
	});
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
		
		refreshTables();
			
	});
	
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

// Adds a header row to the table and returns the set of columns.
// Need to do union of keys from all records as some records may not contain
// all records.

function acceptPrint(id,accept) {
     $.post('http://'+domain+'/LibPrint/RequestHandler', {request: (accept ? "acceptPrint" : "rejectPrint"), ID : id},
         function(returnedData){
    	 	refreshTables(); // Refresh tables
			if(returnedData.status === "error"){
				alert(returnedData.message);
            }
         }, "json").fail(function(){
             alert("This operation failed to complete.");
         });
}

function buildHtmlTable(selector, myList, buttons) {
	if (myList.length <= 0) return;
	var columns = addAllColumnHeaders(selector,myList,buttons);
	var rownum=0;
	for (var i = myList.length - 1; i >= 0; i--) {
		var row$ = $('<tr/>');
		for (var colIndex = 0; colIndex < columns.length; colIndex++) {
			var cellValue = myList[i][columns[colIndex]];
			if (cellValue == null) cellValue = "";
			row$.append($('<td/>').html(cellValue));
		}
		if (buttons) row$.append($('<td/>').html("<a href='#' onclick='acceptPrint("+myList[i].ID+",true) ' id='prb"+rownum+"' class='button bgre'> Print </a>"));
		if (buttons) row$.append($('<td/>').html("<a href='#' onclick='acceptPrint("+myList[i].ID+",false)' id='cab"+rownum+"' class='button bred'> Cancel </a>"));
		rownum++;
		$(selector).append(row$);
	}
}


// Adds a header row to the table and returns the set of columns.
// Need to do union of keys from all records as some records may not contain
// all records.
function addAllColumnHeaders(selector,myList,buttons) {
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
	if (buttons) headerTr$.append($('<th/>').html("Print"));
	if (buttons) headerTr$.append($('<th/>').html("Cancel"));
	
	$(selector).append(headerTr$);
	return columnSet;
}