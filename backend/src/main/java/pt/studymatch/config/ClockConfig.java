package pt.studymatch.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Disponibiliza o "relógio" da aplicação.
 *
 * Porquê não usar logo Instant.now()? Porque assim o serviço recebe o
 * relógio de fora e, nos testes, podemos dar-lhe um relógio falso para
 * simular que o tempo passou, sem esperar de verdade.
 */
@Configuration // classe com configurações do Spring
public class ClockConfig {

    // @Bean: o Spring cria este objeto uma vez e entrega-o a quem o pedir.
    @Bean
    public Clock clock() {
        // Relógio real, em UTC (não depende do fuso horário da máquina).
        return Clock.systemUTC();
    }
}
