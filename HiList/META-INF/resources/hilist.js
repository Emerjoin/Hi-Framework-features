/**
 * Hi list component
 * @author The Hi Framework Team
 */

var hiList =  {};


/**
 * The extensions should override methods of this class.
 */
hiList.baseExtension = function(){


    /**
     * Transform the hiList HTML before it gets compiled.
     * @param html the hiList HTML
     * @param scope the hiList angular scope
     */
    this.$preTransform = function(html,scope){


    };


    /**
     * Notifies the extension before filtering is executed.
     * @param scope the angular scope
     * @param DOM element of the hiList instance
     * @param filter the filter properties
     * @param ordering the data ordering properties
     * */
    this.$beforeFilter = function(scope,element, filter,ordering){



    };

    /**
     *
     * @param scope
     * @param element
     * @param display
     */
    this.$beforeDisplay = function(scope,element,display){



    };

    this.$startup = function(){



    };


};

hiList.extensions = {};
hiList.extend = function(factory){

    var extInstance = new hiList.baseExtension();
    var extension = factory.call({},extInstance);

    if(!(extension.hasOwnProperty("build")&&extension.hasOwnProperty("name")))
        throw new Error("A hiList extension should contains at least the name property and the build function");


    extension.$newInstance = function(){

        //use dependency injection
        if(extension.hasOwnProperty("inject")) {


            var fn =  extension.build;
            fn["$inject"] = extension.inject;

            return angular.injector().invoke(fn);

        }


        //Just call the damm function
        return extension.build.call({});


    };

    hiList.extensions[extension.name] = extension;

};

hiList.getExtension = function(name){

    if(!hiList.extensions.hasOwnProperty(name))
        throw new Error("Extension not encountered : "+name);


    var extension = hiList.extensions[name];

    //There is a single instance if the extension in the entire application
    if(extension.hasOwnProperty("singleton")){

        if(extension.hasOwnProperty("instance")){

            return extension.instance;

        }

        extension.instance = extension.$newInstance();
        extension.instance.$startup();
        hiList[name] = extension;
        return extension.instance;

    }

    //The extension supports multiple instances : one instance per hiList
    var extension = extension.$newInstance();
    extension.$startup();
    return extension;


};



//---Extension sample begins here---

hiList.extend(function(extension){

    return {

        name:"name1",
        singleton:false,
        //inject : ['$compile'],
        build: function(){

            //Override the instance methods here
            extension.$startup = function(html){

                console.log("I'm just a stupid extension");

            };

            extension.transformRepeatable = function($scope,attrs,transformable){

                /*
                console.log("About to transform the repeatable from extension");
                console.log(transformable.repeatable);
                var jqElement = $(transformable.repeatable).append($("<h6>").html("Another {{row.age}}"));
                transformable.repeatable = jqElement;
                */

            };

            extension.transformHtml = function($scope,attrs,transformable){

                //console.log("About to transform the HTML from extension");
                //transformable.html = $("<div>").html("<h3>Dummy</h3>");

            };

            extension.preFetch = function($scope, filter){

                //console.log("Pre fetching from extension");

            };

            extension.postFetch = function($scope, result){

                //console.log("Post fetching from extension");

            };


            extension.fetchFail = function($scope, filter, ordering){

                //console.log("Fetch failed from extension");

            };

            extension.fetchFinished = function($scope){

                //Doesn't matter if succeeded or not
                //console.log("Fetch finished");

            };

            extension.invalidResult = function($scope){

                //console.log("Invalid result from extension");

            };


            return extension;

        }

    };

});
//---Extension sample ends here---*/

hiList.html = {};
/*
hiList.html.header =
    '<div class="form-group col-md-4 col-lg-4 col-sm-4 col-xs-12">'+
    '<input type="text" ng-model="filter.text" class="form-control" placeholder="Pesquisar">'+
    '</div>'+
    '<div class="col-md-8 col-lg-8 col-sm-8 hidden-xs">'+
    '<select ng-show="show.maxItemsOptions" style="max-width: 60px;" class="form-control pull-right" ng-model="show.maxItems" ng-options="x for x in maxItemsOptions"></select>'+
    '</div>';*/

