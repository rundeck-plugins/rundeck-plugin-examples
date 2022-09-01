package com.rundeck.plugin.example

import com.dtolabs.rundeck.core.common.INodeEntry
import com.dtolabs.rundeck.core.execution.ExecutionContext
import com.dtolabs.rundeck.core.execution.ExecutionListener
import com.dtolabs.rundeck.core.execution.workflow.SharedOutputContext
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepException
import com.dtolabs.rundeck.core.storage.ResourceMeta
import com.dtolabs.rundeck.core.storage.keys.KeyStorageTree
import com.dtolabs.rundeck.plugins.PluginLogger
import com.dtolabs.rundeck.plugins.step.PluginStepContext
import com.rundeck.plugin.example.util.ExampleApis
import org.rundeck.storage.api.PathUtil
import org.rundeck.storage.api.Resource
import org.rundeck.storage.api.StorageException
import com.dtolabs.rundeck.core.storage.StorageTree
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
class ExampleNodeStepPluginSpec extends Specification {
    def "When api key at specified path does not exist"() {
        given: "An instance of ExampleNodeStepPlugin with mocked Rundeck classes"
        def ens = new ExampleNodeStepPlugin()
        final String userBaseApiUrl  = "http://test.local:4440"
        final Integer userApiVersion  = 41
        final String hiddenTestValue = 'hidden test value'
        final String fakeKeyPath     = 'keys/fakeApiKeyPath'

        ens.userBaseApiUrl = userBaseApiUrl
        ens.userApiVersion = userApiVersion
        ens.apiKeyPath = fakeKeyPath
        ens.hiddenTestValue = hiddenTestValue

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
        def entry = Mock(INodeEntry) {
            getNodename() >> "node1"
        }
        when: "The node step plugin is executed"
        ens.executeNodeStep(context, configuration, entry)

        then: "The key should not exist at the path, and return the correct exception class"
        NodeStepException e = thrown()
        e.getMessage() == "Error accessing ${fakeKeyPath}: no resource for path"
    }

    def "Successfully run the executeNodeStep() method"(String nodeName, String nodePayload) {
        given: "An instance of ExampleNodeStepPlugin with mocked Rundeck classes"
        ExampleNodeStepPlugin ens     = new ExampleNodeStepPlugin()
        final String projectName      = 'test project'
        final String userBaseApiUrl   = "http://test.local:4440"
        final Integer userApiVersion  = 41
        final String apiKeyPath       = 'keys/apiKeyPath'
        final String realApiKey       = 'abc1234def5678'
        final String hiddenTestValue  = 'hidden test value'

        ExampleApis exapis = Mock(ExampleApis) {
            getResourceInfoByName(projectName, nodeName) >> nodePayload
        }

        ens.userBaseApiUrl = userBaseApiUrl
        ens.userApiVersion = userApiVersion
        ens.apiKeyPath = apiKeyPath
        ens.hiddenTestValue = hiddenTestValue
        ens.exapis = exapis

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
        def entry = Mock(INodeEntry) {
            getNodename() >> nodeName
        }

        when: "The node step plugin is executed"
        ens.executeNodeStep(context, configuration, entry)

        then: "The API calls for the given notes should return the correct payload"
        ens.exapis.getResourceInfoByName(projectName, nodeName) == nodePayload

        where: "There are two nodes and response payloads"
        nodeName | nodePayload
        'node1'  | '{\"name\":\"node1\",\"hostname\":\"http://test.node1.local\"}'
        'node2'  | '{\"name\":\"node2\",\"hostname\":\"http://test.node2.local\"}'
    }
}