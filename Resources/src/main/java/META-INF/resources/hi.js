
/**
 * Hi Framework Library
 */



var __ = {};



var Hi = {};

/**
 * Contains directives, services and other kind of objects directly related to angular
 */
Hi.$angular = {};

/**
 * Holds configurations of the Hi application.
 */
Hi.$config = {};


Hi.$config.nav = {};
Hi.$config.nav.changeLocation = true;


/**
 * Configurations related to what the user sees
 */
Hi.$config.ui = {};

/**
 * Configurations related to the business logic and view control layer - angular JS.
 */
Hi.$config.angular = {};

//{{config}}


/**
 * Handles all internal processes related to the user-interface
 */
Hi.$ui = {};

/**
 * Holds processes related to user interface markup processement.
 */
Hi.$ui.html = {};

/**
 * Holds processes related to user interface javascript scoping
 */
Hi.$ui.js = {};


/**
 * Handles user navigation window events
 */
Hi.$nav = {};


/**
 * Handles all other operations not handled on none of the previous packages
 */
Hi.$util = {};


/**
 *
 *
 */
Hi.$log = {};
Hi.$log.info = function(text){

    console.info(text);

};

Hi.$log.warn = function(text){

    console.warn(text);

};

Hi.$log.error = function(text){

    console.error(text);

};

/**
 * Contains logic that allows to test the Hi application
 */
Hi.$test = {};


Hi.$test.fakeFrontierPromise = function(){

    var PromiseClass = function(){

        this.fail = function(error,timeout){

            var promisse =  new Hi.$frontiers.Promisse();
            promisse.catch = function(callback){

                promisse.catchCallback = callback;


                if(typeof timeout==="number"){


                    if(timeout>0){

                        setTimeout(function(){

                            promisse.setFail(error);

                        },timeout);

                        return promisse;

                    }


                }


                promisse.setFail(error);

                return promisse;

            };

            return promisse;

        };


        this.return = function(data,timeout){

            var promisse =  new Hi.$frontiers.Promisse();
            promisse.return = function(callback){

                promisse.returnCallback = callback;


                if(typeof timeout==="number"){


                    if(timeout>0){

                        setTimeout(function(){

                            promisse.setSuccess(data);

                        },timeout);

                        return promisse;

                    }


                }


                promisse.setSuccess(data);


                return promisse;

            };

            return promisse;

        };

    };

    return new PromiseClass();

};

Hi.$test.MockFrontier = function(){



};

Hi.$test.MockTemplate = function(data){

    Hi.template(data);

};


Hi.$test.ViewTestPromise = function(path){

    this.route = Hi.$nav.resolveRoute(path);


    this.receptor = {};
    this.gettable = {};
    this.gettable.html = function(){

        //Get the transformed HTML
        return this.receptor.markup;

    };

    this.gettable.controller = function(){

        //Get the controller of the view
        return this.receptor.scope;

    };



    if(!(this.route.hasOwnProperty("controller")&&this.route.hasOwnProperty("action"))){

        this.gettable = false;

    }

    this.viewHtml = "";


    this.build = function(){


        if(!this.gettable)
            return false;

        var vpath = Hi.$nav.getViewPath(this.route.controller,this.route.action);
        Hi.$ui.js.createViewScope(vpath,{},this.viewHtml,false,this.receptor,false,undefined);


        if(!this.receptor.hasOwnProperty("scope")){

            return false;

        }



        this.receptor.scope.$embed = function(){

            //TODO: Implement this

        };


        this.gettable.receptor = this.receptor;
        return this.gettable;


    };


    this.withHtml = function(html){

        //Sets the markup to be used when creating the view scope
        this.viewHtml = html;
        return this;


    };


};

Hi.$test.view = function(path){

    return new Hi.$test.ViewTestPromise(path);

};

//Packages definitions end here


/**
 * - - - - - - - - - - - - -
 * ANGULAR
 * - - - - - - - - - - - - -
 */

/**
 * Angular directives provided by the framework
 */
Hi.$angular.directives = {};

/**
 * This is an attribute restricted directive.
 * Loads an image asynchronously and displays a loader during the process. This directive should be used along with and <img> element.
 * The <img> element should have two attributes: asrc and aload. The aload attribute is the ajax loader and the asrc is the image
 * to be loaded.
 */
Hi.$angular.directives.aload = function(){

    return {

        restrict : 'A',

        link : function(scope,element,attrs){

            var i = new Image();

            var img_source = false;
            var loader = false;
            var loadError = false;




            if(attrs.hasOwnProperty('asrc')){

                img_source = attrs.asrc;

            }else{

                return;

            }


            var doMagic = function(){

                loader = attrs.aload;
                loadError = loader;

                //Error image
                if(attrs.hasOwnProperty("err")){

                    loadError = attrs.err;

                }

                console.log("loader path : "+loader);

                //Mostra o loader
                $(element).attr('src',loader);

                //Carrega a imagem de forma asincrona
                var i = new Image();

                i.onload = function () {

                    //console.log('loaded image '+img_source);
                    $(element).attr('src',img_source);

                };
                i.onerror = function(){

                    $(element).attr('src',loadError);
                    //console.error('error loading image : '+img_source);

                };

                i.src = img_source;


            };

            doMagic();

            attrs.$observe("asrc",function(new_value){


                if(typeof new_value!="undefined"){


                    img_source = attrs.asrc;
                    doMagic();

                }

            });



        }


    };

};

/**
 * This is an attribute restricted directive
 * The directive allows to translate text content based on a i18n dictionary.
 * The directive can be used in one of many ways:
 * a) <element translate></element> - will translate the element's inner HTML using it as a key
 *                                    to find the translated content.
 *
 * b) <element translate="key"></element> - will translate the element's inner HTML using the specified
 *                                    key to find the translated content.
 *
 * c) <element translate-attribute></element> - will translate the specified element's attribute
 *                                    using its value as the key to find the translated content.
 *
 * d) <element translate-attribute="key"></element> - will translate the specified element's attribute
 *                                    using the provided key to find the translated content.
 *
 */
