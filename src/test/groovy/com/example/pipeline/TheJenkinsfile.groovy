package com.example.pipeline

import com.example.tools.StandardOutput
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
        Approvals.verify(_output.toString())
    }

    @BeforeEach
    @Override
    void setUp() {
        super.setUp()
        arrangeGlobalProperties()
        stubPublishHTML()
        stubSh()
        _output.capture()
    }

    @AfterEach
    void tearDown() {
        _output.restore()
    }

    StandardOutput _output = new StandardOutput()

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

