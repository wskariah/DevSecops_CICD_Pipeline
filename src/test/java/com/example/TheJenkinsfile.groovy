package com.example

import com.lesfurets.jenkins.unit.declarative.DeclarativePipelineTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TheJenkinsfile extends DeclarativePipelineTest {

    @Test
    void runs() {
        runScript("Jenkinsfile")
        assertJobStatusSuccess()
    }

    @BeforeEach
    @Override
    void setUp() {
        super.setUp()
        binding.master = "master"
        binding.ARTIFACTORY_USERNAME = "user"
        binding.ARTIFACTORY_PASSWORD = "password"

        stubPublishHTML()
        stubSh()
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