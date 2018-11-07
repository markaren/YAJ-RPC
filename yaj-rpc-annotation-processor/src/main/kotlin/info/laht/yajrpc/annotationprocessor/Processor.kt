package info.laht.yajrpc.annotationprocessor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import info.laht.yajrpc.RpcMethod
import info.laht.yajrpc.net.RpcClient
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateRpcWrapper(val serviceName : String = "")

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("info.laht.yajrpc.annotationprocessor.GenerateRpcWrapper")
@SupportedOptions(GenerateWrappersProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class GenerateWrappersProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(GenerateRpcWrapper::class.java)
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        if(elements.isNotEmpty()){
            val builder = FileSpec.builder("info.laht.yajrpc", "Wrappers")

            for(element in elements){
                val type = TypeSpec.classBuilder("${element.simpleName}Wrapper")
                type.primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter("client", RpcClient::class)
                    .build())
                type.addProperty(PropertySpec.builder("client", RpcClient::class)
                    .initializer("client")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
                )
                val wrapperOptions = element.getAnnotation(GenerateRpcWrapper::class.java)
                val methods = element.enclosedElements
                    .filter { it.kind == ElementKind.METHOD && it.getAnnotation(RpcMethod::class.java) != null}
                    .map { it as ExecutableElement }
                for(method in methods){
                    val methodSpec = FunSpec.builder(method.simpleName.toString())
                    methodSpec.addTypeVariables(method.typeParameters.map { it.asTypeVariableName() })
                    methodSpec.returns(method.returnType.asTypeName().javaToKotlinType())

                    for(parameter in method.parameters){
                        methodSpec.addParameter(parameter.simpleName.toString(), parameter.toTypeName())
                    }

                    methodSpec.addStatement("val result = client.write(\"${wrapperOptions.serviceName.nullIfEmpty()
                        ?: element.simpleName}.${method.simpleName}\", RpcParams.listParams(${method.parameters.joinToString(
                        ", "
                    ) { it.simpleName }})).get()")
                    methodSpec.addStatement("if(result.hasError) throw RuntimeException(result.error!!.toString())")

                    if (method.returnType.kind != TypeKind.VOID) {
                        methodSpec.addStatement("return if(result.hasResult) result.getResult()!! else throw RuntimeException(\"No result\")")
                    }

                    type.addFunction(methodSpec.build())
                }

                builder.addType(type.build())
            }

            val outputFile = File("$kaptKotlinGeneratedDir/info/laht/yajrpc/Wrappers.kt")
            outputFile.parentFile.mkdirs()
            builder.build().writeTo(outputFile)
        }
        return true
    }
}

private fun String.nullIfEmpty() = if(this.isEmpty()) null else this

//See https://github.com/square/kotlinpoet/issues/236
fun Element.toTypeName(): TypeName =
    asType().asTypeName().javaToKotlinType()

fun TypeName.javaToKotlinType(): TypeName {
    return if (this is ParameterizedTypeName) {
        (rawType.javaToKotlinType() as ClassName).parameterizedBy(*typeArguments.map { it.javaToKotlinType() }.toTypedArray())
    } else {
        val className =
            JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))
                ?.asSingleFqName()?.asString()

        return if (className == null) {
            this
        } else {
            ClassName.bestGuess(className)
        }
    }
}