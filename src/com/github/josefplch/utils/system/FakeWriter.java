package com.github.josefplch.utils.system;

import java.io.IOException;
import java.io.Writer;

/**
 * It just does nothing.
 * 
 * @author  Josef Plch
 * @since   2019-03-29
 * @version 2019-03-29
 */
public class FakeWriter extends Writer {
    @Override
    public void write (char [] cbuf, int off, int len) throws IOException {
    }

    @Override
    public void flush () throws IOException {
    }

    @Override
    public void close () throws IOException {
    }
}