hiList.html.header ='<div></div>';

hiList.html.footer =
    '<nav class="col-md-12 col-lg-12 col-sm-12 text-center" ng-show="pagesVisible.length>1">'+
    '<ul class="pagination">'+
    '<li>'+
    '<a href="#" aria-label="Previous">'+
    '<span aria-hidden="true"><b translate>Previous</b></span>'+
    '</a>'+
    '</li>'+
    '<li ng-class=\'{"active":activePage==page}\' ng-click="activatePage(page)" ng-repeat="page in pagesVisible"><a href="#">{{page}}</a></li>'+
    '<li>'+
    '<a href="#" aria-label="Next">'+
    '<span aria-hidden="true"><b translate>Next</b></span>'+
    '</a>'+
    '</li>'+
    '</ul>'+
    '</nav>';

hiList.html.empty =
    '<h3>The list is empty for now</h3>';

hiList.html.fail =
    '<h4>The list request failed</h4>';

hiList.html.delaying =
    '<h5>The server is delaying</h5>';

hiList.utils = {};
hiList.utils.getFrontierAction = function(url){

    var curveIndex = url.indexOf('(');
    var dotIndex = url.indexOf('.');

    if(curveIndex==-1||dotIndex==-1)
        return undefined;

    var controller = url.substr(0,dotIndex);
    var action = url.substr(dotIndex+1,url.substr(dotIndex).length-3);


    try {

        var functionResolved = eval(controller + "." + action);
        if(typeof functionResolved !="function")
            return false;

        return functionResolved;

    }catch(err){

        return false;

    }


};

hiList.utils.generatePagesSequence = function(total){

    var array = [];
    var val = 1;

    while(val<=total){

        array.push(val);
        val++;

    }

    return array;

};

