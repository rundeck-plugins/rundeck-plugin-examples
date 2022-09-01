package com.rundeck.plugin.example

/**
 * Dependencies:
 * any Java SDK must be officially recognized by the vendor for that technology
 * (e.g. AWS Java SDK, SumoLogic, Zendesk) and show reasonably recent development.  Any SDK used must have an
 * open source license such as Apache-2 or MIT.
 */

import com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants
import com.dtolabs.rundeck.plugins.step.StepPlugin
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException
import com.dtolabs.rundeck.core.plugins.Plugin
import com.dtolabs.rundeck.plugins.ServiceNameConstants
import com.dtolabs.rundeck.plugins.step.PluginStepContext
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty
import com.dtolabs.rundeck.plugins.descriptions.RenderingOption
import com.dtolabs.rundeck.plugins.descriptions.RenderingOptions
import groovy.json.JsonOutput
import org.rundeck.storage.api.StorageException

/**
 * If other functions are required for purposes of modularity or clarity, they should either be added to a Util Class
 * (if generic enough), or a PluginHelper Class that is accessible to the Plugin Class.
 */
import com.rundeck.plugin.example.util.ExampleApis
import com.rundeck.plugin.example.util.ExampleConstants
import com.rundeck.plugin.example.util.ExampleUtil

import static com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants.GROUPING
import static com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants.GROUP_NAME

/**
 * ExampleStepPlugin demonstrates a basic {@link com.dtolabs.rundeck.plugins.step.StepPlugin}, it extends the {@link
 * BaseStepPlugin}, and demonstrates how to programmatically build all of the plugin's Properties exposed in the Rundeck
 * GUI.
 * <p/>
 * The plugin class is annotated with {@link Plugin} to define the service and name of this service provider plugin.
 * <p/>
 * The provider name of this plugin statically defined in the class. The service name makes use of {@link
 * ServiceNameConstants} to provide the known Rundeck service names.
 *
 * @author Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
 */
@Plugin(name = PLUGIN_NAME, service = ServiceNameConstants.WorkflowStep)
@PluginDescription(title = PLUGIN_TITLE, description = PLUGIN_DESCRIPTION)
class ExampleWorkflowStepPlugin implements StepPlugin {
    /**
     * Define a name used to identify your plugin. It is a good idea to use a fully qualified package-style name.
     */
    public static final String PLUGIN_NAME = "example-workflow-step-plugin"
    public static final String PLUGIN_TITLE = "Example / Workflow Step"
    public static final String PLUGIN_DESCRIPTION = "EXAMPLE WORKFLOW STEP: Make a single call to the Rundeck API and retrieve info about projects."

    /** Sets up the logging and meta objects for use during execution.
     * log  - We'll add objects to it as the step executes, and then print them and clear the log
     *        for its next use
     * meta - Holds any metadata for use when the log is printed. Usually will just contain the
     *        content type of the log data ("application/json")
     */
    List log = new ArrayList()
    Map<String, String> meta = Collections.singletonMap("content-data-type", "application/json")
    ExampleApis exapis

    /**
     * Plugin Properties must:
     *     * be laid out at the top of the Plugin class, just after any class/instance variables.
     *     * be intuitive for the user to understand, and inform the end-user what is expected for that field.
     *     * use renderingOptions to
     *     * use KeyStorage for storage/retrieval of secrets. See 'Rundeck API Key Path' property below.
     */
    @PluginProperty(
        title = "Rundeck API URL",
        description = """Provide the base URL for this Rundeck instance. It will be used by the plugin to get information \
from the API. If left blank, the call will use a default base API URL.\n\n
When carriage returns are used in the description, any part of the string after them—such as this—will also be collapsed. \
**Markdown** can also be used in this _expanded_ block.""",
        defaultValue = ExampleConstants.BASE_API_URL,
        required = false
    )
    @RenderingOptions(
        [
            @RenderingOption(key = GROUP_NAME, value = "API Configuration")
        ]
    )
    String userBaseApiUrl

    /**
     * Here, we're requesting an integer, which will restrict this field in the GUI to only accept integers.
     * However, the version will need to be a string. So, we'll cast it below.
     */
    @PluginProperty(
        title = "Rundeck API Version",
        description = "Overrides the API version used to make the call. If left blank, the call will use a default API version.",
        defaultValue = ExampleConstants.API_VERSION,
        required = false
    )
    @RenderingOption(key = GROUP_NAME, value = "API Configuration")
    Integer userApiVersion

