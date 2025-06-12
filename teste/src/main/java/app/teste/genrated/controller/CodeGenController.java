package app.teste.genrated.controller;

import app.teste.genrated.service.CodeGenService;
import com.squareup.javapoet.TypeName;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/codegen")
public class CodeGenController {

    private final CodeGenService codeGenService;

    public CodeGenController(CodeGenService codeGenService) {
        this.codeGenService = codeGenService;
    }

    @PostMapping("/generate-crud")
    public String generateCrud(@RequestParam String entityName) throws IOException {
        Map<String, TypeName> fields = new HashMap<>();
        fields.put("id", TypeName.get(Long.class));
        fields.put("name", TypeName.get(String.class));
        fields.put("email", TypeName.get(String.class));
        fields.put("active", TypeName.get(Boolean.class));

        codeGenService.generateFullCrud(entityName, fields);
        return "CRUD généré avec succès pour " + entityName;
    }

    @PostMapping("/generate-custom")
    public String generateCustomCrud(
            @RequestParam String entityName,
            @RequestBody Map<String, String> fields) throws IOException {

        Map<String, TypeName> typeFields = new HashMap<>();
        fields.forEach((name, type) -> {
            switch (type.toLowerCase()) {
                case "string":
                    typeFields.put(name, TypeName.get(String.class));
                    break;
                case "long":
                    typeFields.put(name, TypeName.get(Long.class));
                    break;
                case "boolean":
                    typeFields.put(name, TypeName.get(Boolean.class));
                    break;
                case "double":
                    typeFields.put(name, TypeName.get(Double.class));
                    break;
                default:
                    typeFields.put(name, TypeName.get(String.class));
            }
        });

        codeGenService.generateFullCrud(entityName, typeFields);
        return "CRUD personnalisé généré avec succès pour " + entityName;
    }
}