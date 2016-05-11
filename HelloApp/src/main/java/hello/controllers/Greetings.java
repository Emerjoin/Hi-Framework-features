package hello.controllers;

import mz.co.hi.web.mvc.Controller;
import mz.co.hi.web.mvc.MvcException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class Greetings extends Controller {


    public void hi(Map data) throws MvcException{

        this.callView(data);

    }

}
