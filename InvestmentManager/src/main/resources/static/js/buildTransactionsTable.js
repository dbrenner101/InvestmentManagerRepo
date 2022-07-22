define([
    "dojo/dom",
    "dojo/on", 
    "dijit/registry", 
    "dojo/request", 
    "dojo/json",
    "dojo/domReady!"
], function(dom, on, registry, request){
	
	 on(dom.byId("accountsList"), "change", function(event){
		 
		 
	        var value = dom.byId("accountsList").value;
	
	        // Request the JSON data from the server
	        request.get("api/transactions/account/" + value, {
	            handleAs: "json"
	        }).then(function(data) {
	        	// Display the data sent from the server
                var jsonStr = JSON.stringify(data);
                var trades = JSON.parse(jsonStr);
                
                //var displayJason = dom.byId("jsonStream");
                //displayJason.innerHTML = jsonStr;
                
                var tbl = document.getElementById("tableDiv");
                                
                // remove last table
                tbl.innerHTML = "";
                
                var headerRow = tbl.insertRow(0);
                
                var tradeDateHeader = document.createElement("th");
                tradeDateHeader.innerHTML = "Transaction Date";
                headerRow.appendChild(tradeDateHeader);
                
                var tradeTypeHeader = document.createElement("th");
                tradeTypeHeader.innerHTML = "Transaction Type";
                headerRow.appendChild(tradeTypeHeader);
                
                var symbolHeader = document.createElement("th");
                symbolHeader.innerHTML = "Symbol";
                headerRow.append(symbolHeader);
                
                var quantityHeader = document.createElement("th");
                quantityHeader.innerHTML = "Quantity";
                headerRow.appendChild(quantityHeader);
                
                var priceAtPurchaseHeader = document.createElement("th");
                priceAtPurchaseHeader.innerHTML = "Price at Purchase";
                headerRow.appendChild(priceAtPurchaseHeader);
                

                if (trades != null && trades.length > 0) {
	                for (let i = 0; i < trades.length; i++) {
	                  var row = document.createElement("tr");
	                  let d = new Date(trades[i].transactionDate);
	                  var tradeDateCell = document.createElement("td");
	                  tradeDateCell.innerHTML = '<a href="/editTradeGetDetails?transactionId=' + trades[i].transactionId + '&accountId=' + trades[i].Account.accountId + '">' + d.toDateString() + '</a>';
	                  row.appendChild(tradeDateCell);
	                  
	                  var tradeTypeCell = document.createElement("td");
	                  tradeTypeCell.innerHTML = trades[i].transactionType;
	                  row.appendChild(tradeTypeCell);
	                  
	                  var symbolCell = document.createElement("td");
	                  if (trades[i].investment != null) {
	                	  symbolCell.innerHTML = trades[i].investment.symbol;
	                  }
	                  row.append(symbolCell);
	                  
	                  var quantityCell = document.createElement("td");
	                  quantityCell.innerHTML = trades[i].tradeQuantity.formatMoney(4, '.', ',');
	                  row.appendChild(quantityCell);
	                  
	                  var priceAtPurchaseCell = document.createElement("td");
	                  priceAtPurchaseCell.innerHTML = '$' + trades[i].tradePrice.formatMoney(4, '.', ',');
	                  row.appendChild(priceAtPurchaseCell);
	                  
	                  tbl.appendChild(row);
	                  
	                  
	                }
                }
	        	
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