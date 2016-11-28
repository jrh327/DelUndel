/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Jon Hopkins
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package test;

public abstract class Test {
	public abstract void runTests();
	public abstract void before();
	public abstract void after();
	
	public void assertTrue(String message, boolean expression) {
		if (!expression) {
			if (message == null) {
				throw new AssertionError();
			}
			throw new AssertionError(message);
		}
	}
	
	public void assertTrue(boolean expression) {
		assertTrue(null, expression);
	}
	
	public void assertFalse(String message, boolean expression) {
		if (expression) {
			if (message == null) {
				throw new AssertionError();
			}
			throw new AssertionError(message);
		}
	}
	
	public void assertFalse(boolean expression) {
		assertFalse(null, expression);
	}
	
	public void assertEquals(String message, Object obj1, Object obj2) {
		if (!obj1.equals(obj2)) {
			if (message == null) {
				throw new AssertionError();
			}
			throw new AssertionError(message);
		}
	}
	
	public void assertEquals(Object obj1, Object obj2) {
		assertEquals(null, obj1, obj2);
	}
	
	public void assertNotEquals(String message, Object obj1, Object obj2) {
		if (obj1.equals(obj2)) {
			if (message == null) {
				throw new AssertionError();
			}
			throw new AssertionError(message);
		}
	}
	
	public void assertNotEquals(Object obj1, Object obj2) {
		assertNotEquals(null, obj1, obj2);
	}
}
