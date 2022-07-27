define([
    "dojo/dom",
    "dojo/on", 
    "dijit/registry", 
    "dojo/request", 
    "dojo/json",
    "dojo/domReady!"
], function(dom, on, registry, request) {
	
	 on(dom.byId("bucketSelection"), "change", function(event) {
         
        var value = dom.byId("bucketSelection").value;

        // Request the JSON data from the server
        request.get("api/holdings/bucket/" + value, {
            handleAs: "json"
        }).then(function(data) {
            // Display the data sent from the server
            var jsonStr = JSON.stringify(data);
            var holdings = JSON.parse(jsonStr);
            
            buildHoldingsTable(holdings, dom);
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

function buildHoldingsTable(holdings, dom) {
        
                
    var totalValue = 0;
    var totalValueAtPurchase = 0;
    var differenceInValue = 0;
    
    var tbl = document.getElementById("tableDiv");
                    
    // remove last table
    tbl.innerHTML = "";
    
    var headerRow = tbl.insertRow(0);
    
    var dateHeader = document.createElement("th");
    dateHeader.innerHTML = "Purchase Date";
    headerRow.appendChild(dateHeader);
    
    var holdingHeader = document.createElement("th");
    holdingHeader.innerHTML = "Holding";
    headerRow.appendChild(holdingHeader);
    
    var symbolHeader = document.createElement("th");
    symbolHeader.innerHTML = "Symbol";
    headerRow.appendChild(symbolHeader);
    
    var quantityHeader = document.createElement("th");
    quantityHeader.innerHTML = "Quantity";
    headerRow.appendChild(quantityHeader);
    
    var priceAtPurchaseHeader = document.createElement("th");
    priceAtPurchaseHeader.innerHTML = "Price at Purchase";
    headerRow.appendChild(priceAtPurchaseHeader);
    
    var currentPriceHeader = document.createElement("th");
    currentPriceHeader.innerHTML = "Current Price";
    headerRow.appendChild(currentPriceHeader);
    
    var changeInPriceHeader = document.createElement("th");
    changeInPriceHeader.innerHTML = "Change in Price";
    headerRow.appendChild(changeInPriceHeader);
    
    var valueAtPurchaseHeader = document.createElement("th");
    valueAtPurchaseHeader.innerHTML = "Cost Basis";
    headerRow.appendChild(valueAtPurchaseHeader);
    
    var currentValueHeader = document.createElement("th");
    currentValueHeader.innerHTML = "Current Value";
    headerRow.appendChild(currentValueHeader);
    
    var changeInValueHeader = document.createElement("th");
    changeInValueHeader.innerHTML = "Change in Value";
    headerRow.appendChild(changeInValueHeader);
    
    var bucketHeader = document.createElement("th");
    bucketHeader.innerHTML = "Bucket";
    headerRow.appendChild(bucketHeader);
    
    var editHoldingHeader = document.createElement("th");
    editHoldingHeader.innerHTML = "Edit Holding";
    headerRow.appendChild(editHoldingHeader);
    
    var sellHoldingHeader = document.createElement("th");
    sellHoldingHeader.innerHTML = "Sell Holding";
    headerRow.appendChild(sellHoldingHeader);
    
    var deleteHoldingHeader = document.createElement("th");
    deleteHoldingHeader.innerHTML = "Delete Holding";
    headerRow.appendChild(deleteHoldingHeader);
    
    const options = { year: 'numeric', month: 'long', day: 'numeric' };

    if (holdings != null && holdings.length > 0) {
        for (let i = 0; i < holdings.length; i++) {
          var row = document.createElement("tr");
          
          let purchaseDate = new Date(holdings[i].purchaseDate);
          
          var buyDateCell = document.createElement("td");
          buyDateCell.innerHTML = purchaseDate.toLocaleDateString('en-US', options);
          row.appendChild(buyDateCell);
          
          var holdingCell = document.createElement("td");
          holdingCell.innerHTML = holdings[i].investment.companyName;
          row.appendChild(holdingCell);
          
          var symbolCell = document.createElement("td");
          symbolCell.innerHTML = holdings[i].investment.symbol;
          row.appendChild(symbolCell);
          
          var quantityCell = document.createElement("td");
          quantityCell.innerHTML = holdings[i].quantity;
          row.appendChild(quantityCell);
          
          var priceAtPurchaseCell = document.createElement("td");
          priceAtPurchaseCell.innerHTML = '$' + holdings[i].purchasePrice.formatMoney(2, '.', ',');
          row.appendChild(priceAtPurchaseCell);
          
          var currentPriceCell = document.createElement("td");
          if (holdings[i].mostRecentQuote != null) {
              currentPriceCell.innerHTML = '$' + holdings[i].mostRecentQuote.close.formatMoney(2, '.', ',');
          }
          row.appendChild(currentPriceCell);
          
          var changeInPriceCell = document.createElement("td");
          if (holdings[i].mostRecentQuote != null) {
              changeInPriceCell.innerHTML = '$' + (holdings[i].mostRecentQuote.close - holdings[i].purchasePrice).formatMoney(2, '.', ',');
          }
          row.appendChild(changeInPriceCell);
          
          let valueAtPurchase = holdings[i].valueAtPurchase == null ? "No Quote" : '$' + holdings[i].valueAtPurchase.formatMoney(2, '.', ',');
          var valueAtPurchaseCell = document.createElement("td");
          valueAtPurchaseCell.innerHTML =  valueAtPurchase;
          row.appendChild(valueAtPurchaseCell);
          
          let currentValue = holdings[i].currentValue == null ? "No Quote" : '$' + holdings[i].currentValue.formatMoney(2, '.', ',');
          var currentValueCell = document.createElement("td");
          currentValueCell.innerHTML = currentValue;
          row.appendChild(currentValueCell);
          
          let changeInValue = holdings[i].changeInValue == null ? "No Quote" : '$' + holdings[i].changeInValue.formatMoney(2, '.', ',');
          var changeInValueCell = document.createElement("td");
          changeInValueCell.innerHTML = changeInValue;
          row.appendChild(changeInValueCell);
          
          var bucketCell = document.createElement("td");
          bucketCell.innerHTML = holdings[i].bucketEnum;
          row.appendChild(bucketCell);
          
          totalValue += holdings[i].currentValue;
          totalValueAtPurchase += holdings[i].valueAtPurchase;
          
          var editHoldingCell = document.createElement("td");
          editHoldingCell.innerHTML = "<a href=\"editHolding?holdingId=" + holdings[i].holdingId + "\">(Edit)</a>";
          row.appendChild(editHoldingCell);
          
          var sellHoldingCell = document.createElement("td");
          sellHoldingCell.innerHTML = "<a href=\"retrieveHoldingDetails?holdingId=" + holdings[i].holdingId + "\">(Sell)</a>";
          row.appendChild(sellHoldingCell);
          
          var deleteHoldingCell = document.createElement("td");
          deleteHoldingCell.innerHTML = "<a href=\"deleteHolding?holdingId=" + holdings[i].holdingId + "\">(Delete)</a>";
          row.appendChild(deleteHoldingCell);
          
          tbl.appendChild(row);
        }
        
        /*var currentCash = holdings[0].account.cashOnAccount;
        totalValue = totalValue + currentCash;
        
        differenceInValue = totalValue - totalValueAtPurchase - currentCash;*/
        
        
        dom.byId("totalValueChange").innerHTML = differenceInValue.formatMoney(2, '.', ',');
        dom.byId("valueAtPurchase").innerHTML = totalValueAtPurchase.formatMoney(2, '.', ',');
        //dom.byId("currentCash").innerHTML = currentCash.formatMoney(2, '.', ',');
        dom.byId("totalCurrentValue").innerHTML = totalValue.formatMoney(2, '.', ',');
    }
            
}