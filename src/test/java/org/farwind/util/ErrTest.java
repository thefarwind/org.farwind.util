package org.farwind.util;

import org.junit.Test;
import org.junit.runner.*;

import static org.junit.Assert.*;

import org.farwind.util.Result.Ok;
import org.farwind.util.Result.Err;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Test class for the {@link Err} {@link Result} variant.
 */
public class ErrTest {

    private class TestException1 extends Exception {
        private TestException1() {
            super();
        }
    }

    private class TestException2 extends Exception {
        private TestException2() {
            super();
        }
    }

    @Test
    public void isOk() throws Exception {
        Result<Integer, TestException1> res = Err.of(new TestException1());
        assertFalse(res.isOk());
    }

    @Test
    public void isErr() throws Exception {
        Result<Integer, TestException1> res = Err.of(new TestException1());
        assertTrue(res.isErr());
    }

    @Test
    public void ok() throws Exception {
        Result<Integer, TestException1> res = Err.of(new TestException1());
        assertEquals(Optional.empty(), res.ok());
    }

    @Test
    public void err() throws Exception {
        TestException1 e = new TestException1();
        Result<Integer, TestException1> res = Err.of(e);
        assertEquals(Optional.of(e), res.err());
    }

    @Test
    public void map() throws Exception {
        TestException1 e = new TestException1();
        Result<Integer, TestException1> res = Err.of(e);
        assertEquals(Err.of(e), res.map(Object::toString));
    }

    @Test
    public void mapErr() throws Exception {
        TestException1 e1 = new TestException1();
        TestException2 e2 = new TestException2();
        Result<Integer, TestException1> res = Err.of(e1);
        assertEquals(Err.of(e2), res.mapErr(e->e2));
    }

    @Test
    public void stream() throws Exception {
        Result<String, TestException1> res = Err.of(new TestException1());
        List<String> items = res.stream().collect(Collectors.toList());
        assertEquals(0, items.size());
    }

    @Test
    public void and() throws Exception {
        Result<String, TestException1> res = Err.of(new TestException1());
        assertEquals(res, res.and(Ok.of(5)));
        assertEquals(res, res.and(Err.of(new TestException1())));
    }

    @Test
    public void andThen() throws Exception {
        Result<String, TestException1> res = Err.of(new TestException1());
        assertEquals(res, res.andThen(str->Ok.of(str.length())));
    }

    @Test
    public void or() throws Exception {
        Result<String, TestException1> res = Err.of(new TestException1());
        Result<String, TestException2> res2 = Err.of(new TestException2());
        assertEquals(res2, res.or(res2));
    }

    @Test
    public void orElse() throws Exception {
        TestException1 e1 = new TestException1();
        TestException2 e2 = new TestException2();
        Result<String, TestException1> res1 = Err.of(e1);
        assertEquals(Err.of(e2), res1.orElse(e->Err.of(e2)));
    }

    @Test
    public void unwrapOr() throws Exception {
        assertEquals(5, Err.of(new TestException1()).unwrapOr(5));
    }

    @Test
    public void unwrapOrElse() throws Exception {
        assertEquals(TestException1.class,
                Err.of(new TestException1()).unwrapOrElse(Object::getClass));
    }

    @Test(expected = TestException1.class)
    public void unwrapOrThrow() throws Exception {
        Err.of(new TestException1()).unwrapOrThrow();
    }

    @Test(expected = NoSuchElementException.class)
    public void unwrap() throws Exception {
        Err.of(new TestException1()).unwrap();
    }

    @Test(expected = NoSuchElementException.class)
    public void expect() throws Exception {
        try {
            Err.of(new TestException1()).expect("this is the message");
        } catch (Exception e){
            assertEquals(e.getMessage(), "this is the message");
            throw e;
        }
        fail();
    }

    @Test
    public void unwrapErr() throws Exception {
        TestException1 e = new TestException1();
        assertEquals(e, Err.of(e).unwrapErr());
    }

    @Test
    public void equals() throws Exception {
        TestException1 e1 = new TestException1();
        TestException2 e2 = new TestException2();

        Result<String, TestException1> res = Err.of(e1);
        Result<String, TestException2> res2 = Err.of(e2);
        assertNotEquals(res, res2);

        Result<String, TestException1> res3 = Err.of(e1);
        Result<String, TestException1> res4 = Err.of(e1);
        assertEquals(res3, res4);

        Result<String, TestException1> res5 = Err.of(e1);
        Result<Integer, TestException1> res6 = Err.of(e1);
        assertEquals(res5, res6);

        Result<String, TestException2> res7 = Err.of(new TestException2());
        Result<String, TestException1> res8 = Ok.of("this is okay");
        assertNotEquals(res7, res8);

        Result<String, TestException1> res9 = Err.of(new TestException1());
        Result<String, TestException1> res10 = Ok.of("this is okay");
        assertNotEquals(res9, res10);
    }
}