    /**
     * Here we're requesting the user provides the path to the API key in the Rundeck Key Storage.
     * For security and accessibility, any secure strings of information should always be saved into Key Storage. That includes
     * tokens, passwords, certificates, or any other authentication information.
     * Here, we're setting up the RenderingOptions to display this as a field for keys of the 'password' type (Rundeck-data-type=password).
     * The value of this property will only be a path to the necessary key. You'll see how the actual key is resolved below.
     */
    @PluginProperty(
        title = "Rundeck API Key Path",
        description = "REQUIRED: The path to the Rundeck Key Storage entry for your Rundeck API Key.",
        required = true
    )
    @RenderingOptions([
        @RenderingOption(
            key = StringRenderingConstants.SELECTION_ACCESSOR_KEY,
            value = "STORAGE_PATH"
        ),
        @RenderingOption(
            key = StringRenderingConstants.STORAGE_PATH_ROOT_KEY,
            value = "keys"
        ),
        @RenderingOption(
            key = StringRenderingConstants.STORAGE_FILE_META_FILTER_KEY,
            value = "Rundeck-data-type=password"
        ),
        @RenderingOption(
            key = StringRenderingConstants.GROUP_NAME,
            value = "API Configuration"
        )
    ])
    String apiKeyPath

    @PluginProperty(
        title = "Collapsed test value",
        description = """This is another test property to be output at the end of the execution.\
By default, it will be collapsed in the list of properties, thanks to the '@RenderingOption' \
'GROUPING' key being set to 'secondary'.""",
        required = false
    )
    @RenderingOption(key = GROUP_NAME, value = "Collapsed Configuration")
    /** The secondary grouping RenderingOption is what collapses the field by default in the GUI */
    @RenderingOption(key = GROUPING, value = "secondary")
    String hiddenTestValue

    /**
     * In the main WorkflowStepPlugin class, executeStep() should be the only method.
     * Any other methods supporting the execution should be in another supporting class.
     *
     * Plugins should make good use of logging and log levels in order to provide the user with the right amount
     * of information on execution. Use 'context.getExecutionContext().getExecutionListener().log' to handle logging.
     *
     * Any failure in the execution should be caught and thrown as a StepException
     * StepExceptions require a message and a FailureReason to be provided
     * @param context
     * @param configuration
     * @throws StepException
     */
    @Override
    void executeStep(final PluginStepContext context,
                     final Map<String, Object> configuration
                    ) throws StepException {

        /**
         * We'll resolve the name of the current project. We'll use them to make an
         * API call to Rundeck itself, and get info about the current node.
         */
        String projectName = context.getFrameworkProject()
        String projectInfo
        String userApiVersionString = null
        String userApiKey

        /**
         * Secrets should be retrieved from Key Storage using a try/catch block that fetches credentials/passwords using
         * the user provided path, and the PluginStepContext object.
         */
        try {
            userApiKey = ExampleUtil.getPasswordFromKeyStorage(apiKeyPath, context)
        } catch (StorageException e) {
            throw new StepException(
                "Error accessing ${apiKeyPath}: ${e.getMessage()}",
                ExampleFailureReason.KeyStorageError
            )
        }

        /** Messages can be logged out for the user using print/println */
        System.out.println("Example workflow step executing for project: ${projectName}")

        /**
         * But the preferred method of logging is to write into, and then print out,
         * the executionContext log. First, we add to our logging object from before.
         */
        log.add("Here is a single line log entry. We'll print this as a logLevel 2, along with our next log lines.")
        log.add("Rundeck Plugins use a log level based on the standard syslog model. Here's how it works:")
        log.add(["0": "Error","1": "Warning","2": "Notice","3": "Info","4": "Debug"])
        /**
         * Now that we've added that all to the log, let's print it at logLevel 2, the highest level that
         * the user will see by default.
         */
        context.getExecutionContext()
            .getExecutionListener()
            .log(2, JsonOutput.toJson(log), meta)

        /** Lastly, we'll clear the log. Otherwise, the next time we print, we'll print the previous entries again. */
        log.clear()

        /** Cast the API Version, if it was provided */
        if (userApiVersion) {
            userApiVersionString = userApiVersion.toString()
        }

        try {
            if (!exapis) {
                exapis = new ExampleApis(userBaseApiUrl, userApiVersionString, userApiKey)
            }
            projectInfo = exapis.getProjectInfoByName(projectName)
        } catch (IOException e) {
            throw new StepException(
                "Failed to get resource info with error: '${e.getMessage()}'",
                ExampleFailureReason.ResourceInfoError
            )
        }

        /**
         * At this point, if we haven't failed, we have our result data in hand with projectInfo.
         * Let's save it to outputContext, which will allow the job runner to pass the results
         * to another job step automatically by the context name.
         * In this instance, the resource information in 'projectInfo' can be interpolated into any subsequent job steps by
         * using '${data.projectInfo}'.
         */
        context.getExecutionContext().getOutputContext().addOutput("data", "projectInfo", projectInfo)
        /** Here, we'll get access to 'hiddenTestValue' via '${extra.hiddenTestValue}' */
        context.getExecutionContext().getOutputContext().addOutput("extra", "hiddenTestValue", hiddenTestValue)

        /** Now, we'll add it to the log, print for the user, and call it a day. */
        System.out.println("Job run complete! Results from API call:")
        log.add(projectInfo)
        context.getExecutionContext()
            .getExecutionListener()
            .log(2, JsonOutput.toJson(log), meta)
    }
}
