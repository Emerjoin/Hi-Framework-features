package mz.co.hi.web.frontier;

import javax.servlet.http.Part;
import java.io.File;

/**
 * @author Mário Júnior
 */
public class FileUpload {

    private Part part;

    public FileUpload(Part part){

        this.part = part;

    }

    public File saveAs(File file){


        return null;

    }

    public File saveAs(String path){

        return null;
    }

    public String getUploadFileName(){

        return part.getSubmittedFileName();

    }

}
