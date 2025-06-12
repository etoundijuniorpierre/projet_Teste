package app.teste.genrated.service;



import com.squareup.javapoet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Service
public class CodeGenService {

    @Value("${codegen.base-package:app.teste}")
    private String basePackage;

    public void generateFullCrud(String entityName, Map<String, TypeName> fields) throws IOException {
        // Générer le DTO
        generateDto(entityName + "Dto", fields);

        // Générer le Repository
        generateRepository(entityName);

        // Générer le Service
        generateService(entityName);

        // Générer le Controller
        generateController(entityName);
    }

    public void generateDto(String dtoName, Map<String, TypeName> fields) throws IOException {
        TypeSpec.Builder dtoBuilder = TypeSpec.classBuilder(dtoName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("lombok", "Data")).build())
                .addAnnotation(AnnotationSpec.builder(ClassName.get("lombok", "NoArgsConstructor")).build())
                .addAnnotation(AnnotationSpec.builder(ClassName.get("lombok", "AllArgsConstructor")).build());

        fields.forEach((name, type) ->
                dtoBuilder.addField(FieldSpec.builder(type, name, Modifier.PRIVATE).build())
        );

        JavaFile.builder(basePackage + ".dto", dtoBuilder.build())
                .build()
                .writeTo(Paths.get("src/main/java"));
    }

    public void generateRepository(String entityName) throws IOException {
        String repoPackage = basePackage + ".repository";

        TypeSpec repository = TypeSpec.interfaceBuilder(entityName + "Repository")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get("org.springframework.data.jpa.repository", "JpaRepository"),
                        ClassName.get(basePackage + ".entity", entityName),
                        ClassName.get(Long.class)))
                .build(); // Remove @Repository to avoid duplicate registration

        JavaFile.builder(repoPackage, repository)
                .build()
                .writeTo(Paths.get("src/main/java"));
    }

    public void generateService(String entityName) throws IOException {
        // Interface
        TypeSpec serviceInterface = TypeSpec.interfaceBuilder(entityName + "Service")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("getAll")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(ParameterizedTypeName.get(
                                ClassName.get("java.util", "List"),
                                ClassName.get(basePackage + ".dto", entityName + "Dto")))
                        .build())
                .build();

        // Implémentation
        TypeSpec serviceImpl = TypeSpec.classBuilder(entityName + "ServiceImpl")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(basePackage + ".service", entityName + "Service")) // Implements UserService
                .addMethod(MethodSpec.methodBuilder("getAll")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(
                                ClassName.get(List.class),
                                ClassName.get(basePackage + ".dto", "UserDto")
                        ))
                        .addStatement("return $T.of()", List.class) // Placeholder logic
                        .build())
                .build();

        JavaFile.builder(basePackage + ".service", serviceInterface)
                .build()
                .writeTo(Paths.get("src/main/java"));

        JavaFile.builder(basePackage + ".service.impl", serviceImpl)
                .build()
                .writeTo(Paths.get("src/main/java"));
    }

    public void generateController(String entityName) throws IOException {
        TypeSpec controller = TypeSpec.classBuilder(entityName + "Controller")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "RestController")).build())
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "RequestMapping"))
                        .addMember("value", "$S", "/api/" + entityName.toLowerCase())
                        .build())
                .addField(FieldSpec.builder(
                                ClassName.get(basePackage + ".service", entityName + "Service"),
                                "service",
                                Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(
                                ClassName.get(basePackage + ".service", entityName + "Service"),
                                "service")
                        .addStatement("this.service = service")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getAll")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.web.bind.annotation", "GetMapping"))
                                .addMember("value", "$S", "/getAllElem")
                                .build())
                        .returns(ParameterizedTypeName.get(
                                ClassName.get("java.util", "List"),
                                ClassName.get(basePackage + ".dto", entityName + "Dto")))
                        .addStatement("return service.getAll()")
                        .build())
                .build();

        JavaFile.builder(basePackage + ".controller", controller)
                .build()
                .writeTo(Paths.get("src/main/java"));
    }
}
