require(["dstore/Rest", "dijit/Tree"], function(Rest, Tree) {
    var governmentStore = new Rest({
        target: ".",
        getChildren: function(object, onComplete){
            // object may just be stub object, so get the full object first and then return it's
            // list of children
            this.get(object.id).then(function(fullObject){
                onComplete(fullObject.children);
            });
        },
        getRoot: function(onItem){
            this.get("root").then(onItem);
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
    }, "tree");
    governmentTree.startup();
});
