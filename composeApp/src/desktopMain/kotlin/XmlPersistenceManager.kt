import interfaces.PersistenceManager
import org.w3c.dom.Document
import org.w3c.dom.Element
import utils.XPrintln
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class XmlPersistenceManager<T : Any>(private val clazz: KClass<T>, private val fileName: String) :
    PersistenceManager<T> {
    private val xmlFile = File(fileName)

    /**
     * Creates an initial XML file with the provided list of objects.
     * Crea un archivo XML inicial con la lista de objetos proporcionada.
     *
     * @param objs The list of objects to serialize into XML.
     *             La lista de objetos a serializar en XML.
     */
    override fun createInitialFile(objs: List<T>) {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.newDocument()
        val rootElement = document.createElement(clazz.simpleName?.uppercase())
        document.appendChild(rootElement)

        objs.forEach { obj ->
            val objElement = document.createElement(clazz.simpleName?.lowercase())

            try {
                val constructorParams = clazz.constructors.first().parameters.mapNotNull { it.name }
                val propertiesMap = clazz.memberProperties.associateBy { it.name }

                constructorParams.forEach { paramName ->
                    val prop = propertiesMap[paramName]
                    if (prop != null) {
                        val value = prop.get(obj)?.toString() ?: ""
                        val element = document.createElement(prop.name)
                        element.appendChild(document.createTextNode(value))
                        objElement.appendChild(element)
                    } else {
                        XPrintln.log("Property not found for parameter: $paramName")
                    }
                }

                rootElement.appendChild(objElement)
            } catch (e: Exception) {
                val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                    "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
                }
                XPrintln.log("Error: ${e.message}\n$errorDetails\n")
            }
        }

        saveDocument(document)
    }

    /**
     * Creates an XML element for the given object and appends it to the document.
     * Crea un elemento XML para el objeto dado y lo añade al documento.
     *
     * @param obj The object to serialize into an XML element.
     *            El objeto a serializar en un elemento XML.
     */
    override fun create(obj: T) {
        val document = loadDocument()
        val rootElement = document.documentElement

        val objElement = document.createElement(clazz.simpleName?.lowercase())

        try {
            val constructorParams = clazz.constructors
                .first { it.parameters.none { param -> param.name == "seen0" || param.name == "serializationConstructorMarker" } }
                .parameters
                .mapNotNull { it.name }

            val propertiesMap = clazz.memberProperties.associateBy { it.name }

            constructorParams.forEach { paramName ->
                val prop = propertiesMap[paramName]
                if (prop != null) {
                    val value = prop.get(obj)?.toString() ?: ""
                    val element = document.createElement(prop.name)
                    element.appendChild(document.createTextNode(value))
                    objElement.appendChild(element)
                } else {
                    XPrintln.log("Property not found for parameter: $paramName")
                }
            }

            rootElement.appendChild(objElement)
            saveDocument(document)
            XPrintln.log("$obj")
        } catch (e: Exception) {
            val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
            }
            XPrintln.log("Error: ${e.message}\n$errorDetails\n")
        }
    }

    /**
     * Reads an object of type T from the XML document based on the provided object's ID.
     * Lee un objeto de tipo T desde el documento XML basado en el ID proporcionado del objeto.
     *
     * @param obj The object containing the ID to search for.
     *            El objeto que contiene el ID que se desea buscar.
     * @return The deserialized object if found, or null if not found or on error.
     *         El objeto deserializado si se encuentra, o null si no se encuentra o hay un error.
     */
    override fun read(obj: T): T? {
        try {
            val document = loadDocument()
            val nodeList = document.getElementsByTagName(clazz.simpleName?.lowercase())
            val objId = clazz.memberProperties.first { it.name == "id" }.get(obj).toString()

            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i) as Element
                val idElement = node.getElementsByTagName("id").item(0)?.textContent

                if (idElement == objId) {
                    val params = clazz.constructors.first().parameters.map { param ->
                        val prop = clazz.memberProperties.firstOrNull { it.name == param.name }
                        if (prop != null) {
                            val element = node.getElementsByTagName(prop.name).item(0)
                            when (prop.returnType.toString()) {
                                "kotlin.Int" -> element?.textContent?.toInt()
                                "kotlin.Double" -> element?.textContent?.toDouble()
                                "kotlin.Float" -> element?.textContent?.toFloat()
                                "kotlin.Boolean" -> element?.textContent?.toBoolean()
                                else -> element?.textContent
                            }
                        } else {
                            null
                        }
                    }.toTypedArray()

                    val filteredParams = params.filterNotNull().toTypedArray()
                    XPrintln.log("Filtered Params: ${filteredParams.joinToString()}")

                    val constructor =
                        clazz.constructors.find { it.parameters.size == filteredParams.size }
                            ?: throw IllegalArgumentException("No matching constructor found")

                    return constructor.call(*filteredParams)
                }
            }
        } catch (e: Exception) {
            val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
            }
            XPrintln.log("Error: ${e.message}\n$errorDetails\n")
        }
        return null
    }

    override fun readAll(): List<T> {
        return try {
            val document = loadDocument()
            val nodeList = document.getElementsByTagName(clazz.simpleName?.lowercase())
            val result = mutableListOf<T>()

            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i) as Element
                val constructor = clazz.constructors.first()
                val params = constructor.parameters.map { param ->
                    val prop = clazz.memberProperties.firstOrNull { it.name == param.name }
                    if (prop != null) {
                        val element = node.getElementsByTagName(prop.name).item(0)
                        when (prop.returnType.toString()) {
                            "kotlin.Int" -> element?.textContent?.toInt()
                            "kotlin.Double" -> element?.textContent?.toDouble()
                            "kotlin.Float" -> element?.textContent?.toFloat()
                            "kotlin.Boolean" -> element?.textContent?.toBoolean()
                            else -> element?.textContent
                        }
                    } else {
                        null
                    }
                }.toTypedArray()

                val filteredParams = params.filterNotNull().toTypedArray()

                // Verificar que el número de parámetros coincida
                val matchingConstructor =
                    clazz.constructors.find { it.parameters.size == filteredParams.size }
                if (matchingConstructor != null) {
                    result.add(matchingConstructor.call(*filteredParams))
                } else {
                    XPrintln.log("Parameter mismatch: constructor expects ${constructor.parameters.size} but got ${filteredParams.size}")
                }
            }
            result
        } catch (e: Exception) {
            val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
            }
            XPrintln.log("Error: ${e.message}\n$errorDetails\n")
            emptyList<T>()
        }
    }

    /**
     * Updates an existing XML element based on the ID of the provided object [obj].
     * Actualiza un elemento XML existente basado en el ID del objeto proporcionado [obj].
     *
     * If an element with a matching ID is found, updates its properties with the values from [obj].
     * Si se encuentra un elemento con un ID coincidente, actualiza sus propiedades con los valores de [obj].
     */
    override fun update(obj: T) {
        try {
            val document = loadDocument()
            val nodeList = document.getElementsByTagName(clazz.simpleName?.lowercase())
            var updated = false

            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i) as Element
                val idElement = getIdElement(node, clazz.simpleName)

                val objId = clazz.memberProperties.firstOrNull {
                    it.name == "id" ||
                            it.name == "id${clazz.simpleName}" ||
                            it.name == "id${clazz.simpleName?.lowercase()}" ||
                            it.name == "id${clazz.simpleName?.replaceFirstChar { it.uppercase() }}"
                }?.get(obj).toString()

                XPrintln.log("Comparando idElement: $idElement con objId: $objId") // Depuración

                if (idElement == objId) {
                    try {
                        val constructorParams = clazz.constructors
                            .first { it.parameters.none { param -> param.name == "seen0" || param.name == "serializationConstructorMarker" } }
                            .parameters
                            .mapNotNull { it.name }

                        val propertiesMap = clazz.memberProperties.associateBy { it.name }

                        constructorParams.forEach { paramName ->
                            val prop = propertiesMap[paramName]
                            if (prop != null) {
                                val value = prop.get(obj)?.toString() ?: ""
                                val element = node.getElementsByTagName(prop.name).item(0)
                                if (element != null) {
                                    element.textContent = value
                                } else {
                                    XPrintln.log("Element not found for property: ${prop.name}")
                                }
                            } else {
                                XPrintln.log("Property not found for parameter: $paramName")
                            }
                        }
                        updated = true
                    } catch (e: Exception) {
                        val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                            "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
                        }
                        XPrintln.log("Error: ${e.message}\n$errorDetails\n")
                    }
                    break
                }
            }

            if (updated) {
                saveDocument(document)
            } else {
                XPrintln.log("No matching element found for obj: $obj")
            }
        } catch (e: Exception) {
            val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
            }
            XPrintln.log("Error: ${e.message}\n$errorDetails\n")
        }
    }

    private fun getIdElement(node: Element, className: String?): String? {
        val idNames = listOf(
            "id",
            "id$className",
            "id${className?.lowercase()}",
            "id${className?.replaceFirstChar { it.uppercase() }}"
        )
        for (idName in idNames) {
            val idElement = node.getElementsByTagName(idName).item(0)?.textContent
            XPrintln.log("Buscando idName: $idName, encontrado: $idElement") // Depuración
            if (idElement != null) {
                return idElement
            }
        }
        return null
    }

    /**
     * Deletes the XML element corresponding to the given object from the XML document.
     * Elimina el elemento XML que corresponde al objeto dado del documento XML.
     *
     * If no matching element is found, no action is taken.
     * Si no se encuentra ningún elemento coincidente, no se realiza ninguna acción.
     */
    override fun delete(obj: T) {
        try {
            val document = loadDocument()
            val nodeList = document.getElementsByTagName(clazz.simpleName?.lowercase())

            // Obtener el primer parámetro del constructor que sea una propiedad válida
            val constructor = clazz.constructors.first()
            val idProperty = constructor.parameters.mapNotNull { it.name }.firstOrNull { name ->
                clazz.memberProperties.any { it.name == name }
            }
                ?: throw IllegalArgumentException("No valid properties found in class ${clazz.simpleName}")

            val property = clazz.memberProperties.firstOrNull { it.name == idProperty }
            if (property == null) {
                XPrintln.log("Error: No '$idProperty' property found in class ${clazz.simpleName}")
                return
            }

            val objId = property.get(obj).toString()

            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i) as Element
                val idElement = node.getElementsByTagName(idProperty).item(0)?.textContent
                if (idElement == objId) {
                    node.parentNode.removeChild(node)
                    break
                }
            }

            saveDocument(document)
        } catch (e: Exception) {
            val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
            }
            XPrintln.log("Error: ${e.message}\n$errorDetails\n")
        }
    }

    private fun loadDocument(): Document {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        return documentBuilder.parse(xmlFile)
    }

    /**
     * Saves the XML document to the file specified by [xmlFile].
     * Guarda el documento XML en el archivo especificado por [xmlFile].
     *
     * Uses a transformer to format the XML output and handle encoding.
     * Utiliza un transformador para formatear la salida XML y manejar la codificación.
     */
    private fun saveDocument(document: Document) {
        try {
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()

            /**
             * Configurations for XML output
             * Configuraciones para la salida XML
             */
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
            transformer.setOutputProperty(OutputKeys.METHOD, "xml")
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes")

            /**
             * remove auto indent settings
             * Eliminar configuraciones de indentación automática
             */
            transformer.setOutputProperty(OutputKeys.INDENT, "no")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

            /**
             * Source and result of the transformation
             * Fuente y resultado de la transformación
             */
            val source = DOMSource(document)
            val result = StreamResult(xmlFile)
            transformer.transform(source, result)
        } catch (e: Exception) {
            val errorDetails = e.stackTrace.joinToString(separator = "\n") {
                "at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
            }
            XPrintln.log("Error: ${e.message}\n$errorDetails\n")
        }
    }
}
