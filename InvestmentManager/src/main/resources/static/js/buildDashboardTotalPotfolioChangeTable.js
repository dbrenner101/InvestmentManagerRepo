$( document ).ready(function() {
    $.getJSON({
        url: "api/changesInPortfolioForLastMonth",
        type: "GET",
        dataType: "json",
    })
    .done(function(resp) {
        $("#totalPortfolioChange").empty();
            console.log("Purchase value: " + resp.purchaseValue);
            console.log(resp.length);
            var element = $("<span>Purchase value: " + resp.purchaseValue + "</span><br/>");
            $("#totalPortfolioChange").append(element);
        for (let i=0; i<resp.length; i++) {
            var element = $("<span>Purchase value: " + resp[i].purchaseValue + "</span><br/>");
            $("#totalPortfolioChange").append(element);
            /*var element = $("<span>Attitude id: " + resp[i].attitudeId + "<br></span>");
            element.append("<span>Attitude: " + resp[i].attitude + "</span><br>");
            element.append("<span>Description: " + resp[i].description + "</span><br>");
            $( "#attitudeResults").append(element);
            $( "#attitudeResults").append("<p></p>");*/
        }
    })
});

/*define([
    "dojo/dom",
    "dojo/on", 
    "dijit/registry", 
    "dojo/request", 
    "dojo/json",
    "dojo/domReady!"
], function(dom, on, registry, request){
	
        // Request the JSON data from the server
    request.get("api/changesInPortfolioForLastMonth", {
        handleAs: "json"
    }).then(function(data) {
    	// Display the data sent from the server
        var jsonStr = JSON.stringify(data);
        var totalChange = JSON.parse(jsonStr);
        
//                var displayJason = dom.byId("jsonStream");
//                displayJason.innerHTML = jsonStr;
        
        var tbl = document.getElementById("totalPortfolioChange");
                        
        // remove last table
        tbl.innerHTML = "";
        
        var headerRow = tbl.insertRow(0);
        
        var purchaseValueHeader = document.createElement("th");
        purchaseValueHeader.innerHTML = "At Purchase";
        headerRow.appendChild(purchaseValueHeader);
        
        var marketValueHeader = document.createElement("th");
        marketValueHeader.innerHTML = "Market Value";
        headerRow.appendChild(marketValueHeader);

        var changeValueHeader = document.createElement("th");
        changeValueHeader.innerHTML = "Change in Value";
        headerRow.appendChild(changeValueHeader);
        
        var asOfDateHeader = document.createElement("th");
        asOfDateHeader.innerHTML = "As of Date";
        headerRow.appendChild(asOfDateHeader);

        var row = document.createElement("tr");

        var purchaseValueCell = document.createElement("td");
        purchaseValueCell.innerHTML = '$' + totalChange.purchaseValue.formatMoney(2, '.', ',');
        row.append(purchaseValueCell);

        var marketValueCell = document.createElement("td");
        marketValueCell.innerHTML = '$' + totalChange.marketValue.formatMoney(2, '.', ',');
        row.append(marketValueCell);

        var color = "green";
        if (totalChange.changeInValue < 0) {
        	color = "red";
        }
        var changeInValueCell = document.createElement("td");
        changeInValueCell.setAttribute("style", "color:" + color + ";");
        changeInValueCell.innerHTML = '$' + totalChange.changeInValue.formatMoney(2, '.', ',');
        row.append(changeInValueCell);

        var asOfDateCell = document.createElement("td");
        asOfDateCell.innerHTML = totalChange.quoteDate;
        row.append(asOfDateCell);

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
});*/