Hi.$angular.directives.translate = function(){
    return  {
        restrict : 'A',
        link : function($scope,element,attrs){


            var props = Hi.$util.getKidProperties('translate',attrs['$attr']);

            var toMarkup = function(name){

                var capitalKeys=['A','B','C','D','E','F','G','H','I','J','K','L','M',
                    'N', 'O','P','Q','R','S','T','U','V','W','X','Y','Z'];

                var markupProp = "";

                var  i = 0;
                for(i=0;i<name.length;i++){

                    var char = name.charAt(i);

                    //Capital key
                    if(capitalKeys.indexOf(char)!=-1){

                        markupProp+='-';

                    }

                    markupProp+=char.toLowerCase();


                }

                return markupProp;


            };


            props.forEach(function(name,index){


                var propName = Hi.$util.camelCase(['translate',Hi.$util.ucfirst(name)]);
                var propertyHtml = toMarkup(name);


                var propKey = attrs[propName];


                //A key was defined for this property
                if(propKey!=""&&propKey!="undefined"&&propKey!=undefined){


                    var propValue = $(element).attr(propertyHtml);

                    if(typeof propValue === "undefined" || typeof propValue !== 'string'){

                        return;

                    }


                    var translatedKey = Hi.$ui.js.lang.getItem(propKey.trim(),propValue.trim());
                    $(element).attr(propertyHtml,translatedKey);


                }else{
                    //There is no key. Translate the property value


                    var propertyHtml = toMarkup(name);
                    var propValue = $(element).attr(propertyHtml);



                    if(typeof propValue == 'undefined' || typeof propValue != 'string'){

                        console.log(attrs);
                        return;

                    }


                    var translatedValue = Hi.$ui.js.lang.getItem(propValue.trim());
                    $(element).attr(propertyHtml,translatedValue);


                }

                $(element).removeAttr('translate-'+propertyHtml);


            });

            var translateKey = attrs.translate;

            //Translate inner HTML by Key
            if(translateKey!=''){

                var innerHTML = $(element).html();
                var translatedKey = Hi.$ui.js.lang.getItem(translateKey.trim(),innerHTML.trim());
                $(element).html(translatedKey);


            }else{

                //Translate inner HTML content

                var innerHTML = $(element).html();
                var translatedHtml = Hi.$ui.js.lang.getItem(innerHTML.trim());
                $(element).html(translatedHtml);


            }

            $(element).removeAttr('translate');

        }

    };


};

/**
 * This is an element restricted directive.
 * The directive allows processing a route a displaying its content
 * within the active route's view.
 *
 * This directive uses 4 attribute to configure the embedded content
 * a) name - the name of the controller object to be created on the active View's scope
 *
 * b) url - the url to be processed
 *
 * c) getParams - an angular expression that resolves to an object with properties to be processed
 *                as query strings. This attribute should not be used
 *                when the url contains query strings.
 *
 * d) preEmbed - an angular expression that resolves to an object to the passed as parameter
 *               when firing $preEmbed event on the embedded view's controller.
 *
 * e) silent - this makes the embedded content to not change the current url displayed on the
 *             browser.
 *
 * Scoping: the scope created for the embedded view is schild of the active view's scope.
 *
 */
Hi.$angular.directives.embed = function(){

    //TODO: Prevent self embed

    return {

        restrict:'E',
        scope:false,
        link:function($scope,element, attrs){

            var url = false;

            if(attrs.hasOwnProperty('url')){

                url = attrs.url;

            }else{

                throw new Error("No url defined for emmbbeded view");

            }

            var embedOptions = {};

            var silentUrlAttr="silent";
            if(attrs.hasOwnProperty(silentUrlAttr)){

                embedOptions.silentUrl = $scope.$eval(attrs[silentUrlAttr]);


            }

            var getParamsAttr = "getparams";
            if(attrs.hasOwnProperty(getParamsAttr)){

                var getParams = $scope.$eval(attrs[getParamsAttr]);

                if(typeof getParams!="undefined"){

                    getParams = JSON.parse(angular.toJson(getParams));

                    var queryString="";

                    var paramIndex = 0;
                    for(var getParamName in getParams){

                        var getParamValue = getParams[getParamName];

                        if(paramIndex>0){

                            queryString=queryString+"&";

                        }

                        queryString=queryString+getParamName+"="+getParamValue;
                        paramIndex++;

                    }

                    url = url+"?"+queryString;
                    url = url.replace(" ","%20");

                }

            }

            var preEmbbedAttr = "preembed";
            if(attrs.hasOwnProperty(preEmbbedAttr)){


                var preEmbbed = $scope.$eval(attrs[preEmbbedAttr]);
                embedOptions.preEmbbed = preEmbbed;


            }




            //TODO: Validate another attributes


            Hi.$nav.navigateTo(url,false,true,function(receptor){


                $scope[attrs.name] = receptor.scope;
                $scope.$applyAsync(function(){


                    $(element).find(".embed-loader").hide();
                    $(element).append(receptor.element);


                });



            },$scope,embedOptions);

        }

    }

};

/**
 * This is an attribute restricted directive
 * This directive requires the presence of the href attribute on the element.
 * It sets up an onClick event that redirects the application
 * to the url expressed in the href attribute.
 */
