define([
    "dojo/dom",
    "dojo/on", 
    "dijit/registry", 
    "dojo/request", 
    "dojo/json",
    "dojo/domReady!"
], function(dom, on, registry, request){
	
	 on(dom.byId("totalChangeInPortfolioLink"), "click", function(event){
	
	        // Request the JSON data from the server
	        request.get("getTotalChangeInPortfolioAjax", {
	            handleAs: "json"
	        }).then(function(data) {
	        	// Display the data sent from the server
                var jsonStr = JSON.stringify(data);
                var report = JSON.parse(jsonStr);
                
                var tbl = document.getElementById("totalChangeInPortfolioTable");
                tbl.innerHTML = "";
                
                var headerRow = tbl.insertRow(0);
                
                var totalValuePurchase = document.createElement("th");
                totalValuePurchase.innerHTML = "Total Value at Purchase";
                headerRow.appendChild(totalValuePurchase);
                var totalValueChange = document.createElement("th");
                totalValueChange.innerHTML = "Total Change in Value";
                headerRow.append(totalValueChange);
                var totalCurrentValue = document.createElement("th");
                totalCurrentValue.innerHTML = "Total Current Value";
                headerRow.appendChild(totalCurrentValue);
                var totalPriceChange = document.createElement("th");
                totalPriceChange.innerHTML = "Total Change in Price";
                headerRow.appendChild(totalPriceChange);

				  var row = document.createElement("tr");
				  
				  var totalValuePurchaseCell = document.createElement("td");
				  if (report.totalValueAtPurchase != null) {
					  totalValuePurchaseCell.innerHTML = '$' + report.totalValueAtPurchase.formatMoney(2, '.', ',');
				  }
				  row.appendChild(totalValuePurchaseCell);
				  
				  var totalValueChangeCell = document.createElement("td");
				  if (report.totalChangeInValue != null) {
					  totalValueChangeCell.innerHTML = '$' + report.totalChangeInValue.formatMoney(2, '.', ',');
				  }
				  row.append(totalValueChangeCell);
				  
				  var totalCurrentValueCell = document.createElement("td");
				  if (report.totalCurrentValue != null) {
					  totalCurrentValueCell.innerHTML = '$' + report.totalCurrentValue.formatMoney(2, '.', ',');
				  }
				  row.appendChild(totalCurrentValueCell);
				  
				  var totalPriceChangeCell = document.createElement("td");
				  if (report.totalChangeInPrice != null) {
					  totalPriceChangeCell.innerHTML = '$' + report.totalChangeInPrice.formatMoney(2, '.', ',');
				  }
				  row.appendChild(totalPriceChangeCell);
				  
				  tbl.appendChild(row);
	        	
	        },
	        function(error){
	            // Display the error returned
	            resultDiv.innerHTML = error;
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
});