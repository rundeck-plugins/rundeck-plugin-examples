package com.rundeck.plugin.example

import com.dtolabs.rundeck.core.execution.ExecutionContext
import com.dtolabs.rundeck.core.execution.ExecutionListener
import com.dtolabs.rundeck.core.execution.workflow.SharedOutputContext
import com.dtolabs.rundeck.core.storage.ResourceMeta
import com.dtolabs.rundeck.core.storage.StorageTree
import com.dtolabs.rundeck.core.storage.keys.KeyStorageTree
import com.dtolabs.rundeck.plugins.PluginLogger
import com.dtolabs.rundeck.plugins.step.PluginStepContext
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException
import com.rundeck.plugin.example.util.ExampleApis
import org.rundeck.storage.api.PathUtil
import org.rundeck.storage.api.Resource
import org.rundeck.storage.api.StorageException
import spock.lang.Specification

/**
 * Tests should:
 *    * Use the Spock framework
 *    * Be named and stored correspondingly to its class
 *    *    * E.g. src/main/groovy and src/test/groovy, ExampleClass and ExampleClassSpec
 *    * Use mocking/stubbing/spies as necessary to abstract away Rundeck classes for unit tests.
 *    * Exist for each plugin and test at a minimum that:
 *    *    * Credentials must be set
 *    *    * Keys can be pulled from key storage
 *    *    * executeStep()/executeNodeStep() function can successfully execute
 */
class ExampleWorkflowStepPluginSpec extends Specification {
    def "When api key at specified path does not exist"() {
        given: "An instance of ExampleWorkflowStepPlugin with mocked Rundeck classes"
        def ews = new ExampleWorkflowStepPlugin()
        final String userBaseApiUrl  = "http://test.local:4440"
        final Integer userApiVersion  = 41
        final String hiddenTestValue = 'hidden test value'
        final String fakeKeyPath     = 'keys/fakeApiKeyPath'

        ews.userBaseApiUrl = userBaseApiUrl
        ews.userApiVersion = userApiVersion
        ews.apiKeyPath = fakeKeyPath
        ews.hiddenTestValue = hiddenTestValue

        def configuration = [
            'userBaseApiUrl': userBaseApiUrl,
            'userApiVersion': userApiVersion,
            'apiKeyPath': fakeKeyPath,
            'hiddenTestValue': hiddenTestValue
        ]

        def storageTree = Mock(StorageTree)
        storageTree.getResource(fakeKeyPath) >> {
            throw StorageException.readException(PathUtil.asPath(fakeKeyPath), "no resource for path")
        }

        def executionListener = Mock(ExecutionListener)
        def executionContext = Mock(ExecutionContext) {
            getExecutionListener() >> executionListener
            getStorageTree() >> storageTree
        }
        def context = Mock(PluginStepContext) {
            getExecutionContext() >> executionContext
            getLogger() >> Mock(PluginLogger)
        }

        when: "The workflow step plugin is executed"
        ews.executeStep(context, configuration)

        then: "The key should not exist at the path, and return the correct exception class"
        StepException e = thrown()
        e.message == "Error accessing ${fakeKeyPath}: no resource for path"
    }

    def "Successfully run the executeStep() method"() {
        given: "An instance of ExampleWorkflowStepPlugin with mocked Rundeck classes"
        ExampleWorkflowStepPlugin ews = new ExampleWorkflowStepPlugin()
        final String projectName      = 'test project'
        final String userBaseApiUrl   = "http://test.local:4440"
        final Integer userApiVersion  = 41
        final String apiKeyPath       = 'keys/apiKeyPath'
        final String realApiKey       = 'abc1234def5678'
        final String hiddenTestValue  = 'hidden test value'
        final String projInfoPayload  = '{\"name\":\"test project\"}'

        ExampleApis exapis = Mock(ExampleApis) {
            getProjectInfoByName(projectName) >> projInfoPayload
        }

        ews.userBaseApiUrl = userBaseApiUrl
        ews.userApiVersion = userApiVersion
        ews.apiKeyPath = apiKeyPath
        ews.hiddenTestValue = hiddenTestValue
        ews.exapis = exapis

        def configuration = [
            'userBaseApiUrl': userBaseApiUrl,
            'userApiVersion': userApiVersion,
            'apiKeyPath': apiKeyPath,
            'hiddenTestValue': hiddenTestValue
        ]

        ByteArrayInputStream passInputStream = new ByteArrayInputStream(realApiKey.getBytes())
        ResourceMeta passwordContents = Mock(ResourceMeta) {
            getInputStream() >> passInputStream
        }
        Resource treeContents = Mock(Resource) {
            getContents() >> passwordContents
        }
        KeyStorageTree storageTree = Mock(KeyStorageTree) {
            getResource(apiKeyPath) >> treeContents
        }
        ExecutionListener executionListener = Mock(ExecutionListener)
        SharedOutputContext shc = Mock(SharedOutputContext) {
            addOutput() >> null
        }
        ExecutionContext executionContext = Mock(ExecutionContext) {
            getExecutionListener() >> executionListener
            getStorageTree() >> storageTree
            getOutputContext() >> shc
        }
        PluginStepContext context = Mock(PluginStepContext) {
            getExecutionContext() >> executionContext
            getLogger() >> Mock(PluginLogger)
        }

        when: "The workflow step plugin is executed"
        ews.executeStep(context, configuration)

        then: "The API call for the given project should return the correct payload"
        ews.exapis.getProjectInfoByName(projectName) == projInfoPayload
    }
}