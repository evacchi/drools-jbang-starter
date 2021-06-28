package io.github.evacchi.drools;

import org.kie.api.*;
import org.kie.api.builder.*;
import org.kie.api.runtime.*;

public class DroolsLauncher {

    private final KieServices kieServices = KieServices.Factory.get();
    private final KieFileSystem kfs = kieServices.newKieFileSystem();
    private int counter = 0;

    private DroolsLauncher() {}

    public static DroolsLauncher create() { 
        return new DroolsLauncher();
    }

    public DroolsLauncher withRules(String rules) {
        String generatedFilename = String.format("src/main/resources/rules_%s.drl", counter++);
        kfs.write(generatedFilename,
            kieServices.getResources().newReaderResource(
                new java.io.StringReader(rules)));
        return this;
    }

    public KieSession session() {
        var kieBuilder = kieServices.newKieBuilder(kfs).buildAll();

        var results = kieBuilder.getResults();
        if(results.hasMessages(Message.Level.ERROR)){
            System.err.println("Errors building the rule base.");
            System.err.println(results.getMessages());
            System.exit(-1);
        }
        return kieServices
                    .newKieContainer(kieBuilder.getKieModule().getReleaseId())
                    .newKieSession();

    }
}