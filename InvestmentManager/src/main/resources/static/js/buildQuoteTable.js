define([
    "dojo/dom",
    "dojo/on", 
    "dijit/registry", 
    "dojo/request", 
    "dojo/json",
    "dojo/domReady!"
], function(dom, on, registry, request){
	
	var callCount = 0;
	
	 on(dom.byId("investmentSelect"), "change", function(event){
		 
		 
	        var value = dom.byId("investmentSelect").value;
	
	        // Request the JSON data from the server
	        request.get("getQuote?symbol=" + value, {
	            handleAs: "json"
	        }).then(function(data) {
	        	// Display the data sent from the server
                var jsonStr = JSON.stringify(data);
                var quotes = JSON.parse(jsonStr);
                
                var tbl = document.getElementById("tableDiv");
                                
                // remove last table
                //tbl.innerHTML = jsonStr;
                
                if (callCount < 1) {
	                var headerRow = tbl.insertRow(0);
	                
	                var companyHeader = document.createElement("th");
	                companyHeader.innerHTML = "Company";
	                headerRow.appendChild(companyHeader);
	                
	                var symbolHeader = document.createElement("th");
	                symbolHeader.innerHTML = "Symbol";
	                headerRow.appendChild(symbolHeader);
	                
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
	                
	                var volumeHeader = document.createElement("th");
	                volumeHeader.innerHTML = "Volume";
	                headerRow.appendChild(volumeHeader);
	                
	                var changeHeader = document.createElement("th");
	                changeHeader.innerHTML = "Change";
	                headerRow.appendChild(changeHeader);
	                
	                var week52HighHeader = document.createElement("th");
	                week52HighHeader.innerHTML = "52 Week High";
	                headerRow.appendChild(week52HighHeader);
	                
	                var week52LowHeader = document.createElement("th");
	                week52LowHeader.innerHTML = "52 Week Low";
	                headerRow.appendChild(week52LowHeader);
	                
	                var sectorHeader = document.createElement("th");
	                sectorHeader.innerHTML = "Sector";
	                headerRow.appendChild(sectorHeader);
	                
	                var exchangeHeader = document.createElement("th");
	                exchangeHeader.innerHTML = "Exchange";
	                headerRow.appendChild(exchangeHeader);
                }

                if (quotes != null && quotes.length > 0) {
	                for (let i = 0; i < quotes.length; i++) {
	                  var row = document.createElement("tr");
	                  
	                  var companyNameCell = document.createElement("td");
	                  companyNameCell.innerHTML = quotes[i].investment.companyName;
	                  row.appendChild(companyNameCell);
	                  
	                  var symbolCell = document.createElement("td");
	                  symbolCell.innerHTML = quotes[i].investment.symbol;
	                  row.appendChild(symbolCell);
	                  
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
	                  
	                  var volumeCell = document.createElement("td");
	                  volumeCell.innerHTML = quotes[i].volume;
	                  row.appendChild(volumeCell);
	                  
	                  var changeCell = document.createElement("td");
	                  changeCell.innerHTML = quotes[i].priceChange;
	                  row.appendChild(changeCell);
	                  
	                  var week52highCell = document.createElement("td");
	                  week52highCell.innerHTML = quotes[i].week52High;
	                  row.appendChild(week52highCell);
	                  
	                  var week52lowCell = document.createElement("td");
	                  week52lowCell.innerHTML = quotes[i].week52Low;
	                  row.appendChild(week52lowCell);
	                  
	                  var sectorCell = document.createElement("td");
	                  sectorCell.innerHTML = quotes[i].investment.sector;
	                  row.appendChild(sectorCell);
	                  
	                  var exchangeCell = document.createElement("td");
	                  exchangeCell.innerHTML = quotes[i].investment.exchange;
	                  row.appendChild(exchangeCell);
	                  
	                  
	                  tbl.appendChild(row);
	                }
                }
                
                callCount += 1;
	        	
	        },
	        function(error){
	            // Display the error returned
	            resultDiv.innerHTML = error;
	        });
	});
});