package mz.co.hi.web.users;

import mz.co.hi.web.config.AppConfigurations;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Junior.
 */
public class Sessions {

    private static final String U_DETAILS = "user_details";


    public static Map getUserDetails(String remoteUser){


        if(AppConfigurations.get().getUserDetailsProvider()!=null){


            return AppConfigurations.get().getUserDetailsProvider().getDetails(remoteUser);


        }

        return new HashMap<>();

    }


}
