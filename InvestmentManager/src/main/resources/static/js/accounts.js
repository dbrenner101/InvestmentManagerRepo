$( document ).ready(function() {
	var acctListRetrieved = false;
	
	function isRetrieved() {
	    return acctListRetrieved;
	}
	
	function retrieved() {
		acctListRetrieved = true;
	}
	
	$("#listAccountsEvent").click(function(event, acctListRetrieved) {
		if (isRetrieved()) {
			event.preventDefault();
			event.stopPropagation();
			return;
		}
		
		$.ajax({
			 
		    // The URL for the request
		    url: "manageAccounts",
		 
		    // The data to send (will be converted to a query string)
		    data: {},
		 
		    // Whether this is a POST or GET request
		    type: "GET",
		 
		    // The type of data we expect back
		    dataType : "json",
		})
		  // Code to run if the request succeeds (is done);
		  // The response is passed to the function
		  .done(function( accountsList ) {
			  var acctsTable;
			  for (let i=0; i<accountsList.length; i++) {
				  acctsTable = acctsTable + '<tr><td id="account">' + accountsList[i].accountName + '</td>' +
				  	'<td>' + accountsList[i].company + '</td>' +
				  	'<td>' + accountsList[i].owner + '</td>' +
				  	'<td>' + accountsList[i].accountNumber + '</td>' +
				  	'<td>' + accountsList[i].accountType + '</td>' +
				  	'<td>$' + accountsList[i].cashOnAccount.formatMoney(2, '.', ',') + '</td></tr>';
			  }
			  $("#accountsListTable").find('tbody').append( acctsTable );

		      retrieved();
		  })
		  // Code to run if the request fails; the raw request and
		  // status codes are passed to the function
		  .fail(function( xhr, status, errorThrown ) {
		    alert( "Sorry, there was a problem!" );
		    console.log( "Error: " + errorThrown );
		    console.log( "Status: " + status );
		    console.dir( xhr );
		  })
	});
	
	$("table tbody tr").click(function(){
        alert($(this).text());
    });
	
	Number.prototype.formatMoney = function(c, d, t){
        var n = this, 
        c = isNaN(c = Math.abs(c)) ? 2 : c, 
        d = d == undefined ? "." : d, 
        t = t == undefined ? "," : t, 
        s = n < 0 ? "-" : "", 
        i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))), 
        j = (j = i.length) > 3 ? j % 3 : 0;
       return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
     };
 
});