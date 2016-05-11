Hi.view({


    goAgain : function(){

        Hi.redirect("greetings/hi?name=Someone");

    },

    postLoad: function(){

        __.username="Hello kid";

    }

});