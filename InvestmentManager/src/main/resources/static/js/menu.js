require([
    "dijit/Menu",
    "dijit/MenuItem",
    "dijit/MenuBar",
    "dijit/MenuBarItem",
    "dijit/PopupMenuBarItem",
    "dijit/MenuSeparator",
    "dojo/domReady!"
], function(Menu, MenuItem, MenuBar, MenuBarItem, PopupMenuBarItem, MenuSeparator){
    // create the Menu container
    var mainMenu = new MenuBar({}, "mainMenu");

    // create Menu container and child MenuItems
    // for our sub-menu (no srcNodeRef; we will
    //add it under a PopupMenuItem)
    var investmentMenu = new Menu({
        id: "investmentMenu"
    });
    investmentMenu.addChild(new MenuItem({
        id: "addInvestment",
        label: "Add Investment",
        onClick: function() {
        	location.assign("addInvestmentEntry");
        }
    }));
    investmentMenu.addChild(new MenuItem({
        id: "listInvestments",
        label: "List All Investments",
        onClick: function() {
        	location.assign("getAllInvestments");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
        id: "investments",
        label: "Investments",
        popup: investmentMenu
    }));

    
    accountsMenu = new Menu({
    	id: "accountsMenu"
    });
    accountsMenu.addChild(new MenuItem({
    	id: "addAccount",
    	label: "Add an Account",
        onClick: function() {
        	location.assign("getNewAccountForm");
        }
    }));
    accountsMenu.addChild(new MenuItem({
    	id: "editAccount",
    	label: "Edit an Account",
        onClick: function() {
        	location.assign("editAccountPrep");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
    	id: "accounts",
    	label: "Accounts",
    	popup: accountsMenu
    }));
    
    tradesMenu = new Menu({ id: "tradesMenu" })
    tradesMenu.addChild(new MenuItem({
    	id: "buy",
    	label: "Buy",
        onClick: function() {
        	location.assign("prepareDefineTrade");
        }
    }));
    tradesMenu.addChild(new MenuItem({
    	id: "deposit",
    	label: "Make Deposit",
        onClick: function() {
        	location.assign("prepareDepositForm");
        }
    }));
    tradesMenu.addChild(new MenuItem({
    	id: "transferCash",
    	label: "Transfer Cash",
        onClick: function() {
        	location.assign("prepTransferCashForm");
        }
    }));
    tradesMenu.addChild(new MenuItem({
    	id: "applyDividend",
    	label: "Apply Dividend",
        onClick: function() {
        	location.assign("prepDividendForm");
        }
    }));
    tradesMenu.addChild(new MenuItem({
    	id: "listTransactions",
    	label: "List Transactions",
        onClick: function() {
        	location.assign("prepForTransactionsList");
        }
    }))
    mainMenu.addChild(new PopupMenuBarItem({
    	id: "transactions",
    	label: "Transactions",
    	popup: tradesMenu
    }));
    
    holdingsMenu = new Menu({ id: "holdingsMenu" })
    holdingsMenu.addChild(new MenuItem({
    	id: "listHoldings",
    	label: "List Holdings",
        onClick: function() {
        	location.assign("getAccountsForHoldings");
        }
    }));
    holdingsMenu.addChild(new MenuItem({
    	id: "addHolding",
    	label: "Add Holding",
        onClick: function() {
        	location.assign("prepAddHolding");
        }
    }));
    holdingsMenu.addChild(new MenuItem({
    	id: "updateQuotes",
    	label: "Update Quotes",
        onClick: function() {
        	location.assign("getInvestmentsAndMostRecentQuoteDate");
        }
    }));
    holdingsMenu.addChild(new MenuItem({
    	id: "manualQuote",
    	label: "Add Quote",
        onClick: function() {
        	location.assign("prepAddManualQuoteForm");
        }
    }));
    holdingsMenu.addChild(new MenuItem({
    	id: "splitItem",
    	label: "Enter Split",
        onClick: function() {
        	location.assign("prepSplitEntryForm");
        }
    }))
    mainMenu.addChild(new PopupMenuBarItem({
    	id: "holdings",
    	label: "Holdings",
   		popup: holdingsMenu
    }));
    
    reportsMenu = new Menu({ id: "reportsMenu" })
    reportsMenu.addChild(new MenuItem({
    	id: "reportsx",
    	label: "Most Recent Quote",
        onClick: function() {
        	location.assign("prepForMostRecentQuote");
        }
    }));
    reportsMenu.addChild(new MenuItem({
    	id: "getQuotes",
    	label: "Get 3 Month Quotes",
        onClick: function() {
        	location.assign("prepGetQuotesAjax");
        }
    }));
    reportsMenu.addChild(new MenuItem({
    	id: "portfolioPerformance",
    	label: "Visualize Portfolio Performance",
        onClick: function() {
        	location.assign("visualizePortfolioPerformance");
        }
    }));
    reportsMenu.addChild(new MenuItem({
    	id: "rollup",
    	label: "Roll-up Reporting",
        onClick: function() {
        	location.assign("retrieveRollupData");
        }
    }));
    reportsMenu.addChild(new MenuItem({
    	id: "holdingsBreakDown",
    	label: "Holdings Breakdown",
        onClick: function() {
        	location.assign("holdingsByTypeAndSector");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
    	id: "reports",
    	label: "Reports",
    	popup: reportsMenu
    }));
    
    bulkDataMenu = new Menu({ id: "bulkMenu" })
    bulkDataMenu.addChild(new MenuItem({
    	id: "bulkUpload",
    	label: "Upload Bulk Data",
        onClick: function() {
        	location.assign("loadBulkDataForm");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
    	id: "bulk",
    	label: "Bulk Data Management",
    	popup: bulkDataMenu
    }));
    
    watchlistsMenu = new Menu({ id: "watchlistsMenu" })
    watchlistsMenu.addChild(new MenuItem({
    	id: "addWatchlist",
    	label: "Add Watchlist",
        onClick: function() {
        	location.assign("prepWatchlistForm");
        }
    }));
    watchlistsMenu.addChild(new MenuItem({
    	id: "manageWatchlist",
    	label: "Manage Watchlists",
        onClick: function() {
        	location.assign("getWatchlists");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
    	id: "watchlists",
    	label: "Watchlists",
    	popup: watchlistsMenu
    }));
    
    adminMenu = new Menu({ id: "adminMenu" })
    adminMenu.addChild(new MenuItem({
    	id: "logout",
    	label: "Log out",
        onClick: function() {
        	location.assign("logout");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
    	id: "admin",
    	label: "Admin",
    	popup: adminMenu
    }));
    
    var newsMenu = new Menu({
        id: "newsMenu"
    });
    newsMenu.addChild(new MenuItem({
        id: "todaysNews",
        label: "Today's News",
        onClick: function() {
        	location.assign("todaysNews");
        }
    }));
    newsMenu.addChild(new MenuItem({
        id: "companyNews",
        label: "Company News",
        onClick: function() {
        	location.assign("");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
        id: "news",
        label: "News",
        popup: newsMenu
    }));
    
    mainMenu.addChild(new MenuBarItem({
    	id: "homeItem",
    	label: "Home", 
    	onClick: function() {
    		location.assign("index");
    	}
    }));

    mainMenu.startup();
    investmentMenu.startup();
    accountsMenu.startup();
    tradesMenu.startup();
    holdingsMenu.startup();
    reportsMenu.startup();
    bulkDataMenu.startup();
    watchlistsMenu.startup();
    adminMenu.startup();
    newsMenu.startup();
});