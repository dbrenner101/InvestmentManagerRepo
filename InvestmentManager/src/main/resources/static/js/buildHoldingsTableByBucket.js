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