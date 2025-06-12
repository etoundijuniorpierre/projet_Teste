package app.teste;

import app.teste.genrated.controller.CodeGenController;
import app.teste.genrated.service.CodeGenService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupCodeGenerator implements ApplicationRunner {

    private final CodeGenController controller;

    public StartupCodeGenerator(CodeGenController controller, CodeGenService service) {
        this.controller = controller;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        controller.generateCrud("User");


    }
}
