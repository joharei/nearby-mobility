import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties

fun Project.propertiesFromFile(fileName: String): Properties {
    val loadedFile = file(fileName)
    return Properties().apply {
        if (loadedFile.isFile) {
            FileInputStream(loadedFile).use {
                load(it)
            }
        }
    }
}