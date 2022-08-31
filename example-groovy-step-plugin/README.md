# Example Step plugins 

This directory contains a set of example Rundeck step plugins written in Java.

## Plugins
* **ExampleWorkflowStepPlugin** - Calls the Rundeck API to get info about the current project.
* **ExampleNodeStepPlugin** - Calls the Rundeck API to get info about each node.
* **ExampleRemoteScriptNodeStepPlugin** - Generates a script based on user input to be run on remote nodes.

## Usage
### Installation
* Use `./gradlew build` to build the jar file to `build/libs`.
* In your running Rundeck instance, navigate to Plugins > Upload Plugin in your [System Menu](https://docs.rundeck.com/docs/manual/system-configs.html)
* Select the built jar file and install.
* Once complete, the Step plugins will be available from the Edit Job screen.

### Configuration
#### ExampleNode/ExampleWorkflow
The ExampleNode and ExampleWorkflow plugins use the following properties:
* **userBaseApiUrl** (String) - The URL to the API of the Rundeck instance. E.g.: `http://localhost:4440/api/`
* **userApiVersion** (Integer) - The API version to use for calls. E.g.: `41`
* **apiKeyPath** (String) - The path to the API key in Key Storage. E.g.: `keys/apiKey`
* **hiddenTestValue** (String) - An additional value that is printed out at the end of the run. E.g.: `test!`

#### ExampleRemoteScriptNode
The ExampleRemoteScriptNode plugin uses the following properties, all of which are used to exemplify different kinds of
field types and renderingOptions:
* **funky** (String)
* **thesis** (String)
* **jam** (boolean)
* **amount** (Integer)
* **money** (Long)
* **fruit** (String)
* **cake** (String)
* **debug** (boolean)