package org.farwind.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        Result<String, TestException1> res = Ok.of("this is okay");
        assertTrue(res.isOk());
    }

    @Test
    public void isErr() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertFalse(res.isErr());
    }

    @Test
    public void ok() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals(Optional.of("this is okay"), res.ok());
    }

    @Test
    public void err() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals(Optional.empty(), res.err());
    }

    @Test
    public void map() throws Exception {
        Result<String, TestException1> resString = Ok.of("this is okay");
        Result<Integer, TestException1> resInt = resString.map(str->5);
        assertEquals(Ok.of(5), resInt);
    }

    @Test
    public void mapErr() throws Exception {
        Result<String, TestException1> resErr1 = Ok.of("this is okay");
        Result<String, TestException2> resErr2 = resErr1.mapErr(TestException2::new);
        assertEquals(resErr1, resErr2);
    }

    @Test
    public void stream() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        List<String> items = res.stream().collect(Collectors.toList());
        assertEquals(1, items.size());
        assertEquals("this is okay", items.get(0));
    }

    @Test
    public void and() throws Exception {
        Result<String, TestException1> res1 = Ok.of("this is ok");
        Result<Integer, TestException1> res2 = Ok.of(5);
        assertEquals(res2, res1.and(res2));

        Result<String, TestException1> res3 = Ok.of("this is ok");
        Result<Integer, TestException1> res4 = Err.of(new TestException1());
        assertEquals(res4, res3.and(res4));
    }

    @Test
    public void andThen() throws Exception {
        Result<String, TestException1> res1 = Ok.of("this is okay");
        assertEquals(Ok.of(12), res1.andThen(str->Ok.of(str.length())));

        final Result<String, TestException1> res2 = Err.of(new TestException1());
        assertEquals(res2, res1.andThen(str->res2));
    }

    @Test
    public void or() throws Exception {
        Result<String, TestException1> res1 = Ok.of("this is ok");
        Result<String, TestException1> res2 = Ok.of("this is not okay");
        assertEquals(res1, res1.or(res2));

        Result<String, TestException1> res3 = Ok.of("this is ok");
        Result<String, TestException2> res4 = Err.of(new TestException2());
        assertEquals(res3, res3.or(res4));
    }

    @Test
    public void orElse() throws Exception {
        Result<String, TestException1> res1 = Ok.of("this is ok");
        assertEquals(res1, res1.orElse(e->Err.of(new TestException2(e))));
    }

    @Test
    public void unwrapOr() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals("this is okay", res.unwrapOr("this would be wrong"));
    }

    @Test
    public void unwrapOrElse() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals("this is okay", res.unwrapOrElse(TestException1::getMessage));
    }

    @Test
    public void unwrapOrThrow() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals("this is okay", res.unwrapOrThrow());
    }

    @Test
    public void unwrap() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals("this is okay", res.unwrap());
    }

    @Test
    public void expect() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals("this is okay", res.expect("this should never happen"));
    }

    @Test
    public void unwrapErr() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertThrows(NoSuchElementException.class, res::unwrapErr);
    }

    @Test
    public void equals() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        Result<Integer, TestException1> res2 = Ok.of(5);
        assertNotEquals(res, res2);

        Result<String, TestException1> res3 = Ok.of("this is okay");
        Result<String, TestException1> res4 = Ok.of("this is okay");
        assertEquals(res3, res4);

        Result<String, TestException1> res5 = Ok.of("this is okay");
        Result<String, TestException2> res6 = Ok.of("this is okay");
        assertEquals(res5, res6);

        Result<String, TestException1> res7 = Ok.of("this is okay");
        Result<String, TestException2> res8 = Err.of(new TestException2());
        assertNotEquals(res7, res8);

        Result<String, TestException1> res9 = Ok.of("this is okay");
        Result<String, TestException1> res10 = Err.of(new TestException1());
        assertNotEquals(res9, res10);
    }

    @Test
    public void hash() throws Exception {
        Result<String, TestException1> res = Ok.of("this is okay");
        assertEquals(Objects.hash("this is okay"), res.hashCode());
    }
}
