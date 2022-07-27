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
    	label: "Manage Accounts",
        onClick: function() {
        	location.assign("editAccountPrep");
        }
    }));
    
    bulkDataMenu = new Menu({ id: "bulkMenu" })
    bulkDataMenu.addChild(new MenuItem({
        id: "bulkUpload",
        label: "Upload Bulk Data",
        onClick: function() {
            location.assign("loadBulkDataForm");
        }
    }));
    
    adminMenu = new Menu({ id: "adminMenu" })
    adminMenu.addChild(new PopupMenuBarItem({
        id: "investment",
        label: "Investments",
        popup: investmentMenu
    }));
    adminMenu.addChild(new MenuSeparator());
    adminMenu.addChild(new PopupMenuBarItem({
        id: "account",
        label: "Accounts",
        popup: accountsMenu
    }));
    adminMenu.addChild(new MenuSeparator());
    adminMenu.addChild(new PopupMenuBarItem({
        id: "bulk",
        label: "Bulk Data Management",
        popup: bulkDataMenu
    }));
    adminMenu.addChild(new MenuSeparator());
    adminMenu.addChild(new MenuItem({
        id: "logout",
        label: "Log out",
        onClick: function() {
            location.assign("/logout");
        }
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
    
    holdingsMenu = new Menu({ id: "holdingsMenu" })
    holdingsMenu.addChild(new MenuItem({
    	id: "listHoldings",
    	label: "List Holdings by Account",
        onClick: function() {
        	location.assign("getAccountsForHoldings");
        }
    }));
    holdingsMenu.addChild(new MenuItem({
        id: "holdingsByBucket",
        label: "List Holdings by Bucket",
        onClick: function() {
            location.assign("chooseBucketForHoldingsList");
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
    	id: "splitItem",
    	label: "Enter Split",
        onClick: function() {
        	location.assign("prepSplitEntryForm");
        }
    }));
    
    quotesMenu = new Menu({ id: "quotesMenu" })
    quotesMenu.addChild(new MenuItem({
        id: "editQuotes",
        label: "Edit Quotes",
        onClick: function() {
            location.assign("editQuotesStart");
        }
    }));
    quotesMenu.addChild(new MenuItem({
        id: "updateQuotes",
        label: "Retrieve Quotes",
        onClick: function() {
            location.assign("getInvestmentsAndMostRecentQuoteDate");
        }
    }));
    quotesMenu.addChild(new MenuItem({
        id: "manualQuote",
        label: "Enter Manual Quote",
        onClick: function() {
            location.assign("getAllInvestments");
        }
    }));
    
    reportsMenu = new Menu({ id: "reportsMenu" })
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
    reportsMenu.addChild(new MenuItem({
        id: "listHoldingsByBucket",
        label: "Holdings by Bucket",
        onClick: function() {
            location.assign("holdingsByBucket")
        }
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
        id: "admin",
        label: "Admin",
        popup: adminMenu
    }));
    
    
    mainMenu.addChild(new PopupMenuBarItem({
        id: "transactions",
        label: "Transactions",
        popup: tradesMenu
    }));
    
    
    mainMenu.addChild(new PopupMenuBarItem({
        id: "holdings",
        label: "Holdings",
        popup: holdingsMenu
    }));
    
    mainMenu.addChild(new PopupMenuBarItem({
        id: "quotes",
        label: "Quotes",
        popup: quotesMenu
    }))
    
    mainMenu.addChild(new PopupMenuBarItem({
        id: "reports",
        label: "Reports",
        popup: reportsMenu
    }));
    
    
    /*mainMenu.addChild(new PopupMenuBarItem({
        id: "watchlists",
        label: "Watchlists",
        popup: watchlistsMenu
    }));*/
    
    
    /*mainMenu.addChild(new PopupMenuBarItem({
        id: "news",
        label: "News",
        popup: newsMenu
    }));*/
    
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
    //watchlistsMenu.startup();
    adminMenu.startup();
    //newsMenu.startup();
});