Hi.$angular.directives.ajaxify = function(){
    return  {

        restrict : 'A',
        link : function($scope,element,attrs){

            $(element).removeAttr('ajaxify');


            if(attrs.href){

                var href = attrs.href;

                var event = 'click';

                $(element).attr('href','#');

                if(attrs.on){

                    event = attrs.on;
                    $(element).removeAttr('on');

                }

                Hi.$nav.bind($(element),href,event);

            }

        }

    };


};

/**
 * Defines all built-in angular directives in a angular module
 * @param angularModule the angular module where the directives should be defined
 */
Hi.$angular.directivesDefiner = function(angularModule){

    this.angularModule = angularModule;
    this.setModule = function(module){

        this.angularModule = module;

    };

    this.define = function(){

        if(typeof this.angularModule=="object"){

            if(typeof this.angularModule.directive!="function"){

                throw new Error("Angular directives could not defined. No valid angular module supplied");

            }

        }

        for(var directiveName in Hi.$angular.directives){

            var directiveDefinition = Hi.$angular.directives[directiveName];
            this.angularModule.directive(directiveName,directiveDefinition);

        }

    };

};

/**
 * Application's angular module.
 */
Hi.$angular.app = false;

/**
 * Creates the application module and defines all the
 * built-in directives
 */
Hi.$angular.run = function(){

    var modulesInjected = [];
    var appRun = false;


            if(Array.isArray(Hi.$config.angular.modules)){

                modulesInjected = Hi.$config.angular.modules;
                Hi.$log.info("Injecting the following modules to Hi Application : ");
                Hi.$log.info(modulesInjected);

            }else{

                Hi.$log.info("No modules to be injected to Hi Application");

            }

            if(typeof Hi.$config.angular.run=="function"){

                appRun = Hi.$config.angular.run;

            }else{

                Hi.$log.info("No run function for Hi Application");

            }


    var angularApp = angular.module('hi', modulesInjected);
    var directives = new Hi.$angular.directivesDefiner(angularApp);
    directives.define();


    if(Array.isArray(modulesInjected)){


        modulesInjected.push('ng');
        //modulesInjected.push('hi');


    }else{

        throw new Error("Invalid value set for property Hi.$config.angular.modules");

    }

    angularApp.run(function($rootScope,$compile){


        if(typeof sessionStorage!="undefined"){

            var oldDeployId = false;
            if(sessionStorage.hasOwnProperty("hiDeployId_"))
                oldDeployId = sessionStorage["hiDeployId_"];
            else
                Hi.$ui.html.cache.destroy();


            if(oldDeployId){

                //Not the same deploy Id
                if(oldDeployId!=App.deployId)
                    Hi.$ui.html.cache.destroy();

            }

            sessionStorage["hiDeployId_"] = App.deployId;

            if(App.deployMode=="DEVELOPMENT")
                Hi.$ui.html.cache.on = false;



        }

        Hi.setLanguage(appLang);

        Hi.$angular.$injector =  angular.injector(modulesInjected);

        Hi.$angular.$compile = $compile;

        __=$rootScope;
        __.$startedUp = false;

        for(var propName in Hi.$ui.js.root){

            var propValue = Hi.$ui.js.root[propName];
            __[propName] = propValue;

        }


        if(__.hasOwnProperty("$init")){

            if(typeof __.$init=="function"){

                __.$init.call(__);

            }

        }

        if(typeof appRun=="function"){

            Hi.$angular.$injector.invoke(appRun);

        }

        if(typeof $startup!="function"){

            throw new Error("$startup function is undefined");

        }

        //TODO: Review this code
        setTimeout(function(){

            $startup();

        },50);

    });

    Hi.$angular.app = angularApp;


};


/**
 * - - - - - - - -
 * UI
 * - - - - - - - -
 */

//HTML Cache
Hi.$ui.html.cache = {on:true};

//Initialize the cache
Hi.$ui.html.cache.initialize = function(){
    try {

        if (sessionStorage) {

            if (!sessionStorage['hi_cache_regs']) {
                sessionStorage['hi_cache_regs'] = JSON.stringify(new Array());
            }

        }

    }catch(err){

        return false;

    }

};

//Carrega uma view antes de ela ser necessaria
Hi.$ui.html.prepareView = function(route_name_or_object){

    //Objecto da rota
    var route = false;

    //Parametro eh uma string
    if(typeof route_name_or_object ==='string'){

        //Pega uma rota com nome igual
        route = Hi.$nav.getNamedRoute(route_name_or_object);

    }else{

        //Parametro eh um objecto
        route = route_name_or_object;

    }


    //Rota valida
    if(route){



        //Gera o caminho da rota
        var path = Hi.$nav.getTextViewPath(route.controller, route.action);


        var url = Hi.$nav.getURL(route);

        //Esta rota ja foi cacheada
        if(Hi.$ui.html.cache.stores(url)){


            return;

        }



        //Faz a requisicao GET
        $.get(url,function(reponse){


            try{

                //Faz parse da response para JSON
                var JSONResponse = JSON.parse(reponse);

                //Pega o HTMK
                var html = JSONResponse.markup;

                //Coloca na cache
                Hi.$ui.html.cache.storeView(url,html);


            }catch(err){


            }


        });



    }

};



Hi.$ui.html.cache.getStorageKey = function(){

    return "_hi_views_"+App.base_url;

};

Hi.$ui.html.cache.updateCache = function(cache){

    if(typeof localStorage!="undefined"&&typeof cache=="object"){

        localStorage[Hi.$ui.html.cache.getStorageKey()] = JSON.stringify(cache);

    }

};

Hi.$ui.html.cache.getCache = function(){

    var theCache = {};

    if(typeof localStorage!="undefined"){

        var key = Hi.$ui.html.cache.getStorageKey();

        if(localStorage.hasOwnProperty(key)){

            try {

                theCache = JSON.parse(localStorage[key]);

            }catch (err){

                Hi.$log.warn("There was an error when trying to parse views cache JSON");

            }

        }

    }

    return theCache;

};




