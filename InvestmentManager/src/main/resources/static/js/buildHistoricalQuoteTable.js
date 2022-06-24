define([
    "dojo/dom",
    "dojo/on",
    "dijit/registry",
    "dojo/request",
    "dojo/json",
    "dojo/domReady!"
], function(dom, on, registry, request) {

	 on(dom.byId("investmentSelection"), "change", function(event){


	        var value = dom.byId("investmentSelection").value;

	        // Request the JSON data from the server
	        request.get("load30DaysQuotesForHoldingAjax?investmentId=" + value, {
	            handleAs: "json"
	        }).then(function(data) {
	        	// Display the data sent from the server
                var jsonStr = JSON.stringify(data);
                var quotes = JSON.parse(jsonStr);

                // display json
                //document.getElementById("jsonStream").innerHTML = jsonStr;

                var tbl = document.getElementById("tableDiv");
                tbl.innerHTML = "";

                var headerRow = tbl.insertRow(0);

                var companyHeader = document.createElement("th");
                companyHeader.innerHTML = "Date";
                headerRow.appendChild(companyHeader);

                var openHeader = document.createElement("th");
                openHeader.innerHTML = "Open";
                headerRow.append(openHeader);

                var closeHeader = document.createElement("th");
                closeHeader.innerHTML = "Close";
                headerRow.appendChild(closeHeader);

                var highHeader = document.createElement("th");
                highHeader.innerHTML = "High";
                headerRow.appendChild(highHeader);

                var lowHeader = document.createElement("th");
                lowHeader.innerHTML = "Low";
                headerRow.appendChild(lowHeader);

                var changeHeader = document.createElement("th");
                changeHeader.innerHTML = "Change";
                headerRow.appendChild(changeHeader);

                var change = 0;
                if (quotes != null && quotes.length > 0) {
	                for (let i = 0; i < quotes.length; i++) {
	                	var upDown = "";
	                    var priceChange = quotes[i].priceChange;
	                    change += priceChange;
	                    if (priceChange > 0) {
	                        upDown = "color:green;";
	                    }
	                    else if (priceChange == 0){
	                    	upDown = "color:black;";
	                    }
	                    else {
	                        upDown = "color:red;";
	                    }
		                var row = document.createElement("tr");
	                    row.setAttribute("style", upDown);
	
		                var companyNameCell = document.createElement("td");
		                companyNameCell.innerHTML = quotes[i].date;
		                row.appendChild(companyNameCell);
	
		                var openCell = document.createElement("td");
		                openCell.innerHTML = quotes[i].open;
		                row.append(openCell);
	
		                var closeCell = document.createElement("td");
		                closeCell.innerHTML = quotes[i].close;
		                row.appendChild(closeCell);
	
		                var highCell = document.createElement("td");
		                highCell.innerHTML = quotes[i].high;
		                row.appendChild(highCell);
	
		                var lowCell = document.createElement("td");
		                lowCell.innerHTML = quotes[i].low;
		                row.appendChild(lowCell);
	
	                    var changeCell = document.createElement("td");
		                changeCell.innerHTML = priceChange;
		                row.appendChild(changeCell);
		
		                tbl.appendChild(row);
	                }
                }
                
                var sumTbl = document.getElementById("changeSummation");
                sumTbl.innerHTML = "";
                var row = sumTbl.insertRow(0);
                var headerCell = document.createElement("th");
                headerCell.innerHTML = "Change Over the Last 30 Days";
                row.appendChild(headerCell);
                var changeCell = document.createElement("th");
                changeCell.innerHTML = change.toFixed(4);
                row.appendChild(changeCell);

	        },
	        function(error){
	            // Display the error returned
	            resultDiv.innerHTML = error;
	        });
	});
});
