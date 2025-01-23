package com.example

import com.lesfurets.jenkins.unit.declarative.DeclarativePipelineTest
import org.approvaltests.Approvals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TheJenkinsfile extends DeclarativePipelineTest {

    @Test
    void runs() {
        runScript("Jenkinsfile")
        assertJobStatusSuccess()
        String capturedOutput = _output.toString()
        Approvals.verify(capturedOutput)
    }

    @BeforeEach
    @Override
    void setUp() {
        super.setUp()
        arrangeGlobalProperties()
        stubPublishHTML()
        stubSh()
        captureStdOutput()
    }

    @AfterEach
    void tearDown() {
        restoreStdOutput()
    }

    private restoreStdOutput() {
        System.setOut(_originalOut)
    }

    private void captureStdOutput() {
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

    StringWriter _output;
    PrintStream _originalOut;

    private void arrangeGlobalProperties() {
        binding.master = "master"
        binding.ARTIFACTORY_USERNAME = "user"
        binding.ARTIFACTORY_PASSWORD = "password"
    }

    private stubSh() {
        helper.registerAllowedMethod("sh", [String.class], { String cmd ->
            println "sh: ${cmd}"
            return 0
        })
    }

    private stubPublishHTML() {
        helper.registerAllowedMethod("publishHTML", [Map.class], { Map args ->
            println "publishHTML: ${args}"
        })
    }

}