//Limpa a cache
Hi.$ui.html.cache.destroy = function(){

    if(typeof localStorage!="undefined"){

        Hi.$ui.html.cache.updateCache({});

    }

};


//Coloca uma view na cache
Hi.$ui.html.cache.storeView = function(path,markup){


    if(!Hi.$ui.html.cache.on){

        return false;

    }


    var toStore = $("<div>");
    toStore.html(markup);
    $(toStore).find(".hi").remove();
    var html = $(toStore).html();

    var cache = Hi.$ui.html.cache.getCache();
    cache[Hi.$ui.html.cache.normalizePath(path)] = html;
    Hi.$ui.html.cache.updateCache(cache);

    Hi.$log.info("Caching view of path <"+path+">");


};


Hi.$ui.html.cache.normalizePath = function(path){

    var qIndex = path.indexOf('?');

    if(qIndex!=-1){

        return path.substr(0,qIndex);

    }

    return path;

};

//Verifica se a cache contem uma determinada view
Hi.$ui.html.cache.stores = function(path){



    if(!Hi.$ui.html.cache.on){

        return false;

    }

    var cache = Hi.$ui.html.cache.getCache();
    return cache.hasOwnProperty(Hi.$ui.html.cache.normalizePath(path));

};

//Obtem uma view da cahe
Hi.$ui.html.cache.fetch = function(path){

    var cache = Hi.$ui.html.cache.getCache();
    return cache[Hi.$ui.html.cache.normalizePath(path)];


};

//Definir o titulo da pagina
Hi.$ui.html.setTitle = function(title){

    $("title").html(title);

};


Hi.$ui.html.getTitle = function(){

    return $("title").html();

};


Hi.$ui.js.controllers = {};
Hi.$ui.js.loadedControllers = [];

Hi.$ui.js.setLoadedController = function(controller, action){

    Hi.$ui.js.loadedControllers.push(controller+"/"+action);

}

Hi.$ui.js.wasControllerLoaded = function(controller,action){

    var url = controller+"/"+action;
    var urlIndex = Hi.$ui.js.loadedControllers.indexOf(url);
    if(urlIndex==-1){

        return false;

    }

    return true;

};





//Controlador do template
Hi.$ui.js.templatesParent={};

Hi.$ui.js.getViewController = function(controllerName, actionName){

    var view_path = Hi.$nav.getTextViewPath(controllerName,actionName);

    if(Hi.$ui.js.controllers.hasOwnProperty(view_path)){

        var controller = Hi.$ui.js.controllers[view_path];
        return controller;

    }else{

        return false;

    }

};

//Regista um closure de uma view
Hi.$ui.js.setViewController= function(controllerName, actionName, controller){
    var view_path = Hi.$nav.getTextViewPath(controllerName,actionName);
    Hi.$ui.js.controllers[view_path]=controller;
};


Hi.$ui.js.createViewScope = function(viewPath,context_variables,markup,embedded,receptor,$embedScope,embedOptions){

    context_variables.$route = {controller:viewPath.controller,action:viewPath.action};

    //Get the view controller
    var controller = Hi.$ui.js.getViewController(viewPath.controller,viewPath.action);

    if(typeof controller=="undefined"){

        throw new Error("No controller defined. Cant prepare context");
        return;

    }

    if(typeof controller!="function"){

        throw new Error("Invalid view controller");


    };


    var viewScope = false;

    if(embedded){


        if(typeof $embedScope!="undefined"){

            viewScope = $embedScope.$new(false);

        }else{

            //Create a new scope from $rootScope;
            viewScope = __.$new(false);

        }


    }else{

        if(!__.$startedUp&&__.hasOwnProperty("$startup")){

            if(typeof __.$startup=="function"){

                __.$startedUp = true;
                __.$startup.call(__,{controller:viewPath.controller,action:viewPath.action});

            }

        }

        //Create a new scope from $rootScope;
        viewScope = __.$new(true);

    }



    //Inject the scope to the controller function
    var $injector = Hi.$angular.$injector;
    $injector.invoke(controller,false,{_:viewScope,__:__,$scope:viewScope,$rootScope:__,template:__});

    //Apply context variables
    Hi.$ui.js.setScopeProps(viewScope,context_variables);


    //PreLoad on view

    if(viewScope.hasOwnProperty('$preLoad')){

        var newMarkup = viewScope.$preLoad.call(viewScope,markup);
        if(typeof newMarkup!="undefined"){

            markup = newMarkup;

        }

    }

    //PreLoad on template
    if(__.hasOwnProperty("$onPreLoad")){

        if(typeof __.$onPreLoad=="function") {

            var newMarkup = __.$onPreLoad.call(__,viewScope.$route, viewScope, markup);
            if (typeof newMarkup != "undefined") {

                markup = newMarkup;

            }

        }

    }



    var compileFn = Hi.$angular.$compile(markup);
    var compiledElement = compileFn(viewScope);

    if(receptor){



        if(embedOptions) {

            if (typeof embedOptions.preEmbbed != "undefined" && typeof viewScope.$preEmbbed != "undefined") {

                viewScope.$preEmbbed.call(viewScope, embedOptions.preEmbbed);

            }
        }



        receptor.element =compiledElement;
        receptor.scope = viewScope;
        receptor.markup = markup;


        if(typeof viewScope.$postLoad!="undefined"){

            viewScope.$postLoad.call(viewScope);

        }

        return receptor;

    }



    $("#view_content").html("");
    $("#view_content").append(compiledElement);
    Hi.$ui.js.ajaxLoader.hide();

    viewScope.$apply(function(){


        if(typeof viewScope.$postLoad!="undefined"){

            viewScope.$postLoad.call(viewScope);

        }




    });



};



