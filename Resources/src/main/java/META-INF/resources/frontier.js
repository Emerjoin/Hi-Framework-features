
    if($fiis.hasOwnProperty(_$fmut)){

        if(_$si){

            if(_$si_method){

                if(_$abpon){

                    $fiis[_$fmut].abort();

                }else
                    return;


            }else{


                if(JSON.stringify(params)==$fiis[_$fmut].$params){

                    if(_$abpon){

                        $fiis[_$fmut].abort();

                    }else
                        return;



                }

            }

        }

    }

    var promisse = new Hi.$features.frontiers.Promisse();

    var $req = $.ajax({

        method:"POST",
        url : $functionUrl,
        headers : $headers,
        data: JSON.stringify(params),
        dataType:"json",

        success : function(data){

            if(data.hasOwnProperty("$invoke")){

                var cmdInvocations = data["$invoke"];
                for(var cmdName in cmdInvocations){

                    var cmdParams = cmdInvocations[cmdName];
                    Hi.$ui.js.commands.run(cmdName,cmdParams);


                }

            }

            if(data.hasOwnProperty("$root")){

                if(typeof __!="undefined"){

                    for(var key in data.$root){

                        __[key] = data.$root[key];

                    }

                    __.$apply();

                }

            }

            promisse.onReturn(data.result);

        },

        error : function(jqXml, errText,httpError){

            //Request aborted
            if(errText=="abort"){

                //TODO: Handle the abort: do not invoke the always callback
                return;

            }else if(errText=="timeout"){

                //TODO: Handle the timeout
                //TODO: Issue an http error: 3006 (Timeout)
                httpError = 3006;

            }

            promisse.onCatch(httpError);

        },

        complete: function(){

            delete $fiis[_$fmut];
            promisse.onAlways();


        },
        statusCode : {



            500: function () {

                promisse.onCatch(500);

            },

            403: function(){

                promisse.onCatch(403);
                //TODO: Call the global handler for access denied

            }

        }

        //,timeout: 0 TODO: Set the timeout according to Maximum expected call duration

    });
    $req.$params = JSON.stringify(params);

    $fiis[_$fmut] = $req;

    return promisse;