hiList.directive = function($compile,$parse){

    var directive = {};
    directive.restrict='E';
    directive.scope = true;
    directive.link = function($scope,element,attributes) {

        $scope.maxItemsOptions=[25,50,150,200];
        $scope.show ={maxItems:25};
        $scope.activePage = 1;
        $scope.ordering = {empty:true};

        $scope.activePageIndex = 0;
        $scope.maxVisiblePages = 6;
        $scope.pages = [];
        $scope.pagesVisible = [];


        $scope.activatePage = function(page){

            $scope.activePage = page;
            $scope.$doFilter(page);

        };

        //Watch filter text
        $scope.$watch("filter.text",function(newValue,oldValue){

            $scope.$doFilter(1);


        });


        //Watch items per page
        $scope.$watch("show.maxItems",function(newValue,oldValue){

            $scope.$doFilter(1);

        });

        $scope.$hide = {};
        $scope.$show = {};
        $scope.show.maxItemsOptions = true;


        $scope.$elements = {};

        $scope.$failed = false;
        $scope.$empty = false;
        $scope.$delaying = false;

        $scope.$filterInProgress = false;

        $scope.$pages = {};
        $scope.callExtensions = function(method,params){

            for(var extensionName in $scope.$extensions){

                var extension = $scope.$extensions[extensionName];

                if(typeof extension!="object")
                    continue;

                if(extension.hasOwnProperty(method)){

                    extension[method].apply(extension,params)

                }


            }

        };

        $scope.$pages.createPagesList = function(totalPages){

            //Generate the pages list

            $scope.pages = hiList.utils.generatePagesSequence(totalPages);

            var pageCount = 0;
            var visiblePages = [];
            $scope.pages.forEach(function(item,index){

                if(pageCount==$scope.maxVisiblePages)
                    return;

                visiblePages.push(item);
                pageCount++;

            });

            $scope.pagesVisible = visiblePages;
            $scope.activePage = 1;


        };
        $scope.$pages.right = function(pageIndex, lastVisiblePageIndex, firstVisiblePageGlobalIndex){

            //Move to right

            if (pageIndex == lastVisiblePageIndex) {

                //TODO: Move right completely

            }

            var previousPageIndex = firstVisiblePageGlobalIndex - 1;

            if (firstVisiblePageGlobalIndex == 0) {

                //First page already visible. Don't move

            }else{


                if (firstVisiblePageGlobalIndex != 0) {

                    var previousPage = $scope.pages[previousPageIndex];

                    var newVisiblePages = angular.copy($scope.pagesVisible);
                    newVisiblePages.splice(newVisiblePages.length-1, 1);
                    newVisiblePages.unshift(previousPage);
                    $scope.pagesVisible = newVisiblePages;

                }

            }




        };
        $scope.$pages.left = function(pageIndex, lastVisiblePageIndex, lastGlobalPageIndex, lastVisiblePageGlobalIndex){

            //Move to left
            if (pageIndex == lastVisiblePageIndex) {

                //TODO: Move left completely

            }

            var nextPageIndex = lastVisiblePageGlobalIndex + 1;

            if (lastGlobalPageIndex == lastVisiblePageGlobalIndex) {

                //Last page already visible. Don't move

            }else{

                if (lastVisiblePageGlobalIndex != lastGlobalPageIndex) {

                    var nextPage = $scope.pages[nextPageIndex];

                    var newVisiblePages = angular.copy($scope.pagesVisible);
                    newVisiblePages.splice(0, 1);
                    newVisiblePages.push(nextPage);
                    $scope.pagesVisible = newVisiblePages;

                }

            }



        };
        $scope.$processResult = function(result){

            if(typeof result!="object") {

                $scope.pagesVisible = [];//No visible pages
                //Warn all the extensions
                $scope.callExtensions("invalidResult",[$scope]);
                console.error("Invalid result returned by " + $scope.$attributes.name + " data source");
                return

            }


            if(!(result.hasOwnProperty("data")
                &&result.hasOwnProperty("totalRowsMatch")
                &&result.hasOwnProperty("pageNumber")
                &&result.hasOwnProperty("totalPagesMatch"))){

                $scope.pagesVisible = [];//No visible pages

                //Warn all the extensions
                $scope.callExtensions("invalidResult",[$scope]);
                console.error("Invalid result returned by "+$scope.$attributes.name+" data source");
                return;

            }


            if(result.data.length==0||result.totalPagesMatch==0){

                //No result returned
                $scope.$empty = true;
                $scope.pagesVisible = [];
                $scope.rows = [];

            }else{

                $scope.$empty = false;

            }

            //Tell the extension that rows were received
            if($scope.$handlers.postFetch)
                $scope.$handlers.postFetch($scope,{result:result});
            $scope.callExtensions("postFetch",[$scope,result]);

            $scope.rows = result.data;
            $scope.previousPage = $scope.activePage;
            $scope.activePage = result.pageNumber;
            $scope.totalPages = result.totalPagesMatch;
            $scope.totalRowsMatch = result.totalRowsMatch;

            var pNumber = $scope.activePage;

            if(pNumber==1){

                $scope.$pages.createPagesList(result.totalPagesMatch);

            }else{

                //Apply the pages shift effect
                var lastVisiblePageIndex = $scope.pagesVisible.indexOf($scope.pagesVisible[$scope.pagesVisible.length-1]);
                var lastVisiblePageGlobalIndex = $scope.pages.indexOf($scope.pagesVisible[$scope.pagesVisible.length-1]);
                var firstVisiblePageGlobalIndex = $scope.pages.indexOf($scope.pagesVisible[0]);
                var lastGlobalPageIndex = $scope.pages.length-1;

                var middleIndex = lastVisiblePageIndex/2;
                var pageIndex = $scope.pagesVisible.indexOf(pNumber);

                if(pageIndex>middleIndex) {

                    //Scroll left
                    $scope.$pages.left(pageIndex,lastVisiblePageIndex,lastGlobalPageIndex,lastVisiblePageGlobalIndex);

                }else if(pageIndex<middleIndex){

                    //Scroll right
                    $scope.$pages.right(pageIndex,lastVisiblePageIndex,firstVisiblePageGlobalIndex);

                }else{

                    //Dont move

                }

            }


        };


        $scope.$delay = 300;
        $scope.$delayTimeout = false;

        //Filter data and update the list
        $scope.$doFilter = function(p){

            if($scope.$delayTimeout)
                clearTimeout($scope.$delayTimeout);

            $scope.$delayTimeout = setTimeout(function(){
                $scope.$delaying = true;
                $scope.$applyAsync();
            },$scope.$delay);

            $scope.$failed = false;

            if(typeof $scope.$ds!="function"){

                console.error("Cant proceed with filtering : invalid list data-source");

            }

            if(!$scope.$filterInProgress){

                $scope.$filterInProgress = true;

            }

            var page = $scope.activePage;

            if(typeof p!="undefined")
                page = p;


            //int pageNumber, int itemsPerPage, Map filter, Map ordering

            var itemsPerPage = $scope.show.maxItems;
            //var filter = $scope.filter;
            var filter = {empty:true};
            var ordering = $scope.ordering;


            //Tell the status handler and all extensions that the filtering is about to start
            if($scope.$handlers.preFetch)
                $scope.$handlers.preFetch($scope,{filter:$scope.filter});
            $scope.callExtensions("preFetch",[$scope,filter]);

            $scope.$ds.call({},page,itemsPerPage,filter,ordering)

                .try(function(result){


                    $scope.$processResult(result);


                }).catch(function(err){


                    //TODO: Show the failure element : based on the list fail attribute

                    //Tell the extensions that the fetch failed
                    $scope.callExtensions("fetchFail",[$scope]);
                    $scope.pagesVisible = [];//No visible pages
                    $scope.$failed = true;
                    $scope.rows=[];
                    console.error("Datasource frontier request failed for " + $scope.$attributes.name + ".");


                }).finally(function(){

                    $scope.callExtensions("fetchFinished",[$scope]);
                    $scope.$delaying = false;
                    clearTimeout($scope.$delayTimeout);

                    if($scope.$filterInProgress){

                        //TODO: Tell the status handler that the filtering is complete
                        $scope.$filterInProgress = false

                    }

                    $scope.$apply();


                });


        };


        //The public method
        $scope.refresh = function(){

            //TODO: Implement this function


        };

        $scope.goToPage = function(number){

            //TODO: Implement this function


        };

        $scope.goToLastPage = function() {

            //TODO: Implement this function

        };

        $scope.goToFirstPage = function(){

            //TODO: Implement this function

        };

        $scope.getCurrentPage = function(){

            //TODO: Implement this function

        };

        $scope.goToNextPage = function(){

            //TODO: Implement this function

        };

        $scope.goToPreviousPage = function(){

            //TODO: Implement this function

        };





        //Extension instances for this particular List
        $scope.$extensions = {};
        $scope.$extensionList = [];

        $scope.$bootExtensions = function(){

            //Load the extensions for this hiList instance
            var props = Hi.$util.getKidProperties('plugin',attributes['$attr']);
            if(props.length>0){

                $scope.$extensionList = props;

                for(var index in props){

                    var item = props[index];
                    if(typeof item=="string"){

                        var extension  = hiList.getExtension(item);
                        $scope.$extensions[item] = extension;

                    }

                }



            }

        };



        $scope.$bootExtensions();

        //$scope.rows = [{id:1,name:"Mario Junior"}];



        //Name
        if(!attributes.hasOwnProperty("name"))
            throw new Error("The list element should have a <name> attribute");

        //Repeatale
        if(!attributes.hasOwnProperty("repeatElement"))
            throw new Error("The list element should have a <repeat-element> attribute that specifies the element that should be repeated.");


        //Item name
        if(!attributes.hasOwnProperty("each"))
            throw new Error("The list element should have a <item> attribute that specifies the name to repeat");

        //Data source
        if(!attributes.hasOwnProperty("src"))
            throw new Error("The list element should have a <src> attribute that specifies the data source.");


        //Items per page
        if(attributes.hasOwnProperty("perPage")){

            var itemsPerPage = parseInt(attributes.perPage);
            if(!isNaN(itemsPerPage)){

                if($scope.maxItemsOptions.indexOf(itemsPerPage)==-1) {
                    throw new Error("Invalid max items per page : " + itemsPerPage + ". Should be one of the following : "+$scope.maxItemsOptions.toString());
                }

                $scope.show.maxItems = itemsPerPage;

            }

        }

        var dataSource = attributes.src;
        var frontierMethod = hiList.utils.getFrontierAction(dataSource);
        if(typeof frontierMethod=="undefined")
            throw new Error("Invalid list data source value format : "+dataSource);
        else{

            if(!frontierMethod){

                throw new Error("Data source could not be found. Make sure it was properly created and is available : "+dataSource);

            }

        }

        //Set the data source function
        $scope.$ds = frontierMethod;

        var eachItem = attributes.each;
        var scopeParent = $scope.$parent;
        var listName = attributes.name;
        var listRepeatElement = attributes["repeatElement"];


        var jqRepeatable = element.find(listRepeatElement);

        if(jqRepeatable.length==0)
            throw new Error("Repeatable element could not be found using the specifiend jQuery selector : "+listRepeatElement);



        if(scopeParent.hasOwnProperty(listName))
            console.warn("The list name <"+listName+"> is already in use on its parent scope");


        //The attributes of the element
        $scope.$attributes = attributes;



        if(attributes.hasOwnProperty("delay")){

            var delayValue = attributes["delay"];

            try{

                $scope.$delay = parseInt(delayValue);

            }catch(err){

                throw new Error("Invalid delay detect time : "+delayValue);

            }

        }


        $scope.filter = {text:""};
        $scope.$handlers = {};

        if(attributes.hasOwnProperty("prefetch")){

            var pfetchFname = attributes["prefetch"];
            var parsed = $parse(pfetchFname);
            $scope.$handlers.preFetch = parsed;

        }

        if(attributes.hasOwnProperty("postfetch")){

            var pfetchFname = attributes["postfetch"];
            var parsed = $parse(pfetchFname);
            $scope.$handlers.postFetch = parsed;

        }

        //Tell extensions to transform the repeatable item
        var transformable = {repeatable: jqRepeatable};
        $scope.callExtensions("transformRepeatable",[$scope,attributes,transformable]);
        jqRepeatable = transformable.repeatable;


        //Add the ng-repeat
        jqRepeatable.attr("ng-repeat",eachItem+" in rows");


        //TODO: Add the header
        var header = $("<header>").addClass("row hilist-header").html(hiList.html.header);
        $(element).prepend(header);


        //TODO: Add the footer
        var footer = $("<footer>").addClass("row hilist-footer").html(hiList.html.footer);
        $(element).append(footer);


        var html = $(element).html();


        //Call plugins to transform the markup
        transformable = {html:html};
        $scope.callExtensions("transformHtml",[$scope,attributes,transformable]);
        html = transformable.html;


        var angularElement = angular.element(html);

        var compile = $compile(angularElement,function(){

            //TODO: Call the plugins to transform the scope

        });

        element.html(angularElement);





        //Add the scope object to the its parent
        scopeParent[listName] = $scope;

        //
        compile($scope);


    };

    return directive;


};


Hi.$ui.js.component("list",hiList.directive);