Hi.$ui.js.commands = {};

//Todos comandos Hi
Hi.$ui.js.commands.all={};

//Define um comando Hi
Hi.$ui.js.commands.set = function(key,callback){

    Hi.$ui.js.commands.all[key]=callback;

};


//Comando para recarregar a pagina
Hi.$ui.js.commands.set('$reload',function(url){

  document.location.reload();
  return true;

});



//Update the url shown in browser
Hi.$ui.js.commands.set('$url',function(data){

    if(data.hasOwnProperty("value"))
        Hi.$nav.setLocation(data.value,{});

});




//Executa um commando Hi
Hi.$ui.js.commands.run = function(key,params){

    if(Hi.$ui.js.commands.all.hasOwnProperty(key)){

        var command = Hi.$ui.js.commands.all[key];
        return command(params);


    }

    return false;

};

Hi.$ui.js.setScopeProps = function(context,context_variables){

    var UIRoot = {};

    if(context_variables.$root){

        UIRoot = context_variables.$root;

    }


    for(var root_variable_name in UIRoot){
        var root_variable_value = UIRoot[root_variable_name];
        __[root_variable_name]=root_variable_value;

    }


    __['__t']=__t;


    for(var context_variable_name in context_variables){

        var context_variable_value = context_variables[context_variable_name];
        context[context_variable_name]=context_variable_value;
    }



};

Hi.$ui.js.ajaxLoader = {};

Hi.$ui.js.ajaxLoader.hide = function(){
    
    

};



Hi.$ui.js.ajaxLoader.show = function(){

    

};


//I18n Support

Hi.$ui.js.lang = {};
Hi.$ui.js.lang.dict = {};
Hi.$ui.js.lang.getItem = function(key,default_value){

    if((typeof Hi.$ui.js.lang.dict[key] !=undefined) && (Hi.$ui.js.lang.dict[key] != false) && Hi.$ui.js.lang.dict[key]!=undefined  ){

        return Hi.$ui.js.lang.dict[key];

    }

    if(default_value){

        return default_value;

    }else{

        return key;

    }

};

Hi.$ui.js.root = {};



/**
 * - - - - - - - -
 * UTIL
 * - - - - - - - -
 */

Hi.$util.strip = function(obj){

    var json =angular.toJson(obj);

    return JSON.parse(json);

};

Hi.$util.empty = function(){

    return angular.copy({});

};


Hi.$util.ucfirst = function(str){

    var firstLetter = str.substr(0, 1);
    return firstLetter.toUpperCase() + str.substr(1);

};

Hi.$util.lcfirst = function(str){

    var firstLetter = str.substr(0, 1);
    return firstLetter.toLowerCase() + str.substr(1);

};

Hi.$util.encodeGetParams = function(params){

    var params_uri = "";

    var index = 0;
    for(var param_name in params){

        var param_value = params[param_name];

        if(index!==0){

            params_uri = params_uri+'&';

        }

        params_uri = params_uri+param_name+'='+param_value;

        index++;

    }

    return encodeURI(params_uri);


};

Hi.$util.getKidProperties = function(name,attrs){




    var kids = [];


    for(var propertyIndex in attrs){

        if(propertyIndex.indexOf(name)>-1){

            var property = propertyIndex.replace(name,'').trim();

            if(property!=''){

                kids.push(Hi.$util.lcfirst(property));

            }

        }

    }


    return kids;



};

Hi.$util.camelCase = function (parts){

    var camelCased = '';

    parts.forEach(function(item,index){

        if(index==0){

            camelCased = item;

        }else{

            var firstCapital = item[0].toUpperCase();
            var restLowercase = item.substr(1,item.length-1);

            var unitedCamel = firstCapital+restLowercase;

            camelCased = camelCased+unitedCamel;

        }

    });

    return camelCased;

};


Hi.$util.explode = function(glueChar,string){

    var array = new Array();
    var pendingString ="";

    if(string.trim()==""){

        return [];

    }

    for(var charIndex in string){
        var char = string[charIndex];

        if(typeof char !='string'){

            continue;

        }

        if(char==glueChar){

            if(pendingString!=''){

                array.push(pendingString);
                pendingString="";

            }

        }else{

            pendingString = pendingString+char;

        }

    }

    if(pendingString!=''&&array.length==0){

        array.push(pendingString);

    }else if(array.length>0){

        if(array[array.length-1].valueOf()!=pendingString){

            array.push(pendingString);

        }


    }

    return array;

};




/**
 * - - - - - - - -
 * NAV
 * - - - - - - - -
 */

Hi.$nav.goto = function(element_id,name){

    var route_command = "Hi.$nav.navigateTo('"+name+"');";
    $("#"+element_id).attr('onClick',route_command);

};


//Faz bind do clique ou de outro evento de um elemento a uma rota
Hi.$nav.bind = function(element_id,name,eventName){

    var route_command = "Hi.$nav.navigateTo('"+name+"');";
    if(eventName){

        if(typeof element_id === 'string'){

            $("#"+element_id).bind(eventName,function(){

                eval(route_command);

            });

        }else{

            $(element_id).bind(eventName,function(){

                eval(route_command);

            });

        }


    }else{


        $("#"+element_id).attr('onClick',route_command);

    }


};


Hi.$nav.getViewPath = function(controller,action){

    var vp = {controller:controller,action:action};
    return vp;

};

Hi.$nav.getTextViewPath = function(controller, action){

    return controller+"_"+action;

};

Hi.$nav.routeBack = function(route_name_or_object,getParams){

    Hi.$nav.isGoingBack = true;
    Hi.$nav.navigateTo(route_name_or_object,getParams);


};



Hi.$nav.history = [];
Hi.$nav.last =  false;
Hi.$nav.isGoingBack = false;
Hi.$nav.namedRoutes={};


