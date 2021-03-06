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
    
    var expenseRatioHeader = document.createElement("th");
    expenseRatioHeader.innerHTML = "Expense Ratio";
    headerRow.appendChild(expenseRatioHeader);
    
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
          
          var expenseRatioCell = document.createElement("td");
          expenseRatioCell.innerHTML = holdings[i].investment.expenseRatio;
          row.appendChild(expenseRatioCell);
          
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
        
        differenceInValue = totalValue - totalValueAtPurchase;
        
        
        dom.byId("totalValueChange").innerHTML = differenceInValue.formatMoney(2, '.', ',');
        dom.byId("valueAtPurchase").innerHTML = totalValueAtPurchase.formatMoney(2, '.', ',');
        dom.byId("totalCurrentValue").innerHTML = totalValue.formatMoney(2, '.', ',');
    }     
}