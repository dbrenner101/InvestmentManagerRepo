define([
    'dojo/dom',
    'dojo/request',
    'dojo/json',
    'dojo/_base/declare',
    'dgrid/Grid',
    'dgrid/Keyboard',
    'dgrid/Selection',
    'dstore/Store',
    'dojo/domReady!'
], function(dom, request, json, declare, Grid, Keyboard, Selection, Store){
	
        request.get("api/changesInPortfolioBySector", {
            handleAs: "json"
        }).then(function(data) {
            var jsonStr = JSON.stringify(data);
            var totalChange = JSON.parse(jsonStr);
            
//                var displayJason = dom.byId("jsonStream");
//                displayJason.innerHTML = jsonStr;
            
            var CustomGrid = declare([ Grid, Keyboard, Selection ]);
            var grid = new CustomGrid({
                columns: [
                	{
                		label: 'Sector',
                		field: 'sector' 
                	},
                	{
                		field: 'purchaseValue',
                		label: 'Value at Purchase',
                		get: function(object) {
                			return "$" + object.purchaseValue.formatMoney(2, '.', ',');
                		}
                	},
                	{
                		label: 'Market Value', 
                		field: 'marketValue',
                		get: function(object) {
                			return "$" + object.marketValue.formatMoney(2, '.', ',');
                		}
                	}, 
                	{
                		label: 'Change In Value',
                		field: 'changeInValue',
                		get: function(object) {
                			return "$" + object.changeInValue.formatMoney(2, '.', ',');
                		}
                	}
                ],
                // for Selection; only select a single row at a time
                selectionMode: 'single',
                // for Keyboard; allow only row-level keyboard navigation
                cellNavigation: false
            }, 'totalPortfolioChangeBySector');
            grid.renderArray(totalChange);
            
            grid.on('dgrid-select', function (event) {
                // Get the rows that were just selected
                var rows = event.rows;
                
                console.log(rows[0].data.sector);

                // Iterate through all currently-selected items
                for (var id in grid.selection) {
                    if (grid.selection[id]) {
                        
                    }
                }
            });
        	
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