//Registar uma nova rota
Hi.$nav.newNamedRoute = function(name,object){

    if(!Hi.$nav.hasOwnProperty(name)){
        Hi.$nav.namedRoutes[name]=object;
        return object;
    }

    return false;

};


//Obtem a rota
Hi.$nav.getNamedRoute = function(name){

    //O parametro eh uma string
    if(typeof name==='string'){

        //Eexiste uma rota com o nome especificado
        if(Hi.$nav.namedRoutes.hasOwnProperty(name)){


            //Pega o objecto da rota
            return Hi.$nav.namedRoutes[name];

        }

        return false;
    }

};



Hi.$nav.setActivePath = function(url){

    Hi.$nav.history.push(url);
    return Hi.$nav.history.length -1;

};


Hi.$nav.getPreviousPath = function(url){

    if(Hi.$nav.history.length>0){

        var theUrl = Hi.$nav.history[0];
        Hi.$nav.history.removeVal(theUrl);
        return theUrl;

    }


    return false;

};



//Direcciona o usuario para uma rota
Hi.$nav.navigateTo = function(route_name_or_object,getParams,embed,callback,$embedScope,embedOptions){


    //Hi.$nav.last = {name:route_name_or_object,params:params};
    Hi.$nav.last = {name:route_name_or_object};

    var route_object = Hi.$nav.resolveRoute(route_name_or_object);

    route_object = JSON.parse(JSON.stringify(route_object));

    //Objecto de rota invalido
    if(!route_object){

        return false;

    }

    //Path da View
    var clean_path = Hi.$nav.getURL(route_object);


    //Parametros get do request
    if(route_object.get){

        getParams = route_object.get;

    }


    if(getParams){

        route_object['get']=getParams;

    }

    //Gera o path novamente
    var path = Hi.$nav.getURL(route_object);


    if(!route_object.dialog){


        var cached_view = false;

        var server_directives = {};

        //A view esta na cache
        if(Hi.$ui.html.cache.stores(path)){

            cached_view = Hi.$ui.html.cache.fetch(path);
            server_directives = {'Ignore-View':'true'};

        }

        if(Hi.$ui.js.wasControllerLoaded(route_object.controller,route_object.action)){

            server_directives["Ignore-Js"] = 'true';

        }


        Hi.$nav.requestData(route_object,function(server_response){

            if(server_response.response!=200){

                console.error("Request to server returned error");
                //TODO: Implement handler
                return;

            }

            var context_variables = server_response.data;

            //Commands to be executed on the client-side
            if(context_variables.hasOwnProperty('$invoke')){


                var cmdInvocations = context_variables["$invoke"];
                for(var cmdName in cmdInvocations){

                    var cmdParams = cmdInvocations[cmdName];
                    Hi.$ui.js.commands.run(cmdName,cmdParams);


                }


            }


            var markup = server_response.view;

            if(server_response.controller) {

                Hi.$nav.setNextControllerInfo(route_object.controller, route_object.action);
                eval(server_response.controller);
                Hi.$ui.js.setLoadedController(route_object.controller, route_object.action);

            }


            //A view nao esta na cache
            if(!Hi.$ui.html.cache.stores(clean_path)){

                Hi.$ui.html.cache.storeView(clean_path,markup);

            }

            //A view esta na cache
            if(Hi.$ui.html.cache.stores(clean_path)){

                markup = Hi.$ui.html.cache.fetch(clean_path);

            }


            var controller = route_object.controller;
            var action = route_object.action;


            //Hi.Internal.setNextViewPath(module_name,controller,action); OLD
            var viewPath =  Hi.$nav.getViewPath(controller,action);//New

            var setPageLocation = Hi.$config.nav.changeLocation;


            if(embed){

                if(typeof embedOptions!="undefined"){


                    if(embedOptions.silentUrl){

                        setPageLocation = false;

                    }

                }

            }


            if(setPageLocation){

                var routeIndex = Hi.$nav.setActivePath(path);
                Hi.$nav.setLocation(path,JSON.stringify({index:routeIndex}));

            }

            var generated = Hi.$ui.js.createViewScope(viewPath,context_variables,markup,embed,false,$embedScope,embedOptions);

            if(embed){


                if(typeof callback=="function"){


                    callback.call({},generated);

                }

            }




        },server_directives);

    }


};



//Gera a URL de uma rota
Hi.$nav.getURL= function(route,covw) {

    var app_context = App.context;
    var route_url = App.base_url;

    if (route.controller && !route.controller) {

        route_url = route_url + route.controller + "/";

    } else if (route.controller && !route.controller) {

        route_url = route_url + route.controller + "/";

    } else if (route.controller && route.controller) {

        route_url = route_url + route.controller + "/";
    }

    if (route.action) {

        route_url = route_url + route.action;
    }



    //Parametros get
    if(route.get){

        if(typeof route.get ==="string"){

            route_url = route_url+"?"+route.get;

        }else{

            var get_params = Hi.$util.encodeGetParams(route.get);
            route_url = route_url+"?"+get_params;
        }

        Hi.$log.info('URL with getParams : '+route_url);

    }


    return route_url;

};


Hi.$nav.currentPage=false;
Hi.$nav.currentRoute = false;

Hi.$nav.setLocation = function (location,route){

    Hi.$nav.currentPage=location;
    Hi.$nav.currentRoute = route;

    if(!Hi.$nav.isGoingBack)
        window.history.pushState(route,Hi.$ui.html.getTitle()+Math.random(),location);
    else
        Hi.$nav.isGoingBack = false;


};

Hi.$nav.resetLocation = function(){

    if(Hi.$nav.currentPage){

        window.history.replaceState(Hi.$nav.currentRoute,Hi.$nav.currentPage+Math.random(),Hi.$nav.currentPage);

    }


};


