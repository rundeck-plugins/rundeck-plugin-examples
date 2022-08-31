package com.rundeck.plugin.example.util

import org.rundeck.storage.api.PathUtil
import org.rundeck.storage.api.StorageException
import com.dtolabs.rundeck.core.storage.ResourceMeta
import com.dtolabs.rundeck.plugins.step.PluginStepContext

/**
 * A “Util” class should be written to handle common methods for renderingOptions, retrieving keys from KeyStorage,
 * auth-settings, and any other generic methods that can be used for support across your suite.
 */
class ExampleUtil {
    static String getPasswordFromKeyStorage(String path, PluginStepContext context){
        try{
            ResourceMeta contents = context.getExecutionContext().getStorageTree().getResource(path).getContents()
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            contents.writeContent(byteArrayOutputStream)
            String password = new String(byteArrayOutputStream.toByteArray())

            return password
        } catch (Exception e){
            throw StorageException.readException(
                PathUtil.asPath(path), e.getMessage()
            )
        }
    }
}