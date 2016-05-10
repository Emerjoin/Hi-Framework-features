
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

    var promisse = new FrontierPromisse();

    var $req = $.ajax({

        method:"POST",
        url : $functionUrl,
        headers : $headers,
        data: JSON.stringify(params),
        dataType:"json",

        success : function(data){

            promisse.onReturn(data.result);

        },

        error : function(jqXml, errText,httpError){

            //Request aborted
            if(errText=="abort"){

                return;

            }else if(errText=="timeout"){

                //TODO: Handle the timeout

            }

            promisse.onCatch(err);

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