Hi.$nav.isSameRoute = function(url){

    return Hi.$nav.currentPage===url;

};

//Efectua uma requisicao get de uma rota
Hi.$nav.requestData = function(route,callback,server_directives){

    if(route){

        //Obtem a url da rota
        var route_url = Hi.$nav.getURL(route);

        var server_response=false;

        var request_params = {};

        if(server_directives){

            request_params = server_directives;

        }

        //Efectua a requisicao GET
        var request = $.ajax({

            url:route_url,
            headers : server_directives,
            success: function(server){


                callback(server);


            },
            error: function(jqXHR ,textStatus,errorThrown){

                if(__.hasOwnProperty("$onRedirectError")){


                    if(typeof __.$onRedirectError=="function")
                        __.$onRedirectError.call(__,route,jqXHR.status,request);

                }

            }});

    }


};

Hi.$nav.nextControllerInfo = {};
Hi.$nav.setNextControllerInfo = function(controller, action){
    Hi.$nav.nextControllerInfo = {controller:controller,action:action};
};



//Resolve uma rota
Hi.$nav.resolveRoute = function(param){


    if(typeof param==='string'){

        //Possui slashes
        if(param.indexOf('/')!==-1){

            var lastIndex = param.length-1;
            var previousIndex = -1;

            //var parts = new Array();
            //var params = new Array();
            //var params_str = false;

            var controller = false;
            var action = false;

            var pending_word = '';
            var getParams = '';

            // movel/view?name=healy

            for(var charIndex in param){

                var char = param[charIndex];

                if(char!=='/'&&char!=='?'){

                    pending_word = pending_word+char;

                }else{


                    if(previousIndex==-1){

                        throw new Error("Invalid route supplied : "+param);

                    }

                    if(!controller&&!action) {

                        controller = pending_word;
                        pending_word = '';

                    }else if(controller&&!action){

                        action = pending_word;
                        pending_word = '';

                    }else if(controller&&action){

                        pending_word = pending_word+ char;

                    }

                }


                if(charIndex == lastIndex){

                    if(!action&&controller){

                        action = pending_word;

                    }else if(controller&&action){

                        getParams = pending_word;

                    }

                }

                previousIndex = charIndex;

            }


            if(!(controller&&action))
                throw new Error("Invalid route supplied : "+param);


            var route = {};
            route.controller = controller;
            route.action = action;

            if(getParams){

                route.get = getParams;

            }


            return route;

        }

        return Hi.$nav.getNamedRoute(param);

    }else{

        if(param.hasOwnProperty('controller')||param.hasOwnProperty('view')){

            var resolved = JSON.parse(JSON.stringify(param));

            if(param.hasOwnProperty('controller')){
                resolved['controller'] = param.controller;
                delete resolved['controller'];
            }

            if(param.hasOwnProperty('view')){
                resolved['action'] = param.view;
                delete resolved['view'];
            }



            return resolved;
        }

        return param;

    }

};


Hi.$nav.toSlashes = function(route){

    return Hi.$nav.getURL(route);

};


/**
 * FRONTIERS
 */

