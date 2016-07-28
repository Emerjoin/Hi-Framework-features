hiList.extend(function(extension){
    return {

        name:"bulk",
        singleton:false,
        //inject : ['$compile'],
        build: function(){

            //Override the instance methods here
            extension.$startup = function(html){



            };

            extension.transformRepeatable = function($scope,attrs,transformable){

                var each = attrs.each;

                var checkboxPlaceholder = false;
                var jqRepeatable = transformable.repeatable;
                var checkbox = $("<input>")
                    .attr("type","checkbox")
                    .attr("ng-model",each+".$bulkIsChecked")
                    .attr("ng-click","checkboxClicked("+each+")")
                    .addClass("bulk-checkbox");

                if(attrs.hasOwnProperty("bulkPlaceholder")){

                    var jqSelector = attrs["bulkPlaceholder"];
                    checkboxPlaceholder = jqRepeatable.find(jqSelector);
                    checkboxPlaceholder.html(checkbox);

                }else{

                    jqRepeatable.prepend(checkbox);

                }

            };

            extension.apiSetup = function($scope, attrs){

                var checked = [];
                var keyAttribute = "id";
                var eachRow = attrs.each;

                var getRowId = function(row){

                    if(row.hasOwnProperty(keyAttribute)){

                        var keyValue = row[keyAttribute];
                        return keyValue;

                    }

                    return false;

                };


                var forceCheck = function(row){

                    var id = getRowId(row);
                    if(checked.indexOf(id)===-1){

                        checked.push(id);

                    }


                    row.$bulkIsChecked = true;

                };

                var forceUncheck = function(row){

                    var id = getRowId(row);
                    var index = checked.indexOf(id);
                    if(index!==-1){

                        checked.splice(index,1);

                    }

                    row.$bulkIsChecked = false;

                };

                if(attrs.hasOwnProperty("bulkKey")){

                    this.keyAttribute = attrs.bulkKey;

                }


                $scope.checkAll = function(){

                    if($scope.rows.length===0)
                        return;

                    $scope.rows.forEach(function(row){

                        forceCheck(row);

                    });

                    $scope.$applyAsync();

                };

                $scope.uncheckAll = function(){

                    if($scope.rows.length===0)
                        return;

                    $scope.rows.forEach(function(row){

                        forceUncheck(row);

                    });

                    $scope.$applyAsync();

                };

                $scope.getAllChecked = function(){

                    return checked;

                };


                $scope.isChecked = function(row){

                    var id = getRowId(row);
                    return checked.indexOf(id)!==-1;

                };

                $scope.checkboxClicked = function(row){

                    if(row.$bulkIsChecked){

                        if(!$scope.isChecked(row))
                            checked.push(getRowId(row));

                    }else{

                        if($scope.isChecked(row)){

                            var keyIndex = checked.indexOf(getRowId(row));
                            checked.splice(keyIndex,1);

                        }

                    }

                };

                $scope.clearChecked = function(){

                    checked = [];
                    $scope.$applyAsync();

                };

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
                if(result.data.length===0)
                    return;

                result.data.forEach(function(item){

                    item.$bulkIsChecked = $scope.isChecked(item);

                });

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