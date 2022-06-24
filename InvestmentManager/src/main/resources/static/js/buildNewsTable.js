define([
    "dojo/dom",
    "dojo/on", 
    "dijit/registry", 
    "dojo/request", 
    "dojo/json",
    "dojo/domReady!"
], function(dom, on, registry, request){
	
	 on(dom.byId("symbolsList"), "change", function(event){
		 
		 
	        var value = dom.byId("symbolsList").value;
	
	        // Request the JSON data from the server
	        request.get("companyNews?symbol=" + value, {
	            handleAs: "json"
	        }).then(function(data) {
	        	// Display the data sent from the server
                var jsonStr = JSON.stringify(data);
                var news = JSON.parse(jsonStr);
                
//                var displayJason = dom.byId("jsonStream");
//                displayJason.innerHTML = jsonStr;
                
                var totalValue = 0;
                var totalValueAtPurchase = 0;
                var differenceInValue = 0;
                
                var tbl = document.getElementById("newsTable");
                                
                // remove last table
                tbl.innerHTML = "";
                
                var headerRow = tbl.insertRow(0);
                
                var dateHeader = document.createElement("th");
                dateHeader.innerHTML = "Date";
                headerRow.appendChild(dateHeader);
                
                var headlineHeader = document.createElement("th");
                headlineHeader.innerHTML = "Headline";
                headerRow.appendChild(headlineHeader);
                
                var sourceHeader = document.createElement("th");
                sourceHeader.innerHTML = "Source";
                headerRow.appendChild(sourceHeader);
                
                var summaryHeader = document.createElement("th");
                summaryHeader.innerHTML = "Summary";
                headerRow.appendChild(summaryHeader);

                if (news != null && news.length > 0) {
	                for (let i = 0; i < news.length; i++) {
	                  var row = document.createElement("tr");
	                  
	                  var dateCell = document.createElement("td");
	                  dateCell.innerHTML = news[i].publishedDate;
	                  row.appendChild(dateCell);
	                  
	                  var headlineCell = document.createElement("td");
	                  headlineCell.innerHTML = "<a href=\"" + news[i].articleUrl + "\" target=\"_new\">" + news[i].headline + "</a>";
	                  row.appendChild(headlineCell);
	                  
	                  var sourceCell = document.createElement("td");
	                  sourceCell.innerHTML = news[i].source;
	                  row.appendChild(sourceCell);
	                  
	                  var summaryCell = document.createElement("td");
	                  summaryCell.innerHTML = news[i].summary;
	                  row.appendChild(summaryCell);
	                  
	                  tbl.appendChild(row);
	                }
                }
	        	
	        },
	        function(error){
	            // Display the error returned
	            resultDiv.innerHTML = error;
	        });
	});
});