package test.com.web.yayee.validation;


import com.web.yayee.validation.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.web.yayee.validation.Assert.*;

public class AssertTest {


    @Test
    public void testAssertEnum1(){

        try {


            assertTrue(enumeration(MyEnum.class, "HELL", "EARTH"));

        }catch (Exception ex){

            fail();

        }

    }


    @Test(expected = ValidationException.class)
    public void testAssertEnum2() throws ValidationException{


        assertFalse(enumeration(MyEnum.class, "HELL", "EARTHX"));



    }

    @Test
    public void testAssertInt1(){

        try {

            assertTrue(range(0,10,1));

        } catch (ValidationException e) {

            fail();

        }

    }

    @Test(expected = ValidationException.class)
    public void testAssertInt2() throws ValidationException {


        assertTrue(range(0,10,-1));


    }

    @Test
    public void testAssertLong1(){

        try {

            assertTrue(range(0l,10l,1l));

        } catch (ValidationException e) {

            fail();

        }

    }

    @Test(expected = ValidationException.class)
    public void testAssertLong2() throws ValidationException {


        assertTrue(range(0l,10l,-1l));


    }


    @Test
    public void testAssertFloat1(){

        try {

            assertTrue(range(0f,10f,1f));

        } catch (ValidationException e) {

            fail();

        }

    }

    @Test(expected = ValidationException.class)
    public void testAssertFloat2() throws ValidationException {


        assertTrue(range(0f,10f,-1f));


    }

    @Test
    public void testAssertDouble1(){

        try {

            assertTrue(range(0d,10d,1d));

        } catch (ValidationException e) {

            fail();

        }

    }

    @Test(expected = ValidationException.class)
    public void testAssertDouble2() throws ValidationException {


        assertTrue(range(0d,10d,-1d));


    }

    @Test
    public void testNotNull1(){


        try {
            assertTrue(notNull("Hey"));
        } catch (ValidationException e) {
            fail();
        }

    }

    @Test(expected = ValidationException.class)
    public void testNotNull2() throws ValidationException {


       assertTrue(notNull(null));


    }



}