Hi.$frontiers = {};
Hi.$frontiers.Promisse = function(){

    var setTo = {obj:false,prop:false,callback:false};
    var forbiddenCallback = undefined;
    var timeoutCallback = undefined;
    var offlineCallback = undefined;
    var interruptedCallback = undefined;
    var overequestCallback = undefined;
    var catchCallback = undefined;
    var finallyCallback = undefined;

    var request = undefined;


    var getGlobalHandler = function(name){

        if(__.hasOwnProperty("$frontiers")) {

            if (__.$frontiers.hasOwnProperty(name))
                return __.$frontiers[name];

        }

        return undefined;

    };

    var getGlobalHandlers = function(){

        if(__.hasOwnProperty("$frontiers")) {

            return __.$frontiers;

        }

        return undefined;

    };

    this._setRequest = function(req){

        request = req;

    };

    this._setResult = function(data){

        if(typeof setTo.callback=="function")
            setTo.callback.call(this,data);



        this._setRequestFinished();

    };

    this._setRequestFinished = function(){

        if(typeof finallyCallback=="function")

            finallyCallback.call(this);

        else{

            var handler = getGlobalHandler("finally");

            if(typeof handler!="undefined") {

                handler.call(getGlobalHandlers(),this);

            }

        }

    };

    this._setHttpError = function(code){


        if(typeof catchCallback=="function"){

            catchCallback.call(this,code);

        }else{

            var handler = getGlobalHandler("catch");

            if(typeof handler!="undefined") {

                handler.call(getGlobalHandlers(),this,code);

            }

        }

        this._setRequestFinished();

    };

    this._setTimedOut = function(){

        var gTimeoutHandler = getGlobalHandler("timeout");
        var gErrorHandler = getGlobalHandler("catch");

        if(typeof timeoutCallback=="function") {

            timeoutCallback.call(this);

        }else if(gTimeoutHandler=="function"){

            gTimeoutHandler.call(getGlobalHandlers(),this);

        }else if(typeof catchCallback=="function"){

            this._setHttpError(408);

        }else if(typeof gErrorHandler=="function"){

            gErrorHandler.call(getGlobalHandlers(),this,408);

        }

        this._setRequestFinished();

    };

    this._setOffline = function(){

        var gOfflineHandler = getGlobalHandler("offline");
        var gErrorHandler = getGlobalHandler("catch");

        if(typeof offlineCallback=="function") {

            offlineCallback.call(this);

        }else if(typeof gOfflineHandler=="function"){

            gOfflineHandler.call(getGlobalHandlers(),this);

        }else if(typeof catchCallback=="function"){

            this._setHttpError(0);

        }else if(typeof gErrorHandler=="function"){

            gErrorHandler.call(getGlobalHandlers(),this,0);

        }

        this._setRequestFinished();

    };

    this._setForbidden = function(){

        var gForbiddenHandler = getGlobalHandler("forbidden");
        var gErrorHandler = getGlobalHandler("catch");

        if(typeof forbiddenCallback=="function") {

            forbiddenCallback.call(this);

        }else if(typeof gForbiddenHandler=="function"){

            gForbiddenHandler.call(getGlobalHandlers(),this);

        }else if(typeof catchCallback=="function"){

            this._setHttpError(403);

        }else if(typeof gErrorHandler=="function"){

            gErrorHandler.call(getGlobalHandlers(),403);

        }

        this._setRequestFinished();

    };

    this._setInterruped = function(){

        var gInterruptedHandler = getGlobalHandler("interrupted");
        var gErrorHandler = getGlobalHandler("catch");

        if(typeof interruptedCallback=="function") {

            interruptedCallback.call(this);

        }else if(typeof gInterruptedHandler=="function"){

            gInterruptedHandler.call(getGlobalHandlers(),this);

        }else if(typeof catchCallback=="function"){

            this._setHttpError(421);

        }else if(typeof gErrorHandler=="function"){

            gErrorHandler.call(getGlobalHandlers(),this,421);

        }

        this._setRequestFinished();

    };

    this._setOverRequest = function(){


        var gOverequestHandler = getGlobalHandler("overrequest");
        var gErrorHandler = getGlobalHandler("catch");

        if(typeof overequestCallback=="function") {

            overequestCallback.call(this);

        }else if(typeof gOverequestHandler=="function") {

            gOverequestHandler.call(getGlobalHandlers(), this);

        }else if(typeof catchCallback=="function"){

            this._setHttpError(429);

        }else if(typeof gErrorHandler=="function"){

            catchCallback.call(getGlobalHandlers(),this,429);
        }

        this._setRequestFinished();


    };

    this._setException = function(type){

        var gErrorHandler = getGlobalHandler("catch");

        if(typeof catchCallback=="function") {

            catchCallback.call(this, type);

        }else if(typeof gErrorHandler=="function") {

            gErrorHandler.call(getGlobalHandlers(), this, type);

        }

        this._setRequestFinished();

    };

    //--public interface


    this.try = function(obj) {


        if(typeof obj=="function"){

            setTo.callback = obj;

        }else{

            throw new Error("Wrong parameters");

        }

        return this;

    };

    this.finally = function(obj){

        if(typeof obj=="function"){

            finallyCallback = obj;

        }else{

            throw new Error("Wrong parameters");

        }

        return this;

    };

    this.forbidden = function(callback){

        if(typeof callback!="function")
            throw new Error("Wrong parameters");

        forbiddenCallback = callback;
        return this;

    };

    this.timeout = function(callback){

        if(typeof callback!="function")
            throw new Error("Wrong parameters");

        timeoutCallback = callback;
        return this;

    };

    this.offline = function(callback){

        if(typeof callback!="function")
            throw new Error("Wrong parameters");

        offlineCallback = callback;
        return this;

    };

    this.interrupted = function(callback){

        if(typeof callback!="function")
            throw new Error("Wrong parameters");

        interruptedCallback = callback;
        return this;

    };

    this.overrequest = function(callback){

        if(typeof callback!="function")
            throw new Error("Wrong parameters");

        overequestCallback = callback;
        return this;

    };

    this.catch = function(callback){

        if(typeof callback!="function")
            throw new Error("Wrong parameters");

        catchCallback = callback;
        return this;

    };

    this.getRequest = function(){

        return request;

    };

};


/**
 * -----------
 * Public API
 * -----------
 */

Hi.rootTemplate = function(properties){

    Hi.$ui.js.root = properties;

};

//Regista o controlador do template
Hi.template = function(master){

    jQuery.extend(master,Hi.$ui.js.root);
    Hi.$ui.js.root=master;

};

Hi.redirect = Hi.$nav.navigateTo;
Hi.ajaxify = Hi.$nav.navigateTo;

//Cria um closure javascript para uma determinada view
Hi.view = function(controller){

    //Obtem informacao sobre o controller que esta sendo registrado
    var viewPath = Hi.$nav.nextControllerInfo;

    //Resolved route
    var route = Hi.$nav.resolveRoute(viewPath);

    //Rota correspondente a esta view
    controller.$route = route;

    //Regista o controller
    Hi.$ui.js.setViewController(viewPath.controller,viewPath.action,controller);

};


Hi.setLanguage = function(dictionary){

    Hi.$ui.js.lang.dict = dictionary;

};



String.prototype.startsWith = function(str){
    return this.indexOf(str)===0;
};


Array.prototype.removeVal = function(el){
    var array = this;
    var index = array.indexOf(el);
    if (index > -1) {
        array.splice(index, 1);
    }
};


window.onpopstate = function(param){

    var the_base_url = App.base_url;//The base URL

    var the_requested_url = window.location.toString(); //The requested URL

    //The base URL is the same
    if(the_requested_url.startsWith(the_base_url)){

        if(param.state){

            var destination = the_requested_url.replace(App.base_url,"");
            console.log("sate found for <"+destination+">");
            Hi.$nav.routeBack(destination);

        }else{

            console.log("state not found for <"+the_requested_url+">");

        }

    }else{

        console.log("base url is different  : "+the_requested_url);

    }

};


function __t(string,key){

    if(key){

        return Hi.$ui.js.lang.getItem(key,string);

    }else{

        return Hi.$ui.js.lang.getItem(string);

    }

}


/**
 * INITIALIZATIONS
 */


var $fiis = {};

/*Hi Framework static file ends here*/
