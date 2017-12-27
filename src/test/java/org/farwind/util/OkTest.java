package org.farwind.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.*;

import org.farwind.util.Result.Ok;
import org.farwind.util.Result.Err;

/**
 * Test class for the {@link Ok} {@link Result} variant.
 */
public class OkTest {

    private class TestException1 extends Exception {
        private TestException1() {
            super();
        }
    }

    private class TestException2 extends Exception {
        private TestException2() {
            super();
        }

        private TestException2(Throwable err){
            super(err);
        }
    }

    @Test
    public void isOk() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertTrue(res.isOk());
    }

    @Test
    public void isErr() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertFalse(res.isErr());
    }

    @Test
    public void ok() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertEquals(Optional.of("this is okay"), res.ok());
    }

    @Test
    public void err() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertEquals(Optional.empty(), res.err());
    }

    @Test
    public void map() throws Exception {
        Result<String, TestException1> resString = new Ok<>("this is okay");
        Result<Integer, TestException1> resInt = resString.map(str->5);
        assertEquals(new Ok<>(5), resInt);
    }

    @Test
    public void mapErr() throws Exception {
        Result<String, TestException1> resErr1 = new Ok<>("this is okay");
        Result<String, TestException2> resErr2 = resErr1.mapErr(TestException2::new);
        assertEquals(resErr1, resErr2);
    }

    @Test
    public void stream() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        List<String> items = res.stream().collect(Collectors.toList());
        assertEquals(1, items.size());
        assertEquals("this is okay", items.get(0));
    }

    @Test
    public void and() throws Exception {
        Result<String, TestException1> res1 = new Ok<>("this is ok");
        Result<Integer, TestException1> res2 = new Ok<>(5);
        assertEquals(res2, res1.and(res2));

        Result<String, TestException1> res3 = new Ok<>("this is ok");
        Result<Integer, TestException1> res4 = new Err<>(new TestException1());
        assertEquals(res4, res3.and(res4));
    }

    @Test
    public void andThen() throws Exception {
        Result<String, TestException1> res1 = new Ok<>("this is okay");
        assertEquals(new Ok<>(12), res1.andThen(str->new Ok<>(str.length())));

        final Result<String, TestException1> res2 = new Err<>(new TestException1());
        assertEquals(res2, res1.andThen(str->res2));
    }

    @Test
    public void or() throws Exception {
        Result<String, TestException1> res1 = new Ok<>("this is ok");
        Result<String, TestException1> res2 = new Ok<>("this is not okay");
        assertEquals(res1, res1.or(res2));

        Result<String, TestException1> res3 = new Ok<>("this is ok");
        Result<String, TestException2> res4 = new Err<>(new TestException2());
        assertEquals(res3, res3.or(res4));
    }

    @Test
    public void orElse() throws Exception {
        Result<String, TestException1> res1 = new Ok<>("this is ok");
        assertEquals(res1, res1.orElse(e->new Err<>(new TestException2(e))));
    }

    @Test
    public void unwrapOr() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertEquals("this is okay", res.unwrapOr("this would be wrong"));
    }

    @Test
    public void unwrapOrElse() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertEquals("this is okay", res.unwrapOrElse(TestException1::getMessage));
    }

    @Test
    public void unwrap() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertEquals("this is okay", res.unwrap());
    }

    @Test
    public void expect() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertEquals("this is okay", res.expect("this should never happen"));
    }

    @Test(expected = NoSuchElementException.class)
    public void unwrapErr() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        assertEquals(null, res.unwrapErr());
    }

    @Test
    public void equals() throws Exception {
        Result<String, TestException1> res = new Ok<>("this is okay");
        Result<Integer, TestException1> res2 = new Ok<>(5);
        assertNotEquals(res, res2);

        Result<String, TestException1> res3 = new Ok<>("this is okay");
        Result<String, TestException1> res4 = new Ok<>("this is okay");
        assertEquals(res3, res4);

        Result<String, TestException1> res5 = new Ok<>("this is okay");
        Result<String, TestException2> res6 = new Ok<>("this is okay");
        assertEquals(res5, res6);

        Result<String, TestException1> res7 = new Ok<>("this is okay");
        Result<String, TestException2> res8 = new Err<>(new TestException2());
        assertNotEquals(res7, res8);

        Result<String, TestException1> res9 = new Ok<>("this is okay");
        Result<String, TestException1> res10 = new Err<>(new TestException1());
        assertNotEquals(res9, res10);
    }
}
