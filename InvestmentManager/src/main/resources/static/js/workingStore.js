require(["dstore/Rest", "dijit/Tree"], function(Rest, Tree) {
	var governmentStore = new Rest({
		target: "restful/changesInPortfolioBySector",
		getChildren: function(object, onComplete){
			// object may just be stub object, so get the full object first and then return it's
			// list of children
			this.get(object.id).then(function(fullObject){
				onComplete(fullObject.children);
			});
		},
		getRoot: function(onItem){
			this.get(".").then(onItem);
		},
		mayHaveChildren: function(item){
			return "children" in item;
		},
		getLabel: function(object){
			return object.name;
		}
		
	});
	// set up the tree, assigning governmentModel;
	var governmentTree = new Tree({
		model: governmentStore,
		onOpenClick: true
	}, "totalPortfolioChangeBySector");
	governmentTree.startup();
});



/*define([
    'dojo/_base/lang',
    'dojo/_base/declare',
    'dojo/request',
    'dstore/Store',
    'dstore/RequestMemory',
    'dgrid/OnDemandGrid',
    'dgrid/Keyboard',
    'dgrid/Tree',
], function (lang, declare, request, Store, RequestMemory, OnDemandGrid, Keyboard, Tree) {
	var store = new RequestMemory({
	    target: 'restful/changesInPortfolioBySector',
	    getChildren: function(object, onComplete){
            // object may just be a stub object, so get the full object first and then
            // return its list of children
            this.get(object.id).then(function(fullObject){
            	console.log("getChildren");
                onComplete(fullObject.children);
            });
        }
	});
	
	var treeGrid = new (declare([ OnDemandGrid, Keyboard, Tree ]))({
        collection: store,
        columns:  [
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
        	},
        	{
        		label: 'Sector',
        		field: 'sector',
        		id: 'sector', 
        		'children': [
        	        {
        	            "name": "Symbol",
        	            "id": "symbol"

        	        }
        	    ]
        	}
        ]
    }, 'totalPortfolioChangeBySector');
    
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

