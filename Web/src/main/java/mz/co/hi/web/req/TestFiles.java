package mz.co.hi.web.req;

import mz.co.hi.web.Helper;
import mz.co.hi.web.HiException;
import mz.co.hi.web.RequestContext;
import mz.co.hi.web.config.AppConfigurations;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Mario Junior.
 */
@HandleRequests(regexp = "^\\$test\\/[A-Za-z0-9_\\/.-]+[.js]")
@ApplicationScoped
public class TestFiles extends ReqHandler {

    @Override
    public boolean handle(RequestContext requestContext) throws ServletException, IOException {

        String testToken = "$test/";
        String route = requestContext.getRouteUrl();
        int tokenIndex = route.indexOf(testToken);
        String testFilePath = route.substring(tokenIndex + testToken.length(), route.length());

        if (!AppConfigurations.get().getTestFiles().containsKey(testFilePath))
            return false;


        URL resourceURL = requestContext.getServletContext().getResource("/" + testFilePath);
        if (resourceURL == null)
            return false;


        InputStream fileStream = resourceURL.openStream();
        requestContext.getResponse().setHeader("Content-Type", "text/javascript");
        requestContext.readToEnd(fileStream);


        return true;

    }

}
