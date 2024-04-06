package org.bz.app.mspeople.util.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RunAfterStartupUtil {

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        log.info("Microservicio iniciado y aparentemente listo para recibir peticiones.");
    }
}