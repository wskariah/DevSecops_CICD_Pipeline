package com.example.tools

class StandardOutput {
    StringWriter _output;
    PrintStream _originalOut;

    void capture() {
        _originalOut = System.out
        _output = new StringWriter()
        PrintWriter printWriter = new PrintWriter(_output)
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            void write(int b) throws IOException {
                printWriter.write(b)
            }
        }))
    }

    void restore() {
        System.setOut(_originalOut)
    }
    
    @Override
    String toString() {
        return _output.toString()